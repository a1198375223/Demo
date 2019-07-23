package com.example.media.common;

/**
 * 用来管理视频播放进度
 */
public abstract class ProgressManager {
    public abstract void saveProgress(String url, long progress);

    public abstract long getSavedProgress(String url);
}
