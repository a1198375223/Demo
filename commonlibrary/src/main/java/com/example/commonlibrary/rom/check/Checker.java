package com.example.commonlibrary.rom.check;

import android.text.TextUtils;

public abstract class Checker implements IChecker {

    // 获取制造商名称
    public abstract String getManufacturer();

    // 对比制造商的名称来判断是那个厂商
    @Override
    public boolean checkManufacturer(String manufacturer) {
        return TextUtils.equals(manufacturer.toLowerCase(), getManufacturer());
    }
}
