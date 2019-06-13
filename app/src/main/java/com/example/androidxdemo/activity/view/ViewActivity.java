package com.example.androidxdemo.activity.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidxdemo.R;
import com.example.androidxdemo.activity.main.adapter.UtilRecyclerViewAdapter;
import com.example.androidxdemo.activity.view.chronometer.ChronometerActivity;
import com.example.androidxdemo.activity.view.color_filter.ColorFilterActivity;
import com.example.androidxdemo.activity.view.dot.DotViewActivity;
import com.example.androidxdemo.activity.view.view_pager.VerticalPagerActivity;
import com.example.androidxdemo.activity.view.view_pager.ViewPager2Activity;
import com.example.androidxdemo.activity.view.voice.VoiceActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewActivity extends AppCompatActivity {
    private static final String TAG = "ViewActivity";

    public static final int ITEM_DOT_VIEW= 0;
    public static final int ITEM_BOTTOM_POPUP_VIEW = 1;
    public static final int ITEM_NO_SCROLL_VIEW_PAGER = 2;
    public static final int ITEM_VERTICAL_VIEW_PAGER = 3;
    public static final int ITEM_MY_TAB_LAYOUT = 4;
    public static final int ITEM_COLOR_FILTER = 5;
    public static final int ITEM_TIME_CLOCK = 6;
    public static final int ITEM_TIME_VOICE = 7;
    public static final int ITEM_VIEW_PAGER2 = 8;


    private RecyclerView mRecyclerView;
    private UtilRecyclerViewAdapter mAdapter;
    private List<String> mData;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util_main);
        mData = new ArrayList<>();
        Collections.addAll(mData,
                "园点进度条",
                "底部弹窗view",
                "不可滑动的ViewPager",
                "垂直滑动的ViewPager",
                "自定义的TabLayout",
                "测试color filter",
                "google计时器 chronometer",
                "自定义声音动画",
                "ViewPager2");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new UtilRecyclerViewAdapter((view, position) -> {
            switch (position) {
                case ITEM_DOT_VIEW: // 圆点进度条
                    Intent dotIntent = new Intent(ViewActivity.this, DotViewActivity.class);
                    startActivity(dotIntent);
                    break;
                case ITEM_BOTTOM_POPUP_VIEW: // 底部弹窗view
                    break;
                case ITEM_NO_SCROLL_VIEW_PAGER: // 不可滑动的ViewPager
                    break;
                case ITEM_VERTICAL_VIEW_PAGER: // 垂直滑动的ViewPager|
                    Intent verticalViewPagerIntent = new Intent(ViewActivity.this, VerticalPagerActivity.class);
                    startActivity(verticalViewPagerIntent);
                    break;
                case ITEM_MY_TAB_LAYOUT: // 自定义的TabLayout
                    break;
                case ITEM_COLOR_FILTER: // setColorFilter()测试
                    Intent colorFilterIntent = new Intent(ViewActivity.this, ColorFilterActivity.class);
                    startActivity(colorFilterIntent);
                    break;
                case ITEM_TIME_CLOCK: // chronometer 计时器
                    Intent chronometerIntent = new Intent(ViewActivity.this, ChronometerActivity.class);
                    startActivity(chronometerIntent);
                    break;
                case ITEM_TIME_VOICE:
                    Intent voiceIntent = new Intent(ViewActivity.this, VoiceActivity.class);
                    startActivity(voiceIntent);
                    break;
                case ITEM_VIEW_PAGER2:
                    Intent viewPager2Intent = new Intent(ViewActivity.this, ViewPager2Activity.class);
                    startActivity(viewPager2Intent);
                    break;
            }
        });
        mAdapter.setData(mData);
        mRecyclerView.setAdapter(mAdapter);
    }
}
