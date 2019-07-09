package com.example.media.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.media.R;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.TrackSelectionView;

import java.util.Collections;
import java.util.List;

public class TrackSelectionViewFragment extends Fragment implements TrackSelectionView.TrackSelectionListener {
    private static final String TAG = "TrackSelectionViewFragm";
    private boolean allowAdaptiveSelections;
    private boolean allowMultipleOverrides;
    private MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
    private int rendererIndex;
    List<DefaultTrackSelector.SelectionOverride> overrides;
    boolean isDisabled;

    public TrackSelectionViewFragment() {
        setRetainInstance(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "dialog select view create");
        View rootView = inflater.inflate(R.layout.fragment_track_select, container, false);
        TrackSelectionView trackSelectionView = rootView.findViewById(R.id.exo_track_selection_view);
        trackSelectionView.setShowDisableOption(true);
        // 是否可以多选
        trackSelectionView.setAllowMultipleOverrides(allowMultipleOverrides);
        // 是否可以选择
        trackSelectionView.setAllowAdaptiveSelections(allowAdaptiveSelections);
        trackSelectionView.init(mappedTrackInfo, rendererIndex, isDisabled, overrides, this);
        return rootView;
    }

    /**
     * 对fragment进行初始化
     * @param mappedTrackInfo 轨道信息
     * @param rendererIndex 渲染器的索引
     * @param initialIsDisabled 初始化完成是否显示
     * @param override
     * @param allowAdaptiveSelections 是否允许选择
     * @param allowMultipleOverrides 是否允许多选
     */
    public void init(MappingTrackSelector.MappedTrackInfo mappedTrackInfo, int rendererIndex,
                     boolean initialIsDisabled, DefaultTrackSelector.SelectionOverride override,
                     boolean allowAdaptiveSelections, boolean allowMultipleOverrides) {
        Log.d(TAG, "dialog select view init rendererIndex=" + rendererIndex + "\n" +
                "initialIsDisable=" + initialIsDisabled + "\n" +
                "allowAdaptiveSelections=" + allowAdaptiveSelections + "\n" +
                "allowMultipleOverrides=" + allowMultipleOverrides);
        this.mappedTrackInfo = mappedTrackInfo;
        this.rendererIndex = rendererIndex;
        this.isDisabled = initialIsDisabled;
        this.overrides = overrides == null ? Collections.emptyList() : Collections.singletonList(override);
        this.allowAdaptiveSelections = allowAdaptiveSelections;
        this.allowMultipleOverrides = allowMultipleOverrides;
    }

    @Override
    public void onTrackSelectionChanged(boolean isDisabled, List<DefaultTrackSelector.SelectionOverride> overrides) {
        Log.d(TAG, "onTrackSelectionChanged");
        this.isDisabled = isDisabled;
        this.overrides = overrides;
    }
}
