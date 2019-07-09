package com.example.media.model;

import android.content.Context;
import android.content.Intent;

import com.example.media.activity.PlayActivity;
import com.example.media.activity.PlayManagerActivity;
import com.example.media.presenter.GalileoPlayerManager;

public class Sample {
    public final String name;
    public final DrmInfo drmInfo;

    public Sample(String name, DrmInfo drmInfo) {
        this.name = name;
        this.drmInfo = drmInfo;
    }


    public Intent buildIntent(Context context, boolean preferExtensionDecoders, String abrAlgorithm) {
        Intent intent = new Intent(context, PlayManagerActivity.class);
        intent.putExtra(GalileoPlayerManager.PREFER_EXTENSION_DECODERS_EXTRA, preferExtensionDecoders);
        intent.putExtra(GalileoPlayerManager.ABR_ALGORITHM_EXTRA, abrAlgorithm);
        if (drmInfo != null) {
            drmInfo.updateIntent(intent);
        }
        return intent;
    }
}
