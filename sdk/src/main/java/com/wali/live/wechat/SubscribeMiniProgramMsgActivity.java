package com.wali.live.wechat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wali.live.R;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelbiz.SubscribeMiniProgramMsg;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wali.live.wechat.common.Constants;

public class SubscribeMiniProgramMsgActivity extends Activity {

    private IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.APP_ID,true);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_mini_program_msg);
        Button checkSubscribeMsgBtn = findViewById(R.id.check_subscribe_message_btn);
        checkSubscribeMsgBtn.setOnClickListener(v -> {
            boolean supported = api.getWXAppSupportAPI() >= Build.SUBSCRIBE_MINI_PROGRAM_MSG_SUPPORTED_SDK_INT;
            Toast.makeText(SubscribeMiniProgramMsgActivity.this, supported ? "support" : "not support", Toast.LENGTH_SHORT).show();
        });

        final EditText miniProgramAppIdEt = findViewById(R.id.mini_program_appid_et);

        Button subscribeMsgBtn = findViewById(R.id.subscribe_message_btn);
        subscribeMsgBtn.setOnClickListener(v -> {
            SubscribeMiniProgramMsg.Req req = new SubscribeMiniProgramMsg.Req();
            req.miniProgramAppId = miniProgramAppIdEt.getText().toString().trim();

            boolean ret = api.sendReq(req);
            String message = String.format("sendReq ret : %s", ret);
            Toast.makeText(SubscribeMiniProgramMsgActivity.this, message, Toast.LENGTH_SHORT).show();
        });
    }

}
