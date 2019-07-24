package com.example.media.activity.pip;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.media.R;
import com.example.media.utils.PIPManager;
import com.example.media.view.StandardVideoController;
import com.example.media.view.VideoView;
import com.yanzhenjie.permission.AndPermission;

import org.jetbrains.annotations.NotNull;

public class PIPActivity extends AppCompatActivity {
    private static final String TAG = "PIPActivity";
    private PIPManager mPIPManager;
    //    private static final String URL = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
    private static final String URL = "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4";
//    private static final String URL = "http://youku163.zuida-bofang.com/20190126/26805_c313a74d/index.m3u8";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pip);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_pip_demo);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        FrameLayout playerContainer = findViewById(R.id.player_container);
        mPIPManager = PIPManager.getInstance();
        VideoView videoView = mPIPManager.getVideoView();
        StandardVideoController controller = new StandardVideoController(this);
        videoView.setVideoController(controller);
        if (mPIPManager.isStartFloatWindow()) {
            mPIPManager.stopFloatWindow();
            controller.setPlayerState(videoView.getCurrentPlayerState());
            controller.setPlayState(videoView.getCurrentPlayState());
        } else {
            mPIPManager.setActClass(PIPActivity.class);
//        int widthPixels = getResources().getDisplayMetrics().widthPixels;
//        videoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 4 * 3));
            Glide.with(this)
                    .asBitmap()
//                    .animate(R.anim.dkplayer_anim_alpha_in)
                    .placeholder(android.R.color.darker_gray)
                    .load("http://sh.people.com.cn/NMediaFile/2016/0112/LOCAL201601121344000138197365721.jpg")
                    .into(controller.getThumb());
            videoView.setUrl(URL);
            controller.setTitle("香港卫视");
        }
        playerContainer.addView(videoView);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        mPIPManager.pause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        mPIPManager.resume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        mPIPManager.reset();
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        if (mPIPManager.onBackPress()) return;
        super.onBackPressed();
    }

    public void startFloatWindow(View view) {
        Log.d(TAG, "startFloatWindow: ");
        // 请求悬浮窗权限
        AndPermission
                .with(this)
                .overlay()
                .onGranted(data -> {
                    Log.d(TAG, "startFloatWindow: granted");
                    mPIPManager.startFloatWindow();
                    finish();
                })
                .onDenied(data -> {

                })
                .start();
    }
}

