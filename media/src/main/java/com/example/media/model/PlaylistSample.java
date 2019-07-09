package com.example.media.model;

import android.content.Context;
import android.content.Intent;

import com.example.media.presenter.GalileoPlayerManager;

public class PlaylistSample extends Sample {
    public final UriSample[] children;


    public PlaylistSample(String name, DrmInfo drmInfo,
                          UriSample... children) {
        super(name, drmInfo);
        this.children = children;
    }

    @Override
    public Intent buildIntent(Context context, boolean preferExtensionDecoders, String abrAlgorithm) {
        String[] uris = new String[children.length];
        String[] extensions = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            uris[i] = children[i].uri.toString();
            extensions[i] = children[i].extension;
        }
        return super.buildIntent(context, preferExtensionDecoders, abrAlgorithm)
                .putExtra(GalileoPlayerManager.URI_LIST_EXTRA, uris)
                .putExtra(GalileoPlayerManager.EXTENSION_LIST_EXTRA, extensions)
                .setAction(GalileoPlayerManager.ACTION_VIEW_LIST);

    }
}
