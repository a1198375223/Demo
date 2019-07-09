package com.example.media.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.media.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackSelectionDialog extends DialogFragment {
    private static final String TAG = "TrackSelectionDialog";

    private final SparseArray<TrackSelectionViewFragment> tabFragments;
    private final ArrayList<Integer> tabTrackTypes;


    private int titleId;
    private DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onDismissListener;

    // 通过DefaultTrackSelector来创建TrackSelectionDialog
    public static TrackSelectionDialog createForTrackSelector(DefaultTrackSelector trackSelector, DialogInterface.OnDismissListener onDismissListener) {
        // 获取轨道信息
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(trackSelector.getCurrentMappedTrackInfo());
        TrackSelectionDialog trackSelectionDialog = new TrackSelectionDialog();
        // 获取轨道参数
        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
        trackSelectionDialog.init(
                R.string.track_selection_title, // dialog title
                mappedTrackInfo,
                parameters,
                true,
                false,
                (dialogInterface, which) -> { // 点击确定按钮
                    DefaultTrackSelector.ParametersBuilder builder = parameters.buildUpon();
                    for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
                        builder.clearSelectionOverrides(i)
                                .setRendererDisabled(i, trackSelectionDialog.getIsDisabled(i));

                        List<DefaultTrackSelector.SelectionOverride> overrides = trackSelectionDialog.getOverrides(i);

                        if (!overrides.isEmpty()) {
                            builder.setSelectionOverride(i, mappedTrackInfo.getTrackGroups(i), overrides.get(0));
                        }
                    }
                    trackSelector.setParameters(builder);
                },
                onDismissListener
        );
        return trackSelectionDialog;
    }


    /**
     * 通过MappedTrackInfo和DefaultTrackSelector.Parameters来创建dialog
     * @param titleId 标题
     * @param mappedTrackInfo 轨道信息
     * @param initialParameters 参数
     * @param allowAdaptiveSelections 是否允许选择
     * @param allowMultipleOverrides 是否有多个override
     * @param onClickListener 确定的点击事件
     * @param onDismissListener 取消点击事件
     * @return 返回dialog
     */
    public static TrackSelectionDialog createForMappedTrackInfoAndParameters(
            int titleId,
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo,
            DefaultTrackSelector.Parameters initialParameters,
            boolean allowAdaptiveSelections,
            boolean allowMultipleOverrides,
            DialogInterface.OnClickListener onClickListener,
            DialogInterface.OnDismissListener onDismissListener) {
        Log.d(TAG, "create dialog");
        TrackSelectionDialog trackSelectionDialog = new TrackSelectionDialog();
        trackSelectionDialog.init(
                titleId,
                mappedTrackInfo,
                initialParameters,
                allowAdaptiveSelections,
                allowMultipleOverrides,
                onClickListener,
                onDismissListener
        );

        return trackSelectionDialog;
    }

    private TrackSelectionDialog() {
        tabFragments = new SparseArray<>();
        tabTrackTypes = new ArrayList<>();
        setRetainInstance(true);
    }

    private void init(int titleId, MappingTrackSelector.MappedTrackInfo mappedTrackInfo,
                      DefaultTrackSelector.Parameters initialParameters,
                      boolean allowAdaptiveSelections, boolean allowMultipleOverrides,
                      DialogInterface.OnClickListener onClickListener, DialogInterface.OnDismissListener onDismissListener) {
        this.titleId = titleId;
        this.onClickListener = onClickListener;
        this.onDismissListener = onDismissListener;
        Log.d(TAG, "init dialog renderer count=" + mappedTrackInfo.getRendererCount());

        // 得到渲染的数量
        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
            if (showTabForRenderer(mappedTrackInfo, i)) {
                int trackType = mappedTrackInfo.getRendererType(i);
                TrackGroupArray trackGroupArray = mappedTrackInfo.getTrackGroups(i);
                TrackSelectionViewFragment tabFragment = new TrackSelectionViewFragment();
                tabFragment.init(mappedTrackInfo,
                        i,
                        initialParameters.getRendererDisabled(i), // 渲染器是否可用
                        initialParameters.getSelectionOverride(i, trackGroupArray), // 当前渲染器的override
                        allowAdaptiveSelections,
                        allowMultipleOverrides);
                tabFragments.put(i, tabFragment);
                tabTrackTypes.add(trackType);
            } else {
                Log.e(TAG, "Failed to show tab for renderer count=" + mappedTrackInfo.getRendererCount() + " current not show id=" + i);
            }
        }
    }


    // 是否能显示渲染器
    private static boolean showTabForRenderer(MappingTrackSelector.MappedTrackInfo mappedTrackInfo, int renderIndex) {
        // 获取所有的轨道
        TrackGroupArray trackGroupArray = mappedTrackInfo.getTrackGroups(renderIndex);
        if (trackGroupArray.length == 0) {
            return false;
        }

        int trackType = mappedTrackInfo.getRendererType(renderIndex);
        return isSupportedTrackType(trackType);
    }

    // 是否支持轨道类型
    private static boolean isSupportedTrackType(int trackType) {
        switch (trackType) {
            case C.TRACK_TYPE_VIDEO: // 2
            case C.TRACK_TYPE_AUDIO: // 1
            case C.TRACK_TYPE_TEXT: // 3
                return true;
            default:
                Log.e(TAG, "not support type=" + trackType);
                return false;
        }
    }


    // 判断是否显示
    public boolean getIsDisabled(int rendererIndex) {
        TrackSelectionViewFragment rendererView = tabFragments.get(rendererIndex);
        return rendererView != null && rendererView.isDisabled;
    }


    // 返回overrides
    public List<DefaultTrackSelector.SelectionOverride> getOverrides(int rendererIndex) {
        TrackSelectionViewFragment rendererView = tabFragments.get(rendererIndex);
        return  rendererView == null ? Collections.emptyList() : rendererView.overrides;
    }


    // 判断dialog是否有内容显示
    public static boolean willHaveContent(DefaultTrackSelector trackSelector) {
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        return  mappedTrackInfo != null && willHaveContent(mappedTrackInfo);
    }


    // 判断dialog是否有内容显示
    public static boolean willHaveContent(MappingTrackSelector.MappedTrackInfo mappedTrackInfo) {
        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
            if (showTabForRenderer(mappedTrackInfo, i)) {
                return true;
            }
        }
        return false;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AppCompatDialog dialog = new AppCompatDialog(requireActivity(), R.style.TrackSelectionDialogThemeOverlay);
        dialog.setTitle(titleId);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "dialog create view");
        View dialogView = inflater.inflate(R.layout.dialog_track_selection, container, false);
        TabLayout tabLayout = dialogView.findViewById(R.id.track_selection_dialog_tab_layout);
        ViewPager viewPager = dialogView.findViewById(R.id.track_selection_dialog_view_pager);
        Button cancelButton = dialogView.findViewById(R.id.track_selection_dialog_cancel_button);
        Button okButton = dialogView.findViewById(R.id.track_selection_dialog_ok_button);

        viewPager.setAdapter(new FragmentAdapter(getChildFragmentManager()));

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(tabFragments.size() > 1? View.VISIBLE : View.GONE);
        cancelButton.setOnClickListener(view -> dismiss());
        okButton.setOnClickListener(view -> {
            onClickListener.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
            dismiss();
        });
        return dialogView;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        onDismissListener.onDismiss(dialog);
    }

    @Override
    public int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        Log.d(TAG, "show dialog");
        return super.show(transaction, tag);
    }

    private final class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return tabFragments.valueAt(position);
        }

        @Override
        public int getCount() {
            return tabFragments.size();
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return getTrackTypeString(getResources(), tabTrackTypes.get(position));
        }
    }

    private String getTrackTypeString(Resources resources, int trackType) {
        switch (trackType) {
            case C.TRACK_TYPE_VIDEO:
                return resources.getString(R.string.exo_track_selection_title_video);
            case C.TRACK_TYPE_AUDIO:
                return resources.getString(R.string.exo_track_selection_title_audio);
            case C.TRACK_TYPE_TEXT:
                return resources.getString(R.string.exo_track_selection_title_text);
            default:
                throw new IllegalArgumentException();
        }
    }
}
