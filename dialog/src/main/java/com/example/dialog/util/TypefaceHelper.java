package com.example.dialog.util;

import android.content.Context;
import android.graphics.Typeface;

import androidx.collection.SimpleArrayMap;

/**
 * 使用这个类来帮助从Asset目录下创建字体, 并进行缓存处理, 这样可以减少重复创建字体所带来的内存消耗
 */
public class TypefaceHelper {
    private static final SimpleArrayMap<String, Typeface> cache = new SimpleArrayMap<>();

    public static Typeface get(Context c, String name) {
        synchronized (cache) {
            if (!cache.containsKey(name)) {
                try {
                    Typeface t = Typeface.createFromAsset(
                            c.getAssets(), String.format("fonts/%s", name));
                    cache.put(name, t);
                    return t;
                } catch (RuntimeException e) {
                    return null;
                }
            }
            return cache.get(name);
        }
    }
}
