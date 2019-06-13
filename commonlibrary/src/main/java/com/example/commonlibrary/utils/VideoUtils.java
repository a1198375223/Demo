package com.example.commonlibrary.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import androidx.annotation.NonNull;


public class VideoUtils {

    public static Bitmap getVideoCover(String videoUrl) {
        Bitmap bitmap = createVideoThumbnail(videoUrl);
        //ImageSaveUtils.saveToLocal(bitmap,);
        return bitmap;
    }


    // 创建video第一帧缩略图
    private static Bitmap createVideoThumbnail(@NonNull String filePath) {
        Bitmap bitmap = null;

        //MediaMetadataRetriever 是android中定义好的一个类，提供了统一的接口，用于从输入的媒体文件中取得帧和元数据
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);

        bitmap = retriever.getFrameAtTime();

        retriever.release();
        if (bitmap == null) {
            return  null;
        }

        // Scale down the bitmap if it's too large.
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int max = Math.max(width, height);
        if (max > 512) {
            float scale = 512f / max;
            int w = Math.round(scale * width);
            int h = Math.round(scale * height);
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        }
        return bitmap;
    }
}
