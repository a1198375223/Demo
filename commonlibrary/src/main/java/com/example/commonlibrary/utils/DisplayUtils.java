package com.example.commonlibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;

/**
 * 使用前需要在Application中调用init方法进行初始化
 */
public class DisplayUtils {
    private static float sDensity = 3.0f;
    private static int sScreenHeight = 1920;
    private static int sScreenWidth = 1080;
    private static int sNavigationBarHeight = 0;
    private static int sStatusBarHeight = 72;


    private static void init(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if (dm != null) {
            sDensity = dm.density;
            sScreenHeight = Math.max(dm.heightPixels, dm.widthPixels);
            sScreenWidth = Math.min(dm.heightPixels, dm.widthPixels);
        }

        try {
            @SuppressLint("PrivateApi") Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("navigation_bar_height");
            int x = (int) field.get(o);
            sNavigationBarHeight = context.getResources().getDimensionPixelSize(x);
            Field field1 = c.getField("status_bar_height");
            int y = (int) field1.get(o);
            sStatusBarHeight = context.getResources().getDimensionPixelSize(y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float getDensity(){
        return sDensity;
    }

    public static int dip2px(int dp) {
        return (int) (dp * sDensity + 0.5f);
    }

    public static int px2dip(int px) {
        return (int) (px * sDensity + 0.5f);
    }

    public static int getScreenHeight() {
        return sScreenHeight;
    }

    public static int getScreenWidth() {
        return sScreenWidth;
    }

    public static int getNavigationBarHeight() {
        return sNavigationBarHeight;
    }

    public static int getStatusBarHeight() {
        return sStatusBarHeight;
    }


}
