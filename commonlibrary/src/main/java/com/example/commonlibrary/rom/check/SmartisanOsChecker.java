package com.example.commonlibrary.rom.check;

import android.util.Log;

import com.example.commonlibrary.rom.Rom;
import com.example.commonlibrary.rom.RomInfo;
import com.example.commonlibrary.rom.RomProperties;

public class SmartisanOsChecker extends Checker {
    private static final String TAG = "SmartisanOsChecker";
    private final String KEY_DISPLAY_ID = "ro.build.display.id";
    private final String KEY_BASE_OS_VERSION = "ro.build.version.base_os";
    @Override
    public String getManufacturer() {
        return RomInfo.ManufacturerList.SMARTISAN;
    }

    @Override
    public Rom getRom() {
        return Rom.SmartisanOS;
    }

    @Override
    public RomInfo checkBuildProp(RomProperties properties) {
        String versionStr = properties.getProperty(KEY_DISPLAY_ID);
        String versionName = properties.getProperty(KEY_BASE_OS_VERSION);
        Log.d(TAG, "手机类型判断 smartisan checkBuildProp: version str=" + versionStr);
        Log.d(TAG, "手机类型判断 smartisan checkBuildProp: version name=" + versionName);
        return null;
    }
}
