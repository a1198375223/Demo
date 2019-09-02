package com.example.dialog;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;

import androidx.annotation.ColorInt;

public class RippleHelper {
    public static void applyColor(Drawable d, @ColorInt int color) {
        if (d instanceof RippleDrawable)
            ((RippleDrawable) d).setColor(ColorStateList.valueOf(color));
    }
}
