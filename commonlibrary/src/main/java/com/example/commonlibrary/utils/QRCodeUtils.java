package com.example.commonlibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class QRCodeUtils {
    private static final String TAG = "QRCodeUtils";
    private QRCodeUtils() {}


    /**
     * @param content 生成二维码的字符串
     * @param width 二维码的宽
     * @param height 二维码的高
     * @return 返回一个二维码bitmap
     */
    public static Bitmap createQRCodeBitmap(String content, int width, int height) {
        return createQRCodeBitmap(content, width, height, 0);
    }

    /**
     * @param content 生成二维码的字符串
     * @param width 二维码的宽
     * @param height 二维码的高
     * @param margin 二维码的空白边距
     * @return 返回一个二维码bitmap
     */
    public static Bitmap createQRCodeBitmap(String content, int width, int height, int margin) {
        if (content == null || TextUtils.isEmpty(content)) {
            Log.e(TAG, "Invalid argument content.");
            throw new IllegalArgumentException("Invalid argument content.");
        }

        if (width <= 0 || height <= 0) {
            Log.e(TAG, "width or height must be > 0.");
            throw new RuntimeException("width or height must be > 0.");
        }

        Hashtable<EncodeHintType, String> hints = new Hashtable<>();

        // 选择编码模式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // 设置误差纠正
        // L = ~7% correction
        // M = ~15% correction
        // Q = ~25% correction
        // H = ~30% correction
//        hints.put(EncodeHintType.ERROR_CORRECTION, "H");

        // 设置空白边距
        hints.put(EncodeHintType.MARGIN, String.valueOf(margin));


        try {
            /*// 生成二维码矩阵
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // 创建一个像素矩阵, 用来生成bitmap
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) { // 如果返回true表示该点应该是黑色的
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);*/

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
