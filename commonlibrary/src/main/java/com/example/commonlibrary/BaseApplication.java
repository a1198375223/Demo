package com.example.commonlibrary;



import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Configuration;

import com.example.commonlibrary.event.MyEventBusIndex;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executors;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public class BaseApplication extends DaggerApplication implements Configuration.Provider{
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                // 自定义线程池
//                .setExecutor(Executors.newFixedThreadPool(8))
                //

                .setMinimumLoggingLevel(Log.ASSERT)
                .build();
    }

}
