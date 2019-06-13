package com.example.dagger2.java;

import javax.inject.Inject;

public class Pot {
    private Flower flower;

    @Inject
    public Pot(@SanFlower Flower flower) {
        this.flower = flower;
    }

    public String show() {
        return flower.whisper();
    }
}
