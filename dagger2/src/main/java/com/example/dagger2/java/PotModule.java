package com.example.dagger2.java;

import dagger.Module;
import dagger.Provides;

@Module
public class PotModule {
    @Provides
    Pot providePot(@TonyFlower Flower flower) {
        return new Pot(flower);
    }
}
