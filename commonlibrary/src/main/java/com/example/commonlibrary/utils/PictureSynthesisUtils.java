package com.example.commonlibrary.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;

public class PictureSynthesisUtils {
    private static final String TAG = "PictureSynthesisUtils";
    private static final int TYPE_LEFT_TOP = 1;
    private static final int TYPE_LEFT_BOTTOM = 2;
    private static final int TYPE_RIGHT_TOP = 3;
    private static final int TYPE_RIGHT_BOTTOM = 4;

    private static final float TEXT_SIZE = 100f;
    private static final int TEXT_COLOR = Color.WHITE;
    private static final int WATER_MARK_PADDING = DisplayUtils.dip2px(10);

    private static long startTime = 0L;



    public static Bitmap addWaterMarkLeftTop(Bitmap origin, String mark) {
        return addWaterMark(origin, mark, TYPE_LEFT_TOP);
    }

    public static Bitmap addWaterMarkLeftTop(Bitmap origin, Bitmap mark) {
        return addWaterMark(origin, mark, TYPE_LEFT_TOP);
    }

    public static Bitmap addWaterMarkLeftTop(Drawable origin, String mark) {
        return addWaterMark(origin, mark, TYPE_LEFT_TOP);
    }

    public static Bitmap addWaterMarkLeftTop(Drawable origin, Bitmap mark) {
        return addWaterMark(origin, mark, TYPE_LEFT_TOP);
    }

    public static Bitmap addWaterMarkLeftTop(Drawable origin, Drawable mark) {
        return addWaterMark(origin, mark, TYPE_LEFT_TOP);
    }



    public static Bitmap addWaterMarkLeftBottom(Bitmap origin, String mark) {
        return addWaterMark(origin, mark, TYPE_LEFT_BOTTOM);
    }

    public static Bitmap addWaterMarkLeftBottom(Bitmap origin, Bitmap mark) {
        return addWaterMark(origin, mark, TYPE_LEFT_BOTTOM);
    }

    public static Bitmap addWaterMarkLeftBottom(Drawable origin, String mark) {
        return addWaterMark(origin, mark, TYPE_LEFT_BOTTOM);
    }

    public static Bitmap addWaterMarkLeftBottom(Drawable origin, Bitmap mark) {
        return addWaterMark(origin, mark, TYPE_LEFT_BOTTOM);
    }

    public static Bitmap addWaterMarkLeftBottom(Drawable origin, Drawable mark) {
        return addWaterMark(origin, mark, TYPE_LEFT_BOTTOM);
    }




    public static Bitmap addWaterMarkRightTop(Bitmap origin, String mark) {
        return addWaterMark(origin, mark, TYPE_RIGHT_TOP);
    }

    public static Bitmap addWaterMarkRightTop(Bitmap origin, Bitmap mark) {
        return addWaterMark(origin, mark, TYPE_RIGHT_TOP);
    }

    public static Bitmap addWaterMarkRightTop(Drawable origin, String mark) {
        return addWaterMark(origin, mark, TYPE_RIGHT_TOP);
    }

    public static Bitmap addWaterMarkRightTop(Drawable origin, Bitmap mark) {
        return addWaterMark(origin, mark, TYPE_RIGHT_TOP);
    }

    public static Bitmap addWaterMarkRightTop(Drawable origin, Drawable mark) {
        return addWaterMark(origin, mark, TYPE_RIGHT_TOP);
    }


    public static Bitmap addWaterMarkRightBottom(Bitmap origin, String mark) {
        return addWaterMark(origin, mark, TYPE_RIGHT_BOTTOM);
    }

    public static Bitmap addWaterMarkRightBottom(Bitmap origin, Bitmap mark) {
        return addWaterMark(origin, mark, TYPE_RIGHT_BOTTOM);
    }

    public static Bitmap addWaterMarkRightBottom(Drawable origin, String mark) {
        return addWaterMark(origin, mark, TYPE_RIGHT_BOTTOM);
    }

    public static Bitmap addWaterMarkRightBottom(Drawable origin, Bitmap mark) {
        return addWaterMark(origin, mark, TYPE_RIGHT_BOTTOM);
    }

    public static Bitmap addWaterMarkRightBottom(Drawable origin, Drawable mark) {
        return addWaterMark(origin, mark, TYPE_RIGHT_BOTTOM);
    }



    private static Bitmap addWaterMark(Drawable origin, String mark, int type) {
        return addWaterMark(drawableToBitmap(origin), mark, type);
    }

    private static Bitmap addWaterMark(Drawable origin, Bitmap mark, int type) {
        return addWaterMark(drawableToBitmap(origin), mark, type);
    }

    private static Bitmap addWaterMark(Drawable origin, Drawable mark, int type) {
        return addWaterMark(drawableToBitmap(origin), drawableToBitmap(mark), type);
    }


    private static Bitmap addWaterMark(Bitmap origin, String mark, int type) {
        startTime = System.currentTimeMillis();
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
        // 设置文字大小
        paint.setTextSize(TEXT_SIZE);
        // 设置颜色
        paint.setColor(TEXT_COLOR);
        // 设置字体
        paint.setTypeface(Typeface.DEFAULT);
        // 增加下划线
//        paint.setUnderlineText(true);

        float stringWidth = paint.measureText(mark);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float stringHeight = fontMetrics.bottom - fontMetrics.top;

        Bitmap markBitmap = Bitmap.createBitmap(((int) stringWidth), ((int) stringHeight), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(markBitmap);
        canvas.drawText(mark, 0, -fontMetrics.top, paint);
        canvas.save();
        canvas.restore();
        paint.reset();
        return addWaterMark(origin, markBitmap, type);
    }


    private static Bitmap addWaterMark(Bitmap origin, Bitmap mark, int type) {
        if (startTime == 0L) {
            startTime = System.currentTimeMillis();
        }
        int photoWidth = origin.getWidth();
        int photoHeight = origin.getHeight();

        int markWidth = mark.getWidth();
        int markHeight = mark.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(origin, 0, 0, photoWidth, photoHeight);
        Canvas canvas = new Canvas(resultBitmap);
        int x, y;
        if (type == TYPE_LEFT_TOP) {
            x = WATER_MARK_PADDING;
            y = WATER_MARK_PADDING;
        } else if (type == TYPE_LEFT_BOTTOM) {
            x = WATER_MARK_PADDING;
            y = photoHeight - WATER_MARK_PADDING - markHeight;
        } else if (type == TYPE_RIGHT_TOP) {
            x = photoWidth - WATER_MARK_PADDING - markWidth;
            y = WATER_MARK_PADDING;
        } else {
            x = photoWidth - WATER_MARK_PADDING - markWidth;
            y = photoHeight - WATER_MARK_PADDING - markHeight;
        }

        canvas.drawBitmap(mark, ((float) x), ((float) y), null);
        canvas.save();
        canvas.restore();
        Log.d(TAG, "addWaterMark: total time: " + (System.currentTimeMillis() - startTime)+ "ms");
        startTime = 0L;
        return resultBitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
