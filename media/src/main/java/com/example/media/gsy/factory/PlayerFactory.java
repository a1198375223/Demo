package com.example.media.gsy.factory;

import com.example.media.gsy.common.IPlayerManager;
import com.example.media.gsy.manager.IjkPlayerManager;

public class PlayerFactory {
    private static Class<? extends IPlayerManager> sPlayerManager;

    public static void setPlayManager(Class<? extends IPlayerManager> playManager) {
        sPlayerManager = playManager;
    }

    public static IPlayerManager getPlayManager() {
        if (sPlayerManager == null) {
            sPlayerManager = IjkPlayerManager.class;
        }
        try {
            return sPlayerManager.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
