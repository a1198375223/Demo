package com.example.media.common;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.example.commonlibrary.utils.Toasty;
import com.example.media.R;
import com.example.media.fragment.TrackSelectionDialog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadCursor;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadIndex;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.Requirements;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;


public class DownloadTracker {
    private static final String TAG = "DownloadTracker";

    public interface Listener {
        // 下载追踪变更时调用
        void onDownloadsChanged();
    }

    private final Context context;
    private final DataSource.Factory dataSourceFactory;
    private final CopyOnWriteArraySet<Listener> listeners;
    private final HashMap<Uri, Download> downloads;
    private final DownloadIndex downloadIndex;

    private StartDownloadDialogHelper startDownloadDialogHelper;



    public DownloadTracker(Context context, DataSource.Factory dataSourceFactory, DownloadManager downloadManager) {
        this.context = context;
        this.dataSourceFactory = dataSourceFactory;
        this.listeners = new CopyOnWriteArraySet<>();
        this.downloads = new HashMap<>();
        this.downloadIndex = downloadManager.getDownloadIndex();
        downloadManager.addListener(new DownloadManagerListener());
        try {
            DownloadCursor loadedDownLoads = downloadIndex.getDownloads();
            while (loadedDownLoads.moveToNext()) {
                Download download = loadedDownLoads.getDownload();
                downloads.put(download.request.uri, download);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to query downloads", e);
            e.printStackTrace();
        }
    }

    // 添加监听器
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    // 移除监听器
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    // 判断是否下载完成了
    public boolean isDownloaded(Uri uri) {
        Download download = downloads.get(uri);
        return download != null && download.state != Download.STATE_FAILED;
    }

    // 获取下载请求
    public DownloadRequest getDownloadRequest(Uri uri) {
        Download download = downloads.get(uri);
        return download != null && download.state != Download.STATE_FAILED ? download.request : null;
    }

    // 得到DownloadHelper
    private DownloadHelper getDownloadHelper(Uri uri, String extension, RenderersFactory renderersFactory) {
        int type = Util.inferContentType(uri, extension);
        Log.d(TAG, "Get download helper uri=" + uri.toString() + "\n" +
                "extension=" + extension + "\n" +
                "type=" + type);

        switch (type) {
            case C.TYPE_DASH: // 0
                return DownloadHelper.forDash(uri, dataSourceFactory, renderersFactory);
            case C.TYPE_SS: // 1
                return DownloadHelper.forSmoothStreaming(uri, dataSourceFactory, renderersFactory);
            case C.TYPE_HLS: // 2
                return DownloadHelper.forHls(uri, dataSourceFactory, renderersFactory);
            case C.TYPE_OTHER: // 3
                return DownloadHelper.forProgressive(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    // 下载按钮带年纪
    public void toggleDownload(FragmentManager fragmentManager, String name,
                               Uri uri, String extension,
                               RenderersFactory renderersFactory) {
        Log.d(TAG, "download button click. name=" + name + " uri=" + uri.toString() + " extension=" + extension);
        Download download = downloads.get(uri);
        if (download != null) {
            // 删除任务
            Log.d(TAG, "send remove download.");
            DownloadService.sendRemoveDownload(context, ExoDownloadService.class, download.request.id, false);
        } else {
            // 显示dialog
            if (startDownloadDialogHelper != null) {
                startDownloadDialogHelper.release();
            }
            startDownloadDialogHelper = new StartDownloadDialogHelper(fragmentManager, getDownloadHelper(uri, extension, renderersFactory), name);
        }
    }


    // 您可以向DownloadManager添加一个监听器，以便在当前下载更改状态时通知您：
    private class DownloadManagerListener implements DownloadManager.Listener {
        @Override
        public void onInitialized(DownloadManager downloadManager) {
            Log.d(TAG, "onInitialized: ");
        }

        @Override
        public void onDownloadChanged(DownloadManager downloadManager, Download download) {
            Log.d(TAG, "onDownloadChanged: ");
            downloads.put(download.request.uri, download);
            for (Listener listener : listeners) {
                listener.onDownloadsChanged();
            }
        }

        @Override
        public void onDownloadRemoved(DownloadManager downloadManager, Download download) {
            Log.d(TAG, "onDownloadRemoved: ");
            downloads.remove(download.request.uri);
            for (Listener listener : listeners) {
                listener.onDownloadsChanged();
            }
        }

        @Override
        public void onIdle(DownloadManager downloadManager) {
            Log.d(TAG, "onIdle: ");
        }

        @Override
        public void onRequirementsStateChanged(DownloadManager downloadManager, Requirements requirements, int notMetRequirements) {
            Log.d(TAG, "onRequirementsStateChanged: ");
        }
    }


    // 管理下载请求
    private final class StartDownloadDialogHelper implements
            DownloadHelper.Callback, DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
        private final FragmentManager fragmentManager;
        private final DownloadHelper downloadHelper;
        private final String name;

        private TrackSelectionDialog trackSelectionDialog;
        private MappingTrackSelector.MappedTrackInfo mappedTrackInfo;


        public StartDownloadDialogHelper(FragmentManager fragmentManager, DownloadHelper downloadHelper, String name) {
            this.fragmentManager = fragmentManager;
            this.downloadHelper = downloadHelper;
            this.name = name;
            downloadHelper.prepare(this);
        }

        @Override
        public void onPrepared(DownloadHelper helper) {
            Log.d(TAG, "onPrepared: ");
            if (helper.getPeriodCount() == 0) {
                Log.d(TAG, "No periods found. Downloading entire stream.");
                startDownload();
                downloadHelper.release();
                return;
            }

            mappedTrackInfo = downloadHelper.getMappedTrackInfo(0);
            if (!TrackSelectionDialog.willHaveContent(mappedTrackInfo)) {
                Log.d(TAG, "No dialog content. Downloading entire stream.");
                startDownload();
                downloadHelper.release();
                return;
            }

            trackSelectionDialog = TrackSelectionDialog.createForMappedTrackInfoAndParameters(
                    R.string.exo_download_description,
                    mappedTrackInfo,
                    DownloadHelper.DEFAULT_TRACK_SELECTOR_PARAMETERS,
                    false,
                    true,
                    this,
                    this);
            trackSelectionDialog.show(fragmentManager, null);
        }

        @Override
        public void onPrepareError(DownloadHelper helper, IOException e) {
            Toasty.showError(context.getString(R.string.download_start_error));
            Log.e(TAG, "Failed to start download", e);
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            //返回可用的媒体数量。 在准备完成之前不得调用。
            for (int periodIndex = 0; periodIndex < downloadHelper.getPeriodCount(); periodIndex++) {
                downloadHelper.clearTrackSelections(periodIndex);
                for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
                    if (!trackSelectionDialog.getIsDisabled(i)) {
                        // 为单个渲染器添加要下载的轨道选择的便捷方法。 在准备完成之前不得调用。
                        downloadHelper.addTrackSelectionForSingleRenderer(periodIndex, i,
                                DownloadHelper.DEFAULT_TRACK_SELECTOR_PARAMETERS, trackSelectionDialog.getOverrides(i));
                    }
                }
            }

            DownloadRequest downloadRequest = buildDownloadRequest();
            if (downloadRequest.streamKeys.isEmpty()) {
                return;
            }
            startDownload(downloadRequest);
        }

        @Override
        public void onDismiss(DialogInterface dialogInterface) {
            trackSelectionDialog = null;
            downloadHelper.release();
        }

        public void release() {
            downloadHelper.release();
            if (trackSelectionDialog != null) {
                trackSelectionDialog.dismiss();
            }
        }


        private void startDownload() {
            startDownload(buildDownloadRequest());
        }

        private void startDownload(DownloadRequest downloadRequest) {
            DownloadService.sendAddDownload(context, ExoDownloadService.class, downloadRequest, false);
        }

        private DownloadRequest buildDownloadRequest() {
            return downloadHelper.getDownloadRequest(Util.getUtf8Bytes(name));
        }
    }
}
