package com.example.androidxdemo.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.AppUtils;

public class VoiceView extends RelativeLayout {
    private static final String TAG = "VoiceView";
    private static final int HEIGHT_10PX = (int) AppUtils.app().getResources().getDimension(R.dimen.view_dimen_10);
    private static final int HEIGHT_20PX = (int) AppUtils.app().getResources().getDimension(R.dimen.view_dimen_20);
    private static final int HEIGHT_30PX = (int) AppUtils.app().getResources().getDimension(R.dimen.view_dimen_30);
    private static final int HEIGHT_40PX = (int) AppUtils.app().getResources().getDimension(R.dimen.view_dimen_40);
    private AnimatorSet set;



    private View voice1, voice2, voice3, voice4, voice5;


    public VoiceView(Context context) {
        this(context, null);
    }

    public VoiceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_voice, this);
        voice1 = findViewById(R.id.voice_1);
        voice2 = findViewById(R.id.voice_2);
        voice3 = findViewById(R.id.voice_3);
        voice4 = findViewById(R.id.voice_4);
        voice5 = findViewById(R.id.voice_5);

        set = new AnimatorSet();
        set.playTogether(genVoice1Animator(), genVoice2Animator(), genVoice3Animator(), genVoice4Animator(), genVoice5Animator());
        set.start();
    }


    private Animator genVoice1Animator() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(HEIGHT_10PX, HEIGHT_20PX, HEIGHT_40PX, HEIGHT_20PX, HEIGHT_10PX).setDuration(600);
        valueAnimator.addUpdateListener(animation -> {
//            Log.d(TAG, "genVoice1Animator: 1--------> value=" + animation.getAnimatedValue());
            voice1.getLayoutParams().height = (int) animation.getAnimatedValue();
            voice1.requestLayout();
        });
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        return valueAnimator;
    }

    private Animator genVoice2Animator() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(HEIGHT_20PX, HEIGHT_30PX, HEIGHT_20PX, HEIGHT_10PX, HEIGHT_20PX).setDuration(600);
        valueAnimator.addUpdateListener(animation -> {
            voice2.getLayoutParams().height = (int) animation.getAnimatedValue();
            voice2.requestLayout();
        });
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        return valueAnimator;
    }

    private Animator genVoice3Animator() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(HEIGHT_40PX, HEIGHT_30PX, HEIGHT_20PX, HEIGHT_30PX, HEIGHT_40PX).setDuration(600);
        valueAnimator.addUpdateListener(animation -> {
            voice3.getLayoutParams().height = (int) animation.getAnimatedValue();
            voice3.requestLayout();
        });
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        return valueAnimator;
    }


    private Animator genVoice4Animator() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(HEIGHT_20PX, HEIGHT_10PX, HEIGHT_20PX, HEIGHT_30PX, HEIGHT_20PX).setDuration(600);
        valueAnimator.addUpdateListener(animation -> {
            voice4.getLayoutParams().height = (int) animation.getAnimatedValue();
            voice4.requestLayout();
        });
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        return valueAnimator;
    }


    private Animator genVoice5Animator() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(HEIGHT_10PX, HEIGHT_20PX, HEIGHT_30PX, HEIGHT_20PX, HEIGHT_10PX).setDuration(600);
        valueAnimator.addUpdateListener(animation -> {
            voice5.getLayoutParams().height = (int) animation.getAnimatedValue();
            voice5.requestLayout();
        });
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        return valueAnimator;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (set != null) {
            set.cancel();
            set = null;
        }
        super.onDetachedFromWindow();
    }
}
