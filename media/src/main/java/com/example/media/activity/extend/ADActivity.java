package com.example.media.activity.extend;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.common.OnVideoViewStateChangeListener;
import com.example.media.view.AdController;
import com.example.media.view.CacheVideoView;
import com.example.media.view.ControllerListener;
import com.example.media.view.StandardVideoController;
import com.example.media.view.VideoView;

import org.jetbrains.annotations.NotNull;

public class ADActivity extends AppCompatActivity {

    private CacheVideoView mVideoView;
    private static final String URL_VOD = "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4";
    //    private static final String URL_VOD = "http://baobab.wdjcdn.com/14564977406580.mp4";
    //    private static final String URL_VOD = "http://uploads.cutv.com:8088/video/data/201703/10/encode_file/515b6a95601ba6b39620358f2677a17358c2472411d53.mp4";
    private static final String URL_AD = "https://gslb.miaopai.com/stream/IR3oMYDhrON5huCmf7sHCfnU5YKEkgO2.mp4";

    private StandardVideoController mStandardVideoController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_ad);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mVideoView = findViewById(R.id.player);

        AdController adController = new AdController(this);
        adController.setControllerListener(new ControllerListener() {
            @Override
            public void onAdClick() {
                Toast.makeText(ADActivity.this, "广告点击跳转", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSkipAd() {
                playVideo();
            }
        });

        mVideoView.setUrl(URL_AD);
        mVideoView.setCacheEnabled(true);
        mVideoView.setVideoController(adController);

        //监听播放结束
        mVideoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                    playVideo();
                }
            }
        });

        mVideoView.setLifecycleOwner(this);
    }

    /**
     * 播放正片
     */
    private void playVideo() {
        mVideoView.release();
        //重新设置数据
        mVideoView.setUrl(URL_VOD);
        mVideoView.setCacheEnabled(false);
        if (mStandardVideoController == null) {
            mStandardVideoController = new StandardVideoController(ADActivity.this);
        }
        mStandardVideoController.setTitle("正片标题");
        //更换控制器
        mVideoView.setVideoController(mStandardVideoController);
        //开始播放
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

