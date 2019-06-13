package com.example.room.binding.util;

import java.util.TimerTask;

public interface Timer {

    // 重置
    void reset();

    // 开始任务
    void start(TimerTask task);

    // 获取当前的运行时间
    long getElapsedTime();

    // 更新暂停的时间
    void updatePausedTime();

    // 获取暂停的时间
    long getPausedTime();

    // 重置起始时间
    void resetStartTime();

    // 重置暂停时间
    void resetPauseTime();
}
