package com.example.media.activity.extend;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.exo.ExoMediaPlayerFactory;
import com.example.media.factory.AbsPlayerFactory;
import com.example.media.factory.IjkPlayerFactory;
import com.example.media.media_player.AndroidMediaPlayerFactory;
import com.example.media.view.StandardVideoController;
import com.example.media.view.VideoView;

import org.jetbrains.annotations.NotNull;

public class SwitchPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView mVideoView;
    private StandardVideoController mController;
    private static final String URL = "http://cdnxdc.tanzi88.com/XDC/dvideo/2017/12/29/fc821f9a8673d2994f9c2cb9b27233a3.mp4";
//    private static final String URL = "http://zaixian.jingpin88.com/20180430/IGBXbalb/index.m3u8";
//    private static final String URL = "rtmp://live.hkstv.hk.lxdns.com/live/hks";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_switch_player);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mVideoView = findViewById(R.id.player);
        findViewById(R.id.btn_ijk).setOnClickListener(this);
        findViewById(R.id.btn_media).setOnClickListener(this);
        findViewById(R.id.btn_exo).setOnClickListener(this);

        mController = new StandardVideoController(this);
        mVideoView.setUrl(URL);
        mVideoView.setVideoController(mController);
        mVideoView.setLifecycleOwner(this);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
//        AbstractPlayer player = null;
//        switch (id) {
//            case R.id.btn_ijk:
//                player = new IjkPlayer(this);
//                break;
//            case R.id.btn_media:
//                player = new AndroidMediaPlayer(this);
//                break;
//            case R.id.btn_exo:
//                player = new ExoMediaPlayer(this);
//                break;
//        }
//
//        mVideoView.release();
//        mVideoView.setUrl(URL);
//        mVideoView.setVideoController(mController);
//        mVideoView.setCustomMediaPlayer(player);
//        mVideoView.start();

        AbsPlayerFactory factory = null;
        if (id == R.id.btn_ijk) {
            factory = IjkPlayerFactory.create();
        } else if (id == R.id.btn_media) {
            factory = AndroidMediaPlayerFactory.create();
        } else if (id == R.id.btn_exo) {
            factory = ExoMediaPlayerFactory.create();
        }

        mVideoView.release();
        mVideoView.setUrl(URL);
        mVideoView.setVideoController(mController);
        mVideoView.setPlayerFactory(factory);
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
