package com.example.androidxdemo.view.dot_progress_view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.example.androidxdemo.R;


// 记得在根布局中clipChild="false"
public class LineWithPointView extends RelativeLayout {
    private static final String TAG = "LineWithPointView";

    private LineView mLineView;
    private ImageView mDotIv;
    private float lastX = 0;
    private int width;


    public LineWithPointView(Context context) {
        this(context, null);
    }

    public LineWithPointView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineWithPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_line_with_point, this);
        mDotIv = (ImageView) findViewById(R.id.dot_iv);
        mLineView = (LineView) findViewById(R.id.line_view);

        setClipChildren(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d(TAG, "onTouch: X=" + event.getX() + " y=" + event.getY()
//                + "\nrawX=" + event.getRawX() + " rawY=" + event.getRawY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastX = event.getX();
                mDotIv.setTranslationX(lastX);
                mLineView.updateLine(lastX);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                Log.d(TAG, "onTouch: move distance=" + (event.getRawX() - lastX));
                Log.d(TAG, "onCreate: container left=" + getLeft() + " container right=" + getRight()
                        + "\nrx=" + event.getRawX() + " ry=" + event.getRawY());
                float x = event.getX();
                float rx = event.getRawX();
                // 不超过当前父view的宽度
                if (x != lastX && rx >= getLeft() && rx <= getRight()) {
                    mDotIv.setTranslationX(x);
                    mLineView.updateLine(x);
                    lastX = x;
                } else if (rx < getLeft()) {
                    mDotIv.setTranslationX(0);
                    mLineView.updateLine(0);
                    lastX = 0;
                } else if (rx > getRight()) {
                    mDotIv.setTranslationX(width);
                    mLineView.updateLine(width);
                    lastX = width;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                Log.d(TAG, "onTouch: progress=" + mLineView.getProgress());
                break;
            }
        }
        return true;
    }

    public float getProgress() {
        return mLineView.getProgress();
    }
}
