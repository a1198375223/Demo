package com.example.media.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.media.presenter.GalileoPlayerManager;

public class UriSample extends Sample{
    public final Uri uri;
    public final String extension;
    public final String adTagUri;
    public final String sphericalStereoMode;

    public UriSample(String name, DrmInfo drmInfo,
                     Uri uri, String extension,
                     String adTagUri, String sphericalStereoMode) {
        super(name, drmInfo);
        this.uri = uri;
        this.extension = extension;
        this.adTagUri = adTagUri;
        this.sphericalStereoMode = sphericalStereoMode;
    }

    @Override
    public Intent buildIntent(Context context, boolean preferExtensionDecoders, String abrAlgorithm) {
        return super.buildIntent(context, preferExtensionDecoders, abrAlgorithm)
                .setData(uri)
                .putExtra(GalileoPlayerManager.EXTENSION_EXTRA, extension)
                .putExtra(GalileoPlayerManager.AD_TAG_URI_EXTRA, adTagUri)
                .putExtra(GalileoPlayerManager.SPHERICAL_STEREO_MODE_EXTRA, sphericalStereoMode)
                .setAction(GalileoPlayerManager.ACTION_VIEW);
    }
}
