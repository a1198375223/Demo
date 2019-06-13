package com.example.androidxdemo.base.image;

import android.net.Uri;

/**
 * 继承BaseImage来加载本地资源图片
 * 使用Uri来获取资源图片的话应该这么写 Uri.parse("res://" + resId);
 */
public class ResImage extends BaseImage{
    private int mResId;

    public ResImage(int resId) {
        this.mResId = resId;
        generateUri();
    }


    @Override
    public void generateUri() {
        mUri = new Uri.Builder().scheme("res").path(String.valueOf(mResId)).build();
    }
}
