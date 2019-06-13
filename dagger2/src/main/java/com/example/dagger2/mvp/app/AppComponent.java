package com.example.dagger2.mvp.app;

import android.app.Application;

import com.example.dagger2.mvp.Dagger2Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        AndroidSupportInjectionModule.class // 1. 对class进行注册   2. 对string进行注册
})
public interface AppComponent extends AndroidInjector<Dagger2Application> {
    /**
     * AndroidInjector<?> 让@SubComponent注解的Module可以直接使用AndroidInjectiion.inject(this);来进行注入
     */


    // 自动生成component建造者模式
    @Component.Builder
    interface Builder {
        // 可以为所有在Component中依赖的module提供一个Application对象
        // 过程:
        // 1. 会在DaggerAppComponent中的Builder通过下面的方法来保存一个Application对象
        // 2. 在DaggerAppComponent中生成对应的Provider<T>对象
        // 3. 如果对应的module需要application对象,通过module对应的factory类的构造方法传入application对象
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
