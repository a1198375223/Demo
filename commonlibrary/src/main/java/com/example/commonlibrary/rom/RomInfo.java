package com.example.commonlibrary.rom;

// rom中的信息, 主要就是获取os的版本
public class RomInfo {
    private Rom rom;
    private int baseVersion;
    private String version;

    public RomInfo(Rom rom) {
        this.rom = rom;
    }

    public RomInfo(Rom rom, int baseVersion, String version) {
        this.rom = rom;
        this.baseVersion = baseVersion;
        this.version = version;
    }

    public Rom getRom() {
        return rom;
    }

    public void setRom(Rom rom) {
        this.rom = rom;
    }

    public int getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(int baseVersion) {
        this.baseVersion = baseVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    // 制造商名称
    public interface ManufacturerList {
        String HUAWEI = "huawei";    // 华为
        String MEIZU = "meizu";      // 魅族
        String XIAOMI = "xiaomi";    // 小米
        String SONY = "sony";        // 索尼
        String SAMSUNG = "samsung";  // 三星
        String LETV = "letv";        // 乐视
        String ZTE = "zte";          // 中兴
        String YULONG = "yulong";    // 酷派
        String LENOVO = "lenovo";    // 联想
        String LG = "lg";            // LG
        String OPPO = "oppo";        // oppo
        String VIVO = "vivo";        // vivo
        String SMARTISAN = "smartisan";// Smartisan

        String AMIGO = "amigo";      // 金立
        String ONEPLUS = "oneplus";  // 一加
    }

    @Override
    public String toString() {
        return "RomInfo{" +
                "rom=" + rom +
                ", baseVersion=" + baseVersion +
                ", version='" + version + '\'' +
                '}';
    }
}
