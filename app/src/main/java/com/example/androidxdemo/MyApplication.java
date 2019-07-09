package com.example.androidxdemo;


import android.os.Build;
import android.util.Log;

import com.example.androidxdemo.base.image.FrescoConfig;
import com.example.commonlibrary.utils.AppUtils;
import com.example.commonlibrary.utils.NotificationUtils;
import com.example.commonlibrary.utils.ThreadPool;
import com.example.commonlibrary.utils.RomUtils;
import com.example.commonlibrary.rom.RomChecker;
import com.example.media.utils.DownloadUtils;
import com.example.room.RoomApplication;
import com.facebook.drawee.backends.pipeline.Fresco;

public class MyApplication extends RoomApplication {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // 用来初始化AppUtils方便全局使用
        AppUtils.setApp(this);
        // 初始化Fresco
        Fresco.initialize(this, FrescoConfig.getImagePipelineConfig(this));
        // 初始化线程池
        ThreadPool.start();
        // 初始化NotificationChannel
        NotificationUtils.createNotificationChannel();
        NotificationUtils.createNotificationBadge();

        // 通过手机制造商来判断手机的类型
        Log.d(TAG, "手机类型判断: rom name=" + RomUtils.getName() + " version=" + RomUtils.getVersion()
                + " manufacturer=" + Build.MANUFACTURER.toUpperCase() + " display=" + Build.DISPLAY.toLowerCase()
                + "\n是否是小米手机=" + RomUtils.isXiaoMi()
                + "\n是否是vivo手机=" + RomUtils.isVivo()
                + "\n是否是oppo手机=" + RomUtils.isOppo()
                + "\n是否是魅族手机=" + RomUtils.isFlyme()
                + "\n是否是锤子手机=" + RomUtils.isSmartisan()
                + "\n是否是华为手机=" + RomUtils.isHuaWei()
                + "\n是否是一加手机=" + RomUtils.isOnePlus());

        ThreadPool.runOnIOPool(() -> {
            RomChecker.initRom(this);
            Log.d(TAG, "手机类型判断: rom=" + RomChecker.getRom() + " rom info=" + RomChecker.getInfo().toString()
                    + "\n是否是小米手机=" + RomChecker.isXiaoMi()
                    + "\n是否是vivo手机=" + RomChecker.isVivo()
                    + "\n是否是oppo手机=" + RomChecker.isOppo()
                    + "\n是否是魅族手机=" + RomChecker.isMeizu()
                    + "\n是否是sony手机=" + RomChecker.isSony()
                    + "\n是否是华为手机=" + RomChecker.isHuaWei()
                    + "\n是否是一加手机=" + RomChecker.isOnePlus()
                    + "\n是否是samsung手机=" + RomChecker.isSanXing()
                    + "\n是否是乐视手机=" + RomChecker.isLeShi()
                    + "\n是否是金立手机=" + RomChecker.isJinLi()
                    + "\n是否是锤子手机=" + RomChecker.isChuiZi());
        });

        DownloadUtils.getInstance().init();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ThreadPool.stop();
        AppUtils.shutApp();
    }

    /*// 对Fresco进行初始化
    private void initFresco() {
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                // ---------------与已解码的Bitmap缓存相关的信息------------------
                // 设置已解码的Bitmap缓存策略信息
                .setBitmapMemoryCacheParamsSupplier(bitmapCacheParamsSupplier)
                // 观察者模式 保存其他注册的类 用于通知系统内存事件 不自行定义的话默认没有任何操作
                .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
                // 设置用于平台优化的Bitmap工厂
                .setPlatformBitmapFactory(platformBitmapFactory)
                // 设置已解码的Bitmap缓存裁剪策略
                .setBitmapMemoryCacheTrimStrategy(trimStrategy)
                // 设置图片缓存操作的跟踪接口 会组合到MemoryCache中 可以方便我们监听图片缓存操作
                .setImageCacheStatsTracker(imageCacheStatsTracker)
                // ------------------------------------------------------------
                // ---------------与未解码的Bitmap缓存相关的信息------------------
                // 设置未解码的Bitmap缓存策略信息
                .setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier)
                // 观察者模式 保存其他注册的类 用于通知系统内存事件 不自行定义的话默认没有任何操作
                // .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
                // 设置用于平台优化的Bitmap工厂
                // .setPlatformBitmapFactory(platformBitmapFactory)
                // 设置图片缓存操作的跟踪接口 会组合到MemoryCache中 可以方便我们监听图片缓存操作
                // .setImageCacheStatsTracker(imageCacheStatsTracker)
                // ------------------------------------------------------------
                // --------------------与磁盘缓存相关的信息----------------------
                // 设置创建各种pool的工厂
                .setPoolFactory(poolFactory)
                // 设置线程池 为CPU绑定操作提供一个线程池 为IO绑定操作提供另一个线程池
                .setExecutorSupplier(executorSupplier)
                // 设置图片缓存操作的跟踪接口 会组合到MemoryCache中 可以方便我们监听图片缓存操作
                // .setImageCacheStatsTracker(imageCacheStatsTracker)
                // 设置磁盘缓存DiskCache配置信息
                .setMainDiskCacheConfig(mainDiskCacheConfig)
                // 设置DiskCache工厂 用于获得DiskCache实例
                .setFileCacheFactory(fileCacheFactory)
                // ------------------------------------------------------------
                // ----------------与ImagePipeline相关的信息---------------------
                // 设置ImageRequest的监听器
                .setRequestListeners(requestListeners)
                // 设置是否支持预加载 默认是true
                .setIsPrefetchEnabledSupplier(isPrefetchEnabledSupplier)
                // 与已解码的Bitmap缓存相关的信息的所有
                // 与未解码的Bitmap缓存相关的信息的所有
                // 与磁盘缓存的所有
                // 对小图缓存的配置 默认和DiskCache配置信息相同
                .setSmallImageDiskCacheConfig(smallImageDiskCacheConfig)
                // 设置缓存文件生成的CacheKey策略
                .setCacheKeyFactory(cacheKeyFactory)
                // ------------------------------------------------------------
                // 设置内存的缓存模式是本地缓存还是缓冲缓存
                .setMemoryChunkType(MemoryChunkType.NATIVE_MEMORY)
                // 对Bitmap的信息进行配置，主要是决定每个pixel用多个byte存储以及颜色的存储
                .setBitmapsConfig(bigmapConfig)
                // 设置图片的解码decoder 对图片进行解码生成一个实现了Closeable的image实例 方便用于释放资源
                .setImageDecoder(imageDecoder)
                // 设置pipeline使用的network fetcher 默认使用HttpURLConnection类
                .setNetworkFetchProducer(networkFetchProducer)
                // 设置渐进式JPEG配置
                .setProgressiveJpegConfig(progressiveJpegConfig)
                // 设置是否允许缩放和旋转
                .setResizeAndRotateEnabledForNetwork(false)
                // 设置ImageDecoder的信息配置
                .setImageDecoderConfig(imageDecoderConfig)
                // 设置是否允许向下取样
                .setDownsampleEnabled(true)
                // 配置是否支持webp的图片格式
                .setWebpSupportEnabled(true)
                .build();
        Fresco.initialize(this, config);
    }*/
}
