package com.example.androidxdemo.base.image;

import android.net.Uri;
import android.text.TextUtils;

/**
 * 继承BaseImage来加载网络图片 一般加载网络图片都是直接提供一个url
 * 所以直接使用Uri.parse(url)来解析就好了
 */
public class HttpImage extends BaseImage{
    private String mUrl;

    public HttpImage(String url) {
        this.mUrl = url;
        generateUri();
    }

    @Override
    public void generateUri() {
        if (!TextUtils.isEmpty(mUrl)) {
            mUri = Uri.parse(mUrl);
        }
    }
}
