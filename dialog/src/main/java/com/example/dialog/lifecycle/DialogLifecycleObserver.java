package com.example.dialog.lifecycle;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.dialog.MaterialDialog;

public class DialogLifecycleObserver implements LifecycleObserver {
    private static final String TAG = "DialogLifecycleObserver";
    private MaterialDialog dialog;

    public DialogLifecycleObserver(MaterialDialog dialog) {
        this.dialog = dialog;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        dialog.dismiss();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Log.d(TAG, "onPause: ");
        dialog.dismiss();
    }
}
