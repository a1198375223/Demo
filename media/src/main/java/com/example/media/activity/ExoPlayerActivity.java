package com.example.media.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.commonlibrary.utils.Toasty;
import com.example.media.R;
import com.example.media.adapter.SampleAdapter;
import com.example.media.common.DownloadTracker;
import com.example.media.common.ExoDownloadService;
import com.example.media.model.Sample;
import com.example.media.model.SampleGroup;
import com.example.media.presenter.GalileoPlayerManager;
import com.example.media.presenter.SampleListLoader;
import com.example.media.utils.DownloadUtils;
import com.google.android.exoplayer2.offline.DownloadService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * ExoPlayer使用
 * 1. 创建一个播放器
 *      SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(context);
 *
 * 2. 获取ExoPlayer的Looper
 *      exoPlayer.getApplicationLooper();
 *
 * 3. 将ExoPlayer绑定到PlayerView中去
 *      playView.setPlayer(exoPlayer);
 *
 * 4. 使用SimpleExoPlayer的setVideoSurfaceView、setVideoTextureView、setVideoSurfaceHolder
 *      和setVideoSurface方法分别的设置播放器的属性SurfaceView、TextureView、SurfaceHolder和Surface。
 *
 * 5. 在播放的时候，setTextOutput和setId3Output可以被用来接收字幕和ID3元数据输出。
 *
 * 6. 准备播放资源(MediaSource)---->代表DASH资源的DashMediaSource
 *                                 代表SmoothStreaming资源的SsMediaSource
 *                                 代表HLS资源的HlsMediaSource
 *                                 代表一般的多媒体文件的ExtractorMediaSource
 *                                 ConcatenatingMediaSource列表播放视频 ---------> 10.
 *                                 ClippingMediaSource剪辑视频 ------------------> 11.
 *                                 LoopingMediaSource视频循环播放 ---------------> 12.
 *                                 MergingMediaSource侧载一个字幕文件 ------------> 13.
 *
 *      //创建一个DataSource对象，通过它来下载多媒体数据
 *      DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "yourApplicationName"));
 *      //这是一个代表将要被播放的媒体的MediaSource
 *      MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
 *                                      .createMediaSource(mp4VideoUri);
 *      //使用资源准备播放器
 *      player.prepare(videoSource);
 *
 * 7. setPlayWhenReady : 可以开始和暂停播放。
 *    seekTo : 方法可以在媒体资源里进行搜索
 *    setRepeatMode : 控制了多媒体如何循环播放
 *    setShuffleModeEnabled : 控制了是否打乱播放列表
 *    setPlaybackParameters : 用来调整播放的速度和音调。
 *
 * 8. 监听播放器事件(Player.DefaultEventListener/Player.EventListener)
 *      // 添加一个监听来接收播放器事件.
 *      player.addListener(eventListener);
 *
 *      addVideoListener方法允许你获取到视频渲染相关的事件，它可以帮助你调整UI布局（渲染视频的Surface的长宽比）
 *      addAnalyticsListener方法允许你接收更加详细的事件，它有助于你分析一些东西。
 *
 * 9. 释放播放器
 *      exoPlayer.release();
 *
 * 10.播放列表(PlayLists) (这种连接不要求是相同格式的资源)
 *       //先播放第一个视频，再播放第二个视频
 *      ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(firstSource, secondSource);
 *
 * 11.剪辑视频(Clipping video)
 *      // 从第5s开始剪辑到第10s
 *      ClippingMediaSource clippingSource =new ClippingMediaSource(videoSource, /startPositionUs=/  5_000_000,/endPositionUs= / 10_000_000);
 *
 * 12.视频循环播放(Looping video)
 *      // Plays the video twice.
 *      LoopingMediaSource loopingSource = new LoopingMediaSource(source, 2);
 *
 * 13.侧载一个字幕文件(Side-loading a subtitle file)
 *
 *      // 创建一个视频的 MediaSource.
 *      MediaSource videoSource = new ExtractorMediaSource.Factory(...).createMediaSource(videoUri);
 *      // 创建一个字幕的 MediaSource.
 *      Format subtitleFormat = Format.createTextSampleFormat(
 *                                  id, // 一个轨道的标志，可以为空
 *                                  MimeTypes.APPLICATION_SUBRIP, // The mime type. Must be set correctly.
 *                                  selectionFlags, // 轨道的选择标志
 *                                  language); // 字幕的语言，可以为空
 *      MediaSource subtitleSource = new SingleSampleMediaSource.Factory(...)
 *                              .createMediaSource(subtitleUri, subtitleFormat, C.TIME_UNSET);
 *      // 播放带有字幕的视频
 *      MergingMediaSource mergedSource = new MergingMediaSource(videoSource, subtitleSource);
 *
 * 14.轨道选择
 *       DefaultTrackSelector trackSelector = new DefaultTrackSelector();
 *        trackSelector.setParameters(trackSelector
 *                                      .buildUponParameters()
 *                                      .setMaxVideoSizeSd()
 *                                      .setPreferredAudioLanguage("deu"));
 *       SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
 *
 * 15.发送消息给组件
 *      a. 这些消息可以使用createMessage创建，然后使用PlayerMessage.send发送
 *      b. 消息会尽快在播放线程上传递，但是这是可以自定义的通过设置另一个回调线程(使用PlayerMessage.setHandler)或指定一个传递消息的播放位置(使用PlayerMessage.setPosition)
 *
 * 16.自定义播放器
 *      Renderer——您可能想要实现一个自定义Renderer来处理库默认不支持的媒体类型。
 *
 *      TrackSelector——实现自定义TrackSelector允许应用程序开发人员更改MediaSource暴露tracks的方式。它会被每个可用的渲染器选择使用。
 *
 *      LoadControl—实现自定义LoadControl允许应用程序开发人员更改播放器的缓冲策略。
 *
 *      Extractor——如果您需要支持目前该库不支持的容器格式，请考虑实现一个定制的Extractor类，然后可以将其与ExtractorMediaSource一起用于播放该类型的媒体。
 *
 *      MediaSource——如果您希望以自定义的方式获取媒体样本以提供给渲染程序，或者希望实现自定义的MediaSource组合行为，那么实现自定义的MediaSource类可能是最好的选择。
 *
 *      DataSource——ExoPlayer的upstream包已经包含了许多不同用例的DataSource实现。您可能希望实现自己的DataSource，以另一种方式加载数据，例如通过自定义协议、使用自定义HTTP堆栈或从自定义持久缓存加载数据。
 *
 * 17.Exo下载视频
 * DownloadService                  : 包装DownloadManager并将命令转发给它。即使应用程序在后台，该服务也允许DownloadManager继续运行
 * DownloadManager                  : 管理下载 从DownloadIndex中加载下载状态, 获取将下载状态告诉DownloadIndex, 根据网络状态
 *                                    来要求停止或者启动下载任务. 在下载内容的时候, DownloadManager会从HttpDataSource下载数据
 *                                    并且将数据写进Cache中
 * DownloadIndex                    : 保持下载状态
 * DownloadNotificationHelper       : 帮助创建前台通知
 * DownloadRequest                  : 通过这个类告诉DownloadService下载的任务
 */
public class ExoPlayerActivity extends AppCompatActivity implements DownloadTracker.Listener, ExpandableListView.OnChildClickListener {
    private static final String TAG = "ExoPlayerActivity";
    private ExpandableListView mListView;
    private MenuItem mPreferExtensionDecodersMenuItem;
    private MenuItem mRandomAbrMenuItem;
    private SampleAdapter mSampleAdapter;
    private DownloadTracker downloadTracker;

    // 是否使用扩展渲染器
    private boolean useExtensionRenderers;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);

        useExtensionRenderers = DownloadUtils.getInstance().useExtensionRenderers();
        downloadTracker = DownloadUtils.getInstance().getDownloadTracker();


        mSampleAdapter = new SampleAdapter(getSupportFragmentManager(), downloadTracker);
        ExpandableListView sampleListView = findViewById(R.id.sample_list);
        sampleListView.setAdapter(mSampleAdapter);
        sampleListView.setOnChildClickListener(this);


        Intent intent = getIntent();
        String dataUri = intent.getDataString();
        String[] uris;

        if (dataUri != null) {
            uris = new String[]{dataUri};
        } else {
            ArrayList<String> uriList = new ArrayList<>();
            AssetManager assetManager = getAssets();
            try {
                for (String asset : Objects.requireNonNull(assetManager.list(""))) {
                    if (asset.endsWith(".exolist.json")) {
                        uriList.add("asset:///" + asset);
                    }
                }
            } catch (IOException e) {
                Toasty.showError(getString(R.string.sample_list_load_error));
                e.printStackTrace();
            }

            uris = new String[uriList.size()];
            uriList.toArray(uris);
            Arrays.sort(uris);
        }

        SampleListLoader loader = new SampleListLoader(groups -> mSampleAdapter.setSampleGroups(groups));
        loader.execute(uris);

        try {
            DownloadService.start(this, ExoDownloadService.class);
        } catch (IllegalStateException e) {
            DownloadService.startForeground(this, ExoDownloadService.class);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        downloadTracker.addListener(this);
        mSampleAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        downloadTracker.removeListener(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sample_chooser_menu, menu);
        mPreferExtensionDecodersMenuItem = menu.findItem(R.id.prefer_extension_decoders);
        mPreferExtensionDecodersMenuItem.setVisible(useExtensionRenderers);

        mRandomAbrMenuItem = menu.findItem(R.id.random_abr);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        item.setChecked(!item.isChecked());
        if (item.getItemId() == R.id.prefer_extension_decoders) {
            mSampleAdapter.setChecked(item.isChecked());
        }
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
        Log.d(TAG, "click child group position=" + groupPosition + " child position=" + childPosition + " id=" + id);
        Sample sample = (Sample) view.getTag();
//        startActivity(sample.buildIntent(this, mPreferExtensionDecodersMenuItem.isChecked(),
//                mRandomAbrMenuItem.isChecked() ? PlayActivity.ABR_ALGORITHM_RANDOM : PlayActivity.ABR_ALGORITHM_DEFAULT));
        startActivity(sample.buildIntent(this, mPreferExtensionDecodersMenuItem.isChecked(),
                mRandomAbrMenuItem.isChecked() ? GalileoPlayerManager.ABR_ALGORITHM_RANDOM : GalileoPlayerManager.ABR_ALGORITHM_DEFAULT));
        return true;
    }

    @Override
    public void onDownloadsChanged() {
        Log.d(TAG, "onDownloadsChanged: ");
        mSampleAdapter.notifyDataSetChanged();
    }
}
