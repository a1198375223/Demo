package com.example.androidxdemo.mianshi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * 1. Activity生命周期
 * onCreate() -> onStart() -> onResume() -> onPause() -> onStop() -> onDestroy()
 *                   ^            ^            |            |
 *                   |            |            |            |
 *                   |            -------------             |
 *                   |                                      |
 *                   |                                      |
 *                   --------------onRestart()---------------
 *
 * 2. onStart()与onResume()的区别?
 *  onStart()是activity界面被显示出来的时候执行的，但不能与它交互；
 *  onResume()是当该activity与用户能进行交互时被执行，用户可以获得activity的焦点，能够与用户交互。
 *
 * 3. 怎么证明onStart()是不可以与用户交互的状态而onResume()是可以与用户交互的？
 *
 *
 * 4. Activity()的启动流程？
 *   startActivity() -> startActivityForResult() -> ((AMS)ActivityManagerNative.getDefault).startActivity
 *-> AMS.startActivityAsUser() -> ActivityStackSupervisor.startActivityMayWait() -> startActivityLocked()
 *-> startActivityUncheckedLocked() -> ActivityStack.resumeTopActivitiesLocked() -> resumeTopActivityInnerLocked()
 *-> resumeTopActivityInnerLocked() -> ActivityStackSupervisor.startSpecificActivityLocked() -> realStartActivityLocked()
 *-> ActivityThread.scheduleLaunchActivity() -> 通过Handler发送一个消息 -> performLaunchActivity()
 *-> ActivityThread.handleResumeActivity()(onResume)(在这里将DecorView添加到Window中显示)
 *
 *   performLaunchActivity() 步骤：
 *     a. 从ActivityClientRecord中取出待启动的Activity的组件信息
 *     b. 通过Instrumentation的newActivity方法使用类加载器创建Activity对象
 *     c. 通过LoadedApk的makeApplication方法来尝试创建Application对象
 *     d. 创建好Application之后会通过Instrumentation的callApplicationOnCreate()来调用Application的onCreate()方法
 *     e. 创建ContextImpl对象并通过Activity的attach方法来完成一些重要数据的初始化
 *     f. 调用Activity.onCreate() Activity.onStart() Activity.onRestoreInstanceState()方法
 *
 * 5. Activity的启动模式
 *    a. standard  : 标准模式, 每次启动一个Activity都会重新创建一个新的实例.
 *    b. singleTop : 栈顶复用模式, 如果新Activity位于栈顶, 那么次Activity不会重新创建, 同时它的onNewIntent方法会被回调
 *    c. singleTask: 栈内复用模式, 这是一种单实例模式, 在这种模式下, 只要在栈中存在, 那么多次启动次Activity都不会重新创建实例
 *    d. singleInstance: 单实例模式, 单独运行在一个栈中
 *
 * 6. 假设当前Activity A, 如果这时用户打开一个新的Activity B, 那么B的onResume先执行还是A的onPause先执行呢？
 *    先执行A的onPause();
 *
 * 7. Activity A启动另一个Activity B会回调哪些方法？如果Activity B是完全透明呢？如果启动的是一个Dialog呢？
 *    1、A界面==onCreate() ----->A界面==onStart()------> A界面==onResume() ---->A界面==onPause()
 *----> B界面==onCreate() ---->B界面==onStart()------->B界面==onResume()-----A界面==onStop()
 *     Activity B显示后 点击返回按钮 回调的方法 B界面==onPause()------>A界面===onRestart()
 *---->A界面==onStart()---->A界面==onResume()----->B界面==onStop()------>B界面==onDestroy()
 *
 *
 *    2、A界面==onCreate()---> A界面==onStart()------> A界面==onResume()---->A界面==onPause()
 *----->B界面==onCreate()----> B界面==onStart()---->B界面==onResume()
 *    Activity B显示后 点击返回按钮 回调的方法 B界面==onPause()----->A界面==onResume()---->B界面==onStop()----->B界面==onDestroy()
 *
 *
 *    3、A界面==onCreate()---> A界面==onStart()------> A界面==onResume()
 *
 * 8. 谈谈onSaveInstanceState()方法？何时会调用？
 *    onSaveInstanceState()会在Activity即将被异常销销毁的时候会被调用
 *    1. 按下Home键
 *    2. 锁屏
 *    3. 屏幕切换
 *    4. 打开新的Activity
 *    5. 按下菜单键
 *
 * 9. onSaveInstanceState()与onPause()的区别？
 *    onPause()无论是在Activity被正常销毁还是被异常销毁都会被调用的一个生命周期方法
 *    onSaveInstanceState()是在Activity被异常销毁会调用的方法
 *
 *
 * 10. 如何避免配置改变时Activity重建？
 *    1. 在AndroidManifest.xml文件中添加 android:configChanges
 *
 * 11. Activity之间的数据传递为什么不直接用静态数据结构进行传递而大多数采用的是intent？
 *    1. 因为如果使用的是静态数据进行传递, 在内存不足的时候该app进程被杀死, 如果这时尝试重新启动该app, 无法恢复到原先的样子
 *       如果此时是使用intent进行数据传递就可以恢复到原样, 因为安卓系统会为我们保存Activity的task与数据, 这些数据包括了
 *       intent, 所以当我们重新启动的时候还是可以在onCreate()方法中通过getIntent()方法拿到Intent中的数据
 *    2. 但是通过Intent传递数据是有限制的, Intent的大小被限制在一个范围, 不同的android版本是不一样的, 现在是500k.
 *       那如果是要传递大数据怎么办呢？可以讲数据写入一个文件中, 然后将该文件的路径通过Intent进行传递. 如果是小数据的
 *       直接话保存的话, 可以使用SharedPreference.
 *
 * 12. 谈谈singleTop和singleTask的区别以及应用场景？
 *    1. singleTop：
 *          a. 可以解决重复打开activity的问题
 *          b. 在浏览器的书签 特点:检查栈顶是否存在这个实例 如果存在则不重新创建
 *    2. singleTask:
 *          a. 浏览器主页面
 *
 * 13. onNewIntent()调用时机？
 *    1. 在singleTop启动模式下, 如果在任务的栈顶正好存在该Activity的实例, 就重用该实例, 并调用其onNewIntent()
 *    2. 在SingleTask启动模式下, 如果在栈中已经有该Activity的实例，就重用该实例(会调用实例的onNewIntent())
 *    3. 在SingleInstance启动模式下, 每次被重新启动都会调用onNewIntent()
 *    4. 当调用到onNewIntent(intent)的时候，需要在onNewIntent() 中使用setIntent(intent)赋值给Activity的Intent.
 *       否则，后续的getIntent()都是得到老的Intent。
 *
 * 14. 如何启动其他应用的Activity？ (可以看下面的伪代码)
 *    1. 用action启动
 *    2. 用intent设置className或component的办法启动
 *    tips: 如果使用startActivityForResult()方法来启动Activity的话则需要被启动的Activity被销毁掉才会回调onActivityResult方法
 *          也就是要主动调用finish();
 *
 * 15.
 */
public class ActivityDetail {

    void startOtherClassByClassName(Context context, String otherClassName, String otherPackageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName(otherPackageName, otherClassName);
        context.startActivity(intent);
    }


    void startOtherClassByComponent(Context context, String otherClassName, String otherPackageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(new ComponentName(otherPackageName, otherClassName));
        context.startActivity(intent);
    }


    void startOtherClassByAction(Context context, String action) {
        Intent intent = new Intent(action);
        context.startActivity(intent);
    }
}
