package com.example.media.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.media.R;
import com.example.media.utils.PlayerUtils;

public class PadController extends StandardVideoController {
    public PadController(@NonNull Context context) {
        super(context);
    }

    public PadController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PadController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void doStartStopFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) return;
        if (mMediaPlayer.isFullScreen()) {
            mMediaPlayer.stopFullScreen();
        } else {
            mMediaPlayer.startFullScreen();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mIsLocked) {
            show();
            Toast.makeText(getContext(), R.string.dkplayer_lock_tip, Toast.LENGTH_SHORT).show();
            return true;
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) return false;

        if (mMediaPlayer.isFullScreen()) {
            mMediaPlayer.stopFullScreen();
            return true;
        }
        return false;
    }
}
