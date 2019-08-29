package com.wali.live.wechat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.wali.live.R;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.JumpToOfflinePay;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wali.live.wechat.common.Constants;

import org.jetbrains.annotations.NotNull;

public class WeChatSdkActivity extends AppCompatActivity {

    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_sdk);
        checkPermission();
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);

        findViewById(R.id.reg_btn).setOnClickListener(v -> {
            api.registerApp(Constants.APP_ID);
//            registerReceiver(new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    // 将该app注册到微信
//                    api.registerApp(Constants.APP_ID);
//                }
//            }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
        });

        findViewById(R.id.goto_send_btn).setOnClickListener(v -> {
            startActivity(new Intent(WeChatSdkActivity.this, SendToWXActivity.class));
//		        finish();
        });

        findViewById(R.id.launch_wx_btn).setOnClickListener(v -> Toast.makeText(WeChatSdkActivity.this, "launch result = " + api.openWXApp(), Toast.LENGTH_LONG).show());

        findViewById(R.id.goto_subscribe_message_btn).setOnClickListener(v -> {
                startActivity(new Intent(WeChatSdkActivity.this, SubscribeMessageActivity.class));
//				finish();
        });

        findViewById(R.id.goto_subscribe_mini_program_msg_btn).setOnClickListener(v -> {
                startActivity(new Intent(WeChatSdkActivity.this, SubscribeMiniProgramMsgActivity.class));
        });


        findViewById(R.id.jump_to_offline_pay).setOnClickListener(v -> {
            int wxSdkVersion = api.getWXAppSupportAPI();
            if (wxSdkVersion >= Build.OFFLINE_PAY_SDK_INT) {
                api.sendReq(new JumpToOfflinePay.Req());
            }else {
                Toast.makeText(WeChatSdkActivity.this, "not supported", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.auth_weixin).setOnClickListener(view -> {
            WXUtils wxUtils = new WXUtils();
            wxUtils.oAuthByWeiXin("wechat_sdk_activity");
        });
    }

    private void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.PERMISSIONS_REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == Constants.PERMISSIONS_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Get storage permission!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(WeChatSdkActivity.this, "Please give me storage permission!", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
