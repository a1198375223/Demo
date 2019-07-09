package com.example.media.presenter;

import android.view.KeyEvent;

import com.google.android.exoplayer2.ext.cast.MediaItem;

public interface IPlayerCastManager extends ICommonPlayerListener{
    // 重定向keyEvent事件
    boolean dispatchKeyEvent(KeyEvent keyEvent);

    // 将MediaItem添加到media queue.
    void addItem(MediaItem mediaItem);

    // 返回media queue的大小
    int getMediaQueueSize();

    // 选择指定position位置的playback
    void selectQueueItem(int position);

    // 返回当前播放的index, 如果当前没有视频是正在播放的则返回INDEX_UNSET
    int getCurrentItemIndex();

    // 返回指定位置的media item
    MediaItem getItem(int position);

    // 将item从当前位置移动到指定的位置
    boolean moveItem(MediaItem item, int to);

    // 移除指定位置的media item
    boolean removeItem(MediaItem item);
}
