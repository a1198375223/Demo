package com.example.dagger2.java;

import com.example.dagger2.DaggerActivity;

import dagger.Component;

@Component(dependencies = PotComponent.class)
public interface MainActivityComponent {
    // 如果是使用这个方法, 需要在使用的DaggerActivity的Pot属性中添加注解@Inject
    // 使用这个方法, 会在DaggerActivity中去寻早@Inject注解的属性或者方法, 然后去对应的类中生成代码
    void inject(DaggerActivity activity);

    // 如果使用的是这个方法, 就可以不许需要DaggerActivity的Pot属性了
    // 使用这个Dagger会到Pot类中去寻找@Inject注解标注的构造器, 自动生成提供Pot依赖的代码.
//    Pot getPot();
}

