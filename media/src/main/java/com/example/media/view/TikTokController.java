package com.example.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.media.R;

public class TikTokController extends BaseVideoController {
    private static final String TAG = "TikTokController";

    private ImageView thumb;
    public TikTokController(@NonNull Context context) {
        super(context);
    }

    public TikTokController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TikTokController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_tiktok_controller;
    }

    @Override
    protected void init() {
        super.init();
        thumb = mControllerView.findViewById(R.id.iv_thumb);
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);

        switch (playState) {
            case VideoView.STATE_IDLE:
                Log.e(TAG, "STATE_IDLE");
                thumb.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PLAYING:
                Log.e(TAG, "STATE_PLAYING");
                thumb.setVisibility(GONE);
                break;
            case VideoView.STATE_PREPARED:
                Log.e(TAG, "STATE_PREPARED");
                break;
        }
    }

    public ImageView getThumb() {
        return thumb;
    }
}

