package com.example.androidxdemo.activity.service.messenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ProcessUtils;
import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.Toasty;

public class MessengerActivity extends AppCompatActivity {
    private static final String TAG = "MessengerActivity";
    private Messenger mMessenger;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMessenger = new Messenger(iBinder);
            try {
                // 绑定死亡代理
                Log.d(TAG, "link to death");
                iBinder.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onServiceConnected: name=" + componentName);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: name=" + componentName);
        }
    };

    // 当Binder死亡的时候, 系统会调用下面的方法
    // 创建一个死亡代理
    private IBinder.DeathRecipient mDeathRecipient = () -> {
        Log.d(TAG, "binderDied ");
        unBind();

        // 重新绑定远程服务
        bindService();
    };


    public static final int MSG_REPLY = 2;
    private Messenger mReceiverMessenger = new Messenger(new MessengerHandler());

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_REPLY:
                    Toasty.showSuccess("reply message: " + msg.getData().getString("msg"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        bindService();

        ((TextView) findViewById(R.id.tv)).setText("当前进程的名称为:" + ProcessUtils.getCurrentProcessName());

        findViewById(R.id.add_book).setOnClickListener(view -> {
            Message msg = Message.obtain(null, MessengerService.MSG_FROM_CLIENT);
            msg.replyTo = mReceiverMessenger;
            Bundle bundle = new Bundle();
            bundle.putString("msg", "hello, this is client");
            msg.setData(bundle);
            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        unBind();
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    private void unBind() {
        if (mMessenger == null) {
            return;
        }
        mMessenger.getBinder().unlinkToDeath(mDeathRecipient, 0);
        mMessenger = null;
    }

    private void bindService() {
        Intent intent = new Intent("com.example.androidxdemo.MessengerService");
        intent.setPackage("com.example.androidxdemo");
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
