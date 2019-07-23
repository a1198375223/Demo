package com.example.media.ijk;


import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.lifecycle.LifecycleOwner;


import com.example.commonlibrary.utils.AppUtils;
import com.example.commonlibrary.utils.ThreadPool;
import com.example.media.common.AbstractPlayer;
import com.example.media.factory.IjkPlayerFactory;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 *
 */
public class IjkPlayerManager extends AbstractPlayer {
    private static final String TAG = "IjkPlayerManager";
    protected IjkMediaPlayer mMediaPlayer;
    private boolean mIsLooping;
    private boolean mIsEnableMediaCodec;
    private int mBufferedPercent;
    private Context mAppContext;

    public IjkPlayerManager(LifecycleOwner owner) {
        super(owner);
        mAppContext = AppUtils.app().getApplicationContext();
    }

    @Override
    public void initPlayer() {
        mMediaPlayer = new IjkMediaPlayer();
        // 设置日志
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        setOptions();
        // 设置音频流模式
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置监听器
        mMediaPlayer.setOnErrorListener(onErrorListener);
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
        mMediaPlayer.setOnInfoListener(onInfoListener);
        mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mMediaPlayer.setOnPreparedListener(onPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        mMediaPlayer.setOnNativeInvokeListener((i, bundle) -> true);
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) {
        try {
            Uri uri = Uri.parse(path);
            if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(uri.getScheme())) {
                RawDataSourceProvider rawDataSourceProvider = RawDataSourceProvider.create(mAppContext, uri);
                mMediaPlayer.setDataSource(rawDataSourceProvider);
            } else {
                mMediaPlayer.setDataSource(mAppContext, uri, headers);
            }
        } catch (IOException e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError();
            }
            mState = State.ERROR;
        }
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        try {
            mMediaPlayer.setDataSource(new RawDataSourceProvider(fd));
        } catch (Exception e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError();
            }
            mState = State.ERROR;
        }
    }

    @Override
    public void start() {
        try {
            mMediaPlayer.start();
            mState = State.PLAYING;
        } catch (IllegalStateException e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError();
            }
            mState = State.ERROR;
        }
    }

    @Override
    public void pause() {
        try {
            mState = State.PAUSED;
            mMediaPlayer.pause();
        } catch (IllegalStateException e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError();
            }
            mState = State.ERROR;
        }
    }

    @Override
    public void stop() {
        try {
            mState = State.IDLE;
            mMediaPlayer.stop();
        } catch (IllegalStateException e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError();
            }
            mState = State.ERROR;
        }
    }

    @Override
    public void prepareAsync() {
        try {
            mState = State.PREPARING;
            mMediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError();
            }
            mState = State.ERROR;
        }
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        mMediaPlayer.setLooping(mIsLooping);
        setOptions();
        setEnableMediaCodec(mIsEnableMediaCodec);
        mState = State.IDLE;
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public boolean isPaused() {
        return mState == State.PAUSED;
    }

    @Override
    public boolean isStopped() {
        return mState == State.IDLE;
    }

    @Override
    public void seekTo(long time) {
        try {
            mMediaPlayer.seekTo(time);
        } catch (IllegalStateException e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError();
            }
            mState = State.ERROR;
        }
    }

    @Override
    public void release() {
        ThreadPool.runOnIOPool(() -> {
            try {
                mMediaPlayer.release();
                mState = State.IDLE;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getBufferedPercentage() {
        return mBufferedPercent;
    }

    @Override
    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void setVolume(float v1, float v2) {
        mMediaPlayer.setVolume(v1, v2);
    }

    @Override
    public void setLooping(boolean isLooping) {
        this.mIsLooping = isLooping;
        mMediaPlayer.setLooping(isLooping);
    }

    @Override
    public void setEnableMediaCodec(boolean isEnable) {
        this.mIsEnableMediaCodec = isEnable;
        int value = isEnable ? 1 : 0;
        // 开启硬解码
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value);
    }

    @Override
    public void setOptions() {

    }

    @Override
    public void setSpeed(float speed) {
        mMediaPlayer.setSpeed(speed);
    }

    @Override
    public long getTcpSpeed() {
        return mMediaPlayer.getTcpSpeed();
    }


    /**
     * 视频播放发生错误的时候会被调用
     */
    private IMediaPlayer.OnErrorListener onErrorListener = (iMediaPlayer, framework_err, impl_err) -> {
        if (mPlayerEventListener != null) {
            mPlayerEventListener.onError();
        }
        mState = State.ERROR;
        return true;
    };


    /**
     * 视频播放完成的时候会被调用
     */
    private IMediaPlayer.OnCompletionListener onCompletionListener = iMediaPlayer -> {
        if (mPlayerEventListener != null) {
            mPlayerEventListener.onCompletion();
        }
        mState = State.COMPLETED;
    };

    /**
     * 处理第一帧、缓冲的时候会被调用
     */
    private IMediaPlayer.OnInfoListener onInfoListener = (iMediaPlayer, what, extra) -> {
        if (mPlayerEventListener != null) {
            mPlayerEventListener.onInfo(what, extra);
        }
        switch (what) {
            case AbstractPlayer.MEDIA_INFO_BUFFERING_START:
                mState = State.BUFFERING;
                break;
            case AbstractPlayer.MEDIA_INFO_BUFFERING_END:
                mState = State.BUFFERED;
                break;
            case AbstractPlayer.MEDIA_INFO_VIDEO_RENDERING_START: // 视频开始渲染
                mState = State.PLAYING;
                break;
        }
        return true;
    };

    /**
     * 缓冲的时候会被调用
     */
    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
            mBufferedPercent = percent;
        }
    };


    /**
     * 视频准备完成的时候会被调用
     */
    private IMediaPlayer.OnPreparedListener onPreparedListener = iMediaPlayer -> {
        mPlayerEventListener.onPrepared();
        mState = State.PREPARED;
    };

    /**
     * 当视频大小改变的时候被调用
     */
    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = (iMediaPlayer, i, i1, i2, i3) -> {
        int videoWidth = iMediaPlayer.getVideoWidth();
        int videoHeight = iMediaPlayer.getVideoHeight();
        if (videoHeight != 0 && videoWidth != 0 && mPlayerEventListener != null) {
            mPlayerEventListener.onVideoSizeChanged(videoWidth, videoHeight);
        }
    };
}
