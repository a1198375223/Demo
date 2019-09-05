package com.example.androidxdemo.mianshi;

/**
 * View的位置参数
 * left top right bottom : 这些参数都是相对父容器来说的
 *
 * getX/getY        : 相对view的x和y坐标
 * getRawX/getRawY  : 相对屏幕的x和y坐标
 *
 * 1. ViewConfiguration.get(Context).getScaledTouchSlop() 获取视为滑动的最小滑动距离
 *
 * 2. VelocityTracker : 用来做速度追踪
 *      VelocityTracker velocityTracker = VelocityTracker.obtain();
 *      velocityTracker.addMovement(event);
 *      // 获取速度
 *      velocityTracker.computeCurrentVelocity(1000);
 *      int xVelocity = (int) velocityTracker.getXVelocity();
 *      int yVelocity = (int) velocityTracker.getYVelocity();
 *      // 回收
 *      velocityTracker.clear();
 *      velocityTracker.recycle();
 *
 *
 * 3. GestureDetector : 手势检测
 *
 *
 * 4. 事件分发机制
 *
 */
public class ViewDetail {
}
