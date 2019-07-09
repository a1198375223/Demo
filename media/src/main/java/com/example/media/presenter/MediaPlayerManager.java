package com.example.media.presenter;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.lifecycle.LifecycleOwner;

import com.example.commonlibrary.utils.AppUtils;
import com.example.commonlibrary.utils.ThreadPool;
import com.example.commonlibrary.utils.UriUtils;
import com.example.media.callback.IMediaPlayer;
import com.example.media.callback.PlaybackInfoListener;
import com.example.room.mvp.BasePresenter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * media状态机
 *
 *                                       reset() -> {IDLE}  -> release() -> {END}
 *                                                    |
 *                                                    v
 *                                              setDataSource()
 *                                                    |
 *                                                    v
 *             {Preparing} <- prepareAsync() <- {Initialized}
 *                  ^    \                            |
 *                  | onPreparedListener.onPrepared   |
 *                  |            \                    |
 *                  |             v                   |
 *                  |   (seekTo){Prepared} <------prepared()
 *         prepareAsync()         ^ /  |
 *                  ^          / /     |
 *                  |       / /      start()
 *                  |    / /           |
 *                  |  / v             |
 *           --> {STOP}                v
 *          |       |  ^           {STARTED} (seekTo(), start(),
 *           --stop()   \           / ^ \ ^         Looping=true && playback completes)
 *                       \         |  |    \ \
 *                        \        |  |       \ \
 *                       stop()    |  |          \ \
 *                         \       |  |            \ \
 *                          \      |  |              \ \
 *                           \     v  |                v \
 *                  {PlaybackCompleted}               {Paused} (seekTo() pause())
 *                   |              ^                    |
 *                   |              |                    stop()---------> {STOP}
 *                    ---seekTo()---
 */
public class MediaPlayerManager extends BasePresenter implements IMediaPlayer {
    private static final String TAG = "MediaPlayerManager";
    private MediaPlayer mMediaPlayer;
    private PlaybackInfoListener mPlaybackInfoListener;
    private String mDataSource;
    private Runnable mPositionTask;
    private long duration = -1;
    private ScheduledFuture future;
    private int mState = PlaybackInfoListener.State.RESET;

    public MediaPlayerManager(LifecycleOwner owner) {
        super(owner);

        logToUI("MediaPlayer mMediaPlayer = new MediaPlayer();");
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void release() {
        stopUpdatePosition(false);
        if (mMediaPlayer != null) {
            Log.d(TAG, "release");
            logToUI("release() and MediaPlayer = null");
            mMediaPlayer.reset();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void play() {
        if (mMediaPlayer == null) {
            Log.e(TAG, "Must init MediaPlayer first.");
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            Log.d(TAG, "Has one Video playing.");
            return;
        }

        Log.d(TAG, "play");
        logToUI(String.format(Locale.CHINA, "playbackStart() %s", mDataSource));
        mMediaPlayer.start();
        mState = PlaybackInfoListener.State.PLAYING;
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onStateChanged(mState);
        }
        // 开始监听
        startUpdatePosition();
    }

    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            Log.d(TAG, "reset");
            logToUI("playbackReset()");
            mMediaPlayer.reset();
            mState = PlaybackInfoListener.State.RESET;
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(mState);
            }

            // 重置之后要重新setDataSource()
            if (TextUtils.isEmpty(mDataSource)) {
                Log.e(TAG, "Data source may destroy.");
                return;
            }
            setDataSource(mDataSource);

            // 停止监听 并重置到0
            stopUpdatePosition(true);
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mState = PlaybackInfoListener.State.PAUSED;
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(mState);
            }
            Log.d(TAG, "pause");
            logToUI("playbackPause()");
            stopUpdatePosition(false);
        }
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            Log.d(TAG, String.format(Locale.CHINA, "seekTo() %d ms", position));
            logToUI(String.format(Locale.CHINA, "seekTo() %d ms", position));
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public void setDataSource(String path) {
        Log.d(TAG, "setDataSource path=" + path);
        if (TextUtils.isEmpty(path) && (mMediaPlayer != null && mMediaPlayer.isPlaying())) {
            Log.d(TAG, "Video is playing now.");
            logToUI("Video is playing now.");
            return;
        }

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }

        mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            // 播放结束的回调
            stopUpdatePosition(true);
            Log.d(TAG, "MediaPlayer playback completed.");
            logToUI("MediaPlayer playback completed.");
            mState = PlaybackInfoListener.State.COMPLETED;
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(mState);
                mPlaybackInfoListener.onPlaybackCompleted();
            }

            // 如果要循环播放 直接调用play
//                if (mMediaPlayer.isLooping()) {
//                    Log.d(TAG, "looping play.");
//                    play();
//                }
        });

        mMediaPlayer.setOnErrorListener((mediaPlayer, what, extra) -> {
            // 需要reset
            Log.e(TAG, "MediaPlayer move to error state, error message: what=" + what + " extra=" + extra);
            return false;
        });


        mMediaPlayer.setOnPreparedListener(mediaPlayer -> {
            // 当使用prepareAsync()方法会回调这个方法
            Log.d(TAG, "MediaPlayer prepared.");
        });

        mMediaPlayer.setOnSeekCompleteListener(mediaPlayer -> {
            // seek调用的时候被调用
            Log.d(TAG, "MediaPlayer seekTo.");
        });

        mMediaPlayer.setOnInfoListener((mediaPlayer, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                // 第一帧
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // 缓冲
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 开始/恢复播放
            } else {
                Log.d(TAG, "Invalid what: " + what);
            }
            return true;
        });

        mMediaPlayer.setOnBufferingUpdateListener((mediaPlayer, progress) -> {
            // 缓冲时候调用
            Log.d(TAG, "MediaPlayer buffer progress: " + progress);
        });


        mMediaPlayer.setOnVideoSizeChangedListener((mediaPlayer, width, height) -> {
            // 视频宽高改变的时候调用
            Log.d(TAG, "MediaPlayer video size change (" + width + "x" + height + ")");
        });

        // 本地文件 资源文件
        if (path.toLowerCase().startsWith("file:")) { // 手机本地文件
            try {
                File file = new File(new URI(path));
                mDataSource = file.getAbsolutePath();
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    FileDescriptor fileDescriptor = fis.getFD();
                    mMediaPlayer.setDataSource(fileDescriptor);
                    mMediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else if (path.toLowerCase().startsWith("http:") || path.toLowerCase().startsWith("https:")) { // 网络文件
            mDataSource = path;

            try {
                mMediaPlayer.setDataSource(AppUtils.app(), Uri.parse(mDataSource));
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (path.toLowerCase().startsWith("android:")) { // 资源文件
            mDataSource = path;
            AssetFileDescriptor afd = null;
            try {
                Log.d(TAG, "parse uri path -----> resId: " + UriUtils.parseToResourceId(path));
                afd = AppUtils.app().getResources().openRawResourceFd(UriUtils.parseToResourceId(path));
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (afd != null) {
                    try {
                        afd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Log.e(TAG, "Invalid argument path: " + path);
            throw new IllegalArgumentException("Invalid argument path: " + path);
        }

        logToUI("1. setDataSource dataSource: " + mDataSource);
        logToUI("2. prepare");

        duration = mMediaPlayer.getDuration();
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onDurationChanged(duration);
            mPlaybackInfoListener.onPositionChanged(0);
            logToUI(String.format(Locale.CHINA, "firing setPlaybackDuration(%d sec)", TimeUnit.MILLISECONDS.toSeconds(duration)));
            logToUI("firing setPlaybackPosition(0)");
        }
    }

    // 获取视频播放的进度
    @Override
    public long getCurrentPosition() {
        if (mMediaPlayer != null) {
            Log.d(TAG, "Current position=" + mMediaPlayer.getCurrentPosition());
            return mMediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    @Override
    public int getVideoWidth() {
        if (mMediaPlayer != null) {
            Log.d(TAG, "Video width=" + mMediaPlayer.getVideoWidth());
            return mMediaPlayer.getVideoWidth();
        }
        return -1;
    }

    @Override
    public int getVideoHeight() {
        if (mMediaPlayer != null) {
            Log.d(TAG, "Video height=" + mMediaPlayer.getVideoHeight());
            return mMediaPlayer.getVideoHeight();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        if (checkMediaPlayerIsNull()) {
            return;
        }

        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void setSurface(Surface surface) {
        if (checkMediaPlayerIsNull()) {
            return;
        }

        mMediaPlayer.setSurface(surface);
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (checkMediaPlayerIsNull()) {
            return;
        }

        mMediaPlayer.setScreenOnWhilePlaying(screenOn);
    }

    @Override
    public boolean isPaused() {
        if (mMediaPlayer != null) {
            return mState == PlaybackInfoListener.State.PAUSED;
        }
        return false;
    }

    @Override
    public boolean isStopped() {
        if (mMediaPlayer != null) {
            return mState == PlaybackInfoListener.State.COMPLETED;
        }
        return false;
    }

    @Override
    public void setLooping(boolean looping) {
        if (checkMediaPlayerIsNull()) {
            return;
        }

        mMediaPlayer.setLooping(looping);
    }

    // 设置回调监听器
    public void setPlaybackInfoListener(PlaybackInfoListener listener) {
        mPlaybackInfoListener = listener;
    }

    // 将日志发送到UI中
    private void logToUI(String message) {
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onLogUpdated(message);
        }
    }


    // 开始对进度进行监听
    private void startUpdatePosition() {
        if (mPositionTask == null) {
            mPositionTask = () -> {
                Log.d(TAG, "position task current thread is main? " + (Looper.myLooper() == Looper.getMainLooper()));
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    int currentPosition = mMediaPlayer.getCurrentPosition();
                    if (mPlaybackInfoListener != null) {
                        mPlaybackInfoListener.onPositionChanged(currentPosition);
                    }
                }
            };
        }

        // 每隔1秒执行一次
        future = ThreadPool.scheduleAtFixedTime(mPositionTask, 0, 1);
    }


    // 停止监听
    private void stopUpdatePosition(boolean resetToOriginPosition) {
        if (mPositionTask != null && future != null) {
            Log.d(TAG, "Stop update position");
            logToUI("Stop update position");
            // 关闭任务
            future.cancel(false);
            mPositionTask = null;
            if (resetToOriginPosition && mPlaybackInfoListener != null) {
                Log.d(TAG, "reset to 0");
                logToUI("Reset position to 0.");
                mPlaybackInfoListener.onPositionChanged(0);
            }
        }
    }


    // 设置音频的参数 在prepare() 或者 prepareAsync() 方法之前调用
    public void setAudioAttributes(AudioAttributes audioAttributes) {
        if (checkMediaPlayerIsNull()) {
            return;
        }

        mMediaPlayer.setAudioAttributes(audioAttributes);
    }

    // 设置会话id 在setDataSource()之前调用
    public void setAudioSessionId(int sessionId) {
        if (checkMediaPlayerIsNull()) {
            return;
        }

        mMediaPlayer.setAudioSessionId(sessionId);
    }

    // 设置保活模式
    public void setWakeMode(int wakeMode) {
        if (checkMediaPlayerIsNull()) {
            return;
        }

        mMediaPlayer.setWakeMode(AppUtils.app(), wakeMode);
    }


    // 设置视频的缩放模式
    public void setVideoScalingMode(int mode) {
        if (checkMediaPlayerIsNull()) {
            return;
        }

        mMediaPlayer.setVideoScalingMode(mode);
    }


    // 判断MediaPlayer是否为null
    private boolean checkMediaPlayerIsNull() {
        if (mMediaPlayer == null) {
            Log.e(TAG, "Must init MediaPlayer first.");
            logToUI("Must init MediaPlayer first.");
            return true;
        }
        return false;
    }
}
