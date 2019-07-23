package com.example.media.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.media.fragment.RecyclerViewFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private List<RecyclerViewFragment> mFragmentList;
    private List<String> titles;

    public MyPagerAdapter(FragmentManager fm, List<RecyclerViewFragment> fragmentList, List<String> titles) {
        super(fm);
        mFragmentList = new ArrayList<>(fragmentList);
        this.titles = titles;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
