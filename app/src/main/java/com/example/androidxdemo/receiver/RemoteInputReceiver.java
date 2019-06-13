package com.example.androidxdemo.receiver;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class RemoteInputReceiver extends BroadcastReceiver {
    public static final String REMOTE_KEY = "remote_key";
    private OnRemoteListener mListener;

    public RemoteInputReceiver(OnRemoteListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener != null) {
            mListener.onResult(getMessageText(intent));
        }
    }

    public interface OnRemoteListener {
        void onResult(CharSequence text);
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                return remoteInput.getCharSequence(REMOTE_KEY);
            }
        }
        return null;
    }
}
