package com.example.androidxdemo.base.image;

import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

/**
 * 继承BaseImage来实现本地Image的加载内容封装
 * 使用Uri.fromFile(new File()) 来解析的话 例如解析raw文件中的内容就要这么写
 * 如果使用Uri.parse() 来解析的话就要这么写: Uri.parse(""android.resource://" + getPackageName() + "/" + R.mipmap.ic_launcher);
 * 或者 Uri.parse("file:///android_asset/" + "qq.png");
 */
public class LocalImage extends BaseImage {
    private String mPath;

    public LocalImage() {

    }

    public LocalImage(String path) {
        this.mPath = path;
        generateUri();
    }

    @Override
    public void generateUri() {
        if (!TextUtils.isEmpty(mPath)) {
            mUri = Uri.fromFile(new File(mPath));
        }
    }

    public void setPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            mPath = path;
            mUri = Uri.fromFile(new File(mPath));
        }
    }
}
