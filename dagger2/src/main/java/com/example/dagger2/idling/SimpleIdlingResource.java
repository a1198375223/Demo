package com.example.dagger2.idling;

import androidx.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * IdlingResource是用来做异步测试的, 取代在异步线程做的sleep操作
 * 或者可以实现自己的逻辑来判断主线程是否是处于Idle状态
 * 这边是一个计数器例子, 如果 counter == 0 就判断是处于Idle状态
 */
public class SimpleIdlingResource implements IdlingResource {
    private static final String TAG = "SimpleIdlingResource";

    private final String mResourceName;

    private final AtomicInteger counter = new AtomicInteger(0);

    // written from main thread, read from any thread
    private volatile ResourceCallback mCallback;


    public SimpleIdlingResource(String resourceName) {
        this.mResourceName = resourceName;
    }

    /**
     * @return 返回Idling的名字, 必须返回
     */
    @Override
    public String getName() {
        return mResourceName;
    }

    /**
     * 主线程执行的方法
     * @return 主线程是否处于空闲状态
     */
    @Override
    public boolean isIdleNow() {
        return counter.get() == 0;
    }

    /**
     * 如果要让IdleResource生效就必须调用该方法,注册回调事件
     * @param callback 回调方法, 用于通知主线程是否从busy-idle 如果主线程处于idle状态 就可以手动回调callback.onTransitionToIdle();
     *                 判断是否Idle状态是通过 isIdleNow() 方法来进行判断的
     */
    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.mCallback = callback;
    }

    // 加1
    public void increment() {
        counter.getAndIncrement();
    }

    // 减1
    public void decrement() {
        int counterVal = counter.decrementAndGet();
        if (counterVal == 0) {
            if (mCallback != null) {
                mCallback.onTransitionToIdle();
            }
        }

        if (counterVal < 0) {
            throw new IllegalArgumentException("Counter has been corrupted!");
        }
    }
}
