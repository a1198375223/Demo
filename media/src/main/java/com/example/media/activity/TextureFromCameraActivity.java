package com.example.media.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.media.R;

import com.example.media.model.CameraParamModel;
import com.example.media.presenter.Camera2Presenter;
import com.example.media.view.AutoFitTextureView;

public class TextureFromCameraActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = TextureFromCameraActivity.class.getSimpleName();
    // 进度条
    private static final int DEFAULT_ZOOM_PERCENT = 0;
    private static final int DEFAULT_SIZE_PERCENT = 50;
    private static final int DEFAULT_ROTATE_PERCENT = 0;

    private AutoFitTextureView mTextureView;
    private SeekBar mZoomBar;
    private SeekBar mSizeBar;
    private SeekBar mRotationBar;
    private TextView mParamTv;
    private TextView mSizeTv;
    private TextView mZoomTv;



    private CameraParamModel mModel;
    private int mCameraPreviewWidth;
    private int mCameraPreviewHeight;
    private String mCameraPreviewFps;
    private int mRectWidth;
    private int mRectHeight;
    private int mZoomWidth;
    private int mZoomHeight;
    private float mZoomPercent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_from_camera);
        mModel = ViewModelProviders.of(this).get(CameraParamModel.class);

        mZoomBar = findViewById(R.id.zoom_bar);
        mZoomTv = findViewById(R.id.zoom_tv);
        mZoomBar.setOnSeekBarChangeListener(this);
        mZoomBar.setProgress(DEFAULT_ZOOM_PERCENT);

        mSizeBar = findViewById(R.id.size_bar);
        mSizeTv = findViewById(R.id.size_tv);
        mSizeBar.setOnSeekBarChangeListener(this);
        mSizeBar.setProgress(DEFAULT_SIZE_PERCENT);

        mRotationBar = findViewById(R.id.rotation_bar);
        mParamTv = findViewById(R.id.camera_param_tv);
        mRotationBar.setOnSeekBarChangeListener(this);
        mRotationBar.setProgress(DEFAULT_ROTATE_PERCENT);

        mTextureView = findViewById(R.id.camera);


        mModel.mCameraPreviewFps.observe(this, s -> {
            mCameraPreviewFps = s;
            updateControls();
        });

        mModel.mRectHeight.observe(this, integer -> {
            mRectHeight = integer;
            updateControls();
        });

        mModel.mRectWidth.observe(this, integer -> {
            mRectWidth = integer;
            updateControls();
        });

        mModel.mCameraPreviewHeight.observe(this, integer -> {
            mCameraPreviewHeight = integer;
            updateControls();
        });

        mModel.mCameraPreviewWidth.observe(this, integer -> {
            mCameraPreviewWidth = integer;
            updateControls();
        });

        mModel.mZoomWidth.observe(this, integer -> {
            mZoomWidth = integer;
            updateControls();
        });

        mModel.mZoomHeight.observe(this, integer -> {
            mZoomHeight = integer;
            updateControls();
        });

        mModel.mZoomPercent.observe(this, aFloat -> {
            Log.d(TAG, "scale =" + aFloat);
            mZoomPercent = aFloat;
            mZoomBar.setProgress((int) (mZoomPercent));
        });

        updateControls();

        new Camera2Presenter(this, this, mTextureView, mModel);
    }

    public void updateControls() {
        String str = getString(R.string.tfcCameraParams, mCameraPreviewWidth, mCameraPreviewHeight, mCameraPreviewFps);
        ((TextView) findViewById(R.id.camera_param_tv)).setText(str);

        str = getString(R.string.tfcRectSize, mRectWidth, mRectHeight);
        ((TextView) findViewById(R.id.size_tv)).setText(str);

        str = getString(R.string.tfcZoomArea, mZoomWidth, mZoomHeight);
        ((TextView) findViewById(R.id.zoom_tv)).setText(str);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == mZoomBar) {

        } else if (seekBar == mSizeBar) {

        } else if (seekBar == mRotationBar) {

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

}
