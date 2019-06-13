package com.example.room;


import android.util.Log;

import com.example.dagger2.mvp.Dagger2Application;

public class RoomApplication extends Dagger2Application {
    private static final String TAG = "RoomApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        RoomUtils.init(this);
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate: ");
        RoomUtils.destroy();
        super.onTerminate();
    }
}
