package com.example.androidxdemo.mianshi;

import android.view.WindowManager;

/**
 * Window: 每一个Window都对应着一个View和一个ViewRootImpl, Window和View通过ViewRootImpl来建立联系. Window
 *         的具体实现是PhoneWindow
 *
 * 1. WindowManager.LayoutParams:
 *    a. flag: 各种设置吧, 什么获取焦点, 显示在锁屏上的那种功能都是通过设置flag来实现的
 *    b. type: 有3种类型的windows.
 *    应用window(对应Activity)                                                      层级范围：1-99
 *    子window(不能单独存在, 附加在特定的父window.例如Dialog就是一个子window)           层级范围：1000-1999
 *    系统window(是需要申明权限才能创建的window, 例如Toast)                            层级范围：2000-2999
 *
 * 2. WindowManager中有3个方法:
 *    addView()     : 添加view
 *    updateView()  : 更新view
 *    removeView()  : 移除view
 *
 * 图：
 * WindowManager: 用来管理window的类接口
 *     ^
 *     |
 *     |
 * WindowManagerImpl: WindowManager的实现类
 *     |
 *     | 交给它处理
 *     v
 * WindowManagerGlobal: 负责处理WindowManagerImpl传送过来的事件
 *
 * 3. Window与WindowManager的关系?
 *    WindowManager使用来管理Window的添加删除和更新操作的一个管理者。
 *
 * 4. WindowManager的addView()过程？
 *    a. 检查参数是否合法
 *    b. 创建ViewRootImpl将View添加到列表中
 *    c. 通过ViewRootImpl来跟新界面并完成Window的添加过程
 *    d. 最终交给WindowSession来完成Window的添加过程, 因为WindowSession是一个Binder对象. 所以添加View过程是一个ipc过程
 *    e. WindowSession有个实现类是Session, 在Session内部会通过WindowManagerService来实现具体的Window添加过程
 *
 * 5. WindowManager的removeView()过程?
 *    a. 首先通过findViewLocked()来查找待删除的View的索引
 *    b. 然后从ViewRootImpl数组中取出引用
 *    c. 通过removeViewLocked()方法做进一步的删除操作
 *    d. 对View做删除的真正方法ViewRootImpl.dispatchDetachedFromWindow()方法
 *    e. 这个也是一个ipc过程
 *
 * 6. WindowManager的updateView()过程？
 *    a. 替换view的LayoutParam
 *    b. 替换数组中的LayoutParam
 *    c. 然后调用ViewRootImpl.scheduleTraversals()方法来对View重新布局
 *
 * 7. Activity是如何创建Window的？
 *    Activity.attach() (创建Window)--> Activity.setContentView() --> { a.Window.setContentView() b.initWindowDecorActionBar() }
 *    a. 步骤: (DecorView的创建过程)
 *          * 首先创建一个DecorView (FrameLayout)
 *          * 将setContentView()方法传入的布局文件转化成view并通过DecorView.addView()来添加到DecorView中
 *          * 回调Activity.onContentChanged()方法通知Activity视图已经发生改变
 *          * (DecorView添加到Window过程)
 *          * 在ActivityThread的handleResumeActivity方法中, 首先会调用Activity的onResume方法, 接着调用makeVisible()方法
 *            在这个方法中完成了添加和显示的过程
 *   b. 步骤:
 *
 * 8. Dialog是如何创建Window的？
 *   a. 在构造器中创建了Window
 *   b. 通过setContentView()来创建一个DecorView
 *   c. 通过Dialog.show()方法来添加并显示到Window
 *   tips: Dialog必须使用Activity中的context, 不能使用Application的context. 因为需要token
 *
 * 9. Toast是如何创建Window的？
 *   在tn类中的show方法中添加view
 *   show过程
 *   a. 调用NMS的enqueueToast()方法插入到一个队列
 *   b. 调用NMS.showNextToastLocked()方法来显示Toast
 *   tips: Toast需要在有Looper的情况下使用, 因为内部使用了Handler. 为什么需要用Handler呢？
 *         因为在Toast的show和cancel方法中Toast是与NotificationServiceManager交互的, 交互之后会回调TN中的某些方法
 *         而这些方法是运行在Binder线程池中的, 所以需要使用Handler来实现线程的切换
 *
 *
 * 10. Activity、View、Window三者之间的关系？
 *    在Activity启动过程其中的attach()方法中初始化了PhoneWindow，而PhoneWindow是Window的唯一实现类，然后
 *    Activity通过setContentView将View设置到了PhoneWindow上，而View通过WindowManager的addView()、removeView()、updateViewLayout()对View进行管理。
 */
public class WindowDetail {
}
