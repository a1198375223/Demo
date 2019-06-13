package com.example.room.worker;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.commonlibrary.utils.AppUtils;
import com.example.room.mvp.BaseViewModel;

import java.util.List;

public class BlurViewModel extends BaseViewModel {
    private WorkManager mWorkManager;
    private Uri mImageUri;
    private Uri mOutputUri;
    private LiveData<List<WorkInfo>> mSavedWorkInfo;

    public BlurViewModel() {
        mWorkManager = WorkManager.getInstance(AppUtils.app().getApplicationContext());
        mSavedWorkInfo = mWorkManager.getWorkInfosByTagLiveData(Constants.TAG_OUTPUT);
    }

    // 取消worker
    public void cancelWork() {
        mWorkManager.cancelUniqueWork(Constants.IMAGE_MANIPULATION_WORK_NAME);
    }

    // 开始模糊
    public void applyBlur(int blurLevel) {
        WorkContinuation continuation = mWorkManager.beginUniqueWork(
                Constants.IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker.class));

        for (int i = 0; i < blurLevel; i++) {
            OneTimeWorkRequest.Builder blurBuilder = new OneTimeWorkRequest.Builder(BlurWorker.class);

            if (i == 0) {
                blurBuilder.setInputData(createInputDataForUri());
            }

            continuation = continuation.then(blurBuilder.build());
        }

        Constraints constraints = new Constraints.Builder().setRequiresCharging(true).build();

        OneTimeWorkRequest save = new OneTimeWorkRequest.Builder(SaveImageToFileWorker.class)
                .setConstraints(constraints)
                .addTag(Constants.TAG_OUTPUT)
                .build();
        continuation = continuation.then(save);
        continuation.enqueue();
    }

    private Data createInputDataForUri() {
        Data.Builder builder = new Data.Builder();
        if (mImageUri != null) {
            builder.putString(Constants.KEY_IMAGE_URI, mImageUri.toString());
        }
        return builder.build();
    }


    private Uri uriOrNull(String uriString) {
        if (!TextUtils.isEmpty(uriString)) {
            return Uri.parse(uriString);
        }
        return null;
    }

    public void setImageUri(String uri) {
        this.mImageUri = uriOrNull(uri);
    }

    public void setOutputUri(String outputImageUri) {
        this.mOutputUri = uriOrNull(outputImageUri);
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public Uri getOutputUri() {
        return mOutputUri;
    }

    public LiveData<List<WorkInfo>> getSavedWorkInfo() {
        return mSavedWorkInfo;
    }
}
