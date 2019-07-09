package com.example.media.callback;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 播放的回调接口
public abstract class PlaybackInfoListener {
    @IntDef({State.INVALID, State.PLAYING, State.PAUSED, State.RESET, State.COMPLETED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
        int INVALID = -1;
        int PLAYING = 0;
        int PAUSED = 1;
        int RESET = 2;
        int COMPLETED = 3;
    }


    public static String convertStateToString(@State int state) {
        String stateString;
        switch (state) {
            case State.COMPLETED:
                stateString = "COMPLETED";
                break;
            case State.INVALID:
                stateString = "INVALID";
                break;
            case State.PAUSED:
                stateString = "PAUSED";
                break;
            case State.PLAYING:
                stateString = "PLAYING";
                break;
            case State.RESET:
                stateString = "RESET";
                break;
            default:
                stateString = "N/A";
        }
        return stateString;
    }

    // 更新日志
    public void onLogUpdated(String formattedMessage) {}

    // 当时长改变的时候回调
    public void onDurationChanged(long duration) {}

    // 播放进度改变的时候回调
    public void onPositionChanged(int position) {}

    // 播放器状态改变的时候回调
    public void onStateChanged(@State int state) {}

    // 播放结束的回调
    public void onPlaybackCompleted() {}
}
