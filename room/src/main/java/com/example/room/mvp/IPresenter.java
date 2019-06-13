package com.example.room.mvp;


import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * 工作原理解释:
 * 1. 将想要监控生命周期的组件实现 LifecycleOwner 接口, 代表拥有这是一个拥有生命周期的组件. getLifecycle() 返回一个 LifecycleRegistry 对象
 * 2. 在该组件内部使用 LifecycleRegistry 来管理生命周期, 当处于对应周期的时候, 使用 LifecycleRegistry.handleLifecycleEvent() 发送对应的 LifecycleEvent 事件
 * 3. LifecycleRegistry 通过 addObserver 来监听事件的变化 (GenericLifecycleObserver -> LifecycleObserver <- FullLifecycleObserver <- DefaultLifecycleObserver)
 * 4. 自己实现 LifecycleObserver 的类在实现 LifecycleOwner 的类中初始化. getLifecycle().addObserver(new Presenter);
 *
 * 在Lifecycle中的 Event {
 *     ON_CREATE -> {
 *         1. 对于fragment, 在onViewStateRestored之后被调用
 *         2. 对于activity, 在onCreate之后被调用
 *     }
 *     ON_START  -> {
 *         1. 对于fragment, 在onStart之后被调用
 *         2. 对于activity, 在onStart之后被调用
 *     }
 *     ON_RESUME -> {
 *         1. 对于fragment, 在onResume之后被调用
 *         2. 对于activity, 在onResume之后被调用
 *     }
 *     ON_PAUSE  -> {
 *         1. 对于fragment, 在onPause之前被调用
 *         2. 对于activity, 在onPause之前被调用
 *     }
 *     ON_STOP   -> {
 *         1. 对于fragment, 在onStop之前被调用
 *         2. 对于activity, 在onStop之前被调用
 *     }
 *     ON_DESTROY-> {
 *         1. 对于fragment, 在onDestroy之前被调用
 *         2. 对于activity, 在onDestroy之前被调用
 *     }
 *     ON_ANY    -> 对应activity和fragment的任意一个event生命周期
 * }
 *
 * 在Lifecycle中的State {
 *     DESTROYED   -> 从onDestroy方法中到之后的销毁方法都表现为 DESTROY
 *     INITIALIZED -> 在onCreate中表现为 INITIALIZED
 *     CREATED     -> 在onCreate执行完表现为 CREATE 在onStop方法中表现为 CREATE
 *     STARTED     -> 在onStart执行完表现为 START  在onPause方法中表现为 START
 *     RESUMED     -> 在onResume执行完 表现为 RESUME
 * }
 *
 * LifecycleObserver接口中不带任何方法, 是依赖于OnLifecycleEvent注解的
 */
public interface IPresenter extends LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void create(LifecycleOwner owner);

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start(LifecycleOwner owner);

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void resume(LifecycleOwner owner);

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void pause(LifecycleOwner owner);

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop(LifecycleOwner owner);

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void destroy(LifecycleOwner owner);

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onLifecycleChange(LifecycleOwner owner, Lifecycle.Event event);
}
