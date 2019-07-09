package com.example.media.callback;

import android.view.Surface;
import android.view.SurfaceHolder;

public interface IMediaPlayer {
    // 释放资源
    void release();

    // 是否正在播放
    boolean isPlaying();

    // 播放
    void play();

    // 重置
    void reset();

    // 暂停
    void pause();

    // 拖动到指定位置
    void seekTo(int position);

    // 设置视频源
    void setDataSource(String path);

    // 获取当前position
    long getCurrentPosition();

    // 获取视频的宽
    int getVideoWidth();

    // 获取视频的高
    int getVideoHeight();

    // 获取视频的时长
    long getDuration();

    // 设置SurfaceHolder
    void setDisplay(SurfaceHolder holder);

    // 设置surface
    void setSurface(Surface surface);

    // 设置屏幕长亮
    void setScreenOnWhilePlaying(boolean screenOn);

    // 是否是pause状态
    boolean isPaused();

    // 是否是停止状态
    boolean isStopped();

    // 设置是否循环播放
    void setLooping(boolean looping);
}
