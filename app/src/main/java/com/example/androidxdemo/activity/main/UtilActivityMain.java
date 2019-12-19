package com.example.androidxdemo.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidxdemo.R;
import com.example.androidxdemo.activity.anim.AnimationActivity;
import com.example.androidxdemo.activity.annotation.AnnotationActivity;
import com.example.androidxdemo.activity.bar.BarActivity;
import com.example.androidxdemo.activity.bubble.FlowActivity;
import com.example.androidxdemo.activity.camera.CameraActivity;
import com.example.androidxdemo.activity.image.ImageActivity;
import com.example.androidxdemo.activity.launch.LaunchModeActivity;
import com.example.androidxdemo.activity.main.adapter.UtilRecyclerViewAdapter;
import com.example.androidxdemo.activity.receiver.ReceiverActivity;
import com.example.androidxdemo.activity.service.ServiceActivity;
import com.example.androidxdemo.activity.share.ShareActivity;
import com.example.androidxdemo.activity.test.TestActivity;
import com.example.androidxdemo.activity.util.UtilActivity;
import com.example.androidxdemo.activity.view.ViewActivity;
import com.example.dagger2.DaggerActivity;
import com.example.dialog.DialogActivity;
import com.example.media.VideoActivity;
import com.example.opengles.OpenGLESActivity;
import com.example.room.RoomActivity;
import com.wali.live.SDKMainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UtilActivityMain extends AppCompatActivity {
    private static final String TAG = "UtilActivityMain";
    public static final int ITEM_VIEW = 0;
    public static final int ITEM_IMAGE = 1;
    public static final int ITEM_VIDEO = 2;
    public static final int ITEM_MUSIC = 3;
    public static final int ITEM_RECEIVER = 4;
    public static final int ITEM_SERVICE = 5;
    public static final int ITEM_BROADCAST = 6;
    public static final int ITEM_DAGGER2 = 7;
    public static final int ITEM_ROOM = 8;
    public static final int ITEM_CAMERA = 9;
    public static final int ITEM_SHARE = 10;
    public static final int ITEM_BUBBLES = 11;
    public static final int ITEM_OPENGL = 12;
    public static final int ITEM_ANNOTATION = 13;
    public static final int ITEM_ANIMATION = 14;
    public static final int ITEM_BAR_UTILS = 15;
    public static final int ITEM_SDK = 16;
    public static final int ITEM_UTIL = 17;
    public static final int ITEM_TEST = 18;
    public static final int ITEM_DIALOG = 19;
    public static final int ITEM_SELF = 20;
    public static final int ITEM_ACTIVITY = 21;



    private RecyclerView mRecyclerView;
    private UtilRecyclerViewAdapter mAdapter;
    private List<String> mData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util_main);
        mData = new ArrayList<>();
        Collections.addAll(mData,
                "自定义view",
                "图片",
                "视频",
                "音乐",
                "receiver",
                "service",
                "broadcast",
                "dagger2测试",
                "room测试",
                "相机(CameraX)",
                "分享",
                "浮动activity",
                "OpenGL",
                "注解测试",
                "动画",
                "状态栏测试",
                "SDK测试",
                "启动外部activity",
                "用来测试",
                "dialog测试",
                "启动自己",
                "测试singleTask为主页的启动模式");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new UtilRecyclerViewAdapter((view, position) -> {
            switch (position) {
                case ITEM_VIEW:
                    Intent viewIntent = new Intent(UtilActivityMain.this, ViewActivity.class);
                    startActivity(viewIntent);
                    break;
                case ITEM_IMAGE:
                    Intent imageIntent = new Intent(UtilActivityMain.this, ImageActivity.class);
                    startActivity(imageIntent);
                    break;
                case ITEM_VIDEO:
                    Intent videoIntent = new Intent(UtilActivityMain.this, VideoActivity.class);
                    startActivity(videoIntent);
                    break;
                case ITEM_MUSIC:
                    break;
                case ITEM_RECEIVER:
                    Intent receiverIntent = new Intent(UtilActivityMain.this, ReceiverActivity.class);
                    startActivity(receiverIntent);
                    break;
                case ITEM_SERVICE:
                    Intent serviceIntent = new Intent(UtilActivityMain.this, ServiceActivity.class);
                    startActivity(serviceIntent);
                    break;
                case ITEM_BROADCAST:
                    break;
                case ITEM_DAGGER2:
                    Intent dagger2Intent = new Intent(UtilActivityMain.this, DaggerActivity.class);
                    startActivity(dagger2Intent);
                    break;
                case ITEM_ROOM:
                    Intent roomIntent = new Intent(UtilActivityMain.this, RoomActivity.class);
                    startActivity(roomIntent);
                    break;
                case ITEM_CAMERA:
                    Intent cameraIntent = new Intent(UtilActivityMain.this, CameraActivity.class);
                    startActivity(cameraIntent);
                    break;
                case ITEM_SHARE:
                    Intent shareIntent = new Intent(UtilActivityMain.this, ShareActivity.class);
                    startActivity(shareIntent);
                    break;
                case ITEM_BUBBLES:
                    Intent flowIntent = new Intent(UtilActivityMain.this, FlowActivity.class);
                    startActivity(flowIntent);
                    break;
                case ITEM_OPENGL:
                    Intent openGlIntent = new Intent(UtilActivityMain.this, OpenGLESActivity.class);
                    startActivity(openGlIntent);
                    break;
                case ITEM_ANNOTATION:
                    Intent annotationIntent = new Intent(UtilActivityMain.this, AnnotationActivity.class);
                    startActivity(annotationIntent);
                    break;
                case ITEM_ANIMATION:
                    Intent animationIntent = new Intent(UtilActivityMain.this, AnimationActivity.class);
                    startActivity(animationIntent);
                    break;
                case ITEM_BAR_UTILS:
                    Intent barIntent = new Intent(UtilActivityMain.this, BarActivity.class);
                    startActivity(barIntent);
                    break;
                case ITEM_SDK:
                    Intent sdkIntent = new Intent(UtilActivityMain.this, SDKMainActivity.class);
                    startActivity(sdkIntent);
                    break;
                case ITEM_UTIL:
                    Intent utilIntent = new Intent(UtilActivityMain.this, UtilActivity.class);
                    startActivity(utilIntent);
                    break;
                case ITEM_TEST:
                    Intent testIntent = new Intent(UtilActivityMain.this, TestActivity.class);
                    startActivity(testIntent);
                    break;
                case ITEM_DIALOG:
                    Intent intent = new Intent(UtilActivityMain.this, DialogActivity.class);
                    startActivity(intent);
                    break;
                case ITEM_SELF:
                    Intent selfIntent = new Intent(UtilActivityMain.this, UtilActivityMain.class);
                    startActivity(selfIntent);
                    break;
                case ITEM_ACTIVITY:
                    Intent launchIntent = new Intent(UtilActivityMain.this, LaunchModeActivity.class);
                    startActivity(launchIntent);
                    break;
            }
        });
        mAdapter.setData(mData);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("SingleTask test->", "onNewIntent: UtilActivityMain");
        Intent newIntent = getIntent();
        if (newIntent == null) {
            Log.d("SingleTask test->", "onNewIntent: getIntent == null");
        } else {
            Log.d("SingleTask test->", "onNewIntent: getIntent != null");
        }

        if (intent == null) {
            Log.d("SingleTask test->", "onNewIntent: intent == null");
        } else {
            Log.d("SingleTask test->", "onNewIntent: intent != null");
        }
    }
}
