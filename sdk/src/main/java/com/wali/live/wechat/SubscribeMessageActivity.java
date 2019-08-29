package com.wali.live.wechat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wali.live.R;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wali.live.wechat.common.Constants;
import com.wali.live.wechat.utils.Util;

public class SubscribeMessageActivity extends Activity {

    private IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.APP_ID,true);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_message);

        Button checkSubscribeMsgBtn = findViewById(R.id.check_subscribe_message_btn);
        checkSubscribeMsgBtn.setOnClickListener(v -> {
            boolean supported = api.getWXAppSupportAPI() >= Build.SUBSCRIBE_MESSAGE_SUPPORTED_SDK_INT;
            Toast.makeText(SubscribeMessageActivity.this, supported ? "supported" : "unsupported", Toast.LENGTH_SHORT).show();
        });

        Button subscribeMsgBtn = findViewById(R.id.subscribe_message_btn);
        subscribeMsgBtn.setOnClickListener(v -> {
            EditText sceneEt = findViewById(R.id.scene_et);
            EditText templateIdEt = findViewById(R.id.templateid_et);
            EditText reservedEt = findViewById(R.id.reserved_et);

            SubscribeMessage.Req req = new SubscribeMessage.Req();
            req.scene = Util.parseInt(sceneEt.getText().toString().trim(), 0);
            req.templateID = templateIdEt.getText().toString().trim();
            req.reserved = reservedEt.getText().toString().trim();

            boolean ret = api.sendReq(req);
            Toast.makeText(SubscribeMessageActivity.this, "sendReq result = " + ret, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
