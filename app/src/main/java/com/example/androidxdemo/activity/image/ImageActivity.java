package com.example.androidxdemo.activity.image;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidxdemo.R;
import com.example.androidxdemo.activity.image.fresco.FrescoActivity;
import com.example.androidxdemo.activity.image.image_decode.ImageDecoderActivity;
import com.example.androidxdemo.activity.main.adapter.UtilRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";

    public static final int ITEM_FRESCO = 0;
    public static final int ITEM_IMAGE_DECODER = 1;

    private RecyclerView mRecyclerView;
    private UtilRecyclerViewAdapter mAdapter;
    private List<String> mData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util_main);
        mData = new ArrayList<>();
        Collections.addAll(mData,
                "fresco",
                "image decoder");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new UtilRecyclerViewAdapter((view, position) -> {
            switch (position) {
                case ITEM_FRESCO:
                    Intent frescoIntent = new Intent(ImageActivity.this, FrescoActivity.class);
                    startActivity(frescoIntent);
                    break;
                case ITEM_IMAGE_DECODER:
                    Intent imageDecoderIntent = new Intent(ImageActivity.this, ImageDecoderActivity.class);
                    startActivity(imageDecoderIntent);
                    break;
            }
        });
        mAdapter.setData(mData);
        mRecyclerView.setAdapter(mAdapter);
    }
}
