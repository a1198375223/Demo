package com.example.androidxdemo.base.image;


import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.example.androidxdemo.base.callback.IFrescoCallBack;
import com.example.commonlibrary.utils.DisplayUtils;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.request.Postprocessor;

/**
 * 这个类用来保存在加载fresco之前的所有设置操作
 */
public abstract class BaseImage {

    /**
     * scale type 类型
     * FIT_XY           无视宽高比填充满
     * FIT_START        保持宽高比，缩放，直到一边到界
     * FIT_CENTER       同上，但是最后居中
     * FIT_END          同上上，但与显示边界右或下对齐
     * CENTER           居中无缩放
     * CENTER_INSIDE    使的图片都在边界内，与FIT_CENTER不同的是，只会缩小不会放大，默认是这个吧
     * CENTER_CROP      保持宽高比，缩小或放大，使两边都大于等于边界，居中。
     * FOCUS_CROP       同CENTER_CROP，但中心点可以设置
     * FIT_BOTTOM_START
     */
    protected ScaleType mScaleType;

    // 图片的uri
    protected Uri mUri;

    // 图片的高和宽
    protected int mWidth = DisplayUtils.dip2px(40);
    protected int mHeight = DisplayUtils.dip2px(40);

    // 加载失败的图片
    protected Drawable mFailureDrawable;
    protected ScaleType mFailureScaleType = ScaleType.CENTER_INSIDE;

    // 加载中的图片
    protected Drawable mLoadingDrawable;
    protected ScaleType mLoadingScaleType = ScaleType.CENTER_INSIDE;

    // 重试的图片
    protected Drawable mRetryDrawable;
    protected ScaleType mRetryScaleType = ScaleType.CENTER_INSIDE;

    // 加载进度图片
    protected ProgressBarDrawable mProgressBarDrawable = null;

    // 低分辨率的图片
    protected Uri mLowImageUri = null;

    // 是否支持渐进式加载
    protected boolean mProgressiveRenderingEnable  = false;

    // 是否是圆形图片
    protected boolean mIsCircle = false;

    // 是否支持自动播放动画
    protected boolean mIsAutoPlayAnimation = false;

    // 后处理
    protected Postprocessor mPostProcessor;

    // 边界的宽
    protected float mBorderWidth = -1;

    // 边界的颜色
    protected int mBorderColor = -1;

    // 圆角的半径
    protected int mCornerRadius = -1;
    protected float[] mCornerRadii;

    // 加载的优先级
    protected Priority mRequestPriority = Priority.MEDIUM;

    // 生成uri
    public abstract void generateUri();

    // 图片加载回调的接口
    protected IFrescoCallBack mCallback;

    public IFrescoCallBack getCallback() {
        return mCallback;
    }

    public void setCallback(IFrescoCallBack mCallback) {
        this.mCallback = mCallback;
    }

    public ScaleType getScaleType() {
        return mScaleType;
    }

    public Uri getUri() {
        return mUri;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public Drawable getFailureDrawable() {
        return mFailureDrawable;
    }

    public ScaleType getFailureScaleType() {
        return mFailureScaleType;
    }

    public Drawable getLoadingDrawable() {
        return mLoadingDrawable;
    }

    public ScaleType getLoadingScaleType() {
        return mLoadingScaleType;
    }

    public Drawable getRetryDrawable() {
        return mRetryDrawable;
    }

    public ScaleType getRetryScaleType() {
        return mRetryScaleType;
    }

    public ProgressBarDrawable getProgressBarDrawable() {
        return mProgressBarDrawable;
    }

    public Uri getLowImageUri() {
        return mLowImageUri;
    }

    public boolean isProgressiveRenderingEnable() {
        return mProgressiveRenderingEnable;
    }

    public boolean isCircle() {
        return mIsCircle;
    }

    public boolean isAutoPlayAnimation() {
        return mIsAutoPlayAnimation;
    }

    public Postprocessor getPostProcessor() {
        return mPostProcessor;
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public int getCornerRadius() {
        return mCornerRadius;
    }

    public float[] getCornerRadii() {
        return mCornerRadii;
    }

    public Priority getRequestPriority() {
        return mRequestPriority;
    }


    public void setScaleType(ScaleType mScaleType) {
        this.mScaleType = mScaleType;
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public void setFailureDrawable(Drawable mFailureDrawable) {
        this.mFailureDrawable = mFailureDrawable;
    }

    public void setFailureScaleType(ScaleType mFailureScaleType) {
        this.mFailureScaleType = mFailureScaleType;
    }

    public void setLoadingDrawable(Drawable mLoadingDrawable) {
        this.mLoadingDrawable = mLoadingDrawable;
    }

    public void setLoadingScaleType(ScaleType mLoadingScaleType) {
        this.mLoadingScaleType = mLoadingScaleType;
    }

    public void setRetryDrawable(Drawable mRetryDrawable) {
        this.mRetryDrawable = mRetryDrawable;
    }

    public void setRetryScaleType(ScaleType mRetryScaleType) {
        this.mRetryScaleType = mRetryScaleType;
    }

    public void setProgressBarDrawable(ProgressBarDrawable mProgressBarDrawable) {
        this.mProgressBarDrawable = mProgressBarDrawable;
    }

    public void setLowImageUri(Uri mLowImageUri) {
        this.mLowImageUri = mLowImageUri;
    }

    public void setProgressiveRenderingEnabled(boolean mProgressiveRenderingEnable) {
        this.mProgressiveRenderingEnable = mProgressiveRenderingEnable;
    }

    public void setIsCircle(boolean mIsCircle) {
        this.mIsCircle = mIsCircle;
    }

    public void setIsAutoPlayAnimation(boolean mIsAutoPlayAnimation) {
        this.mIsAutoPlayAnimation = mIsAutoPlayAnimation;
    }

    public void setPostProcessor(Postprocessor mPostProcessor) {
        this.mPostProcessor = mPostProcessor;
    }

    public void setBorderWidth(float mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
    }

    public void setBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
    }

    public void setCornerRadius(int mCornerRadius) {
        this.mCornerRadius = mCornerRadius;
    }

    public void setCornerRadii(float[] mCornerRadii) {
        this.mCornerRadii = mCornerRadii;
    }

    public void setRequestPriority(Priority mRequestPriority) {
        this.mRequestPriority = mRequestPriority;
    }
}
