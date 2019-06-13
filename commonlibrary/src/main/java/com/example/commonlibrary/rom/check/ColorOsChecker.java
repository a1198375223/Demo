package com.example.commonlibrary.rom.check;

import android.text.TextUtils;
import android.util.Log;

import com.example.commonlibrary.rom.Rom;
import com.example.commonlibrary.rom.RomInfo;
import com.example.commonlibrary.rom.RomProperties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorOsChecker extends Checker {
    private static final String TAG = "ColorOsChecker";
    // OPPO : ColorOS
    String COLOROS_ROM_VERSION = "ro.rom.different.version"; // "ColorOS2.1"
    @Override
    public String getManufacturer() {
        return RomInfo.ManufacturerList.OPPO;
    }

    @Override
    public Rom getRom() {
        return Rom.ColorOS;
    }

    @Override
    public RomInfo checkBuildProp(RomProperties properties) {
        RomInfo info = null;
        String versionStr = properties.getProperty(COLOROS_ROM_VERSION);
        Log.d(TAG, "手机类型判断 oppo checkBuildProp: version str=" + versionStr);
        if (!TextUtils.isEmpty(versionStr)) {
            Matcher matcher = Pattern.compile("ColorOS([\\d.]+)").matcher(versionStr); // ColorOS2.1
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
