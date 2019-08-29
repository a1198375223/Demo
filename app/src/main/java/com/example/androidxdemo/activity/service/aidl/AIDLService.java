package com.example.androidxdemo.activity.service.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.blankj.utilcode.util.ProcessUtils;
import com.example.androidxdemo.Book;
import com.example.androidxdemo.IBookManager;

import java.util.ArrayList;
import java.util.List;

public class AIDLService extends Service {
    private static final String TAG = "AIDLService";
    private final List<Book> mList;

    public AIDLService() {
        mList = new ArrayList<>();
        mList.add(new Book(1, "dyx"));
        mList.add(new Book(2, "zza"));
        mList.add(new Book(3, "zys"));
        mList.add(new Book(4, "jcl"));
    }

    private final IBookManager.Stub mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mList.add(book);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: service process is " + ProcessUtils.getCurrentProcessName());
    }
}
