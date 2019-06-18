package com.example.commonlibrary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ThumbnailUtils;

import androidx.exifinterface.media.ExifInterface;

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
}
