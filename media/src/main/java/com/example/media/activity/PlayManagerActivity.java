package com.example.media.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.callback.PlaybackListenerWrapper;
import com.example.media.fragment.TrackSelectionDialog;
import com.example.media.presenter.GalileoPlayerManager;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class PlayManagerActivity extends AppCompatActivity {
    private static final String TAG = "PlayManagerActivity";
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

    // 视频播放管理器
    private GalileoPlayerManager manager;
    private boolean isShowingTrackSelectionDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 获取环绕模式
        String sphericalStereoMode = getIntent().getStringExtra(GalileoPlayerManager.SPHERICAL_STEREO_MODE_EXTRA);
        if (sphericalStereoMode != null) {
            setTheme(R.style.PlayerTheme_Spherical);
        }
        // 设置主题需要在super.onCreate(savedInstanceState)之前
        super.onCreate(savedInstanceState);

        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        setContentView(R.layout.activity_play);


        debugRootView = findViewById(R.id.controls_root);
        debugTextView = findViewById(R.id.debug_text_view);
        selectTracksButton = findViewById(R.id.select_tracks_button);
        playerView = findViewById(R.id.player_view);
        playerView.setControllerVisibilityListener(visibility -> {
            Log.d(TAG, "onVisibilityChange: ");
            debugRootView.setVisibility(visibility);
        });

        selectTracksButton.setOnClickListener(view -> {
            if (!isShowingTrackSelectionDialog && manager.willDialogHaveContent()) {
                isShowingTrackSelectionDialog = true;
                TrackSelectionDialog trackSelectionDialog =
                        TrackSelectionDialog.createForTrackSelector(
                                manager.getSelector(),
                                /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
                trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);
            }
        });

        manager = new GalileoPlayerManager(this, this, playerView, sphericalStereoMode);
        manager.setPlaybackListener(new PlaybackListenerWrapper(){
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == Player.STATE_ENDED) {
                    showControls();
                }
                updateButtonVisibility();
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                super.onPlayerError(error);

                if (!GalileoPlayerManager.isBehindLiveWindow(error)) {
                    updateButtonVisibility();
                    showControls();
                }
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                updateButtonVisibility();
                super.onTracksChanged(trackGroups, trackSelections);
            }
        });
        manager.setDebugView(debugTextView);
        manager.setIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        manager.onNewIntent(intent);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        manager.onSaveInstanceState(outState);
    }



    private void updateButtonVisibility() {
        selectTracksButton.setEnabled(manager.willDialogHaveContent());
    }

    private void showControls() {
        debugRootView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        manager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
