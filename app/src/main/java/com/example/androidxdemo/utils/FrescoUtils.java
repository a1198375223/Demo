package com.example.androidxdemo.utils;


import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.example.androidxdemo.base.callback.IImageLoadCallBack;
import com.example.androidxdemo.base.image.BaseImage;
import com.example.androidxdemo.base.image.FrescoConfig;
import com.example.androidxdemo.base.image.MLCacheKeyFactory;
import com.example.commonlibrary.utils.ThreadPool;
import com.example.commonlibrary.utils.AppUtils;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;

import java.io.File;


public class FrescoUtils {


    /**
     * 加载图片
     */
    public static void loadImage(SimpleDraweeView draweeView, BaseImage baseImage) {
        loadImage(draweeView, baseImage, false, null);
    }


    /**
     * 使用BaseImage来加载Fresco图片
     * @param draweeView 待显示图片的view
     * @param baseImage 存储了加载信息的model类
     * @param isPostBitmap 是否需要使用bitmap
     * @param callBack 如果需要使用bitmap需要传这个接口进行回调否则填null
     */
    public static void loadImage(SimpleDraweeView draweeView, BaseImage baseImage, boolean isPostBitmap, final IImageLoadCallBack callBack) {
        // 如果参数有一个为null 不进行加载操作
        if (draweeView == null || baseImage == null) {
            return;
        }

        // 用来确保是在ui线程进行操作的
        if (!ThreadPool.isUiThread()) {
            return;
        }

        // 取出BaseImage的参数来填充到SimpleDraweeView中
        // 填充实际的裁切类型
        if (baseImage.getScaleType() != null) {
            draweeView.getHierarchy().setActualImageScaleType(baseImage.getScaleType());
        }

        // 填充加载中的图片
        if (baseImage.getLoadingDrawable() != null) {
            draweeView.getHierarchy().setPlaceholderImage(baseImage.getLoadingDrawable(), baseImage.getLoadingScaleType());
        }

        // 填充失败的图片
        if (baseImage.getFailureDrawable() != null) {
            draweeView.getHierarchy().setFailureImage(baseImage.getFailureDrawable(), baseImage.getFailureScaleType());
        }

        // 填充重试的图片
        if (baseImage.getRetryDrawable() != null) {
            draweeView.getHierarchy().setRetryImage(baseImage.getRetryDrawable(), baseImage.getRetryScaleType());
        }

        // 设置进度条图片
        if (baseImage.getProgressBarDrawable() != null) {
            draweeView.getHierarchy().setProgressBarImage(baseImage.getProgressBarDrawable());
        }

        // 设置是否是圆形
        RoundingParams params = draweeView.getHierarchy().getRoundingParams();
        if (params == null) {
            params = new RoundingParams();
        }
        params.setRoundAsCircle(baseImage.isCircle());

        // 设置圆角的颜色和宽度
        if (baseImage.getBorderWidth() != -1) {
//            params.setBorderWidth(baseImage.getBorderWidth());
//            params.setBorderColor(baseImage.getBorderColor());
            params.setBorder(baseImage.getBorderColor(), baseImage.getBorderWidth());
        } else {
            params.setBorderWidth(0);
        }

        // 设置圆角半径
        if (baseImage.getCornerRadius() != -1) {
            params.setCornersRadius(baseImage.getCornerRadius());
        } else if (baseImage.getCornerRadii() != null) {
            params.setCornersRadii(baseImage.getCornerRadii());
        } else {
            params.setCornersRadius(0);
        }

        // 重新设置RoundingParams
        draweeView.getHierarchy().setRoundingParams(params);

        // 获取ImageRequest配置
        ImageRequest imageRequest = FrescoConfig.getImageRequest(baseImage).build();

        // 获取低分辨率的ImageRequest
        ImageRequest lowerImageRequest = baseImage.getLowImageUri() == null ? null : FrescoConfig.getLowerImageRequest(baseImage).build();

        // 判断是否需要使用到bitmap
        if (isPostBitmap) {
            DataSource<CloseableReference<CloseableImage>> source = Fresco.getImagePipeline().fetchDecodedImage(imageRequest, AppUtils.app());
            source.subscribe(new BaseBitmapDataSubscriber() {
                @Override
                protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                    if (callBack != null) {
                        callBack.loadSuccess(bitmap);
                    }
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                    if (callBack != null) {
                        callBack.loadFailure();
                    }
                }

                @Override
                public void onProgressUpdate(DataSource<CloseableReference<CloseableImage>> dataSource) {
                    if (callBack != null) {
                        callBack.updateProgress(dataSource.getProgress());
                    }
                }
            }, UiThreadImmediateExecutorService.getInstance());
        }

        // 配置PipelineDraweeController
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setTapToRetryEnabled(true) // 设置是否支持点击重新加载图片
                .setLowResImageRequest(lowerImageRequest)
                .setImageRequest(imageRequest)
                .setOldController(draweeView.getController())
                .setAutoPlayAnimations(baseImage.isAutoPlayAnimation())
                .setControllerListener(FrescoConfig.genControllerListener(draweeView, baseImage))
                .build();

        draweeView.setController(controller);
    }


    // 异步得到bitmap, 所以需要回调接口
    public static void getBitmapFromImage(BaseImage baseImage, final IImageLoadCallBack callBack) {
        ImageRequest imageRequest = FrescoConfig.getImageRequest(baseImage).build();
        ImagePipeline pipeline = Fresco.getImagePipeline();
        // 获取已经解码的图片
        DataSource<CloseableReference<CloseableImage>> dataSource = pipeline.fetchDecodedImage(imageRequest, AppUtils.app());
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            // 加载成功
            @Override
            protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                // 你可以直接在这里使用Bitmap，没有别的限制要求，也不需要回收
                // 你不能在这个之外的地方使用Bitmap, 一旦超过这个范围, 这个Bitmap就会被回收
                // 当然可以传给RemoteView或者是通知栏
                if (callBack != null) {
                    callBack.loadSuccess(bitmap);
                }
            }

            // 加载失败
            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (callBack != null) {
                    callBack.loadFailure();
                }
            }

            // 显示进度
            @Override
            public void onProgressUpdate(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (callBack != null) {
                    callBack.updateProgress(dataSource.getProgress());
                }
            }
        }, UiThreadImmediateExecutorService.getInstance());
    }


    // 得到图片的文件路径
    public static File getCacheFileFromFrescoDiskCache(Uri uri) {
        // 先判断uri是否是null
        ImageRequest request = ImageRequest.fromUri(uri);
        if (request == null) {
            return null;
        }

        File cacheFile = null;

        // 判断是否存在缓存
        if (isCachedInFile(uri)) {
            BinaryResource resource = Fresco
                    .getImagePipelineFactory()
                    .getMainFileCache()
                    .getResource(MLCacheKeyFactory.getInstance().getEncodedCacheKey(request, null));
            cacheFile = ((FileBinaryResource)resource).getFile();
        } else if (isSmallCachedInFile(uri)) {
            BinaryResource resource = Fresco
                    .getImagePipelineFactory()
                    .getSmallImageFileCache()
                    .getResource(MLCacheKeyFactory.getInstance().getEncodedCacheKey(request, null));
            cacheFile = ((FileBinaryResource)resource).getFile();
        }
        return cacheFile;
    }

    public static File getCacheFileFromFrescoDiskCache(String url) {
        return getCacheFileFromFrescoDiskCache(Uri.parse(url));
    }


    // 该方法用来判断是否uri对应的图片是否在file中缓存过了
    // 可以使用Fresco.getImagePipeline().isInDiskCache(uri).getResult()代替
    public static boolean isCachedInFile(Uri uri) {
        // 先判断uri是否是null
        ImageRequest request = ImageRequest.fromUri(uri);
        if (request == null) {
            return false;
        }
        return Fresco.getImagePipelineFactory().getMainFileCache().hasKey(MLCacheKeyFactory.getInstance().getEncodedCacheKey(request, null));
    }


    public static boolean isCachedInFile(String url) {
        return isCachedInFile(Uri.parse(url));
    }


    // 该方法用来判断是否uri对应的小图是否在file中缓存过了
    public static boolean isSmallCachedInFile(Uri uri) {
        // 先判断uri是否是null
        ImageRequest request = ImageRequest.fromUri(uri);
        if (request == null) {
            return false;
        }
        return Fresco.getImagePipelineFactory().getSmallImageFileCache().hasKey(MLCacheKeyFactory.getInstance().getEncodedCacheKey(request, null));
    }

    public static boolean isSmallCachedInFile(String url) {
        return isSmallCachedInFile(Uri.parse(url));
    }


    // 该方法用来判断是否uri对应的小图是否在memory中缓存过了
    // 不常用, 因为内存缓存是会被回收的,不好进行判断
    // 而且自定义key缓存之后就好像不能正确的返回了, 连Fresco.getImagePipeline().isInBitmapMemoryCache(uri);都不能成功返回
    // 并且这个判断无论是不是自定义key都好像不能成功返回
    public static boolean isCachedInMemory(Uri uri) {
        // 先判断uri是否是null
        ImageRequest request = ImageRequest.fromUri(uri);
        if (request == null) {
            return false;
        }

        return Fresco.getImagePipelineFactory().getMainBufferedDiskCache().containsSync(MLCacheKeyFactory.getInstance().getEncodedCacheKey(request, null));
    }


    // 使用这个方法来清除uri对应的缓存
    public static void removeCache(Uri uri) {
        Fresco.getImagePipeline().evictFromDiskCache(uri);
        Fresco.getImagePipeline().evictFromCache(uri);
        Fresco.getImagePipeline().evictFromMemoryCache(uri);
    }
}
