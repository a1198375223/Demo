package com.example.commonlibrary.utils;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RomUtils {
    private static final String TAG = "RomUtils";

    //----------------------------------------------------------------------------------------------
    // 这是通过Build.MANUFACTURER来判断是那一个厂商来判断是那种手机, 只有小米 华为 oppo 锤子 vivo是通过rom信息
    // 来判断是那种手机
    public static final String SYS_HUAWEI = "HUAWEI"; // 华为系统 emui
    public static final String SYS_XIAOMI = "XIAOMI"; // 小米系统 miui
    public static final String SYS_OPPO = "OPPO";  // oppo系统
    public static final String SYS_MEIZU = "MEIZU"; // 魅族系统 flyme
    public static final String SYS_VIVO = "VIVO"; // vivo系统
    public static final String SYS_CHUIZI = "SMARTISAN"; // 锤子手机
    public static final String SYS_ONEPLUS = "ONEPLUS"; // 一加手机 h2os


    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";

    private static String sName = null;
    private static String sVersion = null;

    public static String getName() {
        if (sName == null) {
            checkPhone("");
        }
        return sName;
    }

    public static String getVersion() {
        if (sVersion == null) {
            checkPhone("");
        }
        return sVersion;
    }

    // 是否是华为手机
    public static boolean isHuaWei() {
        return checkPhone(SYS_HUAWEI);
    }

    // 是否是魅族手机
    public static boolean isXiaoMi() {
        return checkPhone(SYS_XIAOMI);
    }

    // 是否是vivo手机
    public static boolean isVivo() {
        return checkPhone(SYS_VIVO);
    }

    // 是否是oppo手机
    public static boolean isOppo() {
        return checkPhone(SYS_OPPO);
    }

    // 是否是魅族手机
    public static boolean isFlyme() {
        return checkPhone(SYS_MEIZU);
    }

    // 是否是锤子手机
    public static boolean isSmartisan() {
        return checkPhone(SYS_CHUIZI);
    }

    // 是否是一加手机
    public static boolean isOnePlus() {
        return checkPhone(SYS_ONEPLUS);
    }



    private static boolean checkPhone(String rom) {
        if (sName != null) {
            return sName.equals(rom);
        }

        if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) { // miui
            sName = SYS_XIAOMI;
        }else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_EMUI))){ // emui
            sName = SYS_HUAWEI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_OPPO))){ // oppo
            sName = SYS_OPPO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_VIVO))){ // vivo
            sName = SYS_VIVO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_SMARTISAN))){ // chuizi
            sName = SYS_CHUIZI;
        } else {
            sVersion = Build.DISPLAY;
            if (sVersion.toUpperCase().contains(SYS_MEIZU)) { // flyme
                sName = SYS_MEIZU;
            } else {
                sVersion = Build.UNKNOWN;
                sName = Build.MANUFACTURER.toUpperCase();
            }
        }
        return sName.equals(rom);
    }


    private static String getProp(String name) {
        String line = null;
        BufferedReader input = null;

        try {
            Log.d(TAG, "手机类型判断 getProp: exec=getprop " + name);
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            Log.d(TAG, "手机类型判断 getProp: read prop=" + line);
            input.close();
        } catch (IOException e) {
            Log.d(TAG, "手机类型判断 getProp: error to read prop");
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }
}
