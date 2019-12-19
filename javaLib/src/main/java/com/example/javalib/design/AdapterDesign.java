package com.example.javalib.design;

/**
 * 适配器模式(Adapter Pattern)：将一个接口转换成客户希望的另一个接口，使接口不兼容的那些类可以一起工作
 * 其别名为包装器(Wrapper)。适配器模式既可以作为类结构型模式，也可以作为对象结构型模式。
 */
public class AdapterDesign {
    public class Adaptee {
        public void adapteeRequest() {
            System.out.println("被适配者的方法");
        }
    }

    public interface Target {
        void request();
    }

    public class ConcreteTarget implements Target {
        @Override
        public void request() {
            System.out.println("concreteTarget目标方法");
        }
    }

    public class Adapter implements Target{
        // 适配者是对象适配器的一个属性
        private Adaptee adaptee = new Adaptee();

        @Override
        public void request() {
            //...
            adaptee.adapteeRequest();
            //...
        }
    }

}
