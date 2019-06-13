package com.example.dagger2.mvp;

public interface BasePresenter<T> {

    // 建立view和presenter的关系
    void takeView(T view);

    // 解除与view的依赖关系
    void dropView();
}
