package com.example.androidxdemo.activity.test.visibility;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;

public class VisibilityLinearLayout extends LinearLayout {
    public VisibilityLinearLayout(Context context) {
        super(context);
    }

    public VisibilityLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VisibilityLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onVisibilityChanged(@NotNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (mOnVisibilityChangeListener != null && changedView instanceof VisibilityLinearLayout){
            mOnVisibilityChangeListener.onVisibilityChange(changedView, visibility);
        }
    }

    private OnVisibilityChangeListener mOnVisibilityChangeListener;

    public void setOnVisibilityChangeListener(OnVisibilityChangeListener listener){
        mOnVisibilityChangeListener = listener;
    }

    public interface OnVisibilityChangeListener{
        void onVisibilityChange(View changedView, int visibility);
    }
}
