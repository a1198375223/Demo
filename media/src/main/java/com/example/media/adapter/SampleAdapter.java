package com.example.media.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.example.commonlibrary.utils.AppUtils;
import com.example.commonlibrary.utils.Toasty;
import com.example.media.R;
import com.example.media.common.DownloadTracker;
import com.example.media.model.Sample;
import com.example.media.model.SampleGroup;
import com.example.media.model.UriSample;
import com.example.media.utils.DownloadUtils;
import com.example.media.utils.SampleUtils;
import com.google.android.exoplayer2.RenderersFactory;

import java.util.Collections;
import java.util.List;

public class SampleAdapter extends BaseExpandableListAdapter implements View.OnClickListener {
    private static final String TAG = "SampleAdapter";

    private List<SampleGroup> sampleGroups;
    private DownloadTracker downloadTracker;
    private boolean checked;
    private FragmentManager manager;

    public SampleAdapter(FragmentManager manager, DownloadTracker downloadTracker) {
        sampleGroups = Collections.emptyList();
        this.downloadTracker = downloadTracker;
        this.manager = manager;
    }

    public void setSampleGroups(List<SampleGroup> sampleGroups) {
        this.sampleGroups = sampleGroups;
        notifyDataSetChanged();
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public void onClick(View view) {
        Sample sample = (Sample) view.getTag();

        int downloadUnsupportedStringId = SampleUtils.getDownloadUnsupportedStringId(sample);
        if (downloadUnsupportedStringId != 0) {
            Toasty.showError(AppUtils.app().getString(downloadUnsupportedStringId));
        } else {
            UriSample uriSample = ((UriSample) sample);
            RenderersFactory renderersFactory = DownloadUtils.getInstance().buildRenderersFactory(checked);
            downloadTracker.toggleDownload(manager, sample.name, uriSample.uri, uriSample.extension, renderersFactory);
        }
    }

    // 获取组的数量
    @Override
    public int getGroupCount() {
        return sampleGroups.size();
    }

    // 获取组的信息
    @Override
    public SampleGroup getGroup(int groupPosition) {
        return sampleGroups.get(groupPosition);
    }

    // 获取组的id
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // 获取组的view
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }
        ((TextView) view).setText(getGroup(groupPosition).title);
        return view;
    }

    // 获取子view的数量
    @Override
    public int getChildrenCount(int groupPosition) {
        return sampleGroups.get(groupPosition).samples.size();
    }

    // 获取子view的数据
    @Override
    public Sample getChild(int groupPosition, int childPosition) {
        return sampleGroups.get(groupPosition).samples.get(childPosition);
    }


    // 获取子view的Id
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // 是否拥有稳定的id
    @Override
    public boolean hasStableIds() {
        return false;
    }

    // 获取子view
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
            View downloadButton = view.findViewById(R.id.download_button);
            downloadButton.setOnClickListener(this);
            downloadButton.setFocusable(false);
        }
        Sample sample = sampleGroups.get(groupPosition).samples.get(childPosition);
        view.setTag(sample);

        ((TextView) view.findViewById(R.id.sample_title)).setText(sample.name);

        boolean canDownload = SampleUtils.getDownloadUnsupportedStringId(sample) == 0;

        boolean isDownloaded = canDownload && downloadTracker.isDownloaded(((UriSample) sample).uri);

        ImageButton downloadButton = view.findViewById(R.id.download_button);

        downloadButton.setTag(sample);
        downloadButton.setColorFilter(canDownload ? (isDownloaded ? 0xFF42A5F5 : 0xFFBDBDBD) : 0xFFEEEEEE);
        downloadButton.setImageResource(isDownloaded ? R.drawable.ic_download_done : R.drawable.ic_download);
        return view;
    }

    // 子view是否可以选择
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
