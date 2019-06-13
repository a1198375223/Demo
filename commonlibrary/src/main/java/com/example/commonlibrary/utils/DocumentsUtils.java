package com.example.commonlibrary.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import java.io.FileNotFoundException;

/**
 * 对文档进行操作
 */
public class DocumentsUtils {
    public static final int READ_REQUEST_CODE = 42;
    public static final int EDIT_REQUEST_CODE = 44;


    public static void openImageDir(Activity activity, int requestCode) {
        openFileSearch(activity, "image/*", requestCode);
    }

    public static void openVideoDir(Activity activity, int requestCode) {
        openFileSearch(activity, "video/*", requestCode);
    }

    public static void openMusicDir(Activity activity, int requestCode) {
        openFileSearch(activity, "audio/*", requestCode);
    }

    /**
     * 显示系统的文档界面
     * @param type 需要显示的MIME的类型
     */
    private static void openFileSearch(Activity activity, String type, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(type);
            activity.startActivityForResult(intent, requestCode);
        }
    }


    /**
     * 创建一个新的文档
     * @param activity 创建文档的activity
     * @param mimeType 创建文档的MIME类型
     * @param fileName 创建文档的名字
     * @param requestCode onActivityResult的requestCode
     */
    public static void createFile(Activity activity, String mimeType, String fileName, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_TITLE, fileName);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 删除某个文档
     * @param activity 删除文档的activity
     * @param uri 文档的uri,在onActivityResult中使用intent.getData()获取的uri
     */
    public static void deleteFile(Activity activity, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                DocumentsContract.deleteDocument(activity.getContentResolver(), uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 打开一个可以编辑的文档,进行编辑操作
     * @param activity 打开的activity
     * @param type MIME的类型
     */
    public static void editFile(Activity activity, String type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(type);
            activity.startActivityForResult(intent, EDIT_REQUEST_CODE);
        }
    }

    /**
     * 获取uri对应文件的永久访问权限
     * @param activity 请求的activity
     * @param intent onActivityResult中的intent
     * @param uri onActivityResult中intent.getData()获取的uri
     */
    public static void getForeverPermission(Activity activity, Intent intent, Uri uri) {
        int takeFlags = intent.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getContentResolver().takePersistableUriPermission(uri, takeFlags);
        }
    }


}
