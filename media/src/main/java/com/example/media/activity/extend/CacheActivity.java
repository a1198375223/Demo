package com.example.media.activity.extend;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.view.CacheVideoView;
import com.example.media.view.StandardVideoController;

public class CacheActivity extends AppCompatActivity {

    private CacheVideoView mCacheVideoView;

    private static final String URL = "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        mCacheVideoView = findViewById(R.id.player);
        mCacheVideoView.setUrl(URL);
        mCacheVideoView.setVideoController(new StandardVideoController(this));
        mCacheVideoView.setLifecycleOwner(this);

        //删除url对应默认缓存文件
//        VideoCacheManager.clearDefaultCache(this, URL);
        //清除缓存文件中的所有缓存
//        VideoCacheManager.clearAllCache(this);
    }

    @Override
    public void onBackPressed() {
        if (!mCacheVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
