package com.example.media.media_player;


import androidx.lifecycle.LifecycleOwner;

import com.example.media.common.AbstractPlayer;
import com.example.media.factory.AbsPlayerFactory;

public class AndroidMediaPlayerFactory extends AbsPlayerFactory {


    public AndroidMediaPlayerFactory() { }

    public static AndroidMediaPlayerFactory create() {
        return new AndroidMediaPlayerFactory();
    }

    @Override
    public AbstractPlayer createPlayer(LifecycleOwner owner) {
        return new AndroidMediaPlayer(owner);
    }
}
