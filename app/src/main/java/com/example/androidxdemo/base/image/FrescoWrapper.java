package com.example.androidxdemo.base.image;

import com.example.androidxdemo.base.callback.IFrescoCallBack;
import com.facebook.imagepipeline.image.ImageInfo;

/**
 * 有的时候我们并不关心失败或者成功事件,只是关心其中的一个事件就可以重写其中的某个方法
 */
public class FrescoWrapper implements IFrescoCallBack {
    @Override
    public void processWithInfo(ImageInfo info) {

    }

    @Override
    public void processWithFailure() {

    }
}
