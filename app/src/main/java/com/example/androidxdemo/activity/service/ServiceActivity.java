package com.example.androidxdemo.activity.service;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidxdemo.R;
import com.example.androidxdemo.activity.main.adapter.UtilRecyclerViewAdapter;
import com.example.androidxdemo.activity.service.aidl.AIDLActivity;
import com.example.androidxdemo.activity.service.messenger.MessengerActivity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceActivity extends AppCompatActivity {
    private static final String TAG = "ServiceActivity";
    public static final int ITEM_AIDL = 0;
    public static final int ITEM_MESSENGER = 1;


    private RecyclerView mRecyclerView;
    private UtilRecyclerViewAdapter mAdapter;
    private List<String> mData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util_main);
        mData = new ArrayList<>();
        Collections.addAll(mData,
                "AIDL服务",
                "Messenger通信");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new UtilRecyclerViewAdapter((view, position) -> {
            switch (position) {
                case ITEM_AIDL:
                    Intent aidlIntent = new Intent(this, AIDLActivity.class);
                    startActivity(aidlIntent);
                    break;
                case ITEM_MESSENGER:
                    Intent messengerIntent = new Intent(this, MessengerActivity.class);
                    startActivity(messengerIntent);
                    break;
                default:
            }
        });
        mAdapter.setData(mData);
        mRecyclerView.setAdapter(mAdapter);
    }
}
