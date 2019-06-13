package com.example.androidxdemo.base.callback;

import com.facebook.imagepipeline.image.ImageInfo;

public interface IFrescoCallBack {
    // 图片加载成功回调的接口, 处理ImageInfo
    void processWithInfo(ImageInfo info);

    // 图片加载失败回调的接口
    void processWithFailure();
}
