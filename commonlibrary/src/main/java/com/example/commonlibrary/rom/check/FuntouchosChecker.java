package com.example.commonlibrary.rom.check;

import android.text.TextUtils;
import android.util.Log;

import com.example.commonlibrary.rom.Rom;
import com.example.commonlibrary.rom.RomInfo;
import com.example.commonlibrary.rom.RomProperties;

public class FuntouchosChecker extends Checker {
    private static final String TAG = "FuntouchosChecker";
    // vivo : FuntouchOS
    String FUNTOUCHOS_OS_VERSION = "ro.vivo.os.version"; // "3.0"
    String FUNTOUCHOS_DISPLAY_ID = "ro.vivo.os.build.display.id"; // "FuntouchOS_3.0"
    String FUNTOUCHOS_ROM_VERSION = "ro.vivo.rom.version"; // "rom_3.1"

    @Override
    public String getManufacturer() {
        return RomInfo.ManufacturerList.VIVO;
    }

    @Override
    public Rom getRom() {
        return Rom.FuntouchOS;
    }

    @Override
    public RomInfo checkBuildProp(RomProperties properties) {
        RomInfo info = null;
        String versionStr = properties.getProperty(FUNTOUCHOS_OS_VERSION);
        Log.d(TAG, "手机类型判断 vivo checkBuildProp: version str=" + versionStr);
        if (!TextUtils.isEmpty(versionStr) && versionStr.matches("[\\d.]+")) { // 3.0
            try {
                info = new RomInfo(getRom());
                info.setVersion(versionStr);
                info.setBaseVersion(Integer.parseInt(versionStr.split("\\.")[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return info;
    }
}
