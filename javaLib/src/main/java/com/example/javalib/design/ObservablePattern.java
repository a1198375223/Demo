package com.example.javalib.design;

import java.util.ArrayList;
import java.util.List;

/**
 * 观察者模式
 *
 * 抽象被观察者角色：也就是一个抽象主题，它把所有对观察者对象的引用保存在一个集合中，每个主题都可以有任意数量的观察者。抽象主题提供一个接口，可以增加和删除观察者角色。一般用一个抽象类和接口来实现。
 * 抽象观察者角色：为所有的具体观察者定义一个接口，在得到主题通知时更新自己。
 * 具体被观察者角色：也就是一个具体的主题，在集体主题的内部状态改变时，所有登记过的观察者发出通知。
 * 具体观察者角色：实现抽象观察者角色所需要的更新接口，一边使本身的状态与制图的状态相协调。
 */
public class ObservablePattern {

    /**
     * 观察者
     */
    public interface Observer {
        void update(String message);
    }

    /**
     * 被观察者
     */
    public interface Observerable {
        void registerObserver(Observer o);
        void removeObserver(Observer o);
        void notifyObserver();
    }


    public static class WechatServer implements Observerable {
        //注意到这个List集合的泛型参数为Observer接口，设计原则：面向接口编程而不是面向实现编程
        private List<Observer> list;
        private String message;

        public WechatServer() {
            list = new ArrayList<>();
        }

        @Override
        public void registerObserver(Observer o) {
            list.add(o);
        }

        @Override
        public void removeObserver(Observer o) {
            if(!list.isEmpty())
                list.remove(o);
        }

        //遍历
        @Override
        public void notifyObserver() {
            for(int i = 0; i < list.size(); i++) {
                Observer oserver = list.get(i);
                oserver.update(message);
            }
        }

        public void setInfomation(String s) {
            this.message = s;
            System.out.println("微信服务更新消息： " + s);
            //消息更新，通知所有观察者
            notifyObserver();
        }
    }

    public static class User implements Observer {
        private String name;
        private String message;

        public User(String name) {
            this.name = name;
        }

        @Override
        public void update(String message) {
            this.message = message;
            read();
        }

        public void read() {
            System.out.println(name + " 收到推送消息： " + message);
        }
    }

    public static void main(String[] args) {
        WechatServer server = new WechatServer();

        Observer userZhang = new User("ZhangSan");
        Observer userLi = new User("LiSi");
        Observer userWang = new User("WangWu");

        server.registerObserver(userZhang);
        server.registerObserver(userLi);
        server.registerObserver(userWang);
        server.setInfomation("PHP是世界上最好用的语言！");

        System.out.println("----------------------------------------------");
        server.removeObserver(userZhang);
        server.setInfomation("JAVA是世界上最好用的语言！");

    }
}
