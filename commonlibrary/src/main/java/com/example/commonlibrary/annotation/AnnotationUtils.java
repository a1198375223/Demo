package com.example.commonlibrary.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解主要是用来标识/解释代码的
 * 元注解： 标注注解的注解
 * 1. @Retention    : 解释 / 说明了注解的生命周期
 * 2. @Documented   : 将注解中的元素包含到 Javadoc文档中
 * 3. @Target       : 限定了注解作用的目标范围，包括类、方法等等
 * 4. @Inherited    : 使得一个被@Inherited注解的注解 作用的类的子类可以继承该类的注解
 * 5. @Repeatable   : 使得作用的注解可以取多个值
 *
 * Java内置注解
 * 1. @Deprecated           : 过时注解 标记已过时 & 被抛弃的元素（类、方法等）
 * 2. @Override             : 标记该方法需要被子类复写
 * 3. @SuppressWarnings     : 标记的元素会阻止编译器发出警告提醒
 * 4. @SafeVarargs          : 提醒开发者不要用参数做不安全的操作 & 阻止编译器产生 unchecked警告
 * 5. @FunctionalInterface  : 表示该接口 = 函数式接口
 *
 * AnnotationProcessor技术，和 APT(Annotation Processing Tool)技术类似，它是一种注解处理器，
 * 项目编译时对源代码进行扫描检测找出存活时间为RetentionPolicy.CLASS的指定注解，然后对注解进行解析处理，
 * 进而得到要生成的类的必要信息，然后根据这些信息动态生成对应的 java 类
 */
public class AnnotationUtils {

    /**
     * @Retention(RetentionPolicy.CLASS)    : 注解只被保留到编译进行时 & 不会被加载到 JVM
     * @Retention(RetentionPolicy.SOURCE)   : 注解只在源码阶段保留 & 在编译器进行编译时将被丢弃忽视
     * @Retention(RetentionPolicy.RUNTIME)  : 注解保留到程序运行时 & 会被加载进入到 JVM 中，所以在程序运行时可以获取到它们
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestRetention {}


    @Documented
    public @interface TestDocumented {}

    // ElementType.PACKAGE：可以给一个包进行注解
    // ElementType.ANNOTATION_TYPE：可以给一个注解进行注解
    // ElementType.TYPE：可以给一个类型进行注解，如类、接口、枚举
    // ElementType.CONSTRUCTOR：可以给构造方法进行注解
    // ElementType.METHOD：可以给方法进行注解
    // ElementType.PARAMETER 可以给一个方法内的参数进行注解
    // ElementType.FIELD：可以给属性进行注解
    // ElementType.LOCAL_VARIABLE：可以给局部变量进行注解
    @Target(ElementType.ANNOTATION_TYPE)
    public @interface TestTarget {}



    public @interface Job {
        Person[]  value();
    }


    @Repeatable(Job.class)
    public @interface Person{
        String role = "";
    }


    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationTest {
        int id();

        String msg() default "Hi";
    }
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationTest1 {
        int id();

        String msg() default "Hi";
    }

}
