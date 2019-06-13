package com.example.androidxdemo.activity.file.view;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.example.androidxdemo.R;

import java.util.ArrayList;
import java.util.List;


public class OneLoaderView implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "OneLoaderView";
    private Activity activity;
    private View mRootView;
    private List<String> mList;
    private SimpleCursorAdapter mAdapter;


    public OneLoaderView(final Activity activity) {
        this.activity = activity;
        mList = new ArrayList<>();
        mRootView = LayoutInflater.from(activity).inflate(R.layout.layout_file_item, null);
        ListView listView = (ListView) mRootView.findViewById(R.id.list_view);
        mAdapter = new SimpleCursorAdapter(activity, R.layout.layout_common_list_item, null, new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER}, new int[]{R.id.text, R.id.text1}, 0);
        listView.setAdapter(mAdapter);
        activity.getLoaderManager().initLoader(0, null, this);

//        Bundle bundle = new Bundle();
//        bundle.putString(BaseLoader.EXTRA_URI, UserDictionary.Words.CONTENT_URI.toString());
//        bundle.putString(BaseLoader.EXTRA_SORT_ORDER, UserDictionary.Words.DEFAULT_SORT_ORDER);
//        BaseLoader<Cursor> loader = new BaseLoader<Cursor>(activity, 0, bundle) {
//            @Override
//            public void onFinished(Loader<Cursor> loader, Cursor data) {
//                String[] columns = data.getColumnNames();
//                for (String column : columns) {
//                    Log.d(TAG, "onFinished: column name=" + column);
//                }
//                mAdapter.swapCursor(data);
//            }
//
//            @Override
//            public void onReset(Loader<Cursor> loader) {
//                mAdapter.swapCursor(null);
//            }
//        };
    }

    public View getView() {
        return mRootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(activity, CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: ");
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: ");
        mAdapter.swapCursor(null);
    }
}
