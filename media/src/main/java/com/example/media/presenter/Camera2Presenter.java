package com.example.media.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.commonlibrary.utils.DisplayUtils;
import com.example.commonlibrary.utils.PermissionHelper;
import com.example.commonlibrary.utils.Toasty;
import com.example.media.model.CameraParamModel;
import com.example.media.view.AutoFitTextureView;
import com.example.room.mvp.BasePresenter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Camera2Presenter extends BasePresenter {
    private static final String TAG = "Camera2Presenter";
    private static final int MAX_PREVIEW_WIDTH = DisplayUtils.getScreenWidth();
    private static final int MAX_PREVIEW_HEIGHT = DisplayUtils.getScreenHeight();
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NOT_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;
    private int mState = STATE_PREVIEW;
    private CameraParamModel mModel;
    private WeakReference<AutoFitTextureView> mWeakTextureView;
    private WeakReference<Activity> mWeakActivity;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private ImageReader mImageReader;
    private Integer mSensorOrientation;
    private Size mPreviewSize;
    private boolean mFlashSupported;
    private String mCameraId;
    private File mFile;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest mPreviewRequest;


    private Semaphore mSemaphore = new Semaphore(1);


    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            Log.d(TAG, "onSurfaceTextureAvailable: size (" + width + "x" + height + ")");

            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            Log.d(TAG, "onSurfaceTextureSizeChanged: size (" + width + "x" + height + ")");
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            Log.d(TAG, "onSurfaceTextureDestroyed: ");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
//            Log.d(TAG, "onSurfaceTextureUpdated: ");
        }
    };


    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            // 当照片数据可用时激发该方法
            Log.d(TAG, "onImageAvailable: imageReader=" + imageReader);
            mBackgroundHandler.post(new ImageSaver(imageReader.acquireNextImage(), mFile));
        }
    };


    // 相机状态回调接口
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NotNull CameraDevice cameraDevice) {
            Log.d(TAG, "onOpened: cameraId=" + cameraDevice.getId());
            mSemaphore.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NotNull CameraDevice cameraDevice) {
            Log.d(TAG, "onDisconnected: cameraId=" + cameraDevice.getId());
            mSemaphore.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NotNull CameraDevice cameraDevice, int error) {
            Log.e(TAG, "onError: cameraId=" + cameraDevice.getId() + " error=" + error);
            mSemaphore.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = mWeakActivity.get();
            if (null != activity) {
                activity.finish();
            }
        }
    };

    // 拍照的回调接口
    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW:
//                    Log.d(TAG, "process: state preview");
                    break;
                case STATE_WAITING_LOCK:
                    Log.d(TAG, "process: state waiting lock");
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                case STATE_WAITING_PRECAPTURE:
                    Log.d(TAG, "process: state waiting precapture");
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NOT_PRECAPTURE;
                    }
                    break;
                case STATE_WAITING_NOT_PRECAPTURE:
                    Log.d(TAG, "process: state waiting not precapture");
                    Integer aeState2 = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState2 == null || aeState2 != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                case STATE_PICTURE_TAKEN:
                    Log.d(TAG, "process: state picture taken");
                    break;
                default:

            }
        }

        // onCaptureStarted -> onCaptureCompleted 循环调用
        @Override
        public void onCaptureStarted(@NotNull CameraCaptureSession session,
                                     @NotNull CaptureRequest request,
                                     long timestamp,
                                     long frameNumber) {
        }

        @Override
        public void onCaptureProgressed(@NotNull CameraCaptureSession session,
                                        @NotNull CaptureRequest request,
                                        @NotNull CaptureResult partialResult) {
            Log.d(TAG, "onCaptureProgressed: session=" + session +
                    "\nrequest=" + request +
                    "\npartialResult=" + partialResult +
                    "\n\n");
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NotNull CameraCaptureSession session,
                                       @NotNull CaptureRequest request,
                                       @NotNull TotalCaptureResult result) {
            process(result);
        }

        @Override
        public void onCaptureFailed(@NotNull CameraCaptureSession session,
                                    @NotNull CaptureRequest request,
                                    @NotNull CaptureFailure failure) {
            Log.d(TAG, "onCaptureFailed: session=" + session +
                    "\nrequest=" + request +
                    "\nfailure=" + failure +
                    "\n\n");
        }

        @Override
        public void onCaptureSequenceCompleted(@NotNull CameraCaptureSession session,
                                               int sequenceId,
                                               long frameNumber) {
            Log.d(TAG, "onCaptureSequenceCompleted: session=" + session +
                    "\nsequenceId=" + sequenceId +
                    "\nframeNumber=" + frameNumber +
                    "\n\n");
            // 拍照的时候会调用
        }

        @Override
        public void onCaptureSequenceAborted(@NotNull CameraCaptureSession session,
                                             int sequenceId) {
            Log.d(TAG, "onCaptureSequenceAborted: session=" + session +
                    "\nsequenceId=" + sequenceId +
                    "\n\n");
        }

        @Override
        public void onCaptureBufferLost(@NotNull CameraCaptureSession session,
                                        @NotNull CaptureRequest request,
                                        @NotNull Surface target,
                                        long frameNumber) {
            Log.d(TAG, "onCaptureBufferLost: session=" + session +
                    "\nrequest=" + request +
                    "\ntarget=" + target +
                    "\nframeNumber=" + frameNumber +
                    "\n\n");
        }
    };


    public Camera2Presenter(LifecycleOwner owner, Activity activity, AutoFitTextureView textureView, CameraParamModel model) {
        super(owner);
        mWeakTextureView = new WeakReference<>(textureView);
        mWeakActivity = new WeakReference<>(activity);
        mModel = model;
        mFile = new File(activity.getExternalFilesDir(null), "pic.jpg");
    }


    @Override
    public void create(LifecycleOwner owner) {
        super.create(owner);
    }

    @Override
    public void resume(LifecycleOwner owner) {
        super.resume(owner);
        AutoFitTextureView textureView = mWeakTextureView.get();

        if (textureView == null) {
            Log.e(TAG, "Couldn't get texture");
            return;
        }

        startBackgroundThread();

        if (textureView.isAvailable()) {
            openCamera(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void pause(LifecycleOwner owner) {
        closeCamera();
        stopBackgroundThread();
        super.pause(owner);
    }

    @Override
    public void destroy(LifecycleOwner owner) {
        super.destroy(owner);
    }



    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }


    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("MissingPermission")
    private void openCamera(int width, int height) {
        Activity activity = mWeakActivity.get();
        if (activity == null) {
            Log.e(TAG, "Activity may be finished.");
            return;
        }
        if (!PermissionHelper.hasCameraPermission(activity)) {
            PermissionHelper.requestCameraPermission(activity, true);
            return;
        }
        // 设置参数
        setUpCameraOutputs(width, height);
        // 进行矩阵变化
        configureTransform(width, height);
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        if (manager == null) {
            Log.e(TAG, "Couldn't get CameraManager.");
            return;
        }
        try {
            if (!mSemaphore.tryAcquire(1, 2500, TimeUnit.MILLISECONDS)) {
                Log.e(TAG, "Can't not acquire semaphore");
                return;
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭相机
    private void closeCamera() {
        try {
            mSemaphore.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mSemaphore.release();
        }
    }

    // 创建拍照的session
    private void createCameraPreviewSession() {
        AutoFitTextureView textureView = mWeakTextureView.get();
        if (textureView == null) {
            Log.e(TAG, "Could't get TextureView");
            return;
        }
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            if (texture == null) {
                Log.e(TAG, "TextureView get SurfaceTexture failed");
                return;
            }

            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            Surface surface = new Surface(texture);
            // 创建作为预览的CaptureRequest.Builder
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将textureView的surface作为CaptureRequest.Builder的目标
            mPreviewRequestBuilder.addTarget(surface);
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NotNull CameraCaptureSession cameraCaptureSession) {
                    if (mCameraDevice == null) {
                        Log.d(TAG, "Camera already closed.");
                        return;
                    }
                    try {
                        mCaptureSession = cameraCaptureSession;

                        // 设置自动对焦
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                        // 设置自动开启闪光灯
                        setAutoFlash(mPreviewRequestBuilder);
                        mPreviewRequest = mPreviewRequestBuilder.build();
                        // 请求不断重复捕获图像，即实现预览。
                        mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NotNull CameraCaptureSession cameraCaptureSession) {
                    Toasty.showError("设置拍照失败!");
                    Log.e(TAG, "create session failed.");
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Camera参数
     * @param width textureView宽
     * @param height textureView高
     */
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = mWeakActivity.get();
        AutoFitTextureView textureView = mWeakTextureView.get();
        if (activity == null || textureView == null) {
            Log.e(TAG, "Activity or TextureView can not be acquired.");
            return;
        }
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        if (manager == null) {
            Log.e(TAG, "Couldn't get CameraManager.");
            return;
        }

        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // 获取摄像头朝向
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    Log.e(TAG, "Could't not get stream configuration map");
                    return;
                }


                Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                // 选择尺寸
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);
                // 获取fps
                Range<Integer>[] fpsArrays = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);

                mModel.mRectWidth.postValue(width);
                mModel.mRectHeight.postValue(height);
                mModel.mCameraPreviewWidth.postValue(mPreviewSize.getHeight());
                mModel.mCameraPreviewHeight.postValue(mPreviewSize.getWidth());
                mModel.mCameraPreviewFps.postValue(Arrays.toString(fpsArrays));


                int orientation = activity.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    textureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    textureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                // 是否支持闪光灯.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                mCameraId = cameraId;
                return;

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight,
                                          int maxWidth, int maxHeight, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();

        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight
                    && option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        Log.d(TAG, "bigEnough: " + bigEnough.toString());
        Log.d(TAG, "notBigEnough: " + notBigEnough.toString());
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }


    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = mWeakActivity.get();
        AutoFitTextureView textureView = mWeakTextureView.get();
        if (null == textureView || mPreviewSize == null || activity == null) {
            Log.e(TAG, "Set up camera output failed");
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Log.d(TAG, "configureTransform: current rotation=" + rotation + " size (" + viewWidth + "x" + viewHeight + ")");
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        mModel.mZoomHeight.postValue(viewHeight);
        mModel.mZoomWidth.postValue(viewWidth);
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());

            mModel.mZoomPercent.postValue(scale);
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
            mModel.mZoomPercent.postValue(0.0f);
        }
        textureView.setTransform(matrix);
    }

    // 自动使用闪光灯
    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    // 请求拍摄静止图片
    private void captureStillPicture() {
        Activity activity = mWeakActivity.get();
        if (activity == null) {
            Log.e(TAG, "Activity may be finished.");
            return;
        }
        if (mCameraDevice == null) {
            Log.d(TAG, "Camera has been closed.");
            return;
        }

        try {
            CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureBuilder.addTarget(mImageReader.getSurface());
            // 设置自动对焦模式
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 设置自动曝光模式
            setAutoFlash(captureBuilder);

            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));
            CameraCaptureSession.CaptureCallback callback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NotNull CameraCaptureSession session,
                                               @NotNull CaptureRequest request,
                                               @NotNull TotalCaptureResult result) {
                    Log.d(TAG, "Capture onCaptureCompleted: Saved file: " + mFile);
                    Toasty.showSuccess("Saved: " + mFile);
                    unlockFocus();
                }
            };
            // 停止连续取景
            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            // 捕获静态图像
            mCaptureSession.capture(captureBuilder.build(), callback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // 当拍照结束后调用
    private void unlockFocus() {
        try {
            // 重设自动对焦模式
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            // 设置自动曝光模式
            setAutoFlash(mPreviewRequestBuilder);
            // 捕获静态图片
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
            // 回到预览状态
            mState = STATE_PREVIEW;
            // 打开连续取景模式
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    // 锁定聚焦
    private void lockFocus() {
        try {
            // 告诉摄像机开始对焦
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    // 旋转270度
    private int getOrientation(Integer rotation) {
        int rotate = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                rotate = 90;
                break;
            case Surface.ROTATION_90:
                rotate = 0;
                break;
            case Surface.ROTATION_180:
                rotate = 270;
                break;
            case Surface.ROTATION_270:
                rotate = 180;
                break;
        }
        return (rotate + mSensorOrientation + 270) % 360;
    }

    private void runPrecaptureSequence() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }



    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private static class ImageSaver implements Runnable {
        private final Image mImage;
        private final File mFile;

        ImageSaver(Image image, File file) {
            this.mImage = image;
            this.mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    //----------------------------------对外开放--------------------------------------//
    //-------------------------------------------------------------------------------//
    //-------------------------------------------------------------------------------//
    //-------------------------------------------------------------------------------//
    //-------------------------------------------------------------------------------//
    //-------------------------------------------------------------------------------//
    // 拍照调用的方法
    public void takePicture() {
        lockFocus();
    }
}
