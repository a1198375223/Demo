package com.example.media.gsy.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.example.media.gsy.common.GSYVideoShotListener;
import com.example.media.gsy.common.GSYVideoShotSaveListener;
import com.example.media.gsy.common.IGSYSurfaceListener;
import com.example.media.gsy.render.GSYRenderView;
import com.example.media.gsy.render.GSYVideoGLView;
import com.example.media.gsy.render.GSYVideoGLViewBaseRender;
import com.example.media.gsy.helper.MeasureHelper;

import java.io.File;

public class GSYSurfaceView extends SurfaceView implements SurfaceHolder.Callback2, IGSYRenderView, MeasureHelper.MeasureFormVideoParamsListener {
    private static final String TAG = "GSYSurfaceView";

    private IGSYSurfaceListener mIGSYSurfaceListener;

    private MeasureHelper.MeasureFormVideoParamsListener mVideoParamsListener;

    private MeasureHelper measureHelper;

    public GSYSurfaceView(Context context) {
        super(context);
        init();
    }

    public GSYSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        measureHelper = new MeasureHelper(this, this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureHelper.prepareMeasure(widthMeasureSpec, heightMeasureSpec, (int) getRotation());
        setMeasuredDimension(measureHelper.getMeasuredWidth(), measureHelper.getMeasuredHeight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mIGSYSurfaceListener != null) {
            mIGSYSurfaceListener.onSurfaceAvailable(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mIGSYSurfaceListener != null) {
            mIGSYSurfaceListener.onSurfaceSizeChanged(holder.getSurface(), width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //清空释放
        if (mIGSYSurfaceListener != null) {
            mIGSYSurfaceListener.onSurfaceDestroyed(holder.getSurface());
        }
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
    }

    @Override
    public IGSYSurfaceListener getIGSYSurfaceListener() {
        return mIGSYSurfaceListener;
    }

    @Override
    public void setIGSYSurfaceListener(IGSYSurfaceListener surfaceListener) {
        getHolder().addCallback(this);
        this.mIGSYSurfaceListener = surfaceListener;
    }

    @Override
    public int getSizeH() {
        return getHeight();
    }

    @Override
    public int getSizeW() {
        return getWidth();
    }

    @Override
    public Bitmap initCover() {
        Log.d(TAG, "not support initCover now");
        return null;
    }

    @Override
    public Bitmap initCoverHigh() {
        Log.d(TAG, "not support initCoverHigh now");
        return null;
    }

    /**
     * 获取截图
     *
     * @param shotHigh 是否需要高清的
     */
    public void taskShotPic(GSYVideoShotListener gsyVideoShotListener, boolean shotHigh) {
        Log.d(TAG, "not support taskShotPic now");
    }

    /**
     * 保存截图
     *
     * @param high 是否需要高清的
     */
    public void saveFrame(final File file, final boolean high, final GSYVideoShotSaveListener gsyVideoShotSaveListener) {
        Log.d(TAG, "not support saveFrame now");
    }

    @Override
    public View getRenderView() {
        return this;
    }

    @Override
    public void onRenderResume() {
        Log.d(TAG, "not support onRenderResume now");
    }

    @Override
    public void onRenderPause() {
        Log.d(TAG, "not support onRenderPause now");
    }

    @Override
    public void releaseRenderAll() {
        Log.d(TAG, "not support releaseRenderAll now");
    }

    @Override
    public void setRenderMode(int mode) {
        Log.d(TAG, "not support setRenderMode now");
    }


    @Override
    public void setRenderTransform(Matrix transform) {
        Log.d(TAG, "not support setRenderTransform now");
    }

    @Override
    public void setGLRenderer(GSYVideoGLViewBaseRender renderer) {
        Log.d(TAG, "not support setGLRenderer now");
    }

    @Override
    public void setGLMVPMatrix(float[] MVPMatrix) {
        Log.d(TAG, "not support setGLMVPMatrix now");
    }

    /**
     * 设置滤镜效果
     */
    @Override
    public void setGLEffectFilter(GSYVideoGLView.ShaderInterface effectFilter) {
        Log.d(TAG, "not support setGLEffectFilter now");
    }


    @Override
    public void setVideoParamsListener(MeasureHelper.MeasureFormVideoParamsListener listener) {
        mVideoParamsListener = listener;
    }

    @Override
    public int getCurrentVideoWidth() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getCurrentVideoWidth();
        }
        return 0;
    }

    @Override
    public int getCurrentVideoHeight() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getCurrentVideoHeight();
        }
        return 0;
    }

    @Override
    public int getVideoSarNum() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getVideoSarNum();
        }
        return 0;
    }

    @Override
    public int getVideoSarDen() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getVideoSarDen();
        }
        return 0;
    }

    /**
     * 添加播放的view
     */
    public static GSYSurfaceView addSurfaceView(Context context, ViewGroup textureViewContainer, int rotate,
                                                final IGSYSurfaceListener gsySurfaceListener,
                                                final MeasureHelper.MeasureFormVideoParamsListener videoParamsListener) {
        if (textureViewContainer.getChildCount() > 0) {
            textureViewContainer.removeAllViews();
        }
        GSYSurfaceView showSurfaceView = new GSYSurfaceView(context);
        showSurfaceView.setIGSYSurfaceListener(gsySurfaceListener);
        showSurfaceView.setVideoParamsListener(videoParamsListener);
        showSurfaceView.setRotation(rotate);
        GSYRenderView.addToParent(textureViewContainer, showSurfaceView);
        return showSurfaceView;
    }

}