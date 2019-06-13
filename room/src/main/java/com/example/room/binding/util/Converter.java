package com.example.room.binding.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.example.room.R;

public class Converter {
    private static final String pattern = "[^\\d:.]";

    // 将时间转变为m:ss的格式
    @SuppressLint("DefaultLocale")
    public static String fromTenthsToSeconds(int tenths) {
        if (tenths < 600) {
            return String.format("%.1f", tenths / 10.0f);
        }
        int minutes = (tenths / 10) / 60;
        int seconds = (tenths / 10) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    // 清除秒数的值
    public static int cleanSecondsString(String seconds) {
        return 0;
    }

    // 将总数数组转化成字符串格式
    public static String setArrayToString(Context context, int[] values) {
        return context.getString(R.string.sets_format, values[0] + 1, values[1]);
    }

    // 将总数字符串转化为数组形式
    public static int[] stringToSetArray(Context context, String value) {
        if (TextUtils.isEmpty(value)) {
            return new int[]{0, 0};
        }

        return new int[]{0, Integer.valueOf(value)};
    }
}
