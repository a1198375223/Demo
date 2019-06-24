package com.example.commonlibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    /**
     * 通过通配符来获取当前目录下的文件集合
     * @param dir 指定目录
     * @param glob 通配符
     * @return 文件路径集合
     */
    public static String[] getFiles(File dir, String glob) {
        String regex = globToRegex(glob);
        Pattern pattern = Pattern.compile(regex);
        String[] result = dir.list((file, s) -> {
            Matcher matcher = pattern.matcher(s);
            return matcher.matches();
        });
        if (result == null) {
            return null;
        }
        Arrays.sort(result);
        return result;
    }


    /**
     * 将通配符转化成正则表达式
     * @param glob 通配符
     * @return 正则表达式
     */
    private static String globToRegex(String glob) {
        StringBuilder regex = new StringBuilder(glob.length());
        //regex.append('^');
        for (char ch : glob.toCharArray()) {
            switch (ch) {
                case '*':
                    regex.append(".*");
                    break;
                case '?':
                    regex.append('.');
                    break;
                case '.':
                    regex.append("\\.");
                    break;
                default:
                    regex.append(ch);
            }
        }
        //regex.append('$');
        return regex.toString();
    }
}
