package com.example.androidxdemo.activity.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidxdemo.R;
import com.example.androidxdemo.activity.bubble.FlowActivity;
import com.example.androidxdemo.activity.camera.CameraActivity;
import com.example.androidxdemo.activity.image.ImageActivity;
import com.example.androidxdemo.activity.main.adapter.UtilRecyclerViewAdapter;
import com.example.androidxdemo.activity.receiver.ReceiverActivity;
import com.example.androidxdemo.activity.share.ShareActivity;
import com.example.androidxdemo.activity.view.ViewActivity;
import com.example.dagger2.DaggerActivity;
import com.example.media.VideoActivity;
import com.example.room.RoomActivity;

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
                "浮动activity");

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
            }
        });
        mAdapter.setData(mData);
        mRecyclerView.setAdapter(mAdapter);
    }
}
