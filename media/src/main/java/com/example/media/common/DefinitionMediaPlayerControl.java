package com.example.media.common;

import java.util.LinkedHashMap;

// 切换清晰度
public interface DefinitionMediaPlayerControl extends MediaPlayerControl {

    LinkedHashMap<String, String> getDefinitionData();

    void switchDefinition(String definition);
}
