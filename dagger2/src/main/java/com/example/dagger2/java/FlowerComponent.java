package com.example.dagger2.java;

import javax.inject.Named;

import dagger.Component;

@Component(modules = FlowerModule.class)
public interface FlowerComponent {
    @SanFlower
    Flower getSanFlower();

    @TonyFlower
    Flower getTonyFlower();

    @Named("Rose")
    Flower getRoseFlower();

    @Named("Lily")
    Flower getLilyFlower();
}
