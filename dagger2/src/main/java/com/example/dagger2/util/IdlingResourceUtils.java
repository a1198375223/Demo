package com.example.dagger2.util;

import android.util.Log;

import com.example.dagger2.idling.SimpleIdlingResource;


public class IdlingResourceUtils {
    private static final String TAG = "IdlingResourceUtils";

    private static SimpleIdlingResource mSimpleIdlingResource = new SimpleIdlingResource("Global");

    public static void increment() {
        Log.d(TAG, "increment: ");
        mSimpleIdlingResource.increment();
    }

    public static void decrement() {
        Log.d(TAG, "decrement: ");
        mSimpleIdlingResource.decrement();
    }

    public static SimpleIdlingResource getIdlingResource() {
        return mSimpleIdlingResource;
    }
}
