package com.example.commonlibrary.rom.check;

import com.example.commonlibrary.rom.Rom;
import com.example.commonlibrary.rom.RomInfo;
import com.example.commonlibrary.rom.RomProperties;

public interface IChecker {
    // 获取rom
    Rom getRom();

    // 通过check制造商来判断手机类型
    boolean checkManufacturer(String manufacturer);


    // 获取os的版本
    RomInfo checkBuildProp(RomProperties properties);
}
