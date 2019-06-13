package com.example.androidxdemo.base.callback;

import android.graphics.Bitmap;

public interface IImageLoadCallBack {
    // 获取图片成功
    void loadSuccess(Bitmap bitmap);

    // 获取图片失败
    void loadFailure();

    // 图片加载的进度
    void updateProgress(float progress);
}
