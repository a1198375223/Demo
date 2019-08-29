package com.wali.live;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wali.live.wechat.WeChatSdkActivity;


public class SDKMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_main);

        findViewById(R.id.wechat_sdk).setOnClickListener(view -> {
            Intent wechatSdkIntent = new Intent(this, WeChatSdkActivity.class);
            startActivity(wechatSdkIntent);
        });
    }
}
