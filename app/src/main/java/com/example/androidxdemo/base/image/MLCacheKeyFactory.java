package com.example.androidxdemo.base.image;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.cache.BitmapMemoryCacheKey;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.request.ImageRequest;


/**
 * 使用这个类来获取缓存的fresco图片
 * 以 http/https://host/xxx/xxx/xxx.jpg/.jepg/... 的key形式来保存
 * 如果自定义了缓存key策略那么当判断图片缓存是否存在存在手机上用下面的形式
 * ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).build();
 * boolean isCacheInDisk = Fresco.getImagePipelineFactory().getMainFileCache().hasKey(MLCacheKeyFactory.getInstance().getEncodedCacheKey(request));
 *
 * 如果没有在Fresco中设置这个缓存key策略,用下面的方式进行判断
 * boolean isCacheInDisk = Fresco.getImagePipelineFactory().getMainFileCache().hasKey(new SimpleCacheKey(Uri.parse(url)));
 *
 * 用以上两种方法判断的话在list列表可能会出现卡顿现象,所以如果可以的话尽量放在子线程中进行判断
 */
public class MLCacheKeyFactory extends DefaultCacheKeyFactory {
    private static volatile MLCacheKeyFactory sInstance;

    private MLCacheKeyFactory() {}

    public static MLCacheKeyFactory getInstance() {
        if (sInstance == null) {
            synchronized (MLCacheKeyFactory.class) {
                if (sInstance == null) {
                    sInstance = new MLCacheKeyFactory();
                }
            }
        }
        return sInstance;
    }

    private boolean isJpg(String url) {
        boolean isJpg = false;
        if (!TextUtils.isEmpty(url)) {
            url = url.toLowerCase();
            return url.contains(".jpg");
        }
        return isJpg;
    }


    private boolean isJpeg(String url) {
        boolean isJpeg = false;
        if (!TextUtils.isEmpty(url)) {
            url = url.toLowerCase();
            return url.contains(".jpeg");
        }
        return isJpeg;
    }

    private boolean isPng(String url) {
        boolean isPng = false;
        if (!TextUtils.isEmpty(url)) {
            url = url.toLowerCase();
            return url.contains(".png");
        }
        return isPng;
    }

    private boolean isGif(String url) {
        boolean isGif = false;
        if (!TextUtils.isEmpty(url)) {
            url = url.toLowerCase();
            return url.contains(".gif");
        }
        return isGif;
    }

    @Override
    public CacheKey getEncodedCacheKey(ImageRequest request, @Nullable Object callerContext) {
        CacheKey cacheKey = null;
        if (request.getSourceUri().toString().startsWith("http")) {
            Uri uri = this.getCacheKeySourceUri(request.getSourceUri());
            String url = uri.toString();
            String tmp = url;
            if (uri.getHost() != null) {
                tmp = url.replace(uri.getHost(), "host").toLowerCase();
                if (isJpg(tmp)) {
                    tmp = tmp.replace(".jpg", "");
                    tmp += ".jpg";
                } else if (isJpeg(tmp)) {
                    tmp = tmp.replace(".jpeg", "");
                    tmp += ".jpeg";
                } else if (isPng(tmp)) {
                    tmp = tmp.replace(".png", "");
                    tmp += ".png";
                } else if (isGif(url)) {
                    tmp = tmp.replace(".gif", "");
                    tmp += ".gif";
                }
                cacheKey = new SimpleCacheKey(tmp);
            }
        } else {
            cacheKey = super.getEncodedCacheKey(request, callerContext);
        }
        return cacheKey;
    }



    @Override
    public CacheKey getBitmapCacheKey(ImageRequest request, @Nullable Object callerContext) {
        CacheKey cacheKey = null;

        if (request.getSourceUri().toString().startsWith("http")) {

            Uri uri = this.getCacheKeySourceUri(request.getSourceUri());
            String url = uri.toString();
            String tmp = url;
            if (uri.getHost() != null) {
                tmp = url.replace(uri.getHost(), "host");
            }
            if (isJpg(tmp)) {
                tmp = tmp.replace(".jpg", "");
                tmp += ".jpg";
            } else if (isJpeg(tmp)) {
                tmp = tmp.replace(".jpeg", "");
                tmp += ".jpeg";
            } else if (isPng(tmp)) {
                tmp = tmp.replace(".png", "");
                tmp += ".png";
            } else if (isGif(url)) {
                tmp = tmp.replace(".gif", "");
                tmp += ".gif";
            }
            cacheKey = new BitmapMemoryCacheKey(tmp, request.getResizeOptions(), request.getRotationOptions(), request.getImageDecodeOptions(), null, null,
                    callerContext);
        } else {
            cacheKey = super.getBitmapCacheKey(request, callerContext);
        }
        return cacheKey;
    }
}
