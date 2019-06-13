package com.example.androidxdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.commonlibrary.utils.NetworkUtils;


public class NetworkReceiver extends BroadcastReceiver {
    private OnNetworkConnectedListener mListener;

    public NetworkReceiver(OnNetworkConnectedListener listener) {
        this.mListener = listener;
    }

    public interface OnNetworkConnectedListener {
        void onNetworkConnected(String name);

        void onNoneNetwork();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtils.hasNetwork(context)) {
            if (mListener != null) {
                mListener.onNetworkConnected(NetworkUtils.corvertNetoworkName(NetworkUtils.corvertActiveNetworkType(context)));
            }
        } else {
            if (mListener != null) {
                mListener.onNoneNetwork();
            }
        }
    }
}
