package com.example.androidxdemo.activity.web;

import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.example.commonlibrary.utils.Toasty;

// 各种WebView的处理规则
public class WebClient extends WebViewClient {
    private static final String TAG = "WebClient";

    /**
     * 当SafeBrowsing初始化之后，这个方法才会被的调用
     */
    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
        // api >= 27
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            Log.d(TAG, "onSafeBrowsingHit: threatType=" + threatType + " request: {url=" + request.getUrl() + " method=" + request.getMethod() + " request header=" + request.getRequestHeaders().toString() + " }");
            // backToSafety 返回到安全的页面
            // proceed 继续访问不安全的页面
            // showInterstitial  显示时间间隙
            callback.backToSafety(true);
            Toasty.showError("访问不安全的网站.");
        }
    }


    /**
     * 渲染进程消失或者崩溃的时候调用的方法
     * @param view
     * @param detail
     * @return
     */
    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 渲染进程是否被观察到crash true代表crash false代表渲染进程是被系统关闭的
            if (!detail.didCrash()) {
                Log.e(TAG, "系统杀死WebView的渲染进程来恢复内存...");

                // todo 可以创建一个新的WebView来恢复渲染


                return true;
            }
            Log.e(TAG, "WebView渲染进程崩溃了(crash)...");
            return false;
        } else {
            return super.onRenderProcessGone(view, detail);
        }
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }


    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }


    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return super.shouldOverrideKeyEvent(view, event);
    }
}
