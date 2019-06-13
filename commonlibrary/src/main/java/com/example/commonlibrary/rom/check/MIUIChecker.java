package com.example.commonlibrary.rom.check;


import android.text.TextUtils;
import android.util.Log;

import com.example.commonlibrary.rom.Rom;
import com.example.commonlibrary.rom.RomInfo;
import com.example.commonlibrary.rom.RomProperties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MIUIChecker extends Checker {
    private static final String TAG = "MIUIChecker";
    // 小米 : MIUI
    private final String MIUI_VERSION = "ro.build.version.incremental"; // "7.6.15"
    private final String MIUI_VERSION_NANE = "ro.miui.ui.version.name"; // "V8"

    @Override
    public Rom getRom() {
        return Rom.MIUI;
    }

    @Override
    public RomInfo checkBuildProp(RomProperties properties) {
        RomInfo info = null;
        String versionName = properties.getProperty(MIUI_VERSION_NANE);
        Log.d(TAG, "手机类型判断 xiaomi checkBuildProp: version name=" + versionName);
        if (!TextUtils.isEmpty(versionName) && versionName.matches("[Vv]\\d+")) { // V9
            try {
                info = new RomInfo(getRom());
                info.setBaseVersion(Integer.parseInt(versionName.substring(1)));

                String versionStr = properties.getProperty(MIUI_VERSION);
                Log.d(TAG, "手机类型判断 xiaomi checkBuildProp: version str=" + versionStr);
                if (!TextUtils.isEmpty(versionStr)) {
                    // 参考: 8.1.25 & V9.6.2.0.ODECNFD & V10.0.1.0.OAACNFH
                    Matcher matcher = Pattern.compile("[Vv]?(\\d+(\\.\\d+)*)[.A-Za-z]*").matcher(versionStr);
                    if (matcher.matches()) {
                        info.setVersion(matcher.group(1));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return info;
    }

    @Override
    public String getManufacturer() {
        return RomInfo.ManufacturerList.XIAOMI;
    }
}
