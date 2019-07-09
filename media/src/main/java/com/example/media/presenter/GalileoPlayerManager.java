package com.example.media.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.example.commonlibrary.utils.AppUtils;
import com.example.commonlibrary.utils.Toasty;
import com.example.media.R;
import com.example.media.fragment.TrackSelectionDialog;
import com.example.media.utils.DownloadUtils;
import com.example.room.mvp.BasePresenter;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.spherical.SphericalSurfaceView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GalileoPlayerManager extends BasePresenter implements
        ErrorMessageProvider<ExoPlaybackException>,
        Player.EventListener,
        VideoListener,
        AudioListener,
        AnalyticsListener,
        TextOutput,
        MetadataOutput {
    private static final String TAG = "GalileoPlayerManager";

    // 数字版权相关的
    public static final String DRM_SCHEME_EXTRA = "drm_scheme";
    public static final String DRM_LICENSE_URL_EXTRA = "drm_license_url";
    public static final String DRM_KEY_REQUEST_PROPERTIES_EXTRA = "drm_key_request_properties";
    public static final String DRM_MULTI_SESSION_EXTRA = "drm_multi_session";

    // 用来判断声道类型的参数
    public static final String SPHERICAL_STEREO_MODE_MONO = "mono";
    public static final String SPHERICAL_STEREO_MODE_TOP_BOTTOM = "top_bottom";
    public static final String SPHERICAL_STEREO_MODE_LEFT_RIGHT = "left_right";


    // acr算法类型
    public static final String ABR_ALGORITHM_EXTRA = "abr_algorithm";
    public static final String ABR_ALGORITHM_DEFAULT = "default";
    public static final String ABR_ALGORITHM_RANDOM = "random";

    // 扩展解码器
    public static final String PREFER_EXTENSION_DECODERS_EXTRA = "prefer_extension_decoders";

    // 一个视频的action
    public static final String ACTION_VIEW = "com.example.media.exoplayer.action.VIEW";
    // 单个视频的扩展解码器
    public static final String EXTENSION_EXTRA = "extension";

    // 多个视频的action
    public static final String ACTION_VIEW_LIST = "com.example.media.exoplayer.action.VIEW_LIST";
    // 多个视频的uri
    public static final String URI_LIST_EXTRA = "uri_list";
    // 多个视频的扩展解码器
    public static final String EXTENSION_LIST_EXTRA = "extension_list";
    // 广告？
    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";


    // 当屏幕旋转的时候记录的数据
    public static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    public static final String KEY_WINDOW = "window";
    public static final String KEY_POSITION = "position";
    public static final String KEY_AUTO_PLAY = "auto_play";

    // 环绕模式
    public static final String SPHERICAL_STEREO_MODE_EXTRA = "spherical_stereo_mode_extra";



    // 声道类型
    private int stereoMode = -1;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    // 轨道选择器和选择器参数
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    // 最后一次看见的轨道组
    private TrackGroupArray lastSeenTrackGroupArray;
    // 媒体数字版权框架
    private FrameworkMediaDrm mediaDrm;

    // 用来定期更新TextView的helper
    private DebugTextViewHelper debugViewHelper;

    // 媒体工厂
    private DataSource.Factory dataSourceFactory;
    // 媒体资源
    private MediaSource mediaSource;

    // 加载广告的loader
    private AdsLoader adsLoader;
    // 加载的广告uri
    private Uri loadedAdTagUri;

    private boolean startAutoPlay;
    // 开始的窗口
    private int startWindow;
    // 开始的位置
    private long startPosition;

    // 上下文
    private Context context;

    // 数据
    private Intent intent;

    private Bundle saveInstanceState = null;

    // 回调接口
    private IPlaybackListener mPlaybackListener;

    // 显示debug的TextView
    private TextView debugView;

    public GalileoPlayerManager(LifecycleOwner owner, Context context, PlayerView playerView, String sphericalStereoMode) {
        super(owner);
        this.context = context;
        if (sphericalStereoMode != null) {
            stereoMode = parseStereoMode(sphericalStereoMode);
            if (stereoMode == -1) {
                Toasty.showError(AppUtils.app().getResources().getString(R.string.error_unrecognized_stereo_mode));
                throw new IllegalStateException(AppUtils.app().getResources().getString(R.string.error_unrecognized_stereo_mode));
            }
        }
        dataSourceFactory = DownloadUtils.buildDataSourceFactory();
        this.playerView = playerView;

    }

    @Override
    public void create(LifecycleOwner owner) {
        super.create(owner);
        // 初始化playView
        playerView.setErrorMessageProvider(this);
        playerView.requestFocus();

        if (stereoMode != -1) {
            ((SphericalSurfaceView) playerView.getVideoSurfaceView()).setDefaultStereoMode(stereoMode);
        }

        if (saveInstanceState != null) {
            trackSelectorParameters = saveInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
            startAutoPlay = saveInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = saveInstanceState.getInt(KEY_WINDOW);
            startPosition = saveInstanceState.getLong(KEY_POSITION);
        } else {
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }

        Log.d(TAG, "Player param: " + trackSelectorParameters + " startAutoPlay: " + startAutoPlay +
                " startWindow: " + startWindow + " startPosition: " + startPosition);
    }

    @Override
    public void start(LifecycleOwner owner) {
        super.start(owner);
        if (intent == null) {
            throw new IllegalArgumentException("Must init intent before onStart.");
        }
        if (Util.SDK_INT > 23) {
            initializePlayer(context, intent);
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void resume(LifecycleOwner owner) {
        super.resume(owner);
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer(context, intent);
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void pause(LifecycleOwner owner) {
        super.pause(owner);
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }


    @Override
    public void stop(LifecycleOwner owner) {
        super.stop(owner);
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void destroy(LifecycleOwner owner) {
        super.destroy(owner);
        releaseAdsLoader();
    }

    // 解析播放异常的log
    @Override
    public Pair<Integer, String> getErrorMessage(ExoPlaybackException throwable) {
        String errorString = AppUtils.app().getResources().getString(R.string.error_generic);
        if (throwable.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = throwable.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException = (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = AppUtils.app().getResources().getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = AppUtils.app().getResources().getString(R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                    } else {
                        errorString = AppUtils.app().getResources().getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = AppUtils.app().getResources().getString(R.string.error_instantiating_decoder, decoderInitializationException.decoderName);
                }
            }
        }
        Log.e(TAG, "Error message " + errorString + " throwable: ", throwable);
        return Pair.create(0, errorString);
    }


    // 解析得到声道模式
    private int parseStereoMode(String mode) {
        switch (mode) {
            case SPHERICAL_STEREO_MODE_LEFT_RIGHT:
                return C.STEREO_MODE_MONO;
            case SPHERICAL_STEREO_MODE_MONO:
                return C.STEREO_MODE_MONO;
            case SPHERICAL_STEREO_MODE_TOP_BOTTOM:
                return C.STEREO_MODE_TOP_BOTTOM;
            default:
                Toasty.showError(AppUtils.app().getResources().getString(R.string.error_unrecognized_stereo_mode));
                return -1;
        }
    }

    /**
     * 初始化ExoPlayer
     * @param context 上下文对象
     * @param intent 需要播放的媒体数据
     */
    private void initializePlayer(Context context, Intent intent) {
        if (player == null) {
            if (intent == null) {
                Log.e(TAG, "Couldn't initialize player because intent is null.");
                return;
            }

            String action = intent.getAction();
            Uri[] uris;
            String[] extensions;

            // 获取需要播放的uris和对应的视频扩展名
            if (ACTION_VIEW.equals(action)) {
                // 如果只有一个视频就直接用intent的setData来传递uri
                uris = new Uri[]{intent.getData()};
                // 获取单个视频的扩展解码器
                extensions = new String[]{intent.getStringExtra(EXTENSION_EXTRA)};
            } else if (ACTION_VIEW_LIST.equals(action)) {
                String[] uriStrings = intent.getStringArrayExtra(URI_LIST_EXTRA);
                if (uriStrings == null) {
                    Toasty.showError("无法获取视频列表的uri");
                    return;
                }
                uris = new Uri[uriStrings.length];
                for (int i = 0; i < uriStrings.length; i++) {
                    uris[i] = Uri.parse(uriStrings[i]);
                }
                extensions = intent.getStringArrayExtra(EXTENSION_LIST_EXTRA);
                if (extensions == null) {
                    extensions = new String[uriStrings.length];
                }
            } else {
                // 无法识别的action
                Toasty.showError(context.getString(R.string.unexpected_intent_action, action));
                return;
            }

            // 检查uri是否是明文通信
            if (!Util.checkCleartextTrafficPermitted(uris)) {
                Toasty.showError(context.getString(R.string.error_cleartext_not_permitted));
                Log.e(TAG, "不允许进行明文通信.");
                return;
            }

            // 检查权限
            if (Util.maybeRequestReadExternalStoragePermission(((Activity) context), uris)) {
                Toasty.showError("无法初始化视频, 权限不足.");
                Log.e(TAG, "无法初始化视频, 权限不足.");
                return;
            }


            // 创建一个默认的数字版权加密框架
            DefaultDrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
            if (intent.hasExtra(DRM_SCHEME_EXTRA)) {
                // 版权证书url
                String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL_EXTRA);
                // 请求属性
                String[] keyRequestPropertiesArray = intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES_EXTRA);
                // multiSession来说明是否支持视频旋转, 如果支持旋转就要设置成true, 不然会出现视频闪烁的情况
                // 是否支持多个会话
                boolean multiSession = intent.getBooleanExtra(DRM_MULTI_SESSION_EXTRA, false);
                // 出错的string
                int errorStringId = R.string.error_drm_unknown;
                try {
                    // 数字版权管理的uuid
                    UUID drmSchemeUuid = Util.getDrmUuid(intent.getStringExtra(DRM_SCHEME_EXTRA));

                    if (drmSchemeUuid == null) {
                        errorStringId = R.string.error_drm_unsupported_scheme;
                    } else {
                        drmSessionManager = buildDrmSessionManagerV18(drmSchemeUuid, drmLicenseUrl, keyRequestPropertiesArray, multiSession);
                    }
                } catch (UnsupportedDrmException e) {
                    errorStringId = e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME ?
                            R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown;
                }


                if (drmSessionManager == null) {
                    Toasty.showError(context.getString(errorStringId));
                    Log.e(TAG, context.getString(errorStringId));
                    return;
                }
            }


            // 获取渲染器
            boolean preferExtensionDecoders = intent.getBooleanExtra(PREFER_EXTENSION_DECODERS_EXTRA, false);
            RenderersFactory renderersFactory = DownloadUtils.getInstance().buildRenderersFactory(preferExtensionDecoders);
            // 创建轨道选择器 通过abr(平局比特率)算法来创建不同的选择器模式
            TrackSelection.Factory trackSelectionFactory;
            String abrAlgorithm = intent.getStringExtra(ABR_ALGORITHM_EXTRA);
            if (abrAlgorithm == null || ABR_ALGORITHM_DEFAULT.equals(abrAlgorithm)) {
                // 通过网络的质量来选择码率
                trackSelectionFactory = new AdaptiveTrackSelection.Factory();
            } else if (ABR_ALGORITHM_RANDOM.equals(abrAlgorithm)) {
                // 随机选择一个轨道
                trackSelectionFactory = new RandomTrackSelection.Factory();
            } else {
                Toasty.showError(AppUtils.app().getResources().getString(R.string.error_unrecognized_abr_algorithm));
                return;
            }
            trackSelector = new DefaultTrackSelector(trackSelectionFactory);
            trackSelector.setParameters(trackSelectorParameters);

            // 将最后一次可见的轨道组置为null
            lastSeenTrackGroupArray = null;

            // 创建ExoPlayer
            player = ExoPlayerFactory.newSimpleInstance(context, renderersFactory, trackSelector, drmSessionManager);
            // 设置监听器
            player.addListener(this);
            player.addVideoListener(this);
            player.addAnalyticsListener(this);
            player.addAudioListener(this);
            player.addTextOutput(this);
            player.addMetadataOutput(this);
            // 是否自动播放
            player.setPlayWhenReady(startAutoPlay);

            // 绑定player
            playerView.setPlayer(player);
            playerView.setPlaybackPreparer(() -> {
                Log.d(TAG, "Player playback preparer.");
                player.retry();
            });

            // 显示debug
            debugViewHelper = new DebugTextViewHelper(player, debugView);
            debugViewHelper.start();

            // 生成媒体资源
            MediaSource[] mediaSources = new MediaSource[uris.length];
            for (int i = 0; i < uris.length; i++) {
                mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
            }

            // 返回一个媒体资源, 如果是多个多个资源就让他串行播放
            mediaSource = mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);

            // 是否有广告
            String adTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA);
            if (adTagUriString != null) {
                Uri adTagUri = Uri.parse(adTagUriString);
                if (!adTagUri.equals(loadedAdTagUri)) {
                    releaseAdsLoader();
                    // 初始化loadedTagUri
                    loadedAdTagUri = adTagUri;
                }

                // 创建一个AdsMediaSource, 如果有广告就插入到原始的资源上面
                MediaSource adsMediaSource = createAdsMediaSource(context, mediaSource, Uri.parse(adTagUriString));
                if (adsMediaSource != null) {
                    mediaSource = adsMediaSource;
                } else {
                    Log.e(TAG, context.getString(R.string.ima_not_loaded));
                    Toasty.showError(context.getString(R.string.ima_not_loaded));
                }
            } else {
                Log.d(TAG, "Player without ads.");
                releaseAdsLoader();
            }
        }

        // 判断是否是有保存视频播放的起始位置
        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            player.seekTo(startWindow, startPosition);
        }
        // 准备
        player.prepare(mediaSource, !haveStartPosition, false);
    }

    // 释放player资源
    private void releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters();
            updateStartPosition();
            stopDebug();
            player.release();
            player = null;
            mediaSource = null;
            trackSelector = null;
        }

        if (adsLoader != null) {
            adsLoader.setPlayer(null);
        }
        releaseMediaDrm();
    }

    // 更新轨道选择器的参数
    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    // 记录视频播放的记录
    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    /**
     * 通过反射来创建媒体资源, 重复利用ads loader
     * @param context 上下文
     * @param mediaSource 媒体资源
     * @param adTagUri    广告的uri
     * @return 媒体资源
     */
    private MediaSource createAdsMediaSource(Context context, MediaSource mediaSource, Uri adTagUri) {
        try {
            Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");
            if (adsLoader == null) {
                Constructor<? extends AdsLoader> loaderConstructor = loaderClass.asSubclass(AdsLoader.class)
                        .getConstructor(Context.class, Uri.class);
                adsLoader = loaderConstructor.newInstance(context, adTagUri);
            }
            // 将广告ads绑定到player上
            adsLoader.setPlayer(player);

            AdsMediaSource.MediaSourceFactory adMediaSourceFactory = new AdsMediaSource.MediaSourceFactory() {
                @Override
                public MediaSource createMediaSource(Uri uri) {
                    return buildMediaSource(uri);
                }

                @Override
                public int[] getSupportedTypes() {
                    return new int[]{C.TYPE_DASH, C.TYPE_SS, C.TYPE_HLS, C.TYPE_OTHER};
                }
            };
            return new AdsMediaSource(mediaSource, adMediaSourceFactory, adsLoader, playerView);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成媒体类型
     * @param uri 媒体文件uri
     * @return 返回媒体资源
     */
    private MediaSource buildMediaSource(Uri uri) {
        return buildMediaSource(uri, null);
    }

    /**
     * 生成媒体类型
     * @param uri 媒体文件uri
     * @param overrideExtension 扩展名, 如果为null会根据uri推断扩展名
     * @return 返回媒体资源
     */
    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        // 如果当前资源已经被下载过了, 就返回下载媒体的资源
        DownloadRequest downloadRequest = DownloadUtils.getInstance().getDownloadTracker().getDownloadRequest(uri);
        if (downloadRequest != null) {
            Log.d(TAG, "Create media source from download request.");
            return DownloadHelper.createMediaSource(downloadRequest, dataSourceFactory);
        }

        // 推断uri的媒体类型
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        Log.d(TAG, "Media source type: " + type);
        switch (type) {
            case C.TYPE_DASH: // 0
                return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_HLS: // 2
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER: // 3
                return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_SS: // 1
                return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    /**
     * 创建一个数字版权管理器
     * @param uuid 数字版权管理的uid
     * @param licenseUrl 证书url
     * @param keyRequestPropertiesArray 请求的属性
     * @param multiSession 是否支持多个session
     * @return 默认的数字版权会话管理器
     * @throws UnsupportedDrmException 不支持数字版权管理异常
     */
    private DefaultDrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManagerV18(
            UUID uuid, String licenseUrl, String[] keyRequestPropertiesArray, boolean multiSession) throws UnsupportedDrmException {
        // 获取http数据源
        HttpDataSource.Factory licenseDataSourceFactory = DownloadUtils.buildHttpDataSourceFactory();
        // 设置回调
        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl, licenseDataSourceFactory);
        if (keyRequestPropertiesArray != null) {
            for (int i = 0; i < keyRequestPropertiesArray.length; i++) {
                // 设置回调的属性
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i], keyRequestPropertiesArray[i + 1]);
            }
        }

        releaseMediaDrm();
        mediaDrm = FrameworkMediaDrm.newInstance(uuid);
        return new DefaultDrmSessionManager<>(uuid, mediaDrm, drmCallback, null, multiSession);
    }

    // 释放媒体数字版权框架
    private void releaseMediaDrm() {
        if (mediaDrm != null) {
            Log.d(TAG, "Release media drm.");
            mediaDrm.release();
            mediaDrm = null;
        }
    }


    // 释放广告/表层资源
    private void releaseAdsLoader() {
        if (adsLoader != null) {
            adsLoader.release();
            adsLoader = null;
            loadedAdTagUri = null;
            Objects.requireNonNull(playerView.getOverlayFrameLayout()).removeAllViews();
        }
    }


    // 重置状态
    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }


    // 当播放视频的位置在窗口之后的时候会抛出BehindLiveWindowException异常
    public static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }

        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }


    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //-----------------------------------Player.EventListener---------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    // 在刷新时间轴和/或Manifest时调用
    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
        Log.d(TAG, "Player time line changed. timeline: " + timeline + " reason: " + reason);
        if (mPlaybackListener != null) {
            mPlaybackListener.onTimelineChanged(timeline, manifest, reason);
        }
    }

    // 当轨道改变的时候调用
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d(TAG, "Player tracks changed. trackGroups: " + trackGroups + " trackSelectionArrays: " + trackSelections);
        if (trackGroups != lastSeenTrackGroupArray) {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    Toasty.showError(AppUtils.app().getResources().getString(R.string.error_unsupported_video));
                }

                if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    Toasty.showError(AppUtils.app().getResources().getString(R.string.error_unsupported_audio));
                }
            }
            lastSeenTrackGroupArray = trackGroups;
        }
        if (mPlaybackListener != null) {
            mPlaybackListener.onTracksChanged(trackGroups, trackSelections);
        }
    }

    // 开始或者停止加载资源的时候回调
    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.d(TAG, "Player is loading ? " + isLoading);
        if (mPlaybackListener != null) {
            mPlaybackListener.onLoadingChanged(isLoading);
        }
    }

    // 当播放器状态改变的时候会被调用
    // state：
    // Player.STATE_IDLE        ： 这是初始状态，播放器停止时的状态，以及播放失败时的状态。
    // Player.STATE_BUFFERING   ： 正在缓冲
    // Player.STATE_READY       ： 可以播放状态
    // Player.STATE_ENDED       ： 播放器播放完所有媒体
    // 除了这些状态之外，播放器还有一个playWhenReady标志来指示播放器打算播放的意图。如果状态为Player.STATE_READY且playWhenReady = true，则播放器可以播放。
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "Player state changed playWhenReady=" + playWhenReady + " playbackState=" + playbackState);

        if (mPlaybackListener != null) {
            mPlaybackListener.onPlayerStateChanged(playWhenReady, playbackState);
        }
    }

    // 重复模式改变的时候调用
    @Override
    public void onRepeatModeChanged(int repeatMode) {
        Log.d(TAG, "Player repeat mode change=" + repeatMode);
        if (mPlaybackListener != null) {
            mPlaybackListener.onRepeatModeChanged(repeatMode);
        }
    }

    // 是否启用随机播放
    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        Log.d(TAG, "Player shuffle mode enable? " + shuffleModeEnabled);
        if (mPlaybackListener != null) {
            mPlaybackListener.onShuffleModeEnabledChanged(shuffleModeEnabled);
        }
    }

    // 当播放器播放出错的时候会调用这个方法
    // 错误发生时，将在播放状态转换为Player.STATE_IDLE之前立即调用此方法
    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.e(TAG, "Player occurs error. e->", error);
        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
            IOException cause = error.getSourceException();
            if (cause instanceof HttpDataSource.HttpDataSourceException) {
                // An HTTP error occurred.
                HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                // This is the request for which the error occurred.
                DataSpec requestDataSpec = httpError.dataSpec;
                // It's possible to find out more about the error both by casting and by
                // querying the cause.
                if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                    // Cast to InvalidResponseCodeException and retrieve the response code,
                    // message and headers.
                } else {
                    // Try calling httpError.getCause() to retrieve the underlying cause,
                    // although note that it may be null.
                }
            }
        }

        if (isBehindLiveWindow(error)) {
            clearStartPosition();
            initializePlayer(context, intent);
        }

        if (mPlaybackListener != null) {
            mPlaybackListener.onPlayerError(error);
        }
    }

    // 视频播放不连续的时候调用
    @Override
    public void onPositionDiscontinuity(int reason) {
        Log.e(TAG, "Player position discontinuity occurs. reason: " + reason);
        if (mPlaybackListener != null) {
            mPlaybackListener.onPositionDiscontinuity(reason);
        }
    }

    // 当播放参数发生改变的时候调用
    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Log.d(TAG, "Player playback parameters changed. params=" + playbackParameters);
        if (mPlaybackListener != null) {
            mPlaybackListener.onPlaybackParametersChanged(playbackParameters);
        }
    }

    // 当播放器处理seek请求的时候调用
    @Override
    public void onSeekProcessed() {
        Log.d(TAG, "Player start seek processed.");
        if (mPlaybackListener != null) {
            mPlaybackListener.onSeekProcessed();
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //--------------------------------------VideoListener-------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------


    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.d(TAG, "Player size change: (" + width + "x" + height + ")"
                + " unappliedRotationDegrees: " + unappliedRotationDegrees + " pixelWidthHeightRatio: " + pixelWidthHeightRatio);

    }

    @Override
    public void onSurfaceSizeChanged(int width, int height) {
        Log.d(TAG, "Player surface size change: (" + width + "x" + height + ")");
    }

    @Override
    public void onRenderedFirstFrame() {
        Log.d(TAG, "Player rendered Frame.");
    }


    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //--------------------------------------AudioListener-------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------


    @Override
    public void onAudioSessionId(int audioSessionId) {
        Log.d(TAG, "Player audio session id=" + audioSessionId);
    }

    @Override
    public void onAudioAttributesChanged(AudioAttributes audioAttributes) {
        Log.d(TAG, "Player audio attributes changed.");
    }

    @Override
    public void onVolumeChanged(float volume) {
        Log.d(TAG, "Player volume changed");
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //---------------------------------------TextOutput---------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    @Override
    public void onCues(List<Cue> cues) {
        Log.d(TAG, "onCues: " + cues.toString());
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //--------------------------------------MetadataOutput-------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    @Override
    public void onMetadata(Metadata metadata) {
        Log.d(TAG, "onMetadata: " + metadata);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //--------------------------------------对外提供的方法--------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    /**
     * 初始化一个debugTextView用来显示debug信息
     * @param debugView 在这个TextView上更新信息
     */
    public void setDebugView(TextView debugView) {
        this.debugView = debugView;
    }

    /**
     * 停止debug
     */
    private void stopDebug() {
        if (debugViewHelper != null) {
            debugViewHelper.stop();
            debugViewHelper = null;
        }
    }

    // 设置intent, 需要播放的数据都存在intent中, 需要在onStart前调用
    public void setIntent(Intent intent) {
        this.intent = intent;
    }


    // 对应activity的onNewIntent(Intent intent);
    public void onNewIntent(Intent intent) {
        releasePlayer();
        releaseAdsLoader();
        clearStartPosition();
        setIntent(intent);
    }


    // 对应activity的onSaveInstanceState(Bundle outState)
    public void onSaveInstanceState(Bundle outState) {
        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
        saveInstanceState = outState;
    }

    // 设置回调
    public void setPlaybackListener(IPlaybackListener listener) {
        this.mPlaybackListener = listener;
    }


    // 判断是否拥有轨道
    public boolean willDialogHaveContent() {
        return player != null && TrackSelectionDialog.willHaveContent(trackSelector);
    }


    // 获取轨道选择器
    public DefaultTrackSelector getSelector() {
        return trackSelector;
    }


    // 对应activity的onRequestPermissionsRequest
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            // Empty results are triggered if a permission is requested while another request was already
            // pending and can be safely ignored in this case.
            return;
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializePlayer(context, intent);
        } else {
            Toasty.showError(AppUtils.app().getResources().getString(R.string.storage_permission_denied));
        }
    }

    // 对Player.Listener进行回调
    public interface IPlaybackListener {
        void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason);

        // 当轨道改变的时候调用
        void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections);

        // 开始或者停止加载资源的时候回调
        void onLoadingChanged(boolean isLoading);

        // 当播放器状态改变的时候会被调用
        // state：
        // Player.STATE_IDLE        ： 这是初始状态，播放器停止时的状态，以及播放失败时的状态。
        // Player.STATE_BUFFERING   ： 正在缓冲
        // Player.STATE_READY       ： 可以播放状态
        // Player.STATE_ENDED       ： 播放器播放完所有媒体
        // 除了这些状态之外，播放器还有一个playWhenReady标志来指示播放器打算播放的意图。如果状态为Player.STATE_READY且playWhenReady = true，则播放器可以播放。
        void onPlayerStateChanged(boolean playWhenReady, int playbackState);

        // 重复模式改变的时候调用
        void onRepeatModeChanged(int repeatMode);

        // 是否启用随机播放
        void onShuffleModeEnabledChanged(boolean shuffleModeEnabled);

        // 当播放器播放出错的时候会调用这个方法
        // 错误发生时，将在播放状态转换为Player.STATE_IDLE之前立即调用此方法
        void onPlayerError(ExoPlaybackException error);

        // 视频播放不连续的时候调用
        void onPositionDiscontinuity(int reason);

        // 当播放参数发生改变的时候调用
        void onPlaybackParametersChanged(PlaybackParameters playbackParameters);

        // 当播放器处理seek请求的时候调用
        void onSeekProcessed();
    }
}
