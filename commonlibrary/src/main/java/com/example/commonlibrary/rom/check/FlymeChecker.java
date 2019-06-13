package com.example.commonlibrary.rom.check;

import android.text.TextUtils;
import android.util.Log;

import com.example.commonlibrary.rom.Rom;
import com.example.commonlibrary.rom.RomInfo;
import com.example.commonlibrary.rom.RomProperties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlymeChecker extends Checker {
    private static final String TAG = "FlymeChecker";
    // 魅族 : Flyme
    private final String FLYME_DISPLAY_ID = "ro.build.display.id"; // "Flyme OS 4.5.4.2U"
    @Override
    public String getManufacturer() {
        return RomInfo.ManufacturerList.MEIZU;
    }

    @Override
    public Rom getRom() {
        return Rom.Flyme;
    }

    @Override
    public RomInfo checkBuildProp(RomProperties properties) {
        RomInfo info = new RomInfo(getRom());
        String versionStr = properties.getProperty(FLYME_DISPLAY_ID);
        Log.d(TAG, "手机类型判断 meizu checkBuildProp: version str=" + versionStr);
        if (!TextUtils.isEmpty(versionStr)) {
            Matcher matcher = Pattern.compile("Flyme[^\\d]*([\\d.]+)[^\\d]*").matcher(versionStr); // Flyme OS 4.5.4.2U
            if (matcher.find()) {
                try {
                    String version = matcher.group(1);
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
