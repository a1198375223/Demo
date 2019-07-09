package com.example.media.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.example.media.presenter.IPlayerCastManager;
import com.google.android.exoplayer2.ext.cast.MediaItem;

public class MediaQueueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MediaQueueAdapter";
    private IPlayerCastManager manager;

    public MediaQueueAdapter(IPlayerCastManager manager) {
        this.manager = manager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QueueItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof QueueItemViewHolder) {
            ((QueueItemViewHolder) holder).item = manager.getItem(position);
            TextView view = ((QueueItemViewHolder) holder).textView;
            view.setText(((QueueItemViewHolder) holder).item.title);
            view.setTextColor(ColorUtils.setAlphaComponent(view.getCurrentTextColor(), position == manager.getCurrentItemIndex() ? 255 : 100));
        }
    }

    @Override
    public int getItemCount() {
        return manager.getMediaQueueSize();
    }


    class QueueItemViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        MediaItem item;

        public QueueItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = (TextView) itemView;
            textView.setOnClickListener(view -> manager.selectQueueItem(getAdapterPosition()));
        }
    }
}
