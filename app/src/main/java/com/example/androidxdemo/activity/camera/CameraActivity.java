package com.example.androidxdemo.activity.camera;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidxdemo.R;

import com.example.androidxdemo.activity.main.adapter.UtilRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    public static final int ITEM_CAMERAX = 0;
    public static final int ITEM_DEMO_CAMERAX = 1;
    public static final int ITEM_CAMERA2 = 2;
    public static final int ITEM_CAMERA2_VIDEO = 3;


    private RecyclerView mRecyclerView;
    private UtilRecyclerViewAdapter mAdapter;
    private List<String> mData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util_main);

        mData = new ArrayList<>();
        Collections.addAll(mData,
                "cameraX",
                "DemoCameraX",
                "Camera2",
                "Camera2Video");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new UtilRecyclerViewAdapter((view, position) -> {
            switch (position) {
                case ITEM_CAMERAX:
                    Intent frescoIntent = new Intent(CameraActivity.this, CameraXActivity.class);
                    startActivity(frescoIntent);
                    break;
                case ITEM_DEMO_CAMERAX:
                    Intent demoIntent = new Intent(CameraActivity.this, DemoCameraXActivity.class);
                    startActivity(demoIntent);
                    break;
                case ITEM_CAMERA2:
                    Intent camera2Intent = new Intent(CameraActivity.this, Camera2Activity.class);
                    startActivity(camera2Intent);
                    break;
                case ITEM_CAMERA2_VIDEO:
                    Intent camera2VideoIntent = new Intent(CameraActivity.this, Camera2VideoActivity.class);
                    startActivity(camera2VideoIntent);
                    break;
            }
        });
        mAdapter.setData(mData);
        mRecyclerView.setAdapter(mAdapter);
    }
}
