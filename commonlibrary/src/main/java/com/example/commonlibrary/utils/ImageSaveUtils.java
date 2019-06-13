package com.example.commonlibrary.utils;

import android.graphics.Bitmap;

import java.io.FileOutputStream;

public class ImageSaveUtils {

    // 将image保存在本地
    public static void saveToLocal(Bitmap bitmap, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
