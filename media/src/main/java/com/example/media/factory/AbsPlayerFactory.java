package com.example.media.factory;

import androidx.lifecycle.LifecycleOwner;

import com.example.media.common.AbstractPlayer;

public abstract class AbsPlayerFactory {
    public abstract AbstractPlayer createPlayer(LifecycleOwner owner);
}
