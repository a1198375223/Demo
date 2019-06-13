package com.example.androidxdemo.activity.file;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.androidxdemo.R;
import com.example.androidxdemo.activity.file.view.OneLoaderView;
import com.example.androidxdemo.activity.file.view.TwoBaseLoaderView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FileActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        MyPagerAdapter adapter = new MyPagerAdapter();
        List<View> list = new ArrayList<>();
        list.add(new OneLoaderView(FileActivity.this).getView());
        list.add(new TwoBaseLoaderView(FileActivity.this).getView());
        adapter.setData(list);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    private class MyPagerAdapter extends PagerAdapter {
        private List<String> mTitle;
        private List<View> mList;

        public MyPagerAdapter() {
            mList = new ArrayList<>();
            mTitle = new ArrayList<>();
        }

        public void setData(List<View> list) {
            this.mList = list;
            for (int i = 0; i < mList.size(); i++) {
                mTitle.add("this is " + i);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return o == view;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(mList.get(position));
            return mList.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mList.get(position));
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitle.get(position);
        }
    }
}

