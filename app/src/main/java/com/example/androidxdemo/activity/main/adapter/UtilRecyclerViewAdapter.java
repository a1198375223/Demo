package com.example.androidxdemo.activity.main.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidxdemo.R;
import com.example.androidxdemo.base.callback.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class UtilRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "UtilRecyclerViewAdapter";
    private OnItemClickListener mListener;
    private List<String> mList;

    public UtilRecyclerViewAdapter(OnItemClickListener listener) {
        this.mListener = listener;
        this.mList = new ArrayList<>();
    }

    public void setData(List<String> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_util_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof MyViewHolder) {
            ((MyViewHolder) viewHolder).position = i;
            ((MyViewHolder) viewHolder).tv.setText(mList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        int position;
        TextView tv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.item_tv);
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClickListener(v, position);
                }
            });
        }
    }
}
