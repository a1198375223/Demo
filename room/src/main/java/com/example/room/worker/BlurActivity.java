package com.example.room.worker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.bumptech.glide.Glide;
import com.example.room.R;

public class BlurActivity extends AppCompatActivity {
    private static final String TAG = "BlurActivity";

    private Button mBegin, mCancel, mSeePic;
    private RadioGroup mBlurGroup;
    private ImageView mImage;
    private ProgressBar mProgressBar;

    private BlurViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);

        mViewModel = ViewModelProviders.of(this).get(BlurViewModel.class);

        mBegin = findViewById(R.id.begin);
        mCancel = findViewById(R.id.cancel);
        mSeePic = findViewById(R.id.see);
        mBlurGroup = findViewById(R.id.radio_blur_group);
        mImage = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);

        Intent intent = getIntent();
        String imageUriExtra = intent.getStringExtra(Constants.KEY_IMAGE_URI);
        mViewModel.setImageUri(imageUriExtra);
        if (mViewModel.getImageUri() != null) {
            Glide.with(this).load(mViewModel.getImageUri()).into(mImage);
        }

        mBegin.setOnClickListener(view -> mViewModel.applyBlur(getBlurLevel()));

        mSeePic.setOnClickListener(view -> {
            Uri currentUri = mViewModel.getOutputUri();
            if (currentUri != null) {
                Intent actionView = new Intent(Intent.ACTION_VIEW, currentUri);
                if (actionView.resolveActivity(getPackageManager()) != null) {
                    startActivity(actionView);
                }
            }
        });

        mCancel.setOnClickListener(view -> mViewModel.cancelWork());

        mViewModel.getSavedWorkInfo().observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty()) {
                return;
            }

            WorkInfo workInfo = workInfos.get(0);

            boolean finished = workInfo.getState().isFinished();
            if (!finished) {
                showWorkInProgress();
            } else {
                showWorkFinished();

                Data outputData = workInfo.getOutputData();

                String outputImageUri = outputData.getString(Constants.KEY_IMAGE_URI);

                if (!TextUtils.isEmpty(outputImageUri)) {
                    mViewModel.setOutputUri(outputImageUri);
                    mSeePic.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showWorkInProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCancel.setVisibility(View.VISIBLE);
        mBegin.setVisibility(View.GONE);
        mSeePic.setVisibility(View.GONE);
    }

    private void showWorkFinished() {
        mProgressBar.setVisibility(View.GONE);
        mCancel.setVisibility(View.GONE);
        mBegin.setVisibility(View.VISIBLE);
    }

    private int getBlurLevel() {
        int radioId = mBlurGroup.getCheckedRadioButtonId();
        if (radioId == R.id.little_blur) {
            return 1;
        } else if (radioId == R.id.more_blur) {
            return 2;
        } else if (radioId == R.id.most_blur) {
            return 3;
        }

        return 1;
    }
}
