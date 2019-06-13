package com.example.androidxdemo.activity.view.view_pager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.androidxdemo.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPager2Activity extends AppCompatActivity {
    private static final String TAG = "ViewPager2Activity";
    private ViewPager2 mViewPager;
    private RadioGroup mOrientationGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager2);
        mViewPager = findViewById(R.id.view_pager);
        mOrientationGroup = findViewById(R.id.radio_group1);
        mOrientationGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.selection_vertical) {
                mViewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
            } else if (checkedId == R.id.selection_horizontal) {
                mViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
            }
        });

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add("this is " + i);
        }
        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter();
        adapter.setData(list);
        mViewPager.setAdapter(adapter);
    }

    private class ScreenSlidePagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<String> mList;

        public ScreenSlidePagerAdapter() {
            super();
            mList = new ArrayList<>();
        }

        public void setData(List<String> data) {
            mList.clear();
            mList.addAll(data);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: ");
            return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_pager2_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: position=" + position);
            if (holder instanceof MyHolder) {
                ((MyHolder) holder).position = position;
                ((MyHolder) holder).text.setText(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }


        @Override
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            super.onViewRecycled(holder);
            Log.d(TAG, "onViewRecycled: position=" + holder.getAdapterPosition());
        }

        @Override
        public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            Log.d(TAG, "onViewAttachedToWindow: position=" + holder.getAdapterPosition());
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            Log.d(TAG, "onViewDetachedFromWindow: position=" + holder.getAdapterPosition());
        }


        class MyHolder extends RecyclerView.ViewHolder {
            int position;
            TextView text;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.tv);
            }
        }
    }
}
