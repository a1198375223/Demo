package com.example.androidxdemo.view.pager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.commonlibrary.utils.DisplayUtils;


public class MyVerticalViewPager extends ViewPager {
    private static final String TAG = "MyVerticalViewPager";
    private static final int sDistance = DisplayUtils.getScreenHeight() / 4;
    private static final int sFlingVelocity = 0;
    private float mLastY = 0;

    private Scroller mScroller;

    public MyVerticalViewPager(@NonNull Context context) {
        this(context, null);
    }

    public MyVerticalViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setPageTransformer(true, new DefaultTransformer());

        mScroller = new Scroller(context);

        // 通过反射来解决滑动切换页面的时间
//        try {
//            Field field = ViewPager.class.getDeclaredField("mScroller");
//            field.setAccessible(true);
//            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(),
//                    new AccelerateInterpolator());
//            field.set(this, scroller);
//            scroller.setmDuration(200);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // mTouchSlop: 控制最小滑动距离 只有滑动超过这个距离才会开始滑动
        // 使用这个方法来控制切换页面的offset不够明显
//        try {
//            Field mFlingDistance = ViewPager.class.getDeclaredField("mFlingDistance");
//            mFlingDistance.setAccessible(true);
//            mFlingDistance.set(this, sDistance);
//            Field mMinimumVelocity = ViewPager.class.getDeclaredField("mMinimumVelocity");
//            mMinimumVelocity.setAccessible(true);
//            mMinimumVelocity.set(this, sFlingVelocity);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    // 拦截左右滑动事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = super.onInterceptTouchEvent(swapEvent(ev));
        swapEvent(ev);
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapEvent(ev));
    }


    private MotionEvent swapEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();

        //Log.d(TAG, "swapEvent: x=" + event.getX() + " y=" + event.getY() + " swappedX=" + (event.getY() / height) * width + " swappedY=" + (event.getX() / width) * height);
        float swappedX = (event.getY() / height) * width;
        float swappedY = (event.getX() / width) * height;

        event.setLocation(swappedX, swappedY);
        return event;
    }

    class DefaultTransformer implements PageTransformer {

        @Override
        public void transformPage(@NonNull View view, float position) {
            float alpha = 0;

            if (position >= 0 && position <= 1) {
                alpha = 1 - position;
            } else if (position >= -1 && position < 0) {
                alpha = 1 + position;
            }

            view.setAlpha(alpha);

            float transX = view.getWidth() * -position;
            float transY = view.getHeight() * position;
            view.setTranslationX(transX);
            view.setTranslationY(transY);
        }
    }
}

