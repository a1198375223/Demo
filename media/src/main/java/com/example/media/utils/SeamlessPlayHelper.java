package com.example.media.utils;

import com.example.commonlibrary.utils.AppUtils;
import com.example.media.R;
import com.example.media.view.VideoView;

public class SeamlessPlayHelper {
    private VideoView mVideoView;
    private static SeamlessPlayHelper instance;

    private SeamlessPlayHelper() {
        mVideoView = new VideoView(AppUtils.app());
        mVideoView.setId(R.id.video_player);
    }

    public static SeamlessPlayHelper getInstance() {
        if (instance == null) {
            synchronized (SeamlessPlayHelper.class) {
                if (instance == null) {
                    instance = new SeamlessPlayHelper();
                }
            }
        }
        return instance;
    }


    public VideoView getVideoView() {
        return mVideoView;
    }
}
