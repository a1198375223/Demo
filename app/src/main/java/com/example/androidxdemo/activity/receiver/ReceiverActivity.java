package com.example.androidxdemo.activity.receiver;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidxdemo.R;
import com.example.androidxdemo.activity.main.adapter.UtilRecyclerViewAdapter;
import com.example.androidxdemo.activity.receiver.bug.BugReceiverActivity;
import com.example.androidxdemo.activity.receiver.net.NetWorkActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReceiverActivity extends AppCompatActivity {
    private static final String TAG = "ReceiverActivity";

    public static final int ITEM_NET_WORK = 0;
    public static final int ITEM_TOO_MANY_RECEIVER = 1;



    private RecyclerView mRecyclerView;
    private UtilRecyclerViewAdapter mAdapter;
    private List<String> mData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util_main);
        mData = new ArrayList<>();
        Collections.addAll(mData,
                "网络监控",
                "测试广播数量");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new UtilRecyclerViewAdapter((view, position) -> {
            switch (position) {
                case ITEM_NET_WORK:
                    Intent networkIntent = new Intent(ReceiverActivity.this, NetWorkActivity.class);
                    startActivity(networkIntent);
                    break;
                case ITEM_TOO_MANY_RECEIVER:
                    Intent bugIntent = new Intent(ReceiverActivity.this, BugReceiverActivity.class);
                    startActivity(bugIntent);
                    break;
            }
        });
        mAdapter.setData(mData);
        mRecyclerView.setAdapter(mAdapter);
    }
}
