package com.example.androidxdemo.base.image;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.example.androidxdemo.base.callback.IFrescoCallBack;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.request.Postprocessor;


/**
 * 使用这个类来创建一个BaseImage对象 包括LocalImage HttpImage 和ResImage
 */
public class ImageFactory {
    public static ImageFactory.Builder newHttpImage(String url) {
        return new ImageFactory.Builder().setUrl(url);
    }

    public static ImageFactory.Builder newLocalImage(String path) {
        return new ImageFactory.Builder().setPath(path);
    }

    public static ImageFactory.Builder newResImage(int resId) {
        return new ImageFactory.Builder().setResId(resId);
    }



    public static class Builder {
        private BaseImage mBaseImage;

        public Builder() {}

        private ImageFactory.Builder setUrl(String url) {
            mBaseImage = new HttpImage(url);
            return this;
        }

        private ImageFactory.Builder setPath(String path) {
            mBaseImage = new LocalImage(path);
            return this;
        }

        private ImageFactory.Builder setResId(int resId) {
            mBaseImage = new ResImage(resId);
            return this;
        }

        public ImageFactory.Builder setWidth(int width) {
            mBaseImage.setWidth(width);
            return this;
        }

        public ImageFactory.Builder setHeight(int height) {
            mBaseImage.setHeight(height);
            return this;
        }

        public ImageFactory.Builder setScaleType(ScaleType scaleType) {
            mBaseImage.setScaleType(scaleType);
            return this;
        }

        public ImageFactory.Builder setFailureDrawable(Drawable failureDrawable) {
            mBaseImage.setFailureDrawable(failureDrawable);
            return this;
        }

        public ImageFactory.Builder setFailureScaleType(ScaleType scaleType) {
            mBaseImage.setFailureScaleType(scaleType);
            return this;
        }

        public ImageFactory.Builder setLoadingDrawable(Drawable loadingDrawable) {
            mBaseImage.setLoadingDrawable(loadingDrawable);
            return this;
        }

        public ImageFactory.Builder setLoadingScaleType(ScaleType scaleType) {
            mBaseImage.setLoadingScaleType(scaleType);
            return this;
        }

        public ImageFactory.Builder setRetryDrawable(Drawable retryDrawable) {
            mBaseImage.setRetryDrawable(retryDrawable);
            return this;
        }

        public ImageFactory.Builder setRetryScaleType(ScaleType scaleType) {
            mBaseImage.setRetryScaleType(scaleType);
            return this;
        }

        public ImageFactory.Builder setIsCircle(boolean isCircle) {
            mBaseImage.setIsCircle(isCircle);
            return this;
        }

        public ImageFactory.Builder setPostprocessor(Postprocessor postprocessor) {
            mBaseImage.setPostProcessor(postprocessor);
            return this;
        }

        public ImageFactory.Builder setAutoPlayAnimation(boolean isAutoPlayAnim) {
            mBaseImage.setIsAutoPlayAnimation(isAutoPlayAnim);
            return this;
        }

        public ImageFactory.Builder setCornerRadius(int cornerRadius) {
            mBaseImage.setCornerRadius(cornerRadius);
            return this;
        }


        public ImageFactory.Builder setCornerRadii(float[] cornerRadii) {
            mBaseImage.setCornerRadii(cornerRadii);
            return this;
        }

        public ImageFactory.Builder setBorderWidth(int borderWidth) {
            mBaseImage.setBorderWidth(borderWidth);
            return this;
        }

        public ImageFactory.Builder setBorderColor(int borderColor) {
            mBaseImage.setBorderColor(borderColor);
            return this;
        }


        public ImageFactory.Builder setLowImageUri(Uri lowImageUri) {
            mBaseImage.setLowImageUri(lowImageUri);
            return this;
        }


        public ImageFactory.Builder setRequestPriority(Priority requestPriority) {
            mBaseImage.setRequestPriority(requestPriority);
            return this;
        }

        public ImageFactory.Builder setProgressiveRenderingEnabled(boolean progressiveRenderingEnabled) {
            mBaseImage.setProgressiveRenderingEnabled(progressiveRenderingEnabled);
            return this;
        }

        public ImageFactory.Builder setProgressBarDrawable(ProgressBarDrawable progressBarDrawable) {
            mBaseImage.setProgressBarDrawable(progressBarDrawable);
            return this;
        }

        public ImageFactory.Builder setCallBack(IFrescoCallBack callBack) {
            mBaseImage.setCallback(callBack);
            return this;
        }

        public BaseImage build() {
            return mBaseImage;
        }

    }
}
