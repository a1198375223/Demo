package com.example.media.activity;

import android.graphics.Matrix;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import androidx.lifecycle.LifecycleOwner;

import com.example.media.callback.SpeedControlCallback;
import com.example.media.media_player.MoviePlayer;
import com.example.room.mvp.BasePresenter;

import java.io.File;
import java.lang.ref.WeakReference;

public class PlayMoviePresenter extends BasePresenter {
    private static final String TAG = "PlayMoviePresenter";
    private MoviePlayer.PlayTask mPlayTask;
    private MoviePlayer mPlayer;
    private File mSourceFile;
    private Surface mOutputSurface;
    private int fps;

    public PlayMoviePresenter(LifecycleOwner owner, File sourceFile, Surface outputSurface) {
        super(owner);
        this.mSourceFile = sourceFile;
        this.mOutputSurface = outputSurface;
    }


    @Override
    public void pause(LifecycleOwner owner) {
        super.pause(owner);
        if (mPlayTask != null) {
            stopPlayback();
            mPlayTask.waitForStop();
        }
    }

    // 停止视频播放
    public void stopPlayback() {
        if (mPlayTask != null) {
            mPlayTask.requestStop();
        }
    }

    public void setFps(int fps) {
        this.fps = fps;
    }


    /**
     * 开始播放视频
     * @param textureView 播放的surface
     * @param isLoopMode 是否是循环模式
     * @param startCallback 开始播放的回调接口
     * @param stopCallback 停止的回调接口
     */
    public void startPlayback(TextureView textureView, boolean isLoopMode,
                              WeakReference<StartPlayCallback> startCallback,
                              WeakReference<MoviePlayer.PlayerFeedback> stopCallback) {
        if (mPlayTask != null) {
            Log.d(TAG, "movie already playing.");
            return;
        }

        Log.d(TAG, "start play movie.");
        if (stopCallback.get() == null) {
            Log.e(TAG, "Error, stop callback is null.");
            return;
        }

        SpeedControlCallback callback = new SpeedControlCallback();
        callback.setFixedPlaybackRate(fps);
        mPlayer = new MoviePlayer(mSourceFile, mOutputSurface, callback);
        adjustVideoSize(textureView);
        mPlayTask = new MoviePlayer.PlayTask(mPlayer, stopCallback.get());
        if (startCallback.get() != null) {
            startCallback.get().onStart();
        }
        mPlayTask.setLoopMode(isLoopMode);
        mPlayTask.execute();
    }

    public void adjustVideoSize(TextureView video) {
        int viewWidth = video.getWidth();
        int viewHeight = video.getHeight();
        int videoHeight = mPlayer.getVideoHeight();
        int videoWidth = mPlayer.getVideoWidth();

        double aspectRatio = (double) videoHeight / (double) videoWidth;

        int newHeight, newWidth;

        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            newWidth = viewWidth;
            newHeight = (int) (viewWidth * aspectRatio);

        } else {
            newWidth = (int) (viewHeight / aspectRatio);
            newHeight = viewHeight;
        }

        int xoff = (viewWidth - newWidth) / 2;
        int yoff = (viewHeight - newHeight) / 2;

        Log.v(TAG, "video=" + videoWidth + "x" + videoHeight +
                " view=" + viewWidth + "x" + viewHeight +
                " newView=" + newWidth + "x" + newHeight +
                " off=" + xoff + "," + yoff);

        Matrix txform = new Matrix();
        video.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
        txform.postTranslate(xoff, yoff);
        video.setTransform(txform);
    }

    // 手动释放资源
    public void release() {
        if (mPlayTask != null) {
            mPlayTask = null;
        }
    }


    public interface StartPlayCallback {
        void onStart();
    }
}
