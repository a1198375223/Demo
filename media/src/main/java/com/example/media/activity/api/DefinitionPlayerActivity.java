package com.example.media.activity.api;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.example.media.R;
import com.example.media.common.AbstractPlayer;
import com.example.media.factory.AbsPlayerFactory;
import com.example.media.ijk.IjkPlayerManager;
import com.example.media.view.DefinitionController;
import com.example.media.view.DefinitionVideoView;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class DefinitionPlayerActivity extends AppCompatActivity {

    private DefinitionVideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_definition);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVideoView = findViewById(R.id.player);

        DefinitionController controller = new DefinitionController(this);
        controller.setTitle("韩雪：积极的悲观主义者");
//        mVideoView.setCustomMediaPlayer(new IjkPlayer(this) {
//            @Override
//            public void setOptions() {
//                //精准seek
//                mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
//            }
//        });

        mVideoView.setPlayerFactory(new AbsPlayerFactory() {
            @Override
            public AbstractPlayer createPlayer(LifecycleOwner owner) {
                return new IjkPlayerManager(owner) {
                    @Override
                    public void setOptions() {
                        //精准seek
                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
                    }
                };
            }
        });

        LinkedHashMap<String, String> videos = new LinkedHashMap<>();
        videos.put("标清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_sd.flv");
        videos.put("高清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_hd.flv");
        videos.put("超清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_shd.flv");
        mVideoView.setDefinitionVideos(videos);
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
}
