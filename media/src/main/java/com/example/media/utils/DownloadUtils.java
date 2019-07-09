package com.example.media.utils;

import android.util.Log;

import com.example.commonlibrary.utils.AppUtils;
import com.example.media.BuildConfig;
import com.example.media.common.DownloadTracker;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.offline.ActionFileUpgradeUtil;
import com.google.android.exoplayer2.offline.DefaultDownloadIndex;
import com.google.android.exoplayer2.offline.DefaultDownloaderFactory;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.IOException;

public class DownloadUtils {
    private static final String TAG = "DownloadUtils";
    private static DownloadUtils sInstance;
    private static final String DOWNLOAD_ACTION_FILE = "actions";
    private static final String DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions";
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";

    protected static String userAgent;

    private static DatabaseProvider databaseProvider;
    private static File downloadDirectory;
    private static Cache downloadCache;
    private static DownloadManager downloadManager;
    private static DownloadTracker downloadTracker;

    private DownloadUtils() {
        // 生成用户
        userAgent = Util.getUserAgent(AppUtils.app(), "ExoPlayerTest");
    }


    public static DownloadUtils getInstance() {
        if (sInstance == null) {
            synchronized (DownloadUtils.class) {
                if (sInstance == null) {
                    sInstance = new DownloadUtils();
                }
            }
        }
        return sInstance;
    }


    // 获取DownloadManager
    public DownloadManager getDownloadManager() {
        initDownloadManager();
        return downloadManager;
    }

    // 获取DownloadTracker
    public DownloadTracker getDownloadTracker() {
        initDownloadManager();
        return downloadTracker;
    }


    // 初始化DownloadManager
    private static synchronized void initDownloadManager() {
        if (downloadManager == null) {
            DefaultDownloadIndex downloadIndex = new DefaultDownloadIndex(getDatabaseProvider());
            upgradeActionFile(DOWNLOAD_ACTION_FILE, downloadIndex, false);
            upgradeActionFile(DOWNLOAD_TRACKER_ACTION_FILE, downloadIndex, true);

            DownloaderConstructorHelper downloaderConstructorHelper = new DownloaderConstructorHelper(getDownloadCache(), buildHttpDataSourceFactory());

            // 创建DownloadManager实例
            downloadManager = new DownloadManager(AppUtils.app(), downloadIndex, new DefaultDownloaderFactory(downloaderConstructorHelper));

            downloadTracker = new DownloadTracker(AppUtils.app(), buildDataSourceFactory(), downloadManager);
        }
    }

    // 跟新文件
    private static void upgradeActionFile(String fileName, DefaultDownloadIndex downloadIndex, boolean addNewDownloadsAsCompleted) {
        try {
            ActionFileUpgradeUtil.upgradeAndDelete(new File(getDownloadDirectory(), fileName),
                    null,
                    downloadIndex,
                    true,
                    addNewDownloadsAsCompleted);
        } catch (IOException e) {
            Log.e(TAG, "Failed to upgrade action file: " + fileName, e);
            e.printStackTrace();
        }
    }


    // 获取数据库
    private static DatabaseProvider getDatabaseProvider() {
        if (databaseProvider == null) {
            databaseProvider = new ExoDatabaseProvider(AppUtils.app());
        }
        return databaseProvider;
    }


    // 获取下载目录
    private static File getDownloadDirectory() {
        if (downloadDirectory == null) {
            downloadDirectory = AppUtils.app().getExternalFilesDir(null); // /storage/emulated/0/Android/data/com.example.androidxdemo/files
            if (downloadDirectory == null) {
                downloadDirectory = AppUtils.app().getFilesDir(); // /data/user/0/com.example.androidxdemo/files
            }
        }
        return downloadDirectory;
    }

    // 获取下载缓存
    private static synchronized Cache getDownloadCache() {
        if (downloadCache == null) {
            File downloadContentDirectory = new File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY);
            downloadCache = new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor(), getDatabaseProvider());
        }
        return downloadCache;
    }

    // 创建一个从网络读取数据的dataSource
    public static HttpDataSource.Factory buildHttpDataSourceFactory() {
        return new DefaultHttpDataSourceFactory(userAgent);
    }


    // 返回一个媒体工厂
    public static DataSource.Factory buildDataSourceFactory() {
        DefaultDataSourceFactory upstreamFactory = new DefaultDataSourceFactory(AppUtils.app(), buildHttpDataSourceFactory());
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache());
    }


    private static CacheDataSourceFactory buildReadOnlyCacheDataSource(DataSource.Factory upstreamFactory, Cache cache) {
        return new CacheDataSourceFactory(
                cache,
                upstreamFactory,
                new FileDataSourceFactory(),
                null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                null
        );
    }


    // 建立一个默认的渲染器
    public RenderersFactory buildRenderersFactory(boolean preferExtensionRenderer) {
        int extensionRendererMode = useExtensionRenderers() ? (
                preferExtensionRenderer ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
        ) : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;

        Log.d(TAG, "build rendereres factory extension renderer mode=" + getRenderersModeString(extensionRendererMode));
        return new DefaultRenderersFactory(AppUtils.app()).setExtensionRendererMode(extensionRendererMode);
    }

    // 返回是否应使用扩展渲染器
    public boolean useExtensionRenderers() {
        return "withExtensions".equals(BuildConfig.FLAVOR);
    }


    private String getRenderersModeString(int mode) {
        switch (mode) {
            case DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF:
                return "不允许使用扩展渲染器";
            case DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON:
                return "允许使用非核心扩展渲染器";
            case DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER:
                return "允许使用核心渲染器";
            default:
                return "无法识别的模式";
        }
    }
}
