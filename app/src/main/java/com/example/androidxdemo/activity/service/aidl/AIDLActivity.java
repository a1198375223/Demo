package com.example.androidxdemo.activity.service.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ProcessUtils;
import com.example.androidxdemo.Book;
import com.example.androidxdemo.IBookManager;
import com.example.androidxdemo.R;

import java.util.Random;

public class AIDLActivity extends AppCompatActivity {
    private static final String TAG = "AIDLActivity";

    private IBookManager mBookManager;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBookManager = IBookManager.Stub.asInterface(iBinder);
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);

        bindService();

        ((TextView) findViewById(R.id.tv)).setText("当前进程的名称为:" + ProcessUtils.getCurrentProcessName());

        findViewById(R.id.remote_ipc).setOnClickListener(view -> {
            try {
                ((TextView) findViewById(R.id.data)).setText(mBookManager.getBookList().toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.add_book).setOnClickListener(view -> {
            Random random = new Random();
            int id = random.nextInt();
            try {
                mBookManager.addBook(new Book(id, "test name"));
                ((TextView) findViewById(R.id.data)).setText(mBookManager.getBookList().toString());
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
        if (mBookManager == null) {
            return;
        }

        mBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
        mBookManager = null;
    }

    private void bindService() {
        Intent intent = new Intent("com.example.androidxdemo.action");
        intent.setPackage("com.example.androidxdemo");
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
