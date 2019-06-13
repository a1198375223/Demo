package com.example.commonlibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * 将bitmap保存到本地
     * @param applicationContext context
     * @param bitmap 待保存的bitmap
     * @param filePath 子路径
     * @return 保存路径uri
     */
    public static Uri writeBitmapToFile(Context applicationContext, Bitmap bitmap, String filePath) {
        Log.d(TAG, "application context files dir=" + applicationContext.getFilesDir());
        String name = String.format("blur-filter-output-%s.png", UUID.randomUUID().toString());
        File outputDir = new File(applicationContext.getFilesDir(), filePath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File outputFile = new File(outputDir, name);
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(outputFile);
    }
}
