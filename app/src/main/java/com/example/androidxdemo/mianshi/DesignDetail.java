package com.example.androidxdemo.mianshi;

import java.io.Serializable;

/**
 * 1. 谈谈MVC、MVP和MVVM，好在哪里，不好在哪里？
 * MVP的含义：
 *      Model：数据层，负责存储、检索、操纵数据。
 *      View：UI层，显示数据，并向Presenter报告用户行为。
 *      Presenter：作为View与Model交互的中间纽带，从Model拿数据，应用到UI层，管理UI的状态，响应用户的行为。
 *      
 * MVP相比于MVC的优势：
 *      分离了视图逻辑和业务逻辑，降低了耦合。
 *      Activity只处理生命周期的任务，代码变得更加简洁。
 *      视图逻辑和业务逻辑分别抽象到了View和Presenter的接口中去，提高代码的可阅读性。
 *      Presenter被抽象成接口，可以有多种具体的实现，所以方便进行单元测试。
 *      把业务逻辑抽到Presenter中去，避免后台线程引用着Activity导致Activity的资源无法被系统回收从而引起内存泄露和OOM。
 *
 * MVVM的含义：与MVP类似，利用数据绑定(Data Binding)、依赖属性(Dependency Property)、命令(Command)、路由事件(Routed Event)等新特性，打造了一个更加灵活高效的架构。
 *      MVVM相比于MVP的优势：在常规的开发模式中，数据变化需要更新UI的时候，需要先获取UI控件的引用，然后再更新UI，
 *      获取用户的输入和操作也需要通过UI控件的引用，但在MVVM中，这些都是通过数据驱动来自动完成的，数据变化后会自
 *      动更新UI，UI的改变也能自动反馈到数据层，数据成为主导因素。这样MVVM层在业务逻辑处理中只要关心数据，不需要
 *      直接和UI打交道，在业务处理过程中简单方便很多。
 */
public class DesignDetail {

    public static class Singleton implements Serializable {
        private volatile static Singleton singleton;
        private Singleton (){}
        public static Singleton getSingleton() {
            if (singleton == null) {
                synchronized (Singleton.class) {
                    if (singleton == null) {
                        singleton = new Singleton();
                    }
                }
            }
            return singleton;
        }

        private Object readResolve() {
            return singleton;
        }
    }
}
