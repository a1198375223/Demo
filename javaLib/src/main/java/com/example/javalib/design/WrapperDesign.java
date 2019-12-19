package com.example.javalib.design;

public class WrapperDesign {
    public interface Apple {
        void res();
    }

    public class ConcreteApple implements Apple {
        @Override
        public void res() {
            System.out.println("普通的苹果");
        }
    }


    public abstract class Decorator implements Apple {
        protected Apple apple;
        public Decorator(Apple apple) {
            this.apple = apple;
        }

        @Override
        public void res() {
            apple.res();
        }

    }
}
