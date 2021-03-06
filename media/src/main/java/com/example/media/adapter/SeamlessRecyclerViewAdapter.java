package com.example.media.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.media.R;
import com.example.media.bean.VideoBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SeamlessRecyclerViewAdapter extends RecyclerView.Adapter<SeamlessRecyclerViewAdapter.VideoHolder> {

    private List<VideoBean> videos;
    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public SeamlessRecyclerViewAdapter(List<VideoBean> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    @NotNull
    @Override
    public VideoHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_video_seamless_play, parent, false);
        return new VideoHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NotNull final VideoHolder holder, int position) {
        VideoBean videoBean = videos.get(position);
        Glide.with(context)
                .load(videoBean.getThumb())
                .placeholder(android.R.color.white)
                .into(holder.mThumb);
        holder.title.setText(videoBean.getTitle());
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private FrameLayout mPlayerContainer;
        private ImageView mThumb;

        VideoHolder(View itemView) {
            super(itemView);
            mPlayerContainer = itemView.findViewById(R.id.player_container);
            mThumb = itemView.findViewById(R.id.thumb);
            int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
            mPlayerContainer.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels * 9 / 16 + 1));
            title = itemView.findViewById(R.id.tv_title);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
