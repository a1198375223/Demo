package com.example.media.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.media.common.DefinitionMediaPlayerControl;

import java.util.LinkedHashMap;

public class DefinitionVideoView extends VideoView implements DefinitionMediaPlayerControl {
    private static final String TAG = "DefinitionVideoView";
    private LinkedHashMap<String, String> mDefinitionMap;
    private String mCurrentDefinition;

    public DefinitionVideoView(@NonNull Context context) {
        super(context);
    }

    public DefinitionVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DefinitionVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LinkedHashMap<String, String> getDefinitionData() {
        return mDefinitionMap;
    }

    @Override
    public void switchDefinition(String definition) {
        String url = mDefinitionMap.get(definition);
        if (definition.equals(mCurrentDefinition)) return;
        mCurrentUrl = url;
        addDisplay();
        getCurrentPosition();
        startPrepare(true);
        mCurrentDefinition = definition;
    }

    public void setDefinitionVideos(LinkedHashMap<String, String> videos) {
        this.mDefinitionMap = videos;
        this.mCurrentUrl = getValueFromLinkedMap(videos, 0);
    }

    public static String getValueFromLinkedMap(LinkedHashMap<String, String> map, int index) {
        int currentIndex = 0;
        for (String key : map.keySet()) {
            if (currentIndex == index) {
                return map.get(key);
            }
            currentIndex++;
        }
        return null;
    }
}
