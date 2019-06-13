package com.example.androidxdemo.activity.file.view;

import android.app.Activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidxdemo.R;
import com.example.androidxdemo.activity.file.adapter.ListCommonAdapter;
import com.example.androidxdemo.base.bean.MediaItem;
import com.example.androidxdemo.base.callback.OnItemClickListener;
import com.example.androidxdemo.utils.LocalFileUtils;
import com.example.commonlibrary.utils.MimeUtils;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



public class TwoBaseLoaderView {
    private static final String TAG = "TwoBaseLoaderView";
    private View mRootView;
    ListCommonAdapter adapter;


    public TwoBaseLoaderView(final Activity activity) {
        mRootView = LayoutInflater.from(activity).inflate(R.layout.layout_file_item, null);
        ((ListView)mRootView.findViewById(R.id.list_view)).setVisibility(View.GONE);
        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new ListCommonAdapter();
        adapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                Toast.makeText(activity, "item click position=" + position, Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        LocalFileUtils.getFileByType(MimeUtils.TYPE_EXCEL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MediaItem>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(List<MediaItem> mediaItems) {
                        Log.d(TAG, "onNext: size=" + mediaItems.size() + " mediaItems=" + mediaItems.toString());
                        adapter.setData(mediaItems);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "onError: ", throwable);

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }


    public View getView() {
        return mRootView;
    }
}
