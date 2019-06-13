package com.example.dagger2.java;


import dagger.Component;

@Component(modules = PotModule.class, dependencies = FlowerComponent.class)
public interface PotComponent {
    Pot getPot();
}
