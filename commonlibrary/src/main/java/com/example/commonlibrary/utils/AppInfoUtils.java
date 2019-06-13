package com.example.commonlibrary.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.example.commonlibrary.utils.AppUtils;

/**
 * app的应用信息
 */
public class AppInfoUtils {
    private static final String TAG = "AppInfoUtils";

    private static String appName;

    /**
     * 这个方法用来返回app的名称
     */
    public static String getAppName() {
        if (!TextUtils.isEmpty(appName)) {
            return appName;
        }
        try {
            PackageManager pm = AppUtils.app().getPackageManager();
            PackageInfo info = pm.getPackageInfo(AppUtils.app().getPackageName(), 0);
            int labelRes = info.applicationInfo.labelRes;
            return AppUtils.app().getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用来获取应用包名
     */
    public String getPackageName() {
        return AppUtils.app().getPackageName();
    }
}
