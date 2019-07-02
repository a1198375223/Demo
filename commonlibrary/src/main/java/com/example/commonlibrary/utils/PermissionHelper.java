package com.example.commonlibrary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {
    public static final int REQUEST_CODE = 4222;

    public static boolean hasCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO ) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestCameraPermission(Activity activity, boolean requestWritePermission) {
        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) ||
                (requestWritePermission && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE));

        if (showRationale) {
            Toasty.showWarning("已经禁止了权限, 需要重新授权.");
        } else {
            String[] permissions = requestWritePermission ? new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO} :
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
        }
    }
}
