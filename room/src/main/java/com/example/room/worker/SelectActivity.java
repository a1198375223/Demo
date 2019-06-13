package com.example.room.worker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.room.R;

import java.util.Arrays;
import java.util.List;

public class SelectActivity extends AppCompatActivity {
    private static final String TAG = "SelectActivity";
    private static final int REQUEST_CODE_IMAGE = 100;
    private static final int REQUEST_CODE_PERMISSIONS = 101;

    private static final String KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT";
    private static final int MAX_NUMBER_REQUEST_PERMISSIONS = 2;

    private static final List<String> sPermission = Arrays.asList(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    );


    private int mPermissionRequestCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        if (savedInstanceState != null) {
            mPermissionRequestCount = savedInstanceState.getInt(KEY_PERMISSIONS_REQUEST_COUNT, 0);
        }


        requestPermissionsIfNecessary();


        findViewById(R.id.select_bn).setOnClickListener(view -> {
            Intent selectIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(selectIntent, REQUEST_CODE_IMAGE);
        });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_PERMISSIONS_REQUEST_COUNT, mPermissionRequestCount);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: ");
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            requestPermissionsIfNecessary();
        }
    }

    // 手动检查权限
    private void requestPermissionsIfNecessary() {
        Log.d(TAG, "requestPermissionsIfNecessary: ");
        if (!checkAllPermission()) {
            if (mPermissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                mPermissionRequestCount += 1;
                ActivityCompat.requestPermissions(this, sPermission.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
            } else {
                Toast.makeText(this, R.string.set_permissions_in_settings, Toast.LENGTH_SHORT).show();
                findViewById(R.id.select_bn).setEnabled(false);
            }
        }
    }

    // 检查权限
    private boolean checkAllPermission() {
        boolean hasPermissions = true;
        for (String permission : sPermission) {
            hasPermissions &= ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return hasPermissions;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE) {
                handleImageRequestResult(data);
            } else {
                Log.d(TAG, "onActivityResult: requestCode=" + requestCode);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleImageRequestResult(Intent data) {
        if (data == null) {
            Log.e(TAG, "data is null");
            return;
        }
        Uri imageUri = null;
        if (data.getClipData() != null) {
            Log.d(TAG, "clip data not null");
            imageUri = data.getClipData().getItemAt(0).getUri();
        } else if (data.getData() != null) {
            Log.d(TAG, "data not null");
            imageUri = data.getData();
        }

        if (imageUri == null) {
            Log.e(TAG, "invalid input image Uri.");
            return;
        }

        Log.d(TAG, "imageUri=" + imageUri.toString());
        Intent intent = new Intent(this, BlurActivity.class);
        intent.putExtra(Constants.KEY_IMAGE_URI, imageUri.toString());
        startActivity(intent);
    }
}
