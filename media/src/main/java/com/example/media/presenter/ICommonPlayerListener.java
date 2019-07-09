package com.example.media.presenter;

public interface ICommonPlayerListener {
    void setListener(GalileoPlayerManager.IPlaybackListener listener);

    void release();
}
