package com.example.androidxdemo.activity.camera;

import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Rational;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.FlashMode;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

import com.example.room.mvp.BasePresenter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;


public class CameraPresenter extends BasePresenter {
    private boolean isPreviewing = false;
    private Handler mCallback;
    private HandlerThread mHandlerThread;
    private File saveFile;
    private ImageCapture mImageCapture;
    private ImageAnalysis mImageAnalysis;

    public CameraPresenter(LifecycleOwner owner) {
        super(owner);
        mHandlerThread = new HandlerThread("camera-thread");
        mHandlerThread.setPriority(Thread.NORM_PRIORITY - 2);
        mHandlerThread.start();
        mCallback = new Handler(Looper.getMainLooper());
        Log.d(TAG, "CameraPresenter: path=" + Environment.getExternalStorageDirectory().getPath());
        saveFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.example.camera/camera");
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
    }

    // 开启相机
    public void startCamera(TextureView camera, int rotation) {
        if (isPreviewing) {
            return;
        }
        isPreviewing = true;
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                // 设置旋转
                .setTargetRotation(rotation)
                // 设置回调callback
                .setCallbackHandler(mCallback)
                // 设置宽高比
//                .setTargetAspectRatio(new Rational(1, 1))
                .setTargetName("preview-photo")
                // 设置分辨率
//                .setTargetResolution(new Size(1920, 1080))
                // 设置前后摄像头
                .setLensFacing(CameraX.LensFacing.FRONT)
                .build();

        Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(output -> {
            Log.d(TAG, "camera output change.");
            ViewGroup viewGroup = (ViewGroup) camera.getParent();
            viewGroup.removeView(camera);
            viewGroup.addView(camera, 0);
            camera.setSurfaceTexture(output.getSurfaceTexture());
            updateTransform(camera);
        });


        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
                // 设置是用前置摄像头还是后置摄像头
                .setLensFacing(CameraX.LensFacing.FRONT)
                // 设置是否使用闪光灯
                .setFlashMode(FlashMode.AUTO)
                // 设置捕获模式 只有在支持的手机才能生效
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                // 设置名称
                .setTargetName("target-name")
                // 设置是输出的分辨率
//                .setTargetResolution(new Size(1920, 1080))
                // 设置旋转
                .setTargetRotation(rotation)
                // 设置回调handler
                .setCallbackHandler(mCallback)
                // 设置图片的长宽比例
//                .setTargetAspectRatio(new Rational(1, 1))
                .build();

        mImageCapture = new ImageCapture(imageCaptureConfig);



        ImageAnalysisConfig imageAnalysisConfig = new ImageAnalysisConfig.Builder()
                // 设置回调
                .setCallbackHandler(mCallback)
                // 设置前后摄像头
                .setLensFacing(CameraX.LensFacing.FRONT)
                // 设置宽高比
                .setTargetAspectRatio(new Rational(1, 1))
                // 设置名称
                .setTargetName("analysis-photo")
                // 设置旋转
                .setTargetRotation(rotation)
                // 设置分辨率
//                .setTargetResolution(new Size(1920, 1080))
                // 设置摄像机管道可用的图像数量
                .setImageQueueDepth(10)
                // 获取图像的模式, 每次都获取最新的 or 每次都获取下一张图片
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .build();
//
//        mImageAnalysis = new ImageAnalysis(imageAnalysisConfig);

        CameraX.bindToLifecycle((LifecycleOwner) owner.get(), preview);
    }

    // 拍照
    public void takePhoto(WeakReference<TakePhotoCallback> callback) {
        mImageCapture.takePicture(saveFile, new ImageCapture.OnImageSavedListener() {
            @Override
            public void onImageSaved(@NonNull File file) {
                if (callback.get() != null) {
                    callback.get().success(file);
                }
            }

            @Override
            public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                if (callback.get() != null) {
                    callback.get().error(useCaseError, message, cause);
                }
            }
        });
    }

    // 分析图片
    public void analysisPhoto() {
        mImageAnalysis.setAnalyzer(new LuminosityAnalyzer());
    }


    private void updateTransform(TextureView camera) {
        Matrix matrix = new Matrix();

        float centerX = camera.getWidth() / 2f;
        float centerY = camera.getHeight() / 2f;

        int rotationDegrees = 0;

        switch (camera.getDisplay().getRotation()) {
            case Surface.ROTATION_0:
                rotationDegrees = 0;
                break;
            case Surface.ROTATION_90:
                rotationDegrees = 90;
                break;
            case Surface.ROTATION_180:
                rotationDegrees = 180;
                break;
            case Surface.ROTATION_270:
                rotationDegrees = 270;
                break;
            default:
        }

        matrix.postRotate((float) -rotationDegrees, centerX, centerY);

        camera.setTransform(matrix);
    }

    @Override
    public void destroy(LifecycleOwner owner) {
        super.destroy(owner);
        mHandlerThread.quit();
        mHandlerThread = null;
        mCallback = null;
        CameraX.unbindAll();
    }

    public interface TakePhotoCallback {
        void success(File file);

        void error(ImageCapture.UseCaseError error, String message, Throwable cause);
    }


    public class LuminosityAnalyzer implements ImageAnalysis.Analyzer {
        private long lastAnalyzedTimestamp = 0L;

        @Override
        public void analyze(ImageProxy image, int rotationDegrees) {
            long currentTimestamp = System.currentTimeMillis();

            if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();

            }
        }

    }
}

