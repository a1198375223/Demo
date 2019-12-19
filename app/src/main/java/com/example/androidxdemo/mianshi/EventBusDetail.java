package com.example.androidxdemo.mianshi;

/**
 * EventBus
 *
 * 1. getDefault() : 通过双重校验生成单例
 * 2. register(Object object) :
 *     * 获取Object的class对象, 并且通过Class对象来获取里面的被@Subscribe()修饰的方法
 *     * 将获取到的方法封装成SubscriberMethod对象并添加到SubscriberMethods对象中去
 *     * 将方法信息,使用Map来保存，如果是sticker事件则会直接去检查是否之前有对应的sticker事件发出，如果有就调用这个方法
 *     * 总共有4个Map用来保存不同的信息
 *     * 1. Map<Class<?>, List<Class<?>> eventTypesCache : <方法参数的Class对象, 参数Class对象以及子类的Class对象的List>
 *                                                       : 用来保存方法参数类对应的子类（包括自己）的集合
 *     * 2. Map<Object, List<Class<?>> typesBySubscriber : <注册的对象, 参数的Class对象的List>
 *                                                       : 保存注册对象中所有@Subscribe修饰的方法的参数类型的集合
 *     * 3. Map<Class<?>, CopyOnWriterArrayList<Subscription>> : <参数的Class对象, <注册对象, 方法信息>>
 *                                                             : 用来保存方法参数对应的注册对象与注册方法，在register()中subscribe()保存
 *     * 4. Map<Class<?>, Object> stickyEvents : <参数的Class对象, 参数对象>
 *                                             : 用来处理粘性事件，在postSticky()保存，在register中的subscribe()中取出判断。
 *
 * 3. unregister(Object subscriber):
 *     * 将该订阅对象中的方法从Map中移除
 *     * 将该订阅对象从Map中移除
 *
 * 4. post(Object event) :
 *     * 寻找可被响应的方法
 *     * 将该方法以不同的postingState做对应不同的处理
 *
 * 5. postSticky(Object event):
 *     * 将该事件添加到map中
 *     * 调用post(event)
 *
 * 6. removeStickyEvent(Object/Class<T> eventType) 移除改粘性事件
 *
 * 7. 添加索引注解处理器
 *   EventBus.Builder().addIndex().installDefaultEventBus();
 *   使用SimpleSubscriberInfo来管理EventBus
 *   本质就是使用一个HasMap来存储所有的EventBus对象的所有信息
 */
public class EventBusDetail {}
