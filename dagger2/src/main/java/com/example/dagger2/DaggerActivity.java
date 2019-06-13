package com.example.dagger2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dagger2.java.DaggerFlowerComponent;
import com.example.dagger2.java.DaggerMainActivityComponent;
import com.example.dagger2.java.DaggerPotComponent;
import com.example.dagger2.java.Pot;
import com.example.dagger2.mvp.task.TasksActivity;

import javax.inject.Inject;

/**
 * dagger2是为了解决类依赖产生的耦合现象(我的理解), 个人觉得不是很有必要用到这个东西, 用多了对代码的理解会产生不好的影响.抱着学习的心态看一看
 * 注解的解释:
 * @Inject 可以对构造器 方法 属性进行标注
 *         1. 对构造器进行标注 只能标注一个构造器
 *         2. 标注的属性无法使用private修饰
 *         3. 标注在public方法上, dagger2会在构造器执行之后立即调用这个方法(比较少用到)
 *         4. 无法在第三方库中使用@Inject
 *         5. 无法在抽象类中使用@Inject
 * @Component 注入器
 * ------------------------------------------------------------
 * module和provides用来解决@Inject无法对抽象类的子类的构造方法使用的问题
 * @Module 标注在类上
 * @Provides 标注在方法上
 * @Binds 标注在方法上,可以用来取代@Provides减少生成的代码数量 但是有一个限制:使用这个注解的方法最多只能有一个参数
 * tip:记得在@Component加上module -> @Component(module = FlowerModule.class)
 * 例子:
 * @Module
 * public class FlowerModule {
 *     @Provides
 *     Flower provideFlower() {
 *         return new Rose();
 *     }
 * }
 * ------------------------------------------------------------
 * 限定符
 * @Qualifier 用来自定义注解 使用方法, 创建对应的注解类 使用@Qualifier来标注注解接口 然后在@Provides注解下加上自定义的注解 最后在Pot构造方法中使用注解
 * @Name 基于String的限定符 使用方法, 在@Porvides注解下加上@Named("xxx")注解 让后在Pot的构造方法加上@Named("xxx")注解
 * ------------------------------------------------------------
 * dependencies 直接指定依赖的类
 * @Subcomponent 嵌套依赖,生成内部类 @Subcomponent是@Component的内部类
 *
 * @Scope 用来定义注解的 用来管理依赖的生命周期. 实际就是让Component持有绑定以来的引用,
 *        如果实例在Component对象还存活的时候被回收 有可能导致内存泄漏 可以使用 @CanReleaseReferences 注解来自定义@Scope
 *        并在Application的OnLowMemory()中调用 ReleasableReferenceManager.releaseStrongReferences() 来动态改变Component为弱引用
 * @Singleton 是@Scope的默认实现. (使用的是DoubleCheck 类似单例模式的双重检查)需要在@Provides和@Component中同时使用才起作用,
 *            如果是@Binds只需要在@Component就可以其作用. 局部单利, 在不同类中初始化的并不是统一对象. 如果需要全局单利, 则让module对象的初始化放在Application中
 * @Reusable  是@Scope的默认实现. (使用的是SingleCheck)需要在@Provides和@Component中同时使用才起作用, 在多个线程的情况下是可以被创建多个实例的.
 *
 * @MapKey 用在定义一些依赖集合
 * @Lazy 在@Inject的时候并不初始化，而是等到你要使用的时候，主动调用其.get方法来获取实例
 */
public class DaggerActivity extends AppCompatActivity {
    private static final String TAG = "DaggerActivity";
    private TextView mDaggerTv;

    @Inject
    Pot pot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dagger2);
        mDaggerTv = findViewById(R.id.tv);

//        DaggerMainActivityComponent.create().inject(this);

        DaggerMainActivityComponent.builder()
                .potComponent(DaggerPotComponent.builder()
                        .flowerComponent(DaggerFlowerComponent.create())
                        .build())
                .build().inject(this);

        mDaggerTv.setText(pot.show());

        findViewById(R.id.bn).setOnClickListener(v -> {
            Intent intent = new Intent(DaggerActivity.this, TasksActivity.class);
            startActivity(intent);
        });
    }
}
