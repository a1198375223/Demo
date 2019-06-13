package com.example.dagger2.java;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * 这个就是使用module来管理的Factory类, 返回一个父类, 动态注入
 */
@Module
public class FlowerModule {
    @Provides
    @Named("Lily")
    Flower provideLily() {
        return new Lily();
    }

    @Provides
    @Named("Rose")
    Flower provideRose() {
        return new Rose();
    }

    @Provides
    @TonyFlower
    Flower provideTony() {
        return new Tony();
    }

    @Provides
    @SanFlower
    Flower provideSan() {
        return new San();
    }
}
