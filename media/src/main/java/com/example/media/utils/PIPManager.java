package com.example.media.utils;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.commonlibrary.utils.AppUtils;
import com.example.media.view.FloatController;
import com.example.media.view.FloatView;
import com.example.media.view.VideoView;

/**
 * 悬浮播放
 */
public class PIPManager {
    private static final String TAG = "PIPManager";
    private static PIPManager instance;
    private VideoView mVideoView;
    private FloatView floatView;
    private FloatController mFloatController;
    private boolean isShowing;
    //    private KeyReceiver mKeyReceiver;
    private int mPlayingPosition = -1;
    private Class mActClass;
//    private MyVideoListener mMyVideoListener = new MyVideoListener() {
//        @Override
//        public void onComplete() {
//            super.onComplete();
//            reset();
//        }
//    };


    private PIPManager() {
        mVideoView = new VideoView(AppUtils.app());
//        mVideoView.setVideoListener(mMyVideoListener);
//        mKeyReceiver = new KeyReceiver();
        mFloatController = new FloatController(AppUtils.app());
//        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        AppUtils.app().registerReceiver(mKeyReceiver, homeFilter);
        floatView = new FloatView(AppUtils.app(), 0, 0);
    }

    public static PIPManager getInstance() {
        if (instance == null) {
            synchronized (PIPManager.class) {
                if (instance == null) {
                    instance = new PIPManager();
                }
            }
        }
        return instance;
    }

    public VideoView getVideoView() {
        return mVideoView;
    }

    public void startFloatWindow() {
        Log.d(TAG, "startFloatWindow: isShowing: " + isShowing);
        if (isShowing) return;
        removePlayerFormParent();
        mFloatController.setPlayState(mVideoView.getCurrentPlayState());
        mFloatController.setPlayerState(mVideoView.getCurrentPlayerState());
        mVideoView.setVideoController(mFloatController);
        floatView.addView(mVideoView);
        floatView.addToWindow();
        isShowing = true;
    }

    public void stopFloatWindow() {
        Log.d(TAG, "stopFloatWindow: isShowing: " + isShowing);
        if (!isShowing) return;
        floatView.removeFromWindow();
        removePlayerFormParent();
        isShowing = false;
    }

    private void removePlayerFormParent() {
        Log.d(TAG, "removePlayerFormParent: ");
        ViewParent parent = mVideoView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mVideoView);
        }
    }

    public void setPlayingPosition(int position) {
        this.mPlayingPosition = position;
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    public void pause() {
        Log.d(TAG, "pause: isShowing: " + isShowing);
        if (isShowing) return;
        mVideoView.pause();
    }

    public void resume() {
        Log.d(TAG, "resume: isShowing: " + isShowing);
        if (isShowing) return;
        mVideoView.resume();
    }

    public void reset() {
        Log.d(TAG, "reset: isShowing: " + isShowing);
        if (isShowing) return;
        removePlayerFormParent();
        mVideoView.setVideoController(null);
        mVideoView.release();
        mPlayingPosition = -1;
        mActClass = null;
    }

    public boolean onBackPress() {
        return !isShowing && mVideoView.onBackPressed();
    }

    public boolean isStartFloatWindow() {
        return isShowing;
    }

    /**
     * 显示悬浮窗
     */
    public void setFloatViewVisible() {
        if (isShowing) {
            mVideoView.resume();
            floatView.setVisibility(View.VISIBLE);
        }
    }

    public void setActClass(Class cls) {
        this.mActClass = cls;
    }

    public Class getActClass() {
        return mActClass;
    }

}
