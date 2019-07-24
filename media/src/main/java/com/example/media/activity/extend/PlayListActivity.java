package com.example.media.activity.extend;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.bean.VideoBean;
import com.example.media.common.OnVideoViewStateChangeListener;
import com.example.media.utils.DataUtil;
import com.example.media.utils.PlayerUtils;
import com.example.media.view.StandardVideoController;
import com.example.media.view.VideoView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayListActivity extends AppCompatActivity {

    private VideoView mVideoView;

    private List<VideoBean> data = DataUtil.getVideoList();

    private StandardVideoController mStandardVideoController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoView = new VideoView(this);
        setContentView(mVideoView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PlayerUtils.dp2px(this, 240)));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_play_list);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mStandardVideoController = new StandardVideoController(this);

        //加载第一条数据
        VideoBean videoBean = data.get(0);
        mVideoView.setUrl(videoBean.getUrl());
        mStandardVideoController.setTitle(videoBean.getTitle());
        mVideoView.setVideoController(mStandardVideoController);

        //监听播放结束
        mVideoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            private int mCurrentVideoPosition;
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                    if (data != null) {
                        mCurrentVideoPosition++;
                        if (mCurrentVideoPosition >= data.size()) return;
                        mVideoView.release();
                        //重新设置数据
                        VideoBean videoBean = data.get(mCurrentVideoPosition);
                        mVideoView.setUrl(videoBean.getUrl());
                        mStandardVideoController.setTitle(videoBean.getTitle());
                        mVideoView.setVideoController(mStandardVideoController);
                        //开始播放
                        mVideoView.start();
                    }
                }
            }
        });

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
