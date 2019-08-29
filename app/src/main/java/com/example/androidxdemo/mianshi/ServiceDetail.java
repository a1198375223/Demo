package com.example.androidxdemo.mianshi;

/**
 * Service
 *
 * 1. 谈一谈Service的生命周期？
 *    onCreate（）：服务第一次被创建时调用
 *    onStartCommand（）：服务启动时调用
 *    onBind（）：服务被绑定时调用
 *    onUnBind（）：服务被解绑时调用
 *    onDestroy（）：服务停止时调用
 *
 *    onCreate --> onStartCommand(多次调用startService()方法会多次调用) --> onDestroy
 *
 *    onCreate --> onBind(只会调用一次) --> onUnbind --> onDestroy
 *
 * 2. Service的两种启动方式？区别在哪？
 *    同1
 *    startService()：开启Service，调用者退出后Service仍然存在。
 *    bindService()：开启Service，调用者退出后Service也随即退出。
 *
 * 3. 一个Activity先start一个Service后，再bind时会回调什么方法？此时如何做才能回调Service的destroy()方法？
 *    一个Activity先start一个Service后，再bind时会调用 onBind()方法。
 *    此时调用onUnBind()方法才能回调Service的destroy()方法。注意：调用stopService()方法是不行的。
 *
 * 4. Service如何和Activity进行通信？
 *    可以通过bindService的方式，先在Activity里实现一个ServiceConnection接口，并将该接口传递给bindService()
 *    方法，在ServiceConnection接口的onServiceConnected()方法里执行相关操作。
 *
 * 5. 用过哪些系统Service？
 *
 * 6. 是否能在Service进行耗时操作？如果非要可以怎么做？
 *    不可以。
 *    创建一个线程呗
 *
 * 7. AlarmManager能实现定时的原理？
 *
 * 8. 前台服务是什么？和普通服务的不同？如何去开启一个前台服务？
 *    前台服务和普通服务最大的区别是，前者会一直有一个正在运行的图标在系统的状态栏显示，下拉状态栏后可以看到更加
 *    详细的信息，非常类似于通知的效果。使用前台服务或者为了防止服务被回收掉，比如听歌，或者由于特殊的需求，比如实时天气状况。
 *
 *    想要实现一个前台服务非常简单，它和之前学过的发送一个通知非常类似，只不过在构建好一个Notification之后，
 *    不需要NotificationManager将通知显示出来，而是调用了startForeground()方法。
 *
 * 9. 是否了解ActivityManagerService，谈谈它发挥什么作用？
 *    ActivityManagerService(AMS)是Android中最核心的服务，主要负责系统中四大组件的启动、切换、调度及应用程序的管理和调度等工作。
 *
 *    作用:
 *    1、统一调度个应用的程序的Activity。应用程序要运行Activity，会首先报告AMS，然后由AMS决定该Activity是否可
 *       以启动，如果可以，AMS再通知应用程序指定的Activity。换句话说说运行Activity时各应用程序的内政，AMS并不干预
 *       ，但是AMS必须知道各应用程序都运行了哪些Activity。
 *    2、内存管理。Android官方声称，Activity退出后，其所在的进程并不会被立即杀死，从而在下次启动该Activity时能够
 *       提高启动速度，这些Activity只有当系统内存紧张时，才会被自动杀死，应用程序并不关心这些问题，这些都是在AMS中完成的。
 *    3、进程管理，AMS向外提供了查询系统正在运行的进程信息的API。
 *
 * 10. 如何保证Service不被杀死？
 *    1. 在onDestory()中发送广播开启自己
 *    2. 开启两个服务，相互监听，相互启动
 */
public class ServiceDetail {
}
