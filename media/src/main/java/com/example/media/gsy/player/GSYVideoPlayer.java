package com.example.media.gsy.player;

import android.content.Context;
import android.util.AttributeSet;

import com.example.media.gsy.common.GSYVideoViewBridge;
import com.example.media.gsy.manager.GSYVideoManager;

public abstract class GSYVideoPlayer extends GSYBaseVideoPlayer {

    public GSYVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public GSYVideoPlayer(Context context) {
        super(context);
    }

    public GSYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GSYVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*******************************下面方法为管理器和播放控件交互的方法****************************************/

    @Override
    public GSYVideoViewBridge getGSYVideoManager() {
        GSYVideoManager.instance().initContext(getContext().getApplicationContext());
        return GSYVideoManager.instance();
    }

    @Override
    protected boolean backFromFull(Context context) {
        return GSYVideoManager.backFromWindowFull(context);
    }

    @Override
    protected void releaseVideos() {
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    protected int getFullId() {
        return GSYVideoManager.FULLSCREEN_ID;
    }

    @Override
    protected int getSmallId() {
        return GSYVideoManager.SMALL_ID;
    }

}
