package com.example.media.gsy.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.example.media.gsy.common.GSYVideoShotListener;
import com.example.media.gsy.common.GSYVideoShotSaveListener;
import com.example.media.gsy.common.IGSYSurfaceListener;
import com.example.media.gsy.render.GSYRenderView;
import com.example.media.gsy.render.GSYVideoGLView;
import com.example.media.gsy.render.GSYVideoGLViewBaseRender;
import com.example.media.gsy.utils.FileUtils;
import com.example.media.gsy.helper.MeasureHelper;

import java.io.File;

public class GSYTextureView extends TextureView implements TextureView.SurfaceTextureListener, IGSYRenderView, MeasureHelper.MeasureFormVideoParamsListener {
    private static final String TAG = "GSYTextureView";

    private IGSYSurfaceListener mIGSYSurfaceListener;

    private MeasureHelper.MeasureFormVideoParamsListener mVideoParamsListener;

    private MeasureHelper measureHelper;

    private SurfaceTexture mSaveTexture;
    private Surface mSurface;

    public GSYTextureView(Context context) {
        super(context);
        init();
    }

    public GSYTextureView(Context context, AttributeSet attrs) {
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
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSaveTexture == null) {
            Log.d(TAG, "FFFFFFFFFFFF 1");
            mSaveTexture = surface;
            mSurface = new Surface(surface);
            if (mIGSYSurfaceListener != null) {
                mIGSYSurfaceListener.onSurfaceAvailable(mSurface);
            }
        } else {
            setSurfaceTexture(mSaveTexture);
            Log.d(TAG, "FFFFFFFFFFFF 2");
            //mSurface = new Surface(mSaveTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mIGSYSurfaceListener != null) {
            mIGSYSurfaceListener.onSurfaceSizeChanged(mSurface, width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        //清空释放
        if (mIGSYSurfaceListener != null) {
            mIGSYSurfaceListener.onSurfaceDestroyed(mSurface);
        }
        return (mSaveTexture == null);
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //如果播放的是暂停全屏了
        if (mIGSYSurfaceListener != null) {
            mIGSYSurfaceListener.onSurfaceUpdated(mSurface);
        }
    }

    @Override
    public IGSYSurfaceListener getIGSYSurfaceListener() {
        return mIGSYSurfaceListener;
    }

    @Override
    public void setIGSYSurfaceListener(IGSYSurfaceListener surfaceListener) {
        setSurfaceTextureListener(this);
        mIGSYSurfaceListener = surfaceListener;
    }

    @Override
    public int getSizeH() {
        return getHeight();
    }

    @Override
    public int getSizeW() {
        return getWidth();
    }

    /**
     * 暂停时初始化位图
     */
    @Override
    public Bitmap initCover() {
        Bitmap bitmap = Bitmap.createBitmap(
                getSizeW(), getSizeH(), Bitmap.Config.RGB_565);
        return getBitmap(bitmap);

    }

    /**
     * 暂停时初始化位图
     */
    @Override
    public Bitmap initCoverHigh() {
        Bitmap bitmap = Bitmap.createBitmap(
                getSizeW(), getSizeH(), Bitmap.Config.ARGB_8888);
        return getBitmap(bitmap);

    }


    /**
     * 获取截图
     *
     * @param shotHigh 是否需要高清的
     */
    @Override
    public void taskShotPic(GSYVideoShotListener gsyVideoShotListener, boolean shotHigh) {
        if (shotHigh) {
            gsyVideoShotListener.getBitmap(initCoverHigh());
        } else {
            gsyVideoShotListener.getBitmap(initCover());
        }
    }

    /**
     * 保存截图
     *
     * @param high 是否需要高清的
     */
    @Override
    public void saveFrame(final File file, final boolean high, final GSYVideoShotSaveListener gsyVideoShotSaveListener) {
        GSYVideoShotListener gsyVideoShotListener = bitmap -> {
            if (bitmap == null) {
                gsyVideoShotSaveListener.result(false, file);
            } else {
                FileUtils.saveBitmap(bitmap, file);
                gsyVideoShotSaveListener.result(true, file);
            }
        };
        if (high) {
            gsyVideoShotListener.getBitmap(initCoverHigh());
        } else {
            gsyVideoShotListener.getBitmap(initCover());
        }

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
        setTransform(transform);
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
    public static GSYTextureView addTextureView(Context context, ViewGroup textureViewContainer, int rotate,
                                                final IGSYSurfaceListener gsySurfaceListener,
                                                final MeasureHelper.MeasureFormVideoParamsListener videoParamsListener) {
        if (textureViewContainer.getChildCount() > 0) {
            textureViewContainer.removeAllViews();
        }
        GSYTextureView gsyTextureView = new GSYTextureView(context);
        gsyTextureView.setIGSYSurfaceListener(gsySurfaceListener);
        gsyTextureView.setVideoParamsListener(videoParamsListener);
        gsyTextureView.setRotation(rotate);
        GSYRenderView.addToParent(textureViewContainer, gsyTextureView);
        return gsyTextureView;
    }
}
