package com.example.media.utils;

import com.example.media.R;
import com.example.media.model.PlaylistSample;
import com.example.media.model.Sample;
import com.example.media.model.UriSample;

public class SampleUtils {

    /**
     * 判断是否支持下载
     * @param sample 数据
     * @return 0-可以下载 or 错误字符串id
     */
    public static int getDownloadUnsupportedStringId(Sample sample) {
        if (sample instanceof PlaylistSample) {
            return R.string.download_playlist_unsupported;
        }

        UriSample uriSample = (UriSample) sample;

        if (uriSample.drmInfo != null) {
            return R.string.download_drm_unsupported;
        }

        if (uriSample.adTagUri != null) {
            return R.string.download_ads_unsupported;
        }

        String scheme = uriSample.uri.getScheme();
        if (!("http".equals(scheme) || "https".equals(scheme))) {
            return R.string.download_scheme_unsupported;
        }
        return 0;
    }
}
