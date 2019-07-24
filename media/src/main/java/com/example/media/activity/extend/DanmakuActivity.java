package com.example.media.activity.extend;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.common.OnVideoViewStateChangeListener;
import com.example.media.view.StandardVideoController;
import com.example.media.view.VideoView;
import com.example.media.view.danmu.DanmukuVideoView;

import org.jetbrains.annotations.NotNull;

public class DanmakuActivity extends AppCompatActivity {

    private DanmukuVideoView danmukuVideoView;
    private static final String URL_VOD = "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4";
    //    private static final String URL_VOD = "http://uploads.cutv.com:8088/video/data/201703/10/encode_file/515b6a95601ba6b39620358f2677a17358c2472411d53.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danmaku_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_danmu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        danmukuVideoView = findViewById(R.id.player);
        StandardVideoController standardVideoController = new StandardVideoController(this);
        standardVideoController.setTitle("网易公开课-如何掌控你的自由时间");
        danmukuVideoView.setVideoController(standardVideoController);
        danmukuVideoView.setUrl(URL_VOD);
        danmukuVideoView.setLifecycleOwner(this);

        danmukuVideoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_PREPARED) {
                    simulateDanmu();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }


    @Override
    public void onBackPressed() {
        if (!danmukuVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public void showDanMu(View view) {
        danmukuVideoView.showDanMu();
    }

    public void hideDanMu(View view) {
        danmukuVideoView.hideDanMu();
    }

    public void addDanmakuWithDrawable(View view) {
        danmukuVideoView.addDanmakuWithDrawable();
    }

    public void addDanmaku(View view) {
        danmukuVideoView.addDanmaku("这是一条文字弹幕~", true);
    }


    private Handler mHandler = new Handler();

    /**
     * 模拟弹幕
     */
    private void simulateDanmu() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                danmukuVideoView.addDanmaku("666666", false);
                mHandler.postDelayed(this, 100);
            }
        });
    }
}
