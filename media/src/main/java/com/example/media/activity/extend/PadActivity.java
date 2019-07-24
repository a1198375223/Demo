package com.example.media.activity.extend;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.view.PadController;
import com.example.media.view.VideoView;

public class PadActivity extends AppCompatActivity {

    private VideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pad);
        mVideoView = findViewById(R.id.video_view);

        mVideoView.setUrl("http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4");

        mVideoView.setVideoController(new PadController(this));

        mVideoView.setLifecycleOwner(this);
    }

    @Override
    public void onBackPressed() {
        if (!mVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
