package com.example.media.gsy.view;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;

import com.example.media.gsy.common.GSYVideoShotListener;
import com.example.media.gsy.common.GSYVideoShotSaveListener;
import com.example.media.gsy.common.IGSYSurfaceListener;
import com.example.media.gsy.helper.MeasureHelper;
import com.example.media.gsy.render.GSYVideoGLView;
import com.example.media.gsy.render.GSYVideoGLViewBaseRender;

import java.io.File;

public interface IGSYRenderView {
    IGSYSurfaceListener getIGSYSurfaceListener();

    /**
     * Surface变化监听，必须
     */
    void setIGSYSurfaceListener(IGSYSurfaceListener surfaceListener);

    /**
     * 当前view高度，必须
     */
    int getSizeH();

    /**
     * 当前view宽度，必须
     */
    int getSizeW();

    /**
     * 实现该接口的view，必须
     */
    View getRenderView();

    /**
     * 渲染view通过MeasureFormVideoParamsListener获取视频的相关参数，必须
     */
    void setVideoParamsListener(MeasureHelper.MeasureFormVideoParamsListener listener);

    /**
     * 截图
     */
    void taskShotPic(GSYVideoShotListener gsyVideoShotListener, boolean shotHigh);

    /**
     * 保存当前帧
     */
    void saveFrame(final File file, final boolean high, final GSYVideoShotSaveListener gsyVideoShotSaveListener);

    /**
     * 获取当前画面的bitmap，没有返回空
     */
    Bitmap initCover();

    /**
     * 获取当前画面的高质量bitmap，没有返回空
     */
    Bitmap initCoverHigh();

    void onRenderResume();

    void onRenderPause();

    void releaseRenderAll();

    void setRenderMode(int mode);

    void setRenderTransform(Matrix transform);

    void setGLRenderer(GSYVideoGLViewBaseRender renderer);

    void setGLMVPMatrix(float[] MVPMatrix);

    void setGLEffectFilter(GSYVideoGLView.ShaderInterface effectFilter);

}