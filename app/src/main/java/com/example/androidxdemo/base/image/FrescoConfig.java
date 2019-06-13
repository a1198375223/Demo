package com.example.androidxdemo.base.image;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Environment;

import androidx.annotation.Nullable;

import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.AppInfoUtils;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.disk.DiskTrimmable;
import com.facebook.common.disk.DiskTrimmableRegistry;
import com.facebook.common.disk.NoOpDiskTrimmableRegistry;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class FrescoConfig {
    private static final String TAG = "FrescoConfig";

    // 本地缓存路径
    private static final String FRESCO_DIR_PATH = String.format("/%s/fresco", AppInfoUtils.getAppName());


    // 得到主磁盘缓存的配置
    private static DiskCacheConfig getMainDiskCacheConfig(Context context) {
        return DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(new File(Environment.getExternalStorageDirectory(), FRESCO_DIR_PATH))
                .setBaseDirectoryName("disk_cache")
                .setVersion(1)
                .setMaxCacheSize(500 * ByteConstants.MB)
                .setDiskTrimmableRegistry(getDiskTrimmableRegistry())
                .build();
    }

    // 得到小图磁盘缓存的配置
    private static DiskCacheConfig getSmallDiskCacheConfig(Context context) {
        return DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(new File(Environment.getExternalStorageDirectory(), FRESCO_DIR_PATH))
                .setVersion(1)
                .setBaseDirectoryName("thumbnail")
                .setMaxCacheSize(500 * ByteConstants.MB)
                .setDiskTrimmableRegistry(getDiskTrimmableRegistry())
                .build();
    }

    // 设置当磁盘缓存紧张时采取的策略
    private static DiskTrimmableRegistry getDiskTrimmableRegistry() {
        DiskTrimmableRegistry registry = NoOpDiskTrimmableRegistry.getInstance();
        registry.registerDiskTrimmable(new DiskTrimmable() {
            @Override
            public void trimToMinimum() {
                // 清楚磁盘缓存
                Fresco.getImagePipeline().clearDiskCaches();
            }

            @Override
            public void trimToNothing() {

            }
        });
        return registry;
    }

    // 当内存紧张时采取的措施
    private static MemoryTrimmableRegistry getMemoryTrimmabeRegistry() {
        MemoryTrimmableRegistry registry = NoOpMemoryTrimmableRegistry.getInstance();
        registry.registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                // 得到建议裁切的比例
                double suggestedTrimRatio = trimType.getSuggestedTrimRatio();
                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio) {
                    // 清除内存缓存
                    Fresco.getImagePipeline().clearMemoryCaches();
                }
            }
        });
        return registry;
    }

    // 添加fresco日志
    private static Set<RequestListener> getRequestListener() {
        Set<RequestListener> listeners = new HashSet<>();
        listeners.add(new RequestLoggingListener());
        return listeners;
    }

    // 获取ImagePipelineConfig
    public static ImagePipelineConfig getImagePipelineConfig(Context context) {
        return ImagePipelineConfig.newBuilder(context)
                .setMainDiskCacheConfig(getMainDiskCacheConfig(context))
                .setSmallImageDiskCacheConfig(getSmallDiskCacheConfig(context))
                .setMemoryTrimmableRegistry(getMemoryTrimmabeRegistry())
                .setRequestListeners(getRequestListener()) // 设置request的监听器
                .setDownsampleEnabled(true) // 设置是否支持向下采样
                .setCacheKeyFactory(MLCacheKeyFactory.getInstance()) // 设置自定义的key缓存
                .build();
    }

    // 获取ImageDecodeOptions
    private static ImageDecodeOptions getImageDecodeOptions() {
        return ImageDecodeOptions.newBuilder()
//              .setBackgroundColor(Color.TRANSPARENT) // 图片的背景颜色
//              .setDecodeAllFrames(true)              // 解码所有帧
//              .setDecodePreviewFrame(true)           // 解码预览框
//              .setForceOldAnimationCode(true)        // 使用以前动画
//              .setFrom(options)                      // 使用已经存在的图像解码
//              .setMinDecodeIntervalMs(intervalMs)    // 最小解码间隔（分位单位）
                .setUseLastFrameForPreview(true)       // 使用最后一帧进行预览
                .build();
    }


    // 获取ImageRequest
    public static ImageRequestBuilder getImageRequest(BaseImage baseImage) {
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(baseImage.getUri());

        // resize图片 仅支持jepg图片 必须在pipeline中设置setDownsampleEnable(true)来支持png和webp
        if (baseImage.getWidth() > 0 && baseImage.getHeight() > 0) {
            builder.setResizeOptions(new ResizeOptions(baseImage.getWidth(), baseImage.getHeight()));
        }

        // 后处理 就是对图片进行处理 例如在图片上添加水印或者复制一张图片什么的
        if (baseImage.getPostProcessor() != null) {
            builder.setPostprocessor(baseImage.getPostProcessor());
        }

        // 图片解码配置
        builder.setImageDecodeOptions(getImageDecodeOptions());

        // 设置是否自动旋转
        builder.setAutoRotateEnabled(false);

        // 设置是否支持渐进式加载
        if (baseImage.isProgressiveRenderingEnable()) {
            builder.setProgressiveRenderingEnabled(baseImage.isProgressiveRenderingEnable());
        }

        // 设置加载的优先级
        builder.setRequestPriority(baseImage.getRequestPriority());

        // 设置一个最低的请求级别
        // BITMAP_MEMORY_CACHE 检查内存缓存，有如，立刻返回。这个操作是实时的
        // ENCODED_MEMORY_CACHE 检查未解码的图片缓存，如有，解码并返回
        // DISK_CACHE 检查磁盘缓存，如果有加载，解码，返回
        // FULL_FETCH 下载或者加载本地文件
        // FULL_FETCH 耗时可能会长一点但是消耗内存比较少一点
        // builder.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.DISK_CACHE);

        // 设置是否支持缩略图
        builder.setLocalThumbnailPreviewsEnabled(true);

        return builder;
    }


    // 获取低分辨率的ImageRequest
    public static ImageRequestBuilder getLowerImageRequest(BaseImage baseImage) {
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(baseImage.getLowImageUri());

        // resize图片 仅支持jepg图片 必须在pipeline中设置setDownsampleEnable(true)来支持png和webp
        if (baseImage.getWidth() > 0 && baseImage.getHeight() > 0) {
            builder.setResizeOptions(new ResizeOptions(baseImage.getWidth(), baseImage.getHeight()));
        }

        builder.setAutoRotateEnabled(true)
                .setProgressiveRenderingEnabled(true)
                .setImageDecodeOptions(getImageDecodeOptions());
                // 动态查看图片如果只从内存中 cache 取得话会看不到缩略图
                //.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.ENCODED_MEMORY_CACHE);
        return builder;
    }

    // 生成ControllerListener监听器
    public static ControllerListener<ImageInfo> genControllerListener(final SimpleDraweeView draweeView, final BaseImage baseImage) {
        return new BaseControllerListener<ImageInfo>() {
            /**
             * 请求加载图片之前会调用这个方法
             * @param id
             * @param callerContext
             */
            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
            }

            /**
             * 图片成功加载成功之后会调用
             * @param id
             * @param imageInfo
             * @param animatable
             */
            @Override
            public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                // 在加载完成时未image设置一个tag保存uri信息
                draweeView.setTag(R.id.tag_image_uri, baseImage.getUri());
                if (baseImage.getCallback() != null) {
                    baseImage.getCallback().processWithInfo(imageInfo);
                }
            }

            /**
             * 如果允许呈现渐进是jepg,同时图片也是渐进式的 这个方法会在每个扫描被解码后回调
             * @param id
             * @param imageInfo
             */
            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
            }

            /**
             * 渐进式图片加载失败的时候会回调的方法
             * @param id
             * @param throwable
             */
            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            /**
             * 图片加载时候的时候会被调用
             * @param id
             * @param throwable
             */
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                if (baseImage.getCallback() != null) {
                    baseImage.getCallback().processWithFailure();
                }
            }

            /**
             * 释放图片会回调的方法
             * @param id
             */
            @Override
            public void onRelease(String id) {
                super.onRelease(id);
            }
        };
    }
}
