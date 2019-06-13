package com.example.androidxdemo.view.web;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 *
 */
public class NoLeakWebView extends WebView {


    public NoLeakWebView(Context context) {
        this(context, null);
    }

    public NoLeakWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoLeakWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
