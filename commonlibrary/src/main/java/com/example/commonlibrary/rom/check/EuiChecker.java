package com.example.commonlibrary.rom.check;

import android.text.TextUtils;
import android.util.Log;

import com.example.commonlibrary.rom.Rom;
import com.example.commonlibrary.rom.RomInfo;
import com.example.commonlibrary.rom.RomProperties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EuiChecker extends Checker {
    private static final String TAG = "EuiChecker";
    private final String EUI_VERSION = "ro.letv.release.version"; // "5.9.023S"
    private final String EUI_VERSION_DATE = "ro.letv.release.version_date"; // "5.9.023S_03111"

    @Override
    public String getManufacturer() {
        return RomInfo.ManufacturerList.LETV;
    }

    @Override
    public Rom getRom() {
        return Rom.EUI;
    }

    @Override
    public RomInfo checkBuildProp(RomProperties properties) {
        RomInfo info = null;
        String versionStr = properties.getProperty(EUI_VERSION);
        Log.d(TAG, "手机类型判断 eui checkBuildProp: version str=" + versionStr);
        if (!TextUtils.isEmpty(versionStr)) {
            Matcher matcher = Pattern.compile("([\\d.]+)[^\\d]*").matcher(versionStr); // 5.9.023S
            if (matcher.find()) {
                try {
                    info = new RomInfo(getRom());
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
