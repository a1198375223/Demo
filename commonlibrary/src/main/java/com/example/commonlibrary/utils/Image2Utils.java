package com.example.commonlibrary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.View;

import androidx.exifinterface.media.ExifInterface;

import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.io.IOException;

public class Image2Utils {

    // 旋转图片
    private static Matrix decodeExifOrientation(int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
            case ExifInterface.ORIENTATION_UNDEFINED:
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270f);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1f, 1f);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(1f, -1f);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.postScale(-1f, 1f);
                matrix.postRotate(270f);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.postScale(-1f, 1f);
                matrix.postRotate(90f);
                break;
            default:
                throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
        return matrix;
    }

    // 从file中decode图片
    public static Bitmap decodeBitmap(File file) {
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            Matrix transformation = decodeExifOrientation(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90));

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            return Bitmap.createBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()), 0, 0, bitmap.getWidth(), bitmap.getHeight(), transformation, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // 裁切缩略图
    public static Bitmap cropCircularThumbnail(Bitmap bitmap, int diameter) {
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, diameter, diameter);
        Bitmap circular = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint(Color.BLACK);
        Canvas canvas = new Canvas(circular);
        // 先画一个透明的背景
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f - 8, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect rect = new Rect(0, 0, diameter, diameter);
        canvas.drawBitmap(thumbnail, rect, rect, paint);
        return circular;
    }


    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);

            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        Bitmap bitmap;

        if (drawable.getIntrinsicHeight() <= 0 || drawable.getIntrinsicWidth() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        }

        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public static Drawable bitmap2Drawable(final Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(Utils.getApp().getResources(), bitmap);
    }

    public static Bitmap view2Bitmap(final View view) {
        if (view == null) {
            return null;
        }

        Bitmap ret = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ret);
        Drawable bgDrawable = view.getBackground();

        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return ret;
    }
}
