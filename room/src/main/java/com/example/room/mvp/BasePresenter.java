package com.example.room.mvp;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<V extends IView> implements IPresenter {
    protected final String TAG = BasePresenter.class.getSimpleName() + "@" + hashCode();

    protected V view;

    protected WeakReference<LifecycleOwner> owner;

    public BasePresenter(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(this);
        this.owner = new WeakReference<>(owner);
    }


    @Override
    public void create(LifecycleOwner owner) {
        Log.d(TAG, "create: ");    
    }

    @Override
    public void start(LifecycleOwner owner) {
        Log.d(TAG, "start: ");
    }

    @Override
    public void resume(LifecycleOwner owner) {
        Log.d(TAG, "resume: ");
    }

    @Override
    public void pause(LifecycleOwner owner) {
        Log.d(TAG, "pause: ");
    }

    @Override
    public void stop(LifecycleOwner owner) {
        Log.d(TAG, "stop: ");
    }

    @Override
    public void destroy(LifecycleOwner owner) {
        Log.d(TAG, "destroy: ");
    }

    @Override
    public void onLifecycleChange(LifecycleOwner owner, Lifecycle.Event event) {
        Log.d(TAG, "onLifecycleChange: ");
    }
}
