package com.example.media.model;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.util.MimeTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CastModel {
    public static final class Sample {
        /** The uri of the media content. */
        public final String uri;
        /** The name of the sample. */
        public final String name;
        /** The mime type of the sample media content. */
        public final String mimeType;
        /**
         * The {@link UUID} of the DRM scheme that protects the content, or null if the content is not
         * DRM-protected.
         */
        @Nullable
        public final UUID drmSchemeUuid;
        /**
         * The url from which players should obtain DRM licenses, or null if the content is not
         * DRM-protected.
         */
        @Nullable public final Uri licenseServerUri;

        /**
         * @param uri See {@link #uri}.
         * @param name See {@link #name}.
         * @param mimeType See {@link #mimeType}.
         */
        public Sample(String uri, String name, String mimeType) {
            this(uri, name, mimeType, /* drmSchemeUuid= */ null, /* licenseServerUriString= */ null);
        }

        public Sample(
                String uri,
                String name,
                String mimeType,
                @Nullable UUID drmSchemeUuid,
                @Nullable String licenseServerUriString) {
            this.uri = uri;
            this.name = name;
            this.mimeType = mimeType;
            this.drmSchemeUuid = drmSchemeUuid;
            this.licenseServerUri =
                    licenseServerUriString != null ? Uri.parse(licenseServerUriString) : null;
        }

        @Override
        public String toString() {
            return "Sample{" +
                    "uri='" + uri + '\'' +
                    ", name='" + name + '\'' +
                    ", mimeType='" + mimeType + '\'' +
                    ", drmSchemeUuid=" + drmSchemeUuid +
                    ", licenseServerUri=" + licenseServerUri +
                    '}';
        }
    }

    public static final String MIME_TYPE_DASH = MimeTypes.APPLICATION_MPD;
    public static final String MIME_TYPE_HLS = MimeTypes.APPLICATION_M3U8;
    public static final String MIME_TYPE_SS = MimeTypes.APPLICATION_SS;
    public static final String MIME_TYPE_VIDEO_MP4 = MimeTypes.VIDEO_MP4;

    /** The list of samples available in the cast demo app. */
    public static final List<Sample> SAMPLES;

    static {
        // App samples.
        ArrayList<Sample> samples = new ArrayList<>();

        // Clear content.
        samples.add(new Sample(
                        "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd",
                        "Clear DASH: Tears",
                        MIME_TYPE_DASH));
        samples.add(new Sample("https://html5demos.com/assets/dizzy.mp4", "Clear MP4: Dizzy", MIME_TYPE_VIDEO_MP4));

        SAMPLES = Collections.unmodifiableList(samples);
    }
}
