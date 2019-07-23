package com.example.media.factory;

import androidx.lifecycle.LifecycleOwner;

import com.example.media.common.AbstractPlayer;
import com.example.media.ijk.IjkPlayerManager;

public class IjkPlayerFactory extends AbsPlayerFactory {
    private IjkPlayerFactory() {}

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public AbstractPlayer createPlayer(LifecycleOwner owner) {
        return new IjkPlayerManager(owner);
    }
}
