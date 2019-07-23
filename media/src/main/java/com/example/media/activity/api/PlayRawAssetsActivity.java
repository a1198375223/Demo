package com.example.media.activity.api;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.view.StandardVideoController;
import com.example.media.view.VideoView;

import org.jetbrains.annotations.NotNull;


public class PlayRawAssetsActivity extends AppCompatActivity {

    private VideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_raw_assets);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_raw_or_assets);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mVideoView = findViewById(R.id.player);
        StandardVideoController controller = new StandardVideoController(this);
        mVideoView.setVideoController(controller);
        mVideoView.setLifecycleOwner(this);
    }


    public void onButtonClick(View view) {
        mVideoView.release();
        int i = view.getId();
        if (i == R.id.btn_raw) {//IjkPlayer,MediaPlayer
            String url = "android.resource://" + getPackageName() + "/" + R.raw.movie;

            //ExoPlayer请使用如下方式
//                DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.movie));
//                RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(this);
//                try {
//                    rawResourceDataSource.open(dataSpec);
//                } catch (RawResourceDataSource.RawResourceDataSourceException e) {
//                    e.printStackTrace();
//                }
//                String url = rawResourceDataSource.getUri().toString();


            mVideoView.setUrl(url);
        } else if (i == R.id.btn_assets) {//IjkPlayer,MediaPlayer
            mVideoView.createAssetFileDescriptor("test.mp4");

            //ExoPlayer请使用如下方式
//                mVideoView.setUrl("file:///android_asset/" + "test.mp4");
        }

        mVideoView.start();
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
}
