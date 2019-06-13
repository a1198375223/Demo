package com.example.commonlibrary.rom;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.example.commonlibrary.rom.check.AmigoOsChecker;
import com.example.commonlibrary.rom.check.ColorOsChecker;
import com.example.commonlibrary.rom.check.EMUIChecker;
import com.example.commonlibrary.rom.check.EuiChecker;
import com.example.commonlibrary.rom.check.FlymeChecker;
import com.example.commonlibrary.rom.check.FuntouchosChecker;
import com.example.commonlibrary.rom.check.H2OSChecker;
import com.example.commonlibrary.rom.check.IChecker;
import com.example.commonlibrary.rom.check.MIUIChecker;
import com.example.commonlibrary.rom.check.SamSungChecker;
import com.example.commonlibrary.rom.check.SmartisanOsChecker;
import com.example.commonlibrary.rom.check.SonyChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class RomChecker {
    private static final String TAG = "RomChecker";
    private static RomInfo info;
    private static Rom rom;

    private static AmigoOsChecker sAmigoOsChecker = new AmigoOsChecker();
    private static ColorOsChecker sColorOsChecker = new ColorOsChecker();
    private static EMUIChecker sEMUIChecker = new EMUIChecker();
    private static EuiChecker sEuiChecker = new EuiChecker();
    private static FlymeChecker sFlymeChecker = new FlymeChecker();
    private static FuntouchosChecker sFuntouchOsChecker = new FuntouchosChecker();
    private static H2OSChecker sH2OSChecker = new H2OSChecker();
    private static MIUIChecker sMIUIChecker = new MIUIChecker();
    private static SamSungChecker sSamSungChecker = new SamSungChecker();
    private static SonyChecker sSonyChecker = new SonyChecker();
    private static SmartisanOsChecker sSmartisanOsChecker = new SmartisanOsChecker();
    private static IChecker[] sChecks = new IChecker[]{
            sAmigoOsChecker,
            sColorOsChecker,
            sEMUIChecker,
            sEuiChecker,
            sFlymeChecker,
            sFuntouchOsChecker,
            sH2OSChecker,
            sSamSungChecker,
            sSonyChecker,
            sMIUIChecker,
            sSmartisanOsChecker
    };

    public static void destory() {
        if (info != null) {
            info = null;
        }

        if (rom != null) {
            rom = null;
        }

        if (sChecks != null) {
            sChecks = null;
        }

        if (sAmigoOsChecker != null) {
            sAmigoOsChecker = null;
        }

        if (sColorOsChecker != null) {
            sColorOsChecker = null;
        }
        if (sEMUIChecker != null) {
            sEMUIChecker = null;
        }
        if (sEuiChecker != null) {
            sEuiChecker = null;
        }
        if (sFlymeChecker != null) {
            sFlymeChecker = null;
        }
        if (sFuntouchOsChecker != null) {
            sFuntouchOsChecker = null;
        }
        if (sH2OSChecker != null) {
            sH2OSChecker = null;
        }
        if (sMIUIChecker != null) {
            sMIUIChecker = null;
        }
        if (sSamSungChecker != null) {
            sSamSungChecker = null;
        }
        if (sSonyChecker != null) {
            sSonyChecker = null;
        }
        if (sSmartisanOsChecker != null) {
            sSmartisanOsChecker = null;
        }
    }

    public static Rom getRom() {
        return rom;
    }

    public static RomInfo getInfo() {
        return info;
    }

    // 是否是小米手机
    public static boolean isXiaoMi() {
        return rom != null && Rom.MIUI.ordinal() == rom.ordinal();
    }

    // 是否是金立手机
    public static boolean isJinLi() {
        return rom != null && Rom.AmigoOS.ordinal() == rom.ordinal();
    }

    // 是否是魅族手机
    public static boolean isMeizu() {
        return rom != null && Rom.Flyme.ordinal() == rom.ordinal();
    }
    // 是否是华为手机
    public static boolean isHuaWei() {
        return rom != null && Rom.EMUI.ordinal() == rom.ordinal();
    }
    // 是否是oppo手机
    public static boolean isOppo() {
        return rom != null && Rom.ColorOS.ordinal() == rom.ordinal();
    }
    // 是否是vivo手机
    public static boolean isVivo() {
        return rom != null && Rom.FuntouchOS.ordinal() == rom.ordinal();
    }
    // 是否是sony手机
    public static boolean isSony() {
        return rom != null && Rom.Sony.ordinal() == rom.ordinal();
    }
    // 是否是乐视手机
    public static boolean isLeShi() {
        return rom != null && Rom.EUI.ordinal() == rom.ordinal();
    }
    // 是否是三星手机
    public static boolean isSanXing() {
        return rom != null && Rom.SamSung.ordinal() == rom.ordinal();
    }
    // 是否是一加手机
    public static boolean isOnePlus() {
        return rom != null && Rom.H2OS.ordinal() == rom.ordinal();
    }

    // 是否是锤子手机
    public static boolean isChuiZi() {
        return rom != null && Rom.SmartisanOS.ordinal() == rom.ordinal();
    }


    public static void initRom(Context context) {
        initRomInfo(context);
    }

    private static IChecker[] getICheckers() {
        return sChecks;
    }

    // 获取当前的checker
    private static IChecker getCurrentChecker() {
        for (IChecker checker : getICheckers()) {
            if (checker.getRom() == rom) {
                return checker;
            }
        }
        return null;
    }

    /**
     * 获取 ROM 类型
     *
     * @param context Context
     * @return ROM 类型
     */
    private static Rom getRomType(Context context) {
        if (rom == null)
            rom = doGetRomType(context);
        return rom;
    }

    private static Rom doGetRomType(Context context) {
        IChecker[] checkers = getICheckers();
        // 优先检查 Manufacturer
        String manufacturer = Build.MANUFACTURER;
        for (IChecker checker : checkers) {
            if (checker.checkManufacturer(manufacturer)) {
                return checker.getRom();
            }
        }
        return Rom.Other;
    }

    /**
     * 获取 ROM 信息 (包括 ROM 类型和版本信息)
     *
     * @param context Context
     */
    private static void initRomInfo(Context context) {
        if (info == null)
            info = doGetRomInfo(context);
    }

    private static RomInfo doGetRomInfo(Context context) {
        Rom rom = getRomType(context);
        RomProperties properties = new RomProperties();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            FileInputStream is = null;
            try {
                // 获取 build.prop 配置
                Properties buildProperties = new Properties();
                is = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
                buildProperties.load(is);
                properties.setBuildProp(buildProperties);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        RomInfo romInfo = null;
        if (getCurrentChecker() != null) {
            romInfo = getCurrentChecker().checkBuildProp(properties);
        }

        if (romInfo != null) {
            return romInfo;
        }

        return new RomInfo(rom);
    }
}
