package com.example.media.gsy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.media.R;
import com.example.media.R2;
import com.example.media.gsy.activity.simple.SimpleActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GsyMainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1110;


    Unbinder unbinder;

    final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsy_main);
        unbinder = ButterKnife.bind(this);

        boolean hadPermission = hasSelfPermissions();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hadPermission) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, REQUEST_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    /**
     * 检查权限
     * @return 是否拥权限
     */
    private boolean hasSelfPermissions() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            boolean sdPermissionResult = true;
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    sdPermissionResult = false;
                    break;
                }
            }
            if (!sdPermissionResult) {
                Toast.makeText(this, "没获取到sd卡权限，无法播放本地视频哦", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @OnClick({R2.id.open_btn, R2.id.list_btn, R2.id.list_btn_2, R2.id.list_detail, R2.id.clear_cache, R2.id.recycler, R2.id.recycler_2, R2.id.list_detail_list, R2.id.web_detail, R2.id.danmaku_video, R2.id.fragment_video,
            R2.id.more_type, R2.id.input_type, R2.id.open_btn_empty, R2.id.open_control, R2.id.open_filter, R2.id.open_btn_pick, R2.id.open_btn_auto, R2.id.open_scroll, R2.id.open_window, R2.id.open_btn_ad,
            R2.id.open_btn_multi, R2.id.open_btn_ad2, R2.id.open_list_ad, R2.id.open_custom_exo, R2.id.open_simple, R2.id.open_switch, R2.id.media_codec})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.open_simple) {//简单的播放
            Toast.makeText(this, "click open simple.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SimpleActivity.class));
        } else if (i == R.id.open_btn) {//直接一个页面播放的
//            JumpUtils.goToVideoPlayer(this, openBtn);
        } else if (i == R.id.list_btn) {//普通列表播放，只支持全屏，但是不支持屏幕重力旋转，滑动后不持有
//            JumpUtils.goToVideoPlayer(this);
        } else if (i == R.id.list_btn_2) {//支持全屏重力旋转的列表播放，滑动后不会被销毁
//            JumpUtils.goToVideoPlayer2(this);
        } else if (i == R.id.recycler) {//recycler的demo
//            JumpUtils.goToVideoRecyclerPlayer(this);
        } else if (i == R.id.recycler_2) {//recycler的demo
//            JumpUtils.goToVideoRecyclerPlayer2(this);
        } else if (i == R.id.list_detail) {//支持旋转全屏的详情模式
//            JumpUtils.goToDetailPlayer(this);
        } else if (i == R.id.list_detail_list) {//播放一个连续列表
//            JumpUtils.goToDetailListPlayer(this);
        } else if (i == R.id.web_detail) {//正常播放，带preview
//            JumpUtils.gotoWebDetail(this);
        } else if (i == R.id.danmaku_video) {//播放一个弹幕视频
//            JumpUtils.gotoDanmaku(this);
        } else if (i == R.id.fragment_video) {//播放一个弹幕视频
//            JumpUtils.gotoFragment(this);
        } else if (i == R.id.more_type) {//跳到多类型详情播放器，比如切换分辨率，旋转等
//            JumpUtils.gotoMoreType(this);
        } else if (i == R.id.input_type) {
//            JumpUtils.gotoInput(this);
        } else if (i == R.id.open_btn_empty) {
//            JumpUtils.goToPlayEmptyControlActivity(this, openBtn2);
        } else if (i == R.id.open_control) {
//            JumpUtils.gotoControl(this);
        } else if (i == R.id.open_filter) {
//            JumpUtils.gotoFilter(this);
        } else if (i == R.id.open_btn_pick) {//无缝切换
//            JumpUtils.goToVideoPickPlayer(this, openBtn);
        } else if (i == R.id.open_btn_auto) {//列表自动播放
//            JumpUtils.goToAutoVideoPlayer(this);
        } else if (i == R.id.open_scroll) {//列表自动播放
//            JumpUtils.goToScrollDetailPlayer(this);
        } else if (i == R.id.open_window) {//多窗体下的悬浮窗
//            JumpUtils.goToScrollWindow(this);
        } else if (i == R.id.open_btn_ad) {//广告
//            JumpUtils.goToVideoADPlayer(this);
        } else if (i == R.id.open_btn_multi) {//多个同时播放
//            JumpUtils.goToMultiVideoPlayer(this);
        } else if (i == R.id.open_btn_ad2) {//多个同时播放
//            JumpUtils.goToVideoADPlayer2(this);
        } else if (i == R.id.open_list_ad) {//多个同时播放
//            JumpUtils.goToADListVideoPlayer(this);
        } else if (i == R.id.open_custom_exo) {//多个同时播放
//            JumpUtils.goToDetailExoListPlayer(this);
        } else if (i == R.id.open_switch) {
//            JumpUtils.goToSwitch(this);
        } else if (i == R.id.media_codec) {
//            JumpUtils.goMediaCodec(this);
        } else if (i == R.id.clear_cache) {//清理缓存
//            GSYVideoManager.instance().clearAllDefaultCache(MainActivity.this);
            //String url = "https://res.exexm.com/cw_145225549855002";
            //GSYVideoManager.clearDefaultCache(MainActivity.this, url);
        }
    }
}
