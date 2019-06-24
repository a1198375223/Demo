package com.example.media.media_player;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.example.commonlibrary.utils.ThreadPool;
import com.example.commonlibrary.utils.Toasty;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * 封装一下原始的视频player
 */
public class MoviePlayer {
    private static final String TAG = "MoviePlayer";
    private File mSourceFile;
    private Surface mOutputSurface;
    private WeakReference<FrameCallback> mFrameCallback;

    // 是否要停止播放视频
    private volatile boolean mIsStopRequested = false;
    private MediaCodec.BufferInfo mBufferInfo;
    private boolean mLoop;
    private int mVideoHeight;
    private int mVideoWidth;

    public MoviePlayer(File sourceFile, Surface outputSurface, FrameCallback frameCallback) {
        this.mSourceFile = sourceFile;
        this.mOutputSurface = outputSurface;
        this.mFrameCallback = new WeakReference<>(frameCallback);

        MediaExtractor extractor = null;
        try {
            extractor = new MediaExtractor();
            extractor.setDataSource(sourceFile.toString());
            int trackIndex = selectTrack(extractor);

            if (trackIndex < 0) {
                Log.e(TAG, "No video tack found in " + mSourceFile);
            }
            extractor.selectTrack(trackIndex);

            MediaFormat format = extractor.getTrackFormat(trackIndex);

            mVideoHeight = format.getInteger(MediaFormat.KEY_HEIGHT);
            mVideoWidth = format.getInteger(MediaFormat.KEY_WIDTH);

            Log.d(TAG, "Video size is " + mVideoWidth + "x" + mVideoHeight);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (extractor != null) {
                extractor.release();
            }
        }
    }

    // 开始播放
    public void play() {
        // 视频加载器
        MediaExtractor extractor = null;
        // 编码解码器
        MediaCodec decoder = null;

        if (!mSourceFile.canRead()) {
            Toasty.showError("视频文件不可读!");
            return;
        }

        try {
            // 创建一个视频加载器
            extractor = new MediaExtractor();
            // 设置视频路径
            extractor.setDataSource(mSourceFile.toString());
            int trackIndex = selectTrack(extractor);
            if (trackIndex < 0) {
                Log.e(TAG, "无法寻找到视频轨道 file=" + mSourceFile);
            }
            // 为读取器设置轨道
            extractor.selectTrack(trackIndex);

            MediaFormat format = extractor.getTrackFormat(trackIndex);

            // 获取mime类型
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime == null) {
                Toasty.showError("无法识别mime类型");
                return;
            }
            decoder = MediaCodec.createDecoderByType(mime);
            decoder.configure(format, mOutputSurface, null, 0);
            // 开始解码
            decoder.start();

            // 播放视频 直到有停止消息
            doExtract(extractor, trackIndex, decoder, mFrameCallback.get());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (decoder != null) {
                decoder.stop();
                decoder.release();
            }

            if (extractor != null) {
                extractor.release();
            }
        }
    }


    /**
     * 开始读取视频源
     */
    private void doExtract(MediaExtractor extractor, int trackIndex, MediaCodec decoder, FrameCallback frameCallback) {
        // 设定超时时间10s
        int timeout_usec = 10000;
//        ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();

        int inputChunk = 0;
        long firstInputTimeNsec = -1;
        boolean outputDone = false;
        boolean inputDone = false;

        while (!outputDone) {
            Log.d(TAG, "loop begin");

            if (mIsStopRequested) {
                Log.d(TAG, "stop requested");
                return;
            }

            if (!inputDone) {
                // 这个方法返回输入队列数组可以放数据的位置,即一个索引
                int inputBufIndex = decoder.dequeueInputBuffer(timeout_usec);
                if (inputBufIndex >= 0) {
                    if (firstInputTimeNsec == -1) {
                        // 对firstInputTimeNsec重新赋值
                        firstInputTimeNsec = System.nanoTime();
                    }
                    // 获取单个buffer
                    ByteBuffer inputBuf = decoder.getInputBuffer(inputBufIndex);

                    if (inputBuf == null) {
                        Log.e(TAG, "decoder get input buffer error");
                        return;
                    }

                    // 获取chunk的大小
                    int chunkSize = extractor.readSampleData(inputBuf, 0);

                    if (chunkSize < 0) { // 如果chunk size是小于0,证明我们已经读取完毕这个轨道的数据了。
                        Log.d(TAG, "input done!");
                        // 告诉buffer读取结束
                        decoder.queueInputBuffer(inputBufIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                    } else {
                        if (extractor.getSampleTrackIndex() != trackIndex) {
                            Log.d(TAG, "Weird, the track from extractor=" + extractor.getSampleTrackIndex() + ", but the expected track=" + trackIndex);
                        }
                        long presentationTimeUs = extractor.getSampleTime();
                        decoder.queueInputBuffer(inputBufIndex, 0, chunkSize, presentationTimeUs, 0);
                        Log.d(TAG, "submit frame " + inputChunk + " to dec. size=" + chunkSize);
                        inputChunk++;
                        // Extractor移动一个sample的位置,下一次再调用extractor.readSampleData()就会读取下一个sample
                        extractor.advance();
                    }
                } else {
                    // 缓存满了 或者 缓存不可用了
                    Log.e(TAG, "input buffer not available");
                    Toasty.showError("缓存满了或者缓存不可用了!");
                    return;
                }
            }

            // 这层可以去掉这个判断, 但是为了好理解就保留了这个判断
            if (!outputDone) {
                if (mBufferInfo == null) {
                    mBufferInfo = new MediaCodec.BufferInfo();
                }
                int decoderStatus = decoder.dequeueOutputBuffer(mBufferInfo, timeout_usec);
                if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    Log.d(TAG, "decoder没有output了！");
                } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat newFormat = decoder.getOutputFormat();
                    Log.d(TAG, "decoder output format changed: " + newFormat);
                } else if (decoderStatus < 0) {
                    Log.d(TAG, "unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
                } else {
                    // 以上的处理都是异常现象, 这是正常的进行读取
                    if (firstInputTimeNsec != 0) {
                        long nowNsec = System.nanoTime();
                        // 记录从开始写入到读取需要的时间
                        Log.d(TAG, "startup lag " + ((nowNsec - firstInputTimeNsec) / 1000000.0) + " ms");
                        firstInputTimeNsec = 0;
                    }

                    boolean doLoop = false;
                    Log.d(TAG, "surface decoder given buffer " + decoderStatus + " (size=" + mBufferInfo.size + ")");

                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        // 如果读取结束了走这里
                        Log.d(TAG, "output EOS");
                        // 如果是循环模式 则进行循环播放, 如果不是循环模式 就结束播放
                        if (mLoop) {
                            doLoop = true;
                        } else {
                            outputDone = true;
                        }
                    }

                    // 判断是否需要渲染
                    boolean doRender = mBufferInfo.size != 0;

                    if (doRender && frameCallback != null) {
                        // 在渲染之前先回调接口
                        frameCallback.preRender(mBufferInfo.presentationTimeUs);
                    }

                    // 调用渲染接口 只要我们调用了decoder.releaseOutputBuffer()
                    // 就会把输出队列的数据全部输出到Surface上显示,并且释放输出队列的数据
                    decoder.releaseOutputBuffer(decoderStatus, doRender);

                    if (doRender && frameCallback != null) {
                        // 在渲染结束后调用接口
                        frameCallback.postRender();
                    }


                    if (doLoop) {
                        Log.d(TAG, "渲染结束, 循环播放");
                        // 循环 拉回播放起点
                        extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                        inputDone = false;
                        // 重置decoder
                        decoder.flush();
                        if (frameCallback != null) {
                            frameCallback.loopReset();
                        }
                    }
                }
            }
        }
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public void setLoopMode(boolean loopMode) {
        mLoop = loopMode;
    }

    public void requestStop() {
        mIsStopRequested = true;
    }

    /**
     * 寻找第一个是视频的轨道, 然后忽略其他的
     */
    private int selectTrack(MediaExtractor extractor) {
        // 获取加载器的轨道数量
        int numTracks = extractor.getTrackCount();
        // 寻找第一个是视频的轨道
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime != null && mime.startsWith("video/")) {
                Log.d(TAG, "Extractor 读取器选择了轨道=" + i + " mime类型=" + mime);
                return i;
            }
        }
        return -1;
    }


    // 帧回调的接口
    public interface FrameCallback {
        // 在帧开始渲染之前被调用
        void preRender(long presentationTimeUsec);

        // 在渲染帧调用返回后立即调用
        void postRender();

        // 在循环视频播放完最后一帧的时候调用.
        void loopReset();
    }


    public static class PlayTask implements Runnable {
        private static final int MSG_PLAY_STOPPED = 0;
        private PlayerFeedback mFeedback;
        private MoviePlayer mPlayer;
        private boolean mDoLoop;
        private LocalHandler mHandler;


        private final Object mStopLock = new Object();
        private boolean mStopped = false;


        public PlayTask(MoviePlayer player, PlayerFeedback feedback) {
            mPlayer = player;
            mFeedback = feedback;
            mHandler = new LocalHandler();
        }

        // 设置循环模式
        public void setLoopMode(boolean loopMode) {
            this.mDoLoop = loopMode;
        }

        // 开始执行任务
        public void execute() {
            mPlayer.setLoopMode(mDoLoop);
            // 交给线程池去处理当前的任务
            ThreadPool.runOnWorker(this);
        }

        // 停止
        public void requestStop() {
            mPlayer.requestStop();
        }

        // 等待视频播放停止
        public void waitForStop() {
            synchronized (mStopLock) {
                while (!mStopped) {
                    try {
                        mStopLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void run() {
            mPlayer.play();
            synchronized (mStopLock) {
                mStopped = true;
                mStopLock.notifyAll();
            }

            mHandler.sendMessage(mHandler.obtainMessage(MSG_PLAY_STOPPED, mFeedback));
        }

        private static class LocalHandler extends Handler {
            @Override
            public void handleMessage(@NonNull Message msg) {
                int what = msg.what;

                switch (what) {
                    case MSG_PLAY_STOPPED:
                        PlayerFeedback playerFeedback = (PlayerFeedback) msg.obj;
                        playerFeedback.playbackStopped();
                        break;
                    default:
                        Log.e(TAG, "unknown msg " + msg);
                }
            }
        }
    }


    public interface PlayerFeedback {
        void playbackStopped();
    }
}
