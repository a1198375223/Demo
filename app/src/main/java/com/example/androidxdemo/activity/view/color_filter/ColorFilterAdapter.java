package com.example.androidxdemo.activity.view.color_filter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidxdemo.R;

import java.util.ArrayList;
import java.util.List;

public class ColorFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ColorFilterAdapter";

    private List<Pair<PorterDuff.Mode, String>> mData;

    public ColorFilterAdapter() {
        this.mData = new ArrayList<>();
    }

    public void setData(List<Pair<PorterDuff.Mode, String>> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_color_filter_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof MyHolder) {
            ((MyHolder) viewHolder).position = i;
            ((MyHolder) viewHolder).iv.setColorFilter(Color.RED, mData.get(i).first);
            ((MyHolder) viewHolder).tv.setText(mData.get(i).second);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        int position;
        ImageView iv;
        TextView tv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.item_iv);
            tv = itemView.findViewById(R.id.item_tv);
        }
    }
}
