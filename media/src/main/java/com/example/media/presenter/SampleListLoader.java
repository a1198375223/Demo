package com.example.media.presenter;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.widget.Toast;

import com.example.commonlibrary.utils.AppUtils;
import com.example.commonlibrary.utils.Toasty;
import com.example.media.R;
import com.example.media.model.DrmInfo;
import com.example.media.model.PlaylistSample;
import com.example.media.model.Sample;
import com.example.media.model.SampleGroup;
import com.example.media.model.UriSample;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceInputStream;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SampleListLoader extends AsyncTask<String, Void, List<SampleGroup>> {
    private static final String TAG = "SampleListLoader";
    private boolean sawError;
    private Callback callback;

    public interface Callback {
        void callback(List<SampleGroup> groups);
    }

    public SampleListLoader(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected List<SampleGroup> doInBackground(String... uris) {
        List<SampleGroup> result = new ArrayList<>();
        Context context = AppUtils.app();
        String userAgent = Util.getUserAgent(context, "ExoPlayerTest");
        DataSource dataSource =
                new DefaultDataSource(context, userAgent, /* allowCrossProtocolRedirects= */ false);
        for (String uri : uris) {
            DataSpec dataSpec = new DataSpec(Uri.parse(uri));
            InputStream inputStream = new DataSourceInputStream(dataSource, dataSpec);
            try {
                readSampleGroups(new JsonReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)), result);
            } catch (Exception e) {
                Log.e(TAG, "Error loading sample list: " + uri, e);
                sawError = true;
            } finally {
                Util.closeQuietly(dataSource);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<SampleGroup> result) {
        onSampleGroups(result, sawError);
    }

    private void onSampleGroups(final List<SampleGroup> groups, boolean sawError) {
        if (sawError) {
            Toasty.showError(AppUtils.app().getString(R.string.sample_list_load_error));
        }
        if (callback != null) {
            callback.callback(groups);
        }
    }


    private void readSampleGroups(JsonReader reader, List<SampleGroup> groups) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readSampleGroup(reader, groups);
        }
        reader.endArray();
    }


    private void readSampleGroup(JsonReader reader, List<SampleGroup> groups) throws IOException {
        String groupName = "";
        ArrayList<Sample> samples = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "name":
                    groupName = reader.nextString();
                    break;
                case "samples":
                    reader.beginArray();
                    while (reader.hasNext()) {
                        samples.add(readEntry(reader, false));
                    }
                    reader.endArray();
                    break;
                case "_comment":
                    reader.nextString(); // Ignore.
                    break;
                default:
                    throw new ParserException("Unsupported name: " + name);
            }
        }
        reader.endObject();

        SampleGroup group = getGroup(groupName, groups);
        group.samples.addAll(samples);
    }


    private Sample readEntry(JsonReader reader, boolean insidePlaylist) throws IOException {
        String sampleName = null;
        Uri uri = null;
        String extension = null;
        String drmScheme = null;
        String drmLicenseUrl = null;
        String[] drmKeyRequestProperties = null;
        boolean drmMultiSession = false;
        ArrayList<UriSample> playlistSamples = null;
        String adTagUri = null;
        String sphericalStereoMode = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "name":
                    sampleName = reader.nextString();
                    break;
                case "uri":
                    uri = Uri.parse(reader.nextString());
                    break;
                case "extension":
                    extension = reader.nextString();
                    break;
                case "drm_scheme":
                    Assertions.checkState(!insidePlaylist, "Invalid attribute on nested item: drm_scheme");
                    drmScheme = reader.nextString();
                    break;
                case "drm_license_url":
                    Assertions.checkState(!insidePlaylist,
                            "Invalid attribute on nested item: drm_license_url");
                    drmLicenseUrl = reader.nextString();
                    break;
                case "drm_key_request_properties":
                    Assertions.checkState(!insidePlaylist,
                            "Invalid attribute on nested item: drm_key_request_properties");
                    ArrayList<String> drmKeyRequestPropertiesList = new ArrayList<>();
                    reader.beginObject();
                    while (reader.hasNext()) {
                        drmKeyRequestPropertiesList.add(reader.nextName());
                        drmKeyRequestPropertiesList.add(reader.nextString());
                    }
                    reader.endObject();
                    drmKeyRequestProperties = drmKeyRequestPropertiesList.toArray(new String[0]);
                    break;
                case "drm_multi_session":
                    drmMultiSession = reader.nextBoolean();
                    break;
                case "playlist":
                    Assertions.checkState(!insidePlaylist, "Invalid nesting of playlists");
                    playlistSamples = new ArrayList<>();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        playlistSamples.add((UriSample) readEntry(reader, true));
                    }
                    reader.endArray();
                    break;
                case "ad_tag_uri":
                    adTagUri = reader.nextString();
                    break;
                case "spherical_stereo_mode":
                    Assertions.checkState(
                            !insidePlaylist, "Invalid attribute on nested item: spherical_stereo_mode");
                    sphericalStereoMode = reader.nextString();
                    break;
                default:
                    throw new ParserException("Unsupported attribute name: " + name);
            }
        }
        reader.endObject();
        DrmInfo drmInfo =
                drmScheme == null
                        ? null
                        : new DrmInfo(drmScheme, drmLicenseUrl, drmKeyRequestProperties, drmMultiSession);
        if (playlistSamples != null) {
            UriSample[] playlistSamplesArray = playlistSamples.toArray(new UriSample[0]);
            return new PlaylistSample(sampleName, drmInfo, playlistSamplesArray);
        } else {
            return new UriSample(
                    sampleName,
                    drmInfo,
                    uri,
                    extension,
                    adTagUri,
                    sphericalStereoMode);
        }
    }

    private SampleGroup getGroup(String groupName, List<SampleGroup> groups) {
        for (int i = 0; i < groups.size(); i++) {
            if (Util.areEqual(groupName, groups.get(i).title)) {
                return groups.get(i);
            }
        }
        SampleGroup group = new SampleGroup(groupName);
        groups.add(group);
        return group;
    }



}
