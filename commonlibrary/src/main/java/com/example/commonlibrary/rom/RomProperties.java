package com.example.commonlibrary.rom;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.Properties;

// 通过反射来读取build.prop内的属性
public class RomProperties {
    private static final String TAG = "RomProperties";
    private Properties properties;

    public RomProperties() {}

    public void setBuildProp(Properties prop) {
        this.properties = prop;
    }

    public String getProperty(String key) {
        if (properties != null) {
            return properties.getProperty(key);
        }
        return getSystemProperty(key);
    }

    public static String getSystemProperty(String key) {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method getMethod = clazz.getMethod("get", String.class, String.class);
            getMethod.setAccessible(true);
            return (String) getMethod.invoke(clazz, key, null);
        } catch (Exception e) {
            Log.e(TAG, "RomProperties 反射获取 build.prop 属性失败", e);
        }
        return null;
    }
}
