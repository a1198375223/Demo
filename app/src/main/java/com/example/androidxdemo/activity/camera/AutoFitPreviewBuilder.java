package com.example.androidxdemo.activity.camera;

import android.content.Context;
import android.graphics.Matrix;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;

import java.lang.ref.WeakReference;
import java.util.Objects;

// 自适应的摄像头
public class AutoFitPreviewBuilder {
    private static final String TAG = "AutoFitPreviewBuilder";
    private Integer rotation = null;
    private int displayId = -1;
    private Preview preview;
    private Size cameraDimens = new Size(0, 0);
    private Size bufferDimens = new Size(0, 0);
    private DisplayManager mDisplayManager;
    private WeakReference<TextureView> mCamera;

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
            if (mCamera.get() == null) {
                return;
            }
            TextureView cameraX = mCamera.get();
            if (displayId == AutoFitPreviewBuilder.this.displayId) {
                Display display = cameraX.getDisplay();
                Integer rotation = getDisplaySurfaceRotation(display);
                updateTransform(cameraX, rotation, bufferDimens, cameraDimens);
            }
        }
    };



    public AutoFitPreviewBuilder(PreviewConfig config, WeakReference<TextureView> cameraTexture) {
        mCamera = cameraTexture;
        TextureView camera = cameraTexture.get();
        if (camera == null) {
            throw new IllegalArgumentException("Invalid reference to view finder used");
        }

        // 获取camera id
        displayId = camera.getDisplay().getDisplayId();
        // 获取rotation
        rotation = getDisplaySurfaceRotation(camera.getDisplay());

        preview = new Preview(config);

        preview.setOnPreviewOutputUpdateListener(output -> {
            Log.d(TAG, "preview output update listener");
            TextureView cameraX = mCamera.get();
            if (cameraX == null) {
                Log.d(TAG, "cameraTexture is null");
                return;
            }

            ViewGroup parent = (ViewGroup) cameraX.getParent();
            parent.removeView(cameraX);
            parent.addView(cameraX, 0);

            cameraX.setSurfaceTexture(output.getSurfaceTexture());
            rotation = output.getRotationDegrees();
            Integer currentRotate = getDisplaySurfaceRotation(cameraX.getDisplay());
            updateTransform(cameraX, currentRotate, output.getTextureSize(), cameraDimens);
        });

        camera.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            Log.d(TAG, "layout change listener");
            TextureView cameraX = (TextureView) v;
            Size newCameraDimens = new Size(right - left, bottom - top);
            Integer rotation = getDisplaySurfaceRotation(cameraX.getDisplay());
            updateTransform(cameraX, rotation, bufferDimens, newCameraDimens);
        });

        mDisplayManager = (DisplayManager) camera.getContext().getSystemService(Context.DISPLAY_SERVICE);
        mDisplayManager.registerDisplayListener(mDisplayListener, null);

        camera.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                Log.d(TAG, "on view attached to window");
                mDisplayManager.registerDisplayListener(mDisplayListener, null);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                Log.d(TAG, "on view detached from window");
                mDisplayManager.unregisterDisplayListener(mDisplayListener);
            }
        });
    }


    private void updateTransform(TextureView textureView, Integer rotation, Size newBufferDimens, Size newCameraDimens) {
        if (textureView == null) {
            return;
        }

        if (rotation == null) {
            return;
        }

        if (rotation.equals(this.rotation)
                && Objects.equals(newBufferDimens, bufferDimens)
                && Objects.equals(newCameraDimens, cameraDimens)) {
            return;
        }
        this.rotation = rotation;

        if (newBufferDimens.getWidth() == 0 || newBufferDimens.getHeight() == 0) {
            return;
        } else {
            bufferDimens = newBufferDimens;
        }

        if (newCameraDimens.getWidth() == 0 || newCameraDimens.getHeight() == 0) {
            return;
        } else {
            cameraDimens = newCameraDimens;
        }

        Matrix matrix = new Matrix();

        float centerX = textureView.getWidth() / 2f;
        float centerY = textureView.getHeight() / 2f;
        matrix.postRotate(-this.rotation, centerX, centerY);

        float bufferRatio = (float) bufferDimens.getHeight() / (float) bufferDimens.getWidth();
        int scaledWidth;
        int scaledHeight;
        if (textureView.getWidth() > textureView.getHeight()) {
            scaledHeight = textureView.getWidth();
            scaledWidth = Math.round(textureView.getWidth() * bufferRatio);
        } else {
            scaledHeight = textureView.getHeight();
            scaledWidth = Math.round(textureView.getHeight() * bufferRatio);
        }

        float xScale = scaledWidth / (float) textureView.getWidth();
        float yScale = scaledHeight / (float) textureView.getHeight();

        matrix.preScale(xScale, yScale, centerX, centerY);
        textureView.setTransform(matrix);
    }


    private Integer getDisplaySurfaceRotation(Display display) {
        if (display == null) {
            return null;
        }
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                return null;
        }
    }

    public Preview build() {
        return preview;
    }
}
