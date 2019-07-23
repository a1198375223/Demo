package com.example.media.common;

/**
 * 状态改变的回调方法
 */
public interface OnVideoViewStateChangeListener {
    void onPlayerStateChanged(int playerState);
    void onPlayStateChanged(int playState);
}
