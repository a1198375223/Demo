package com.example.media.callback;

import android.util.Log;

import com.example.media.media_player.MoviePlayer;

// 处理回调接口
public class SpeedControlCallback implements MoviePlayer.FrameCallback{
    private static final String TAG = "SpeedControlCallback";

    private static final boolean CHECK_SLEEP_TIME = false;

    private static final long ONE_MILLION = 1000000L;

    private long mPrevPresentUsec; // 上一次视频播放的时间
    private long mPrevMonoUsec; // 上一次视频递减的时间
    private long mFixedFrameDurationUsec; // 每帧的固定持续时间
    private boolean mLoopReset; // 是否是循环播放


    /**
     * 获取视频的时间，并持续递减时间
     * @param presentationTimeUsec 当前显示的时间
     */
    @Override
    public void preRender(long presentationTimeUsec) {
        Log.d(TAG, "preRender: presentationTimeUsec=" + presentationTimeUsec);
        if (mPrevMonoUsec == 0) {
            // 获取当前的时间
            mPrevMonoUsec = System.nanoTime() / 1000;
            mPrevPresentUsec = presentationTimeUsec;
        } else {
            long frameDelta;

            // 没懂
            if (mLoopReset) {
                mPrevPresentUsec = presentationTimeUsec - ONE_MILLION / 30;
                mLoopReset = false;
            }

            if (mFixedFrameDurationUsec != 0) {
                frameDelta = mFixedFrameDurationUsec;
            } else {
                frameDelta = presentationTimeUsec - mPrevPresentUsec;
            }

            if (frameDelta < 0) {
                Log.d(TAG, "Weird, video times went backward.");
                frameDelta = 0;
            } else if (frameDelta == 0) {
                Log.i(TAG, "Warning, current frame and previous frame had same timestamp.");
            } else if (frameDelta > 10 * ONE_MILLION) {
                Log.i(TAG, "Inter-frame pause was " + (frameDelta / ONE_MILLION) + "sec, capping at 5 sec");
                frameDelta = 5 * ONE_MILLION;
            }

            long desiredUsec = mPrevMonoUsec + frameDelta;
            long nowUsec = System.nanoTime() / 1000;
            while (nowUsec < (desiredUsec - 100)) {
                long sleepTimeUsec = desiredUsec - nowUsec;

                if (sleepTimeUsec > 500000) {
                    sleepTimeUsec = 500000;
                }
                try {
                    if (CHECK_SLEEP_TIME) {
                        long startNsec = System.nanoTime();
                        Thread.sleep(sleepTimeUsec / 1000, (int) ((sleepTimeUsec % 1000) * 1000));
                        long actualSleepNsec = System.nanoTime() - startNsec;
                        Log.d(TAG, "sleep=" + sleepTimeUsec + " actual=" + (actualSleepNsec/1000) +
                                " diff=" + (Math.abs(actualSleepNsec / 1000 - sleepTimeUsec)) +
                                " (usec)");
                    } else {
                        Thread.sleep(sleepTimeUsec / 1000, (int) (sleepTimeUsec % 1000) * 1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                nowUsec = System.nanoTime() / 1000;
            }

            mPrevMonoUsec += frameDelta;
            mPrevPresentUsec += frameDelta;
        }
    }

    @Override
    public void postRender() {
        Log.d(TAG, "postRender: ");
    }

    public void setFixedPlaybackRate(int fps) {
        mFixedFrameDurationUsec = ONE_MILLION / fps;
    }

    @Override
    public void loopReset() {
        Log.d(TAG, "loopReset: ");
        mLoopReset = true;
    }
}
