package com.example.androidxdemo.activity.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.androidxdemo.R;

import java.io.File;
import java.lang.ref.WeakReference;

public class CameraXActivity extends AppCompatActivity {
    private static final String TAG = "CameraXActivity";
    private final int REQUEST_CODE_PERMISSIONS = 10;

    private TextureView mTextureView;
    private Button mPreview, mTakePhoto, mAnalyze;
    private CameraPresenter mPresenter;


    private String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA,
    };

    private CameraPresenter.TakePhotoCallback mCallback = new CameraPresenter.TakePhotoCallback() {
        @Override
        public void success(File file) {
            Log.d(TAG, "success: save file path=" + file.getPath() + " file name=" + file.getName());
        }

        @Override
        public void error(ImageCapture.UseCaseError error, String message, Throwable cause) {
            Log.e(TAG, "error: error=" + error.toString() + " message=" + message + " throwable=", cause);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax);
        mTextureView = findViewById(R.id.camera);
        mPresenter = new CameraPresenter(this);

        mPreview = findViewById(R.id.preview);
        mTakePhoto = findViewById(R.id.take_photo);
        mAnalyze = findViewById(R.id.analyze);

        mPreview.setOnClickListener(view -> {
            // 预览
            mPresenter.startCamera(mTextureView, getWindowManager().getDefaultDisplay().getRotation());
        });

        mTakePhoto.setOnClickListener(view -> {
            // 拍照
            mPresenter.takePhoto(new WeakReference<>(mCallback));
        });

        mAnalyze.setOnClickListener(view -> {
            // 分析
            mPresenter.analysisPhoto();
        });

        if (allPermissionsGranted()) {
            mTextureView.post(() -> {
                mPresenter.startCamera(mTextureView, getWindowManager().getDefaultDisplay().getRotation());
            });
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                mTextureView.post(() -> mPresenter.startCamera(mTextureView, getWindowManager().getDefaultDisplay().getRotation()));
            } else {
                Toast.makeText(this, "没有开启权限.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // 检查权限
    private boolean allPermissionsGranted() {
        for (int i = 0; i < REQUIRED_PERMISSIONS.length; i++) {
            if (ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
