package com.wali.live.wechat;

import android.content.Intent;
import android.util.Log;

import com.example.commonlibrary.utils.AppUtils;
import com.example.commonlibrary.utils.Toasty;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXUtils {
    private static final String TAG = WXUtils.class.getSimpleName();

    // 小米直播的appid
    public static final String APP_ID = "wx0b1f5dd377f1cc6c";
    public static final int REQUEST_CODE_WX_SHARE = 3001;
    private static final String PACKAGE_WX = "com.tencent.mm";

    // 微信授权域
    public static final String WEIXIN_REQ_SCOPE = "snsapi_userinfo";


    private IWXAPI mWxApi;

    public WXUtils() {
        registerToWx();
    }

    // 将appid注册到微信
    public void registerToWx() {
        Log.d(TAG, "registerToWx");

        mWxApi = WXAPIFactory.createWXAPI(AppUtils.app().getApplicationContext(), APP_ID, true);
        mWxApi.registerApp(APP_ID);
    }


    public void handleIntent(Intent intent, IWXAPIEventHandler handler) {
        Log.d(TAG, "handleIntent: ");
        mWxApi.handleIntent(intent, handler);
    }


    // 微信登入
    public void oAuthByWeiXin(String state) {
        Log.d(TAG, "oAuthByWeiXin: ");

        // 判断是否有安装微信
        if (!mWxApi.isWXAppInstalled()) {
            Toasty.showError("weixin is not installed");
            return;
        }

        SendAuth.Req req = new SendAuth.Req();
        req.scope = WEIXIN_REQ_SCOPE;
        req.state = state;
        boolean flag = mWxApi.sendReq(req);
        Toasty.showCustom("flag:" + flag);
    }
}
