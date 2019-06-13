package com.example.androidxdemo.activity.web;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.view.web.NoLeakWebView;

public class WebActivity extends AppCompatActivity {
    private static final String TAG = "WebActivity";

    private NoLeakWebView mWebView;
    private boolean isSafeBrowsingInitialized = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWebView = new NoLeakWebView(this);


        isSafeBrowsingInitialized = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // api > 27 启动安全浏览
            WebView.startSafeBrowsing(this, value -> {
                isSafeBrowsingInitialized = true;

                if (!value) {
                    Log.e(TAG, "WebView startSafeBrowsing error!");
                }
            });
        } else {
            isSafeBrowsingInitialized = true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // api >= 26 设置渲染优先级
            // 如果在自定义的client没有实现onRenderProcessGone()方法，不推荐设置渲染优先级
            mWebView.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_BOUND, true);
        }
    }
}
