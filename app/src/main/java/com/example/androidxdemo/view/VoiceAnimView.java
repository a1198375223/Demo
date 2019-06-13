package com.example.androidxdemo.view;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.AppUtils;

public class VoiceAnimView extends View {
    private static final String TAG = "VoiceAnimView";
    private final float HEIGHT_10PX = AppUtils.app().getResources().getDimension(R.dimen.view_dimen_10);
    private final float HEIGHT_20PX = AppUtils.app().getResources().getDimension(R.dimen.view_dimen_20);
    private final float HEIGHT_30PX = AppUtils.app().getResources().getDimension(R.dimen.view_dimen_30);
    private final float HEIGHT_40PX = AppUtils.app().getResources().getDimension(R.dimen.view_dimen_40);

    private final int color_1begin = AppUtils.app().getResources().getColor(R.color.color_c9fc08);
    private final int color_1end = AppUtils.app().getResources().getColor(R.color.color_00ff95);
    private final int color_2begin = AppUtils.app().getResources().getColor(R.color.color_80fd3a);
    private final int color_2end = AppUtils.app().getResources().getColor(R.color.color_00e1ff);
    private final int color_3begin = AppUtils.app().getResources().getColor(R.color.color_71ff32);
    private final int color_3end = AppUtils.app().getResources().getColor(R.color.color_00e1ff);
    private final int color_4begin = AppUtils.app().getResources().getColor(R.color.color_08fc82);
    private final int color_4end = AppUtils.app().getResources().getColor(R.color.color_00bdff);
    private final int color_5begin = AppUtils.app().getResources().getColor(R.color.color_08defc);
    private final int color_5end = AppUtils.app().getResources().getColor(R.color.color_00bdff);

    private final float WIDTH = AppUtils.app().getResources().getDimension(R.dimen.view_dimen_6);
    private final float DISTANCE = WIDTH;

    private final int HEIGHT_MARGIN = (int) AppUtils.app().getResources().getDimension(R.dimen.view_dimen_7);

    // wrap_content的宽高
    private int width = (int) AppUtils.app().getResources().getDimension(R.dimen.view_dimen_54);
    private int height = (int) AppUtils.app().getResources().getDimension(R.dimen.view_dimen_54);
    private float verticalCenterHeight = height / 2f;
    private float eachWidth = WIDTH;
    private float distance = DISTANCE;
    private float corner = eachWidth / 2f;

    private Paint voice1, voice2, voice3, voice4, voice5;
    private int start1, start2, start3, start4, start5;
    private float height1 = HEIGHT_10PX;
    private float height2 = HEIGHT_20PX;
    private float height3 = HEIGHT_40PX;
    private float height4 = HEIGHT_20PX;
    private float height5 = HEIGHT_10PX;
    private float height2_3 = HEIGHT_30PX;

    private ValueAnimator animator1;
    private ValueAnimator animator2;
    private ValueAnimator animator3;
    private ValueAnimator animator4;
    private ValueAnimator animator5;
    private ValueAnimator animatorAll;
    private AnimatorSet animatorSet;

    private boolean isHeightWrapContent = true;
    private boolean isInvalidating = false;

    public VoiceAnimView(Context context) {
        this(context, null);
    }

    public VoiceAnimView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        voice1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        voice2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        voice3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        voice4 = new Paint(Paint.ANTI_ALIAS_FLAG);
        voice5 = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 支持wrap_content
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(width, height);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            isHeightWrapContent = false;
            setMeasuredDimension(width, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, height);
        } else {
            isHeightWrapContent = false;
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // 获取宽高
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        // 计算distance
        distance = (width - eachWidth * 5) / 5f;

        // 计算corner
        corner = distance / 2f;

        // 计算每个view的左边的位置
        start1 = 0;
        start2 = (int) (distance + eachWidth + start1);
        start3 = (int) (distance + eachWidth + start2);
        start4 = (int) (distance + eachWidth + start3);
        start5 = (int) (distance + eachWidth + start4);

        verticalCenterHeight = height / 2f;

        if (!isHeightWrapContent && !isInvalidating) {
            height1 = (height - HEIGHT_MARGIN * 2f) / 4f;
            height5 = height1;

            height2 = (height - HEIGHT_MARGIN * 2f) / 2f;
            height4 = height2;

            height3 = height - HEIGHT_MARGIN * 2f;

            // 4-5
            height2_3 = height1 * 3;
        }
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas){
        LinearGradient color1 = new LinearGradient(start1, verticalCenterHeight - height1 / 2f,
                start1 + eachWidth, verticalCenterHeight + height1 / 2f, color_1begin, color_1end, Shader.TileMode.CLAMP);
        LinearGradient color2 = new LinearGradient(start2, verticalCenterHeight - height2 / 2f,
                start2 + eachWidth, verticalCenterHeight + height2 / 2f, color_2begin, color_2end, Shader.TileMode.CLAMP);
        LinearGradient color3 = new LinearGradient(start3, verticalCenterHeight - height3 / 2f,
                start3 + eachWidth, verticalCenterHeight + height3 / 2f, color_3begin, color_3end, Shader.TileMode.CLAMP);
        LinearGradient color4 = new LinearGradient(start4, verticalCenterHeight - height4 / 2f,
                start4 + eachWidth, verticalCenterHeight + height4 / 2f, color_4begin, color_4end, Shader.TileMode.CLAMP);
        LinearGradient color5 = new LinearGradient(start5, verticalCenterHeight - height5 / 2f,
                start5 + eachWidth, verticalCenterHeight + height5 / 2f, color_5begin, color_5end, Shader.TileMode.CLAMP);
        voice1.setShader(color1);
        voice2.setShader(color2);
        voice3.setShader(color3);
        voice4.setShader(color4);
        voice5.setShader(color5);

        canvas.drawRoundRect(start1, verticalCenterHeight - height1 / 2f, start1 + eachWidth,
                verticalCenterHeight + height1 / 2f, corner, corner, voice1);
        canvas.drawRoundRect(start2, verticalCenterHeight - height2 / 2f, start2 + eachWidth,
                verticalCenterHeight + height2 / 2f, corner, corner, voice2);
        canvas.drawRoundRect(start3, verticalCenterHeight - height3 / 2f, start3 + eachWidth,
                verticalCenterHeight + height3 / 2f, corner, corner, voice3);
        canvas.drawRoundRect(start4, verticalCenterHeight - height4 / 2f, start4 + eachWidth,
                verticalCenterHeight + height4 / 2f, corner, corner, voice4);
        canvas.drawRoundRect(start5, verticalCenterHeight - height5 / 2f, start5 + eachWidth,
                verticalCenterHeight + height5 / 2f, corner, corner, voice5);
    }


    public void startAnimation() {
        if (animatorSet != null) {
            animatorSet.start();
            return;
        }

        float[] set1, set2, set3, set4, set5;
        if (!isHeightWrapContent) {
            set1 = new float[]{height1, height2, height3, height2, height1};
            set2 = new float[]{height2, height1, height2, height2_3, height2};
            set3 = new float[]{height3, height2, height1, height2, height3};
            set4 = new float[]{height4, height2_3, height4, height5, height4};
            set5 = new float[]{height5, height4, height2_3, height4, height5};
        } else {
            set1 = new float[]{HEIGHT_10PX, HEIGHT_20PX, HEIGHT_40PX, HEIGHT_20PX, HEIGHT_10PX};
            set2 = new float[]{HEIGHT_20PX, HEIGHT_10PX, HEIGHT_20PX, HEIGHT_30PX, HEIGHT_20PX};
            set3 = new float[]{HEIGHT_40PX, HEIGHT_20PX, HEIGHT_10PX, HEIGHT_20PX, HEIGHT_40PX};
            set4 = new float[]{HEIGHT_20PX, HEIGHT_30PX, HEIGHT_20PX, HEIGHT_10PX, HEIGHT_20PX};
            set5 = new float[]{HEIGHT_10PX, HEIGHT_20PX, HEIGHT_30PX, HEIGHT_20PX, HEIGHT_10PX};
        }
        animator1 = ValueAnimator.ofFloat(set1).setDuration(600);
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setRepeatMode(ValueAnimator.RESTART);

        animator2 = ValueAnimator.ofFloat(set2).setDuration(600);
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        animator2.setRepeatMode(ValueAnimator.RESTART);

        animator3 = ValueAnimator.ofFloat(set3).setDuration(600);
        animator3.setRepeatCount(ValueAnimator.INFINITE);
        animator3.setRepeatMode(ValueAnimator.RESTART);

        animator4 = ValueAnimator.ofFloat(set4).setDuration(600);
        animator4.setRepeatCount(ValueAnimator.INFINITE);
        animator4.setRepeatMode(ValueAnimator.RESTART);

        animator5 = ValueAnimator.ofFloat(set5).setDuration(600);
        animator5.setRepeatCount(ValueAnimator.INFINITE);
        animator5.setRepeatMode(ValueAnimator.RESTART);

        animatorAll = ValueAnimator.ofFloat(0, 1f).setDuration(600);
        animatorAll.setRepeatCount(ValueAnimator.INFINITE);
        animatorAll.setRepeatMode(ValueAnimator.RESTART);
        animatorAll.addUpdateListener(animation -> {
            height1 = (float) animator1.getAnimatedValue();
            height2 = (float) animator2.getAnimatedValue();
            height3 = (float) animator3.getAnimatedValue();
            height4 = (float) animator4.getAnimatedValue();
            height5 = (float) animator5.getAnimatedValue();
            isInvalidating = true;
            invalidate();
        });

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1, animator2, animator3, animator4, animator5, animatorAll);
        animatorSet.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow: ");
        voice1.reset();
        voice2.reset();
        voice3.reset();
        voice4.reset();
        voice5.reset();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        animatorSet = null;
        animator1 = null;
        animator2 = null;
        animator3 = null;
        animator4 = null;
        animator5 = null;
        super.onDetachedFromWindow();
    }
}
