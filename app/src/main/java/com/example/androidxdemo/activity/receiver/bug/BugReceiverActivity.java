package com.example.androidxdemo.activity.receiver.bug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;

/**
 * 有些华为手机对广播做了限制,最多只能启动500个广播, 所以要做处理
 */
public class BugReceiverActivity extends AppCompatActivity {
    private static final String TAG = "BugReceiverActivity";
    private TextView mTv;
    private Button mBn;
    private Button mStartSelf;
    private int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_receiver);
        mTv = findViewById(R.id.tv);
        mBn = findViewById(R.id.bn);
        mStartSelf = findViewById(R.id.start_self);

        // 一个进程最多不能启动1000个广播
        mBn.setOnClickListener((view) -> {
            for (int i = 0; i < 1000; i++) {
                IntentFilter filter = new IntentFilter();
                filter.addAction("test index : " + i);
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                    }
                }, filter);
                count++;
                Log.d(TAG, "当前注册了: " + count + "个广播接收器");
                mTv.setText("当前注册了: " + count + "个广播接收器");
            }
        });

        mStartSelf.setOnClickListener(view-> {
            Intent self = new Intent(BugReceiverActivity.this, BugReceiverActivity.class);
            startActivity(self);
        });
    }
}
