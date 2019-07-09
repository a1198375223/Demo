package com.example.media.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ActionProvider;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.commonlibrary.utils.Toasty;
import com.example.media.R;
import com.example.media.fragment.TrackSelectionDialog;
import com.example.media.presenter.GalileoPlayerManager;
import com.example.media.utils.DownloadUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
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
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.spherical.SphericalSurfaceView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Objects;
import java.util.UUID;

public class PlayActivity extends AppCompatActivity {
    private static final String TAG = "PlayActivity";
    // For backwards compatibility only.
    private static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";

    private DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;
    private FrameworkMediaDrm mediaDrm;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private DebugTextViewHelper debugViewHelper;
    private TrackGroupArray lastSeenTrackGroupArray;

    // 操作cookie
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }


    // 布局
    private PlayerView playerView;
    private LinearLayout debugRootView;
    private Button selectTracksButton;
    private TextView debugTextView;


    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;
    private Uri loadedAdTagUri;
    private AdsLoader adsLoader;
    private boolean isShowingTrackSelectionDialog;


    private PlayerControlView.VisibilityListener mVisibilityListener = new PlayerControlView.VisibilityListener() {
        @Override
        public void onVisibilityChange(int visibility) {
            Log.d(TAG, "onVisibilityChange: ");
            debugRootView.setVisibility(visibility);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 获取环绕模式
        String sphericalStereoMode = getIntent().getStringExtra(GalileoPlayerManager.SPHERICAL_STEREO_MODE_EXTRA);
        if (sphericalStereoMode != null) {
            setTheme(R.style.PlayerTheme_Spherical);
        }
        // 设置主题需要在super.onCreate(savedInstanceState)之前
        super.onCreate(savedInstanceState);
        dataSourceFactory = DownloadUtils.buildDataSourceFactory();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        setContentView(R.layout.activity_play);


        debugRootView = findViewById(R.id.controls_root);
        debugTextView = findViewById(R.id.debug_text_view);
        selectTracksButton = findViewById(R.id.select_tracks_button);
        selectTracksButton.setOnClickListener(view -> {
            if (!isShowingTrackSelectionDialog && TrackSelectionDialog.willHaveContent(trackSelector)) {
                isShowingTrackSelectionDialog = true;
                TrackSelectionDialog trackSelectionDialog =
                        TrackSelectionDialog.createForTrackSelector(
                                trackSelector,
                                /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
                trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);
            }
        });

        playerView = findViewById(R.id.player_view);
        playerView.setControllerVisibilityListener(mVisibilityListener);
        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        playerView.requestFocus();
        if (sphericalStereoMode != null) {
            int stereoMode;
            if (GalileoPlayerManager.SPHERICAL_STEREO_MODE_MONO.equals(sphericalStereoMode)) {
                stereoMode = C.STEREO_MODE_MONO;
            } else if (GalileoPlayerManager.SPHERICAL_STEREO_MODE_TOP_BOTTOM.equals(sphericalStereoMode)) {
                stereoMode = C.STEREO_MODE_TOP_BOTTOM;
            } else if (GalileoPlayerManager.SPHERICAL_STEREO_MODE_LEFT_RIGHT.equals(sphericalStereoMode)) {
                stereoMode = C.STEREO_MODE_LEFT_RIGHT;
            } else {
                Toasty.showError(getString(R.string.error_unrecognized_stereo_mode));
                finish();
                return;
            }
            ((SphericalSurfaceView) playerView.getVideoSurfaceView()).setDefaultStereoMode(stereoMode);
        }


        if (savedInstanceState != null) {
            trackSelectorParameters = savedInstanceState.getParcelable(GalileoPlayerManager.KEY_TRACK_SELECTOR_PARAMETERS);
            startAutoPlay = savedInstanceState.getBoolean(GalileoPlayerManager.KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(GalileoPlayerManager.KEY_WINDOW);
            startPosition = savedInstanceState.getLong(GalileoPlayerManager.KEY_POSITION);
        } else {
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        releasePlayer();
        releaseAdsLoader();
        clearStartPosition();
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseAdsLoader();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // See whether the player view wants to handle media or DPAD keys events.
        return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            // Empty results are triggered if a permission is requested while another request was already
            // pending and can be safely ignored in this case.
            return;
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializePlayer();
        } else {
            Toasty.showError(getString(R.string.storage_permission_denied));
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putParcelable(GalileoPlayerManager.KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        outState.putBoolean(GalileoPlayerManager.KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(GalileoPlayerManager.KEY_WINDOW, startWindow);
        outState.putLong(GalileoPlayerManager.KEY_POSITION, startPosition);
    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }


    private void initializePlayer() {
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri[] uris;
        String[] extensions;
        if (GalileoPlayerManager.ACTION_VIEW.equals(action)) {
            uris = new Uri[]{intent.getData()};
            extensions = new String[]{intent.getStringExtra(GalileoPlayerManager.EXTENSION_EXTRA)};
        } else if (GalileoPlayerManager.ACTION_VIEW_LIST.equals(action)) {
            String[] uriStrings = intent.getStringArrayExtra(GalileoPlayerManager.URI_LIST_EXTRA);
            uris = new Uri[uriStrings.length];
            for (int i = 0; i < uriStrings.length; i++) {
                uris[i] = Uri.parse(uriStrings[i]);
            }
            extensions = intent.getStringArrayExtra(GalileoPlayerManager.EXTENSION_LIST_EXTRA);
            if (extensions == null) {
                extensions = new String[uriStrings.length];
            }
        } else {
            Toasty.showError(getString(R.string.unexpected_intent_action, action));
            finish();
            return;
        }

        // 检查该uri是否可以被明文加载
        if (!Util.checkCleartextTrafficPermitted(uris)) {
            Toasty.showError(getString(R.string.error_cleartext_not_permitted));
            return;
        }

        // 检查是否需要READ_EXTERNAL_STORAGE权限
        if (Util.maybeRequestReadExternalStoragePermission(/* activity= */ this, uris)) {
            // The player will be reinitialized if the permission is granted.
            return;
        }

        DefaultDrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
        if (intent.hasExtra(GalileoPlayerManager.DRM_SCHEME_EXTRA) || intent.hasExtra(DRM_SCHEME_UUID_EXTRA)) {
            String drmLicenseUrl = intent.getStringExtra(GalileoPlayerManager.DRM_LICENSE_URL_EXTRA);
            String[] keyRequestPropertiesArray =
                    intent.getStringArrayExtra(GalileoPlayerManager.DRM_KEY_REQUEST_PROPERTIES_EXTRA);
            boolean multiSession = intent.getBooleanExtra(GalileoPlayerManager.DRM_MULTI_SESSION_EXTRA, false);
            int errorStringId = R.string.error_drm_unknown;
            if (Util.SDK_INT < 18) {
                errorStringId = R.string.error_drm_not_supported;
            } else {
                try {
                    String drmSchemeExtra = intent.hasExtra(GalileoPlayerManager.DRM_SCHEME_EXTRA) ?
                            GalileoPlayerManager.DRM_SCHEME_EXTRA : DRM_SCHEME_UUID_EXTRA;
                    UUID drmSchemeUuid = Util.getDrmUuid(intent.getStringExtra(drmSchemeExtra));
                    if (drmSchemeUuid == null) {
                        errorStringId = R.string.error_drm_unsupported_scheme;
                    } else {
                        drmSessionManager = buildDrmSessionManagerV18(drmSchemeUuid, drmLicenseUrl, keyRequestPropertiesArray, multiSession);
                    }
                } catch (UnsupportedDrmException e) {
                    errorStringId = e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                            ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown;
                }
            }
            if (drmSessionManager == null) {
                Toasty.showError(Integer.toString(errorStringId));
                finish();
                return;
            }
        }

        TrackSelection.Factory trackSelectionFactory;
        String abrAlgorithm = intent.getStringExtra(GalileoPlayerManager.ABR_ALGORITHM_EXTRA);
        if (abrAlgorithm == null || GalileoPlayerManager.ABR_ALGORITHM_DEFAULT.equals(abrAlgorithm)) {
            trackSelectionFactory = new AdaptiveTrackSelection.Factory();
        } else if (GalileoPlayerManager.ABR_ALGORITHM_RANDOM.equals(abrAlgorithm)) {
            trackSelectionFactory = new RandomTrackSelection.Factory();
        } else {
            Toasty.showError(getString(R.string.error_unrecognized_abr_algorithm));
            finish();
            return;
        }

        boolean preferExtensionDecoders =
                intent.getBooleanExtra(GalileoPlayerManager.PREFER_EXTENSION_DECODERS_EXTRA, false);
        RenderersFactory renderersFactory = DownloadUtils.getInstance().buildRenderersFactory(preferExtensionDecoders);

        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        trackSelector.setParameters(trackSelectorParameters);
        lastSeenTrackGroupArray = null;

        player = ExoPlayerFactory.newSimpleInstance(/* context= */ this, renderersFactory, trackSelector, drmSessionManager);
        player.addListener(new PlayerEventListener());
        player.setPlayWhenReady(startAutoPlay);
        player.addAnalyticsListener(new EventLogger(trackSelector));
        playerView.setPlayer(player);
        playerView.setPlaybackPreparer(mPlaybackPreparer);
        debugViewHelper = new DebugTextViewHelper(player, debugTextView);
        debugViewHelper.start();

        MediaSource[] mediaSources = new MediaSource[uris.length];
        for (int i = 0; i < uris.length; i++) {
            mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
        }
        mediaSource =
                mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);
        String adTagUriString = intent.getStringExtra(GalileoPlayerManager.AD_TAG_URI_EXTRA);
        if (adTagUriString != null) {
            Uri adTagUri = Uri.parse(adTagUriString);
            if (!adTagUri.equals(loadedAdTagUri)) {
                releaseAdsLoader();
                loadedAdTagUri = adTagUri;
            }
            MediaSource adsMediaSource = createAdsMediaSource(mediaSource, Uri.parse(adTagUriString));
            if (adsMediaSource != null) {
                mediaSource = adsMediaSource;
            } else {
                Toasty.showError(getString(R.string.ima_not_loaded));
            }
        } else {
            releaseAdsLoader();
        }
        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
        if(haveStartPosition)

        {
            player.seekTo(startWindow, startPosition);
        }
        player.prepare(mediaSource,!haveStartPosition,false);

        updateButtonVisibility();
    }




    private DefaultDrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManagerV18(
            UUID uuid, String licenseUrl, String[] keyRequestPropertiesArray, boolean multiSession)
            throws UnsupportedDrmException {
        HttpDataSource.Factory licenseDataSourceFactory = DownloadUtils.buildHttpDataSourceFactory();
        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl, licenseDataSourceFactory);
        if (keyRequestPropertiesArray != null) {
            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
                        keyRequestPropertiesArray[i + 1]);
            }
        }
        releaseMediaDrm();
        mediaDrm = FrameworkMediaDrm.newInstance(uuid);
        return new DefaultDrmSessionManager<>(uuid, mediaDrm, drmCallback, null, multiSession);
    }


    private void releaseMediaDrm() {
        if (mediaDrm != null) {
            mediaDrm.release();
            mediaDrm = null;
        }
    }


    private PlaybackPreparer mPlaybackPreparer = () -> {
        Log.d(TAG, "playback: ");
        player.retry();
    };

    private MediaSource buildMediaSource(Uri uri) {
        return buildMediaSource(uri, null);
    }

    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
        DownloadRequest downloadRequest = DownloadUtils.getInstance().getDownloadTracker().getDownloadRequest(uri);
        if (downloadRequest != null) {
            return DownloadHelper.createMediaSource(downloadRequest, dataSourceFactory);
        }
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    private void releaseAdsLoader() {
        if (adsLoader != null) {
            adsLoader.release();
            adsLoader = null;
            loadedAdTagUri = null;
            Objects.requireNonNull(playerView.getOverlayFrameLayout()).removeAllViews();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters();
            updateStartPosition();
            debugViewHelper.stop();
            debugViewHelper = null;
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


    private @Nullable MediaSource createAdsMediaSource(MediaSource mediaSource, Uri adTagUri) {
        // Load the extension source using reflection so the demo app doesn't have to depend on it.
        // The ads loader is reused for multiple playbacks, so that ad playback can resume.
        try {
            Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");
            if (adsLoader == null) {
                // Full class names used so the LINT.IfChange rule triggers should any of the classes move.
                // LINT.IfChange
                Constructor<? extends AdsLoader> loaderConstructor =
                        loaderClass
                                .asSubclass(AdsLoader.class)
                                .getConstructor(android.content.Context.class, android.net.Uri.class);
                // LINT.ThenChange(../../../../../../../../proguard-rules.txt)
                adsLoader = loaderConstructor.newInstance(this, adTagUri);
            }
            adsLoader.setPlayer(player);
            AdsMediaSource.MediaSourceFactory adMediaSourceFactory =
                    new AdsMediaSource.MediaSourceFactory() {
                        @Override
                        public MediaSource createMediaSource(Uri uri) {
                            return PlayActivity.this.buildMediaSource(uri);
                        }

                        @Override
                        public int[] getSupportedTypes() {
                            return new int[] {C.TYPE_DASH, C.TYPE_SS, C.TYPE_HLS, C.TYPE_OTHER};
                        }
                    };
            return new AdsMediaSource(mediaSource, adMediaSourceFactory, adsLoader, playerView);
        } catch (ClassNotFoundException e) {
            // IMA extension not loaded.
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private class PlayerEventListener implements Player.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_ENDED) {
                showControls();
            }
            updateButtonVisibility();
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition();
                initializePlayer();
            } else {
                updateButtonVisibility();
                showControls();
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            updateButtonVisibility();
            if (trackGroups != lastSeenTrackGroupArray) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        Toasty.showError(getString(R.string.error_unsupported_video));
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        Toasty.showError(getString(R.string.error_unsupported_audio));
                    }
                }
                lastSeenTrackGroupArray = trackGroups;
            }
        }
    }


    private void updateButtonVisibility() {
        selectTracksButton.setEnabled(player != null && TrackSelectionDialog.willHaveContent(trackSelector));
    }

    private void showControls() {
        debugRootView.setVisibility(View.VISIBLE);
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
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



    private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {

        @Override
        public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
            String errorString = getString(R.string.error_generic);
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = e.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                            (MediaCodecRenderer.DecoderInitializationException) cause;
                    if (decoderInitializationException.decoderName == null) {
                        if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                            errorString = getString(R.string.error_querying_decoders);
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            errorString =
                                    getString(
                                            R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                        } else {
                            errorString =
                                    getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                        }
                    } else {
                        errorString =
                                getString(
                                        R.string.error_instantiating_decoder,
                                        decoderInitializationException.decoderName);
                    }
                }
            }
            return Pair.create(0, errorString);
        }
    }

}
