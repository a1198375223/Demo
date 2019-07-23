package com.example.media.exo;

import androidx.lifecycle.LifecycleOwner;

import com.example.media.common.AbstractPlayer;
import com.example.media.factory.AbsPlayerFactory;

public class ExoMediaPlayerFactory extends AbsPlayerFactory {

    private ExoMediaPlayerFactory() {}


    public static ExoMediaPlayerFactory create() {
        return new ExoMediaPlayerFactory();
    }

    @Override
    public AbstractPlayer createPlayer(LifecycleOwner owner) {
        return new ExoMediaPlayer(owner);
    }
}
