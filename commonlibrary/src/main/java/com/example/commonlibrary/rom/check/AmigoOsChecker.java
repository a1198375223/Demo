package com.example.commonlibrary.rom.check;

import android.text.TextUtils;
import android.util.Log;

import com.example.commonlibrary.rom.Rom;
import com.example.commonlibrary.rom.RomInfo;
import com.example.commonlibrary.rom.RomProperties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmigoOsChecker extends Checker {
    private static final String TAG = "AmigoOsChecker";
    // 金立 : amigo
    String AMIGO_ROM_VERSION = "ro.gn.gnromvernumber"; // "GIONEE ROM5.0.16"
    String AMIGO_DISPLAY_ID = "ro.build.display.id";
    @Override
    public String getManufacturer() {
        return RomInfo.ManufacturerList.AMIGO;
    }

    @Override
    public Rom getRom() {
        return Rom.AmigoOS;
    }

    @Override
    public RomInfo checkBuildProp(RomProperties properties) {
        RomInfo info = null;
        String versionStr = properties.getProperty(AMIGO_DISPLAY_ID);
        Log.d(TAG, "手机类型判断 amigo checkBuildProp: version str=" + versionStr);
        if (!TextUtils.isEmpty(versionStr)) {
            Matcher matcher = Pattern.compile("amigo([\\d.]+)[a-zA-Z]*").matcher(versionStr); // "amigo3.5.1"
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
