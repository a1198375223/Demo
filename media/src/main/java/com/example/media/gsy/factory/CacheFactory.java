package com.example.media.gsy.factory;

import com.example.media.gsy.common.ICacheManager;
import com.example.media.gsy.manager.ProxyCacheManager;

public class CacheFactory {
    private static Class<? extends ICacheManager> sICacheManager;

    public static void setCacheManager(Class<? extends ICacheManager>  cacheManager) {
        sICacheManager = cacheManager;
    }

    public static ICacheManager getCacheManager() {
        if (sICacheManager == null) {
            sICacheManager = ProxyCacheManager.class;
        }
        try {
            return sICacheManager.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
