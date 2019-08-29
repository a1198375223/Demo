package com.example.androidxdemo.activity.service.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ProcessUtils;

public class MessengerService extends Service {
    private static final String TAG = "MessengerService";
    public static final int MSG_FROM_CLIENT = 1;

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_FROM_CLIENT:
                    String m = msg.getData().getString("msg");
                    Log.d(TAG, "handleMessage: msg = " + m);

                    Messenger client = msg.replyTo;
                    Message message = Message.obtain(null, MessengerActivity.MSG_REPLY);
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", "hello, this is service");
                    message.setData(bundle);
                    try {
                        client.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:

            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: service process is " + ProcessUtils.getCurrentProcessName());
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
