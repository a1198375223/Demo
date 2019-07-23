package com.example.media.activity.api;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.example.media.R;
import com.example.media.common.AbstractPlayer;
import com.example.media.factory.AbsPlayerFactory;
import com.example.media.ijk.IjkPlayerManager;
import com.example.media.view.StandardVideoController;
import com.example.media.view.VideoView;

import org.jetbrains.annotations.NotNull;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

// 仅IjkPlayer支持这个功能, 所以自定义了一个工厂来创建IjkPlayer, 并通过setConfig()来配置config
public class CustomMediaPlayerActivity extends AppCompatActivity {

    private VideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_media_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_rtsp_concat);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mVideoView = findViewById(R.id.player);
        StandardVideoController controller = new StandardVideoController(this);
        mVideoView.setVideoController(controller);
//        mVideoView.setCustomMediaPlayer(new IjkPlayer(this) {
//            @Override
//            public void setOptions() {
//                super.setOptions();
//                //支持concat
//                mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "safe", 0);
//                mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist",
//                        "rtmp,concat,ffconcat,file,subfile,http,https,tls,rtp,tcp,udp,crypto,rtsp");
//                //使用tcp方式拉取rtsp流，默认是通过udp方式
//                mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
//            }
//        });



        mVideoView.setPlayerFactory(new MyPlayerFactory());
        mVideoView.setLifecycleOwner(this);
    }




    public void onButtonClick(View view) {
        mVideoView.release();
        int i = view.getId();
        if (i == R.id.concat) {
            //测试concat,将项目根目录的other文件夹中的test.ffconcat文件复制到sd卡根目录测试
//              String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//              String url = "file://" + absolutePath + File.separator + "test.ffconcat";
            String concatUrl = "http://139.180.165.226/test.ffconcat";
            mVideoView.setUrl(concatUrl);
        } else if (i == R.id.rtsp) {
            String rtspUrl = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov";
            mVideoView.setUrl(rtspUrl);
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

    class MyPlayerFactory extends AbsPlayerFactory {

        @Override
        public AbstractPlayer createPlayer(LifecycleOwner owner) {
            return new IjkPlayerManager(owner) {
                @Override
                public void setOptions() {
                    super.setOptions();
                    //支持concat
                    mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "safe", 0);
                    mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist",
                            "rtmp,concat,ffconcat,file,subfile,http,https,tls,rtp,tcp,udp,crypto,rtsp");
                    //使用tcp方式拉取rtsp流，默认是通过udp方式
                    mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
                }
            };
        }
    }
}

