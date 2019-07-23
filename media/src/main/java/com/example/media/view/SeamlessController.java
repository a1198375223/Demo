package com.example.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.media.R;

public class SeamlessController extends BaseVideoController {

    private ImageView mMute;

    public SeamlessController(@NonNull Context context) {
        super(context);
    }

    public SeamlessController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SeamlessController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_seamless_controller;
    }

    @Override
    protected void init() {
        super.init();
        setClickable(false);
        setFocusable(false);
        mMute = mControllerView.findViewById(R.id.iv_mute);
        mMute.setOnClickListener(v -> toggleMute());
    }

    private void toggleMute() {
        if (mMediaPlayer.isMute()) {
            mMediaPlayer.setMute(false);
            mMute.setSelected(true);
        } else {
            mMediaPlayer.setMute(true);
            mMute.setSelected(false);
        }
    }

    public void resetController() {
        mMute.setSelected(false);
    }
}
