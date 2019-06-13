package com.example.androidxdemo.view.dot_progress_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.AppUtils;


public class LineView extends View {
    private static final String TAG = "LineView";

    private static final int LINE_HEIGHT = (int) AppUtils.app().getResources().getDimension(R.dimen.view_dimen_3);

    private Paint mLinePaint;
    private int height;
    private float mLastX;
    private int width;

    private int color_fff054 = getResources().getColor(R.color.color_fff054);
    private int color_e99e00 = getResources().getColor(R.color.color_e99e00);



    public LineView(Context context) {
        this(context, null);
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.FILL);
//        mLinePaint.setColor(AppUtils.app().getResources().getColor(R.color.color_47c5cb));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: ");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
//        canvas.drawLine(getLeft(), (getTop() + height) / 2f, mLastX, (getTop() + height) / 2f, mLinePaint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LinearGradient gradient = new LinearGradient(getLeft(), getTop(), getRight(), getBottom(),
                    getResources().getColor(R.color.color_fff054), getResources().getColor(R.color.color_e99e00), Shader.TileMode.CLAMP);
            mLinePaint.setShader(gradient);
            canvas.drawRoundRect(getLeft(), getTop(), getRight(), getBottom(),
                    getResources().getDimension(R.dimen.view_dimen_15), getResources().getDimension(R.dimen.view_dimen_15), mLinePaint);
        }
    }

    public void updateLine(float x) {
        mLastX = x;
        invalidate();
    }

    public float getProgress() {
        Log.d(TAG, "getProgress: mLastX=" + mLastX + " width=" + width);
        return mLastX / width;
    }
}
