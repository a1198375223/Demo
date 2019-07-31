package com.example.commonlibrary.utils;

import android.content.Context;
import android.os.Vibrator;

public class VibrateUtils {
    private static Vibrator vibrator;


    private VibrateUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    /**
     * 开始震动
     * @param milliseconds 震动的秒数
     */
    public static void vibrate(final long milliseconds) {
        Vibrator vibrator = getVibrator();

        if (vibrator == null) {
            return;
        }
        vibrator.vibrate(milliseconds);
    }

    public static void vibrate(final long[] pattern, final int repeat) {
        Vibrator vibrator = getVibrator();
        if (vibrator == null) {
            return;
        }

        vibrator.vibrate(pattern, repeat);
    }


    /**
     * 取消震动
     */
    public static void cancel() {
        Vibrator vibrator = getVibrator();
        if (vibrator == null) {
            return;
        }
        vibrator.cancel();
    }


    /**
     * 是否有振动器
     * @return 是否有振动器
     */
    public static boolean hasVibrator() {
        Vibrator vibrator = getVibrator();
        if (vibrator == null) {
            return false;
        }

        return vibrator.hasVibrator();
    }


    private static Vibrator getVibrator() {
        if (vibrator == null) {
            vibrator = (Vibrator) AppUtils.app().getSystemService(Context.VIBRATOR_SERVICE);
        }

        return vibrator;
    }
}
