package com.example.androidxdemo.activity.view.view_pager;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.androidxdemo.CommonFragment;
import com.example.androidxdemo.R;
import com.example.androidxdemo.view.pager.MyVerticalViewPager;

import java.util.ArrayList;
import java.util.List;


public class VerticalPagerActivity extends AppCompatActivity {
    private MyVerticalViewPager mViewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter);
        mViewPager = findViewById(R.id.view_pager);
        List<Fragment> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            CommonFragment fragment = new CommonFragment();
            fragment.setText("this is " + i);
            list.add(fragment);
        }
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
        adapter.setData(list);
        mViewPager.setAdapter(adapter);
    }

    class MyAdapter extends FragmentPagerAdapter {
        private List<Fragment> mList;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            mList = new ArrayList<>();
        }

        public void setData(List<Fragment> data) {
            mList.clear();
            mList.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int i) {
            return mList.get(i);
        }

        @Override
        public int getCount() {
            return mList.size();
        }
    }
}
