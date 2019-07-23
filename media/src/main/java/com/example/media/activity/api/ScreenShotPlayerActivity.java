package com.example.media.activity.api;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.view.StandardVideoController;
import com.example.media.view.VideoView;

import org.jetbrains.annotations.NotNull;

public class ScreenShotPlayerActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private ImageView mScreenShot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_screen_shot);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mVideoView = findViewById(R.id.player);
        mScreenShot = findViewById(R.id.iv_screen_shot);
        StandardVideoController controller = new StandardVideoController(this);
        mVideoView.setUrl("http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4");
        mVideoView.setVideoController(controller);
        mVideoView.setLifecycleOwner(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (!mVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public void doScreenShot(View view) {
        Bitmap bitmap = mVideoView.doScreenShot();
        mScreenShot.setImageBitmap(bitmap);
    }
}

