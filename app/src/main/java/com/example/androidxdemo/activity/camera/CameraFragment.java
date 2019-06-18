package com.example.androidxdemo.activity.camera;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.AppUtils;
import com.example.commonlibrary.utils.Image2Utils;
import com.example.commonlibrary.utils.ThreadPool;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";
    public static final String FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS";
    public static final String PHOTO_EXTENSION = ".jpg";
    public static final long ANIMATION_FAST_MILLIS = 50L;
    public static final long ANIMATION_SLOW_MILLIS = 100L;
    private TextureView mCameraX;
    private ConstraintLayout mContainer;
    private DisplayManager mDisplayManager;
    private LocalBroadcastManager mBroadcastManager;
    private File outputDirectory;

    // camerax
    private int displayId = -1; // camera的id
    private Preview mPreview;
    private ImageCapture mImageCapture;
    private ImageAnalysis mImageAnalysis;
    private CameraX.LensFacing mLensFacing = CameraX.LensFacing.BACK; // 默认使用后置摄像头


    // 接收的局部广播
    private BroadcastReceiver mVolumeDownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra(DemoCameraXActivity.KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN);
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                ImageButton shutter = mContainer.findViewById(R.id.camera_capture_button);
                shutter.performClick();
            }
        }
    };

    private DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int displayId) {
            Log.d(TAG, "onDisplayAdded: id=" + displayId);
        }

        @Override
        public void onDisplayRemoved(int displayId) {
            Log.d(TAG, "onDisplayRemoved: id=" + displayId);
        }

        @Override
        public void onDisplayChanged(int displayId) {
            Log.d(TAG, "onDisplayChanged: id=" + displayId);
            // 改变屏幕的时候会调用
            if (displayId == CameraFragment.this.displayId) {
                if (getView() == null) {
                    return;
                }
                if (mPreview != null) {
                    mPreview.setTargetRotation(getView().getDisplay().getRotation());
                }
                if (mImageCapture != null) {
                    mImageCapture.setTargetRotation(getView().getDisplay().getRotation());
                }
                if (mImageAnalysis != null) {
                    mImageAnalysis.setTargetRotation(getView().getDisplay().getRotation());
                }
            }
        }
    };


    // 拍照监听器
    private ImageCapture.OnImageSavedListener mImageSavedListener = new ImageCapture.OnImageSavedListener() {
        @Override
        public void onImageSaved(@NonNull File file) {
            Log.d(TAG, "take photo success file:" + file.getName() + " path=" + file.getPath());
            setGalleryThumbnail(file);

            // 获取mime type
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1));

            MediaScannerConnection.scanFile(getContext(), new String[]{file.getAbsolutePath()}, new String[]{mimeType}, (path, uri) -> Log.d(TAG, "onScanCompleted: path=" + path + " uri=" + uri.toString()));
        }

        @Override
        public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
            Log.e(TAG, "take photo error:" + message);
            if (cause != null) {
                cause.printStackTrace();
            }
        }
    };

    public CameraFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置生命周期变化不会重启
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContainer = (ConstraintLayout) view;
        mCameraX = mContainer.findViewById(R.id.camera);
        // 注册局部广播
        mBroadcastManager = LocalBroadcastManager.getInstance(requireContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(DemoCameraXActivity.KEY_EVENT_ACTION);
        mBroadcastManager.registerReceiver(mVolumeDownReceiver, filter);

        mDisplayManager = (DisplayManager) mCameraX.getContext().getSystemService(Context.DISPLAY_SERVICE);
        mDisplayManager.registerDisplayListener(mDisplayListener, null);

        outputDirectory = DemoCameraXActivity.getOutputDirectory(requireContext());

        mCameraX.post(() -> {
            displayId = mCameraX.getDisplay().getDisplayId();

            updateCameraUi();
            bindCameraUseCases();

            ThreadPool.runOnIOPool(() -> {
                for (File file : outputDirectory.listFiles()) {
                    if (GalleryFragment.EXTENSION_WHITELIST.contains(file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase())) {
                        setGalleryThumbnail(file);
                    }
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBroadcastManager.unregisterReceiver(mVolumeDownReceiver);
        mDisplayManager.unregisterDisplayListener(mDisplayListener);
    }

    private void bindCameraUseCases() {
        // 先取消绑定所有
        CameraX.unbindAll();

        // 获取camera的DisplayMetrics
        DisplayMetrics metrics = new DisplayMetrics();
        mCameraX.getDisplay().getMetrics(metrics);

        // 获取宽高比
        Rational screenAspectRatio = new Rational(metrics.widthPixels, metrics.heightPixels);
        Log.d(TAG, "Metrics: " + metrics.widthPixels + " x " + metrics.heightPixels);

        // 初始化preview
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setLensFacing(mLensFacing)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(mCameraX.getDisplay().getRotation())
                .build();
        mPreview = new AutoFitPreviewBuilder(previewConfig, new WeakReference<>(mCameraX)).build();

        // 初始化ImageCapture
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
                .setLensFacing(mLensFacing)
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(mCameraX.getDisplay().getRotation())
                .build();
        mImageCapture = new ImageCapture(imageCaptureConfig);


        ImageAnalysisConfig imageAnalysisConfig = new ImageAnalysisConfig.Builder()
                .setLensFacing(mLensFacing)
                .setCallbackHandler(new Handler(ThreadPool.getWorkerLooper()))
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .setTargetRotation(mCameraX.getDisplay().getRotation())
                .build();

        mImageAnalysis = new ImageAnalysis(imageAnalysisConfig);
        mImageAnalysis.setAnalyzer(new LuminosityAnalyzer(luma -> Log.d(TAG, "onFrameAnalyzed: luma=" + luma)));

        CameraX.bindToLifecycle(this, mPreview, mImageCapture, mImageAnalysis);
    }


    @SuppressLint("RestrictedApi")
    private void updateCameraUi() {
        // 首先移除之前的camera相机
        ConstraintLayout uiContainer = mContainer.findViewById(R.id.camera_ui_container);
        if (uiContainer != null) {
            mContainer.removeView(uiContainer);
        }

        View controls = View.inflate(requireContext(), R.layout.layout_camera_ui, mContainer);

        // 拍照
        controls.findViewById(R.id.camera_capture_button).setOnClickListener(view -> {
            if (mImageCapture == null) {
                return;
            }

            // 先创建一个用来保存图片的file
            File photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION);
            ImageCapture.Metadata metadata = new ImageCapture.Metadata();
            metadata.isReversedHorizontal = mLensFacing == CameraX.LensFacing.FRONT;

            // 拍照
            mImageCapture.takePicture(photoFile, mImageSavedListener, metadata);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mContainer.postDelayed(() -> {
                    mContainer.setForeground(new ColorDrawable(Color.WHITE));
                    mContainer.postDelayed(() -> mContainer.setForeground(null), ANIMATION_FAST_MILLIS);
                }, ANIMATION_SLOW_MILLIS);
            }
        });

        // 改变是前置摄像头还是后置摄像头
        controls.findViewById(R.id.camera_switch_button).setOnClickListener(view -> {
            mLensFacing = CameraX.LensFacing.FRONT == mLensFacing ? CameraX.LensFacing.BACK : CameraX.LensFacing.FRONT;

            try {
                CameraX.getCameraWithLensFacing(mLensFacing);
                bindCameraUseCases();
            } catch (CameraInfoUnavailableException e) {
                e.printStackTrace();
            }
        });

        // 查看图库
        controls.findViewById(R.id.photo_view_button).setOnClickListener(view -> {
            Bundle arguments = new Bundle();
            arguments.putString(GalleryFragment.KEY_ROOT_DIRECTORY, outputDirectory.getAbsolutePath());
            // 导航到GalleryFragment去
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                    .navigate(R.id.action_cameraFragment_to_galleryFragment, arguments);
        });
    }

    private File createFile(File baseFolder, String format, String extension) {
        return new File(baseFolder, new SimpleDateFormat(format, Locale.CHINA).format(System.currentTimeMillis()) + extension);
    }

    private void setGalleryThumbnail(File file) {
        ImageButton thumbnail = mContainer.findViewById(R.id.photo_view_button);

        // 在io线程执行操作
        ThreadPool.runOnIOPool(() -> {
            Bitmap bitmap = Image2Utils.decodeBitmap(file);
            Bitmap thumbnailBitmap = Image2Utils.cropCircularThumbnail(bitmap, 128);
            // 切回到主线程
            ThreadPool.runOnUi(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    thumbnail.setForeground(new BitmapDrawable(AppUtils.app().getResources(), thumbnailBitmap));
                } else {
                    Glide.with(requireContext()).load(thumbnailBitmap).into(thumbnail);
                }
            });
        });
    }

    // 分析图片帧
    private class LuminosityAnalyzer implements ImageAnalysis.Analyzer {
        private int frameRateWindow = 8;
        private List<OnFrameAnalyzedListener> listeners = new ArrayList<>();
        private ArrayDeque<Long> frameTimestamps = new ArrayDeque<>(5);
        private double framesPerSecond = -1.0; // 速率
        private long lastAnalyzedTimestamp = 0L;

        @Override
        public void analyze(ImageProxy image, int rotationDegrees) {
            if (listeners.isEmpty()) {
                return;
            }
//            Log.d(TAG, "analyze: current size=" + frameTimestamps.size());
            // 将当前的时间保存起来
            frameTimestamps.push(System.currentTimeMillis());


            while (frameTimestamps.size() >= frameRateWindow) {
                frameTimestamps.removeLast();
            }

            // 计算每秒可以处理多少帧
            framesPerSecond = 1.0 / ((frameTimestamps.peekFirst() - frameTimestamps.peekLast()) / (double) frameTimestamps.size()) * 1000.0;
//            Log.d(TAG, "analyze: rate of frames/s=" + framesPerSecond);

            // 判断当前最新的时间是否超过了1s
            if (frameTimestamps.getFirst() - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {

                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                if (buffer == null) {
                    return;
                } else {
//                    Log.d(TAG, "analyze: buffer=" + buffer.toString());
                }

                buffer.rewind();
//                Log.d(TAG, "analyze: rewind buffer=" + buffer.toString());
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
//                Log.d(TAG, "analyze: get data=" + Arrays.toString(data));

                int[] pixels = new int[data.length];
                for (int i = 0; i < data.length; i++) {
                    pixels[i] = data[i] & 0xff;
                }

                int sum = 0;
                for (int pixel : pixels) {
                    sum += pixel;
                }

                double luma = (double) sum / (double) pixels.length;
//                Log.d(TAG, "analyze: sum=" + sum + " luma=" + luma + " per second=" + String.format("%.01f", framesPerSecond));
                // 回调每一个帧
                for (OnFrameAnalyzedListener listener : listeners) {
                    listener.onFrameAnalyzed(luma);
                }

                lastAnalyzedTimestamp = frameTimestamps.getFirst();
            }
        }

        public LuminosityAnalyzer(OnFrameAnalyzedListener listener) {
            onFrameAnalyzed(listener);
        }

        public void onFrameAnalyzed(OnFrameAnalyzedListener listener) {
            listeners.add(listener);
        }
    }

    public interface OnFrameAnalyzedListener {
        void onFrameAnalyzed(double luma);
    }
}
