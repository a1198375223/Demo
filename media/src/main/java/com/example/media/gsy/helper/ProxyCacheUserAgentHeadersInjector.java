package com.example.media.gsy.helper;

import android.util.Log;

import com.danikula.videocache.headers.HeaderInjector;

import java.util.HashMap;
import java.util.Map;

public class ProxyCacheUserAgentHeadersInjector implements HeaderInjector {
    private static final String TAG = "ProxyCacheUserAgentHead";

    public final static Map<String, String> mMapHeadData = new HashMap<>();

    @Override
    public Map<String, String> addHeaders(String url) {
        Log.e(TAG, "****** proxy addHeaders ****** " + mMapHeadData.size());
        return mMapHeadData;
    }
}