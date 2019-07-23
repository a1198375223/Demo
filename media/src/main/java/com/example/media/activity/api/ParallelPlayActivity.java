package com.example.media.activity.api;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.common.VideoViewManager;
import com.example.media.view.StandardVideoController;
import com.example.media.view.VideoView;

public class ParallelPlayActivity extends AppCompatActivity {

    private static final String VOD_URL = "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallel_play);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_multi_player);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        VideoView player1 = findViewById(R.id.player_1);
        player1.setUrl(VOD_URL);

        //这两项必须设置
        player1.setEnableAudioFocus(false);
        player1.setEnableParallelPlay(true);
        StandardVideoController controller1 = new StandardVideoController(this);
        player1.setVideoController(controller1);
        player1.setLifecycleOwner(this);

        VideoView player2 = findViewById(R.id.player_2);
        player2.setUrl(VOD_URL);
        //这两项必须设置
        player2.setEnableAudioFocus(false);
        player2.setEnableParallelPlay(true);
        StandardVideoController controller2 = new StandardVideoController(this);
        player2.setVideoController(controller2);
        player2.setLifecycleOwner(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoViewManager.getInstance().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoViewManager.getInstance().resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoViewManager.getInstance().release();
    }

    @Override
    public void onBackPressed() {
        if (VideoViewManager.getInstance().onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
