package com.example.commonlibrary.utils;

import android.net.Uri;

public class UriUtils {
    private static final String BASE_URI = "android:resource://" + AppUtils.app().getPackageName() + "/";

    public static Uri createUriForResource(int resId) {
        return Uri.parse(BASE_URI + resId);
    }

    public static int parseToResourceId(String uri) {
        String resId = uri.substring(BASE_URI.length());
        return Integer.valueOf(resId);
    }
}
