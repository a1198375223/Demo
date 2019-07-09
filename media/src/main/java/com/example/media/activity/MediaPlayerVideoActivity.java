package com.example.media.activity;

import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.commonlibrary.utils.UriUtils;
import com.example.media.R;
import com.example.media.callback.PlaybackInfoListener;
import com.example.media.presenter.MediaPlayerManager;

import java.util.Locale;

public class MediaPlayerVideoActivity extends AppCompatActivity {
    private static final String TAG = "MediaPlayerVideoActivit";
    private TextureView mTextureView;
    private MediaPlayerManager mPlayerManager;
    private TextView mTextDebug;
    private ScrollView mScrollContainer;

    private PlaybackInfoListener mListener = new PlaybackInfoListener() {
        @Override
        public void onLogUpdated(String formattedMessage) {
            mTextDebug.append(formattedMessage);
            mTextDebug.append("\n");

            // 滑动到最底部
            mScrollContainer.post(() -> mScrollContainer.fullScroll(ScrollView.FOCUS_DOWN));
        }

        @Override
        public void onDurationChanged(long duration) {
            Log.d(TAG, String.format(Locale.CHINA, "setPlaybackDuration: setMax(%d)", duration));
        }

        @Override
        public void onPositionChanged(int position) {
//            Log.d(TAG, "position change: " + position);
//            Log.d(TAG, "position change current thread main? " + (Looper.myLooper() == Looper.getMainLooper()));
            Log.d(TAG, String.format(Locale.CHINA, "setPlaybackPosition: setProgress(%d)", position));
        }

        @Override
        public void onStateChanged(int state) {
            String stateToString = PlaybackInfoListener.convertStateToString(state);
            onLogUpdated(String.format(Locale.CHINA, "onStateChanged(%s)", stateToString));
            Log.d(TAG, String.format(Locale.CHINA, "onStateChanged(%s)", stateToString));
        }

        @Override
        public void onPlaybackCompleted() {
            Log.d(TAG, "onPlaybackCompleted");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player_video);

        mTextureView = findViewById(R.id.texture);
        mTextDebug = findViewById(R.id.text_debug);
        mScrollContainer = findViewById(R.id.scroll_container);

        mPlayerManager = new MediaPlayerManager(this);
        mPlayerManager.setPlaybackInfoListener(mListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTextureView.isAvailable()) {
            openVideo();
        } else {
            mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                    openVideo();
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isChangingConfigurations() && mPlayerManager.isPlaying()) {
            Log.d(TAG, "onStop: don't release MediaPlayer as screen is rotating & playing.");
        } else {
            mPlayerManager.release();
            Log.d(TAG, "onStop: release MediaPlayer");
        }
    }


    public void openVideo() {
        mPlayerManager.setSurface(new Surface(mTextureView.getSurfaceTexture()));
        mPlayerManager.setDataSource(UriUtils.createUriForResource(R.raw.vid_bigbuckbunny).toString());
        mPlayerManager.play();
    }
}
