package com.example.javalib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义一个类加载器
 *
 * 类加载器的过程
 * 1. 加载：查找并加载二进制数据 ---> 类的加载指的是将类的.class文件中的二进制数据读入到内存中，
 *                                  将其放在运行时数据区的方法区内，然后在堆区创建一个java.lang.Class对象，用来封装类在方法区内的数据结构
 * 2. 连接： --> 连接就是将已经读入到内存的类的二进制数据合并到虚拟机的运行时环境中去。
 *      a. 验证：确保被加载的类的正确性
 *      b. 准备：为类的静态变量分配内存, 并将其初始化为默认值
 * 3. 解析：把类中的符号引用转换为直接引用
 * 4. 初始化：为类的静态变量赋予正确的初始值
 *
 *
 * 类加载机制： 父委托机制
 *
 *
 * 类加载器的类型：
 * 1. 根类加载器（Bootstrap）   : 该加载器没有父加载器. 它负责加载虚拟机的核心类库, 如java.lang.*等。 不继承ClassLoader
 * 2. 扩展类加载器（Extension） : 父容器为根类加载器, 扩展类加载器是纯Java类, 是java.lang.ClassLoader的子类
 * 3. 系统类加载器（System）    : 父容器为扩展类加载器, 是所有用户自定义加载器的父加载器. 是纯Java类, 是java.lang.ClassLoader类的子类
 *
 *
 * 类初始化的时机：
 * 1. 创建类的实例
 * 2. 访问某个类或接口的静态变量，或者对该静态 变量赋值
 * 3. 调用类的静态方法
 * 4. 反射（如 Class.forName(“com.shengsiyuan.Test”) ）
 * 5. 初始化一个类的子类
 * 6. Java虚拟机启动时被标明为启动类的类（Java Test）
 */
public class MyClassLoader extends ClassLoader {

    private String classpath;

    public MyClassLoader(String classpath) {

        this.classpath = classpath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] classDate = getDate(name);
            if (classDate == null) {

            } else {
                //defineClass方法将字节码转化为类
                return defineClass(name, classDate, 0, classDate.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.findClass(name);
    }


    //返回类的字节码
    private byte[] getDate(String className) throws IOException{
        String path=classpath + File.separatorChar +
                className.replace('.', File.separatorChar)+".class";
        try (InputStream in = new FileInputStream(path); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    // 调用ClassLoader类的loadClass方法加载 一个类，并不是对类的主动使用，不会导 致类的初始化。
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }
}
