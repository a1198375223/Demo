package com.example.commonlibrary.rom.check;

import android.text.TextUtils;
import android.util.Log;

import com.example.commonlibrary.rom.Rom;
import com.example.commonlibrary.rom.RomInfo;
import com.example.commonlibrary.rom.RomProperties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EMUIChecker extends Checker {
    private static final String TAG = "EMUIChecker";

    // 华为 : EMUI
    String EMUI_VERSION = "ro.build.version.emui"; // "EmotionUI_3.0"
    @Override
    public String getManufacturer() {
        return RomInfo.ManufacturerList.HUAWEI;
    }


    @Override
    public Rom getRom() {
        return Rom.EMUI;
    }

    @Override
    public RomInfo checkBuildProp(RomProperties properties) {
        RomInfo info = null;
        String versionStr = properties.getProperty(EMUI_VERSION);
        Log.d(TAG, "手机类型判断 huawei checkBuildProp: version str=" + versionStr);
        if (!TextUtils.isEmpty(versionStr)) {
            Matcher matcher = Pattern.compile("EmotionUI_([\\d.]+)").matcher(versionStr); // EmotionUI_3.0
            if (matcher.find()) {
                try {
                    String version = matcher.group(1);
                    info = new RomInfo(getRom());
                    info.setVersion(version);
                    info.setBaseVersion(Integer.parseInt(version.split("\\.")[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return info;
    }
}
