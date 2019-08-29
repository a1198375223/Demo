package com.example.javalib.design;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理模式有两个角色，一个是代理类，一个是委托类，委托类也是真正的业务类，两者都有相同的接口；
 *
 * 代理类主要负责为委托类预处理消息、过滤消息、把消息转发给委托类，以及事后处理消息等。代理类与委托类之间通常会存
 * 在关联关系，一个代理类的对象与一个委托类的对象关联，代理类的对象并不真正实现服务，而是通过调用委托类的对象的相关方法，来提供特定的服务。
 *
 */
public class DynProxy {

    public interface User {
        /**
         * 登录
         * @param username 用户名
         * @param pwd 密码
         * @return
         */
        boolean login(String username, String pwd);

        /**
         * 退出
         */
        void logout(String username);
    }

    public static class UserImpl implements User{

        @Override
        public boolean login(String username, String pwd) {
            // 简化问题，直接登录成功
            System.out.println(username+" 登录成功.");
            return true;
        }

        @Override
        public void logout(String username) {
            System.out.println(username+" 成功退出.");
        }
    }

    public static class UserDynamicProxy implements InvocationHandler {
        // 在线人数
        public static int count = 0;
        // 委托对象
        private Object target;

        /**
         * 返回代理对象
         * @param target
         * @return
         */
        @SuppressWarnings("unchecked")
        public <T> T getProxyInstance(Object target) {
            // 委托对象，真正的业务对象
            this.target = target;
            // 获取Object类的ClassLoader
            ClassLoader cl = target.getClass().getClassLoader();
            // 获取接口数组
            Class<?>[] cs = target.getClass().getInterfaces();
            // 获取代理对象并返回
            return (T) Proxy.newProxyInstance(cl, cs, this);
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object r;
            // 执行之前
            r = method.invoke(target, args);
            // 判断如果是登录方法
            if("login".equals(method.getName())) {
                if((boolean) r) {
                    // 当前在线人数+1
                    count += 1;
                }
            }
            // 判断如果是退出方法
            else if("logout".equals(method.getName())) {
                // 当前在线人数-1
                count -= 1;
            }
            showCount(); // 输出在线人数
            // 执行之后
            return r;
        }


        /**
         * 输出在线人数
         */
        public void showCount() {
            System.out.println("当前在线人数：" + count + " 人.");
        }
    }


    public static void main(String[] args) {
        // 真实角色，委托人
        User user = new UserImpl();    // 可执行真正的登录退出功能

        // 代理类
        UserDynamicProxy proxy = new UserDynamicProxy();

        // 获取委托对象user的代理对象
        User userProxy = proxy.getProxyInstance(user);

        // 系统运行，用户开始登录退出
        userProxy.login("小明", "111");
        userProxy.login("小红", "111");
        userProxy.login("小刚", "111");
        userProxy.logout("小明");
        userProxy.logout("小刚");
        userProxy.login("小黄", "111");
        userProxy.login("小黑", "111");
        userProxy.logout("小黄");
        userProxy.login("小李", "111");
        userProxy.logout("小李");
        userProxy.logout("小黄");
        userProxy.logout("小红");
    }
}
