package com.example.media.activity.extend;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.view.FullScreenController;
import com.example.media.view.FullScreenVideoView;
import com.example.media.view.VideoView;

public class FullScreenActivity extends AppCompatActivity {

    private FullScreenVideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_fullscreen_directly);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVideoView = new FullScreenVideoView(this);
        setContentView(mVideoView);
        mVideoView.setUrl("http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4");
        FullScreenController controller = new FullScreenController(this);
        controller.setTitle("这是一个标题");
        mVideoView.setVideoController(controller);
        mVideoView.setScreenScale(VideoView.SCREEN_SCALE_16_9);
        mVideoView.setLifecycleOwner(this);
    }

    @Override
    public void onBackPressed() {
        if (!mVideoView.onBackPressed()){
            super.onBackPressed();
        }
    }
}
