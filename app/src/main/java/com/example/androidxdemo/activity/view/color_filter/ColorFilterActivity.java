package com.example.androidxdemo.activity.view.color_filter;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidxdemo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColorFilterActivity extends AppCompatActivity {
    private static final String TAG = "ColorFilterActivity";
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_filter);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ColorFilterAdapter adapter = new ColorFilterAdapter();
        List<Pair<PorterDuff.Mode, String>> data = new ArrayList<>();
        // setColorFilter(color, mode) -> 这里的color相当于src上层图片颜色是填充图片的 原先的图片相当于des下层图片
        Collections.addAll(data,
                new Pair<>(PorterDuff.Mode.CLEAR, "clear"), // 清除画布
                new Pair<>(PorterDuff.Mode.SRC, "src"), // 显示setColorFilter的color 填充图片
                new Pair<>(PorterDuff.Mode.SRC_ATOP, "src atop"), // 取下层非交集部分和上层的交集部分
                new Pair<>(PorterDuff.Mode.SRC_OUT, "src out"), // 取上层的非交集部分
                new Pair<>(PorterDuff.Mode.SRC_IN, "src in"), // 取上层交集部分
                new Pair<>(PorterDuff.Mode.SRC_OVER, "src over"), // 正常绘制 上下层叠盖
                new Pair<>(PorterDuff.Mode.DST, "dst"), // 显示原始的图片
                new Pair<>(PorterDuff.Mode.DST_ATOP, "dst atop"), // 去上层非交集部分和下层交集部分
                new Pair<>(PorterDuff.Mode.DST_IN, "dst in"), // 取下层交集部分
                new Pair<>(PorterDuff.Mode.DST_OUT, "dst out"), // 取下层的非交集部分
                new Pair<>(PorterDuff.Mode.DST_OVER, "dst over"), // 正常绘制 上下层叠盖 但是下层在上
                new Pair<>(PorterDuff.Mode.XOR, "xor"), // 去除交集的部分 正常绘制
                new Pair<>(PorterDuff.Mode.DARKEN, "darken"),
                new Pair<>(PorterDuff.Mode.LIGHTEN, "light"),
                new Pair<>(PorterDuff.Mode.MULTIPLY, "multiply"), // 显示交集部分 上下层叠盖
                new Pair<>(PorterDuff.Mode.SCREEN, "screen"),
                new Pair<>(PorterDuff.Mode.ADD, "add"),
                new Pair<>(PorterDuff.Mode.OVERLAY, "overlay")
        );
        mRecyclerView.setAdapter(adapter);
        adapter.setData(data);
    }
}
