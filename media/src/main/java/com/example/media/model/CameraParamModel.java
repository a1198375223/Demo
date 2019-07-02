package com.example.media.model;

import androidx.lifecycle.MutableLiveData;

import com.example.room.mvp.BaseViewModel;

public class CameraParamModel extends BaseViewModel {
    public MutableLiveData<Integer> mCameraPreviewWidth, mCameraPreviewHeight;
    public MutableLiveData<String> mCameraPreviewFps;
    public MutableLiveData<Integer> mRectWidth, mRectHeight;
    public MutableLiveData<Integer> mZoomWidth, mZoomHeight;
    public MutableLiveData<Integer> mRotateDeg;
    public MutableLiveData<Float> mZoomPercent;


    public CameraParamModel() {
        mCameraPreviewFps = new MutableLiveData<>("");
        mCameraPreviewWidth = new MutableLiveData<>(0);
        mCameraPreviewHeight = new MutableLiveData<>(0);
        mRectWidth = new MutableLiveData<>(0);
        mRectHeight = new MutableLiveData<>(0);
        mZoomHeight = new MutableLiveData<>(0);
        mZoomWidth = new MutableLiveData<>(0);
        mRotateDeg = new MutableLiveData<>(0);
        mZoomPercent = new MutableLiveData<>(0.0f);
    }
}
