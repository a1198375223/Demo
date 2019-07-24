package com.example.media.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.media.R;
import com.example.media.bean.VideoBean;
import com.example.media.view.StandardVideoController;
import com.example.media.view.VideoView;

import java.util.List;

public class VideoListViewAdapter extends BaseAdapter {

    private List<VideoBean> videos;

    public VideoListViewAdapter(List<VideoBean> videos) {
        this.videos = videos;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        VideoBean videoBean = videos.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mVideoView.setUrl(videoBean.getUrl());
        viewHolder.mVideoView.setVideoController(viewHolder.controller);
        viewHolder.controller.setTitle(videoBean.getTitle());
        Glide.with(viewHolder.controller.getThumb().getContext())
                .load(videoBean.getThumb())
                .placeholder(android.R.color.darker_gray)
                .into(viewHolder.controller.getThumb());
        return convertView;
    }


    private class ViewHolder {
        private VideoView mVideoView;
        private StandardVideoController controller;

        ViewHolder(View itemView) {
            this.mVideoView = itemView.findViewById(R.id.video_player);
            int widthPixels = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
            mVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels * 9 / 16 + 1));
            controller = new StandardVideoController(itemView.getContext());
        }
    }
}
