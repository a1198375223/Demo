package com.example.media.common;

import android.app.Notification;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.media.R;
import com.example.media.utils.DownloadUtils;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

/**
 * 管理下载
 */
public class ExoDownloadService extends DownloadService {
    private static final String TAG = "ExoDownloadService";

    private static final String CHANNEL_ID = "download_channel";
    private static final int JOB_ID = 1;
    private static final int FOREGROUND_NOTIFICATION_ID = 1;

    private static int nextNotificationId = FOREGROUND_NOTIFICATION_ID + 1;

    private DownloadNotificationHelper notificationHelper;

    protected ExoDownloadService() {
        super(FOREGROUND_NOTIFICATION_ID,
                DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
                CHANNEL_ID,
                R.string.exo_download_notification_channel_name);
        nextNotificationId = FOREGROUND_NOTIFICATION_ID + 1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new DownloadNotificationHelper(this, CHANNEL_ID);
    }

    // 返回要使用的DownloadManager。
    @Override
    protected DownloadManager getDownloadManager() {
        return DownloadUtils.getInstance().getDownloadManager();
    }

    // 返回一个可选的Scheduler，它可以重新启动服务
    // PlatformScheduler      --------> use JobScheduler
    // JobDispatcherScheduler --------> use Firebase JobDispatcher.
    @Nullable
    @Override
    protected PlatformScheduler getScheduler() {
        return Util.SDK_INT >= 21? new PlatformScheduler(this, JOB_ID) : null;
    }

    //返回服务在前台运行时要显示的通知。您可以使用DownloadNotificationHelper.buildProgressNotification以默认样式创建通知。
    @Override
    protected Notification getForegroundNotification(List<Download> downloads) {
        return notificationHelper.buildProgressNotification(R.drawable.ic_download, null, null, downloads);
    }

    @Override
    protected void onDownloadChanged(Download download) {
        Log.d(TAG, "onDownloadChanged: state=" + download.state);
        Notification notification;
        if (download.state == Download.STATE_COMPLETED) {
            notification = notificationHelper.buildDownloadCompletedNotification(R.drawable.ic_download_done,
                    null, Util.fromUtf8Bytes(download.request.data));
        } else if (download.state == Download.STATE_FAILED) {
            notification = notificationHelper.buildDownloadFailedNotification(R.drawable.ic_download_done,
                    null, Util.fromUtf8Bytes(download.request.data));
        } else {
            return;
        }
        NotificationUtil.setNotification(this, nextNotificationId++, notification);
    }

    @Override
    protected void onDownloadRemoved(Download download) {
        Log.d(TAG, "onDownloadRemoved: ");
        super.onDownloadRemoved(download);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved: ");
        super.onTaskRemoved(rootIntent);
    }
}
