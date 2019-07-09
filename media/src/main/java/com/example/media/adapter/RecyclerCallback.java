package com.example.media.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.media.presenter.IPlayerCastManager;
import com.google.android.exoplayer2.C;

public class RecyclerCallback extends ItemTouchHelper.SimpleCallback{
    private static final String TAG = "RecyclerCallback";

    private int draggingFromPosition;
    private int draggingToPosition;
    private MediaQueueAdapter adapter;
    private IPlayerCastManager manager;

    public RecyclerCallback(MediaQueueAdapter adapter, IPlayerCastManager manager) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END);
        draggingFromPosition = C.INDEX_UNSET;
        draggingToPosition = C.INDEX_UNSET;
        this.adapter = adapter;
        this.manager = manager;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        Log.d(TAG, "onMove: from->to: (" + fromPosition + "->" + toPosition + ")");
        if (draggingFromPosition == C.INDEX_UNSET) {
            draggingFromPosition = fromPosition;
        }

        draggingToPosition = toPosition;
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        Log.d(TAG, "onSwiped: remove position: " + position);
        MediaQueueAdapter.QueueItemViewHolder holder = ((MediaQueueAdapter.QueueItemViewHolder) viewHolder);
        if (manager.removeItem(holder.item)) {
            adapter.notifyItemRemoved(position);
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        Log.d(TAG, "clearView: draggingFromPosition: " + draggingFromPosition + " draggingToPosition: " + draggingToPosition);
        if (draggingFromPosition != C.INDEX_UNSET) {
            MediaQueueAdapter.QueueItemViewHolder holder = ((MediaQueueAdapter.QueueItemViewHolder) viewHolder);
            if (!manager.moveItem(holder.item, draggingToPosition)) {
                adapter.notifyDataSetChanged();
            }
        }

        draggingFromPosition = C.INDEX_UNSET;
        draggingToPosition = C.INDEX_UNSET;

    }
}
