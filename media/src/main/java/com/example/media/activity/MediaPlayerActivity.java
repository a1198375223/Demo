package com.example.media.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.commonlibrary.utils.UriUtils;
import com.example.media.R;
import com.example.media.callback.IMediaPlayer;
import com.example.media.callback.PlaybackInfoListener;
import com.example.media.presenter.MediaPlayerManager;

import java.util.Locale;

public class MediaPlayerActivity extends AppCompatActivity {
    private static final String TAG = "MediaPlayerActivity";
    private IMediaPlayer mMediaPlayer;
    private TextView mTextDebug;
    private SeekBar mSeekbar;
    private ScrollView mScrollContainer;
    private boolean mUserIsSeeking = false;

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
            mSeekbar.setMax((int) duration);
            Log.d(TAG, String.format(Locale.CHINA, "setPlaybackDuration: setMax(%d)", duration));
        }

        @Override
        public void onPositionChanged(int position) {
//            Log.d(TAG, "position change: " + position);
            Log.d(TAG, "position change current thread main? " + (Looper.myLooper() == Looper.getMainLooper()));
            if (!mUserIsSeeking) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mSeekbar.setProgress(position, true);
                } else {
                    mSeekbar.setProgress(position);
                }
                Log.d(TAG, String.format(Locale.CHINA, "setPlaybackPosition: setProgress(%d)", position));
            }
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
        setContentView(R.layout.activity_media_player);

        mTextDebug = findViewById(R.id.text_debug);
        mSeekbar = findViewById(R.id.seekbar_audio);
        mScrollContainer = findViewById(R.id.scroll_container);

        findViewById(R.id.button_play).setOnClickListener(view -> mMediaPlayer.play());

        findViewById(R.id.button_pause).setOnClickListener(view -> mMediaPlayer.pause());

        findViewById(R.id.button_reset).setOnClickListener(view -> mMediaPlayer.reset());

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int userSelectedPosition = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged: progress: " + progress + " fromUser: " + fromUser);
                if (fromUser) {
                    userSelectedPosition = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch: ");
                mUserIsSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mUserIsSeeking = false;
                mMediaPlayer.seekTo(userSelectedPosition);
            }
        });

        MediaPlayerManager manager = new MediaPlayerManager(this);
        Log.d(TAG, "Create MediaPlayer");
        manager.setPlaybackInfoListener(mListener);
        mMediaPlayer = manager;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "path=" + UriUtils.createUriForResource(R.raw.jazz_in_paris).toString());
        mMediaPlayer.setDataSource(UriUtils.createUriForResource(R.raw.jazz_in_paris).toString());
        mMediaPlayer.setLooping(true);
        Log.d(TAG, "onStart: create MediaPlayer");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isChangingConfigurations() && mMediaPlayer.isPlaying()) {
            Log.d(TAG, "onStop: don't release MediaPlayer as screen is rotating & playing.");
        } else {
            mMediaPlayer.release();
            Log.d(TAG, "onStop: release MediaPlayer");
        }
    }
}
