package com.example.room.worker;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.commonlibrary.utils.BitmapUtils;
import com.example.commonlibrary.utils.FileUtils;
import com.example.commonlibrary.utils.WorkerUtils;

import java.io.FileNotFoundException;

public class BlurWorker extends Worker {
    private static final String TAG = "BlurWorker";



    public BlurWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: blur worker");
        Context applicationContext = getApplicationContext();
        WorkerUtils.makeStatusNotification(applicationContext, "模糊图片中");

        String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI);
        if (TextUtils.isEmpty(resourceUri)) {
            Log.e(TAG, "Invalid input uri");
            throw new IllegalArgumentException("Invalid input uri");
        }


        try {
            ContentResolver resolver = applicationContext.getContentResolver();
            Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)));
            Bitmap output = BitmapUtils.blurBitmap(bitmap, applicationContext);
            Uri outputUri = FileUtils.writeBitmapToFile(applicationContext, output, Constants.OUTPUT_PATH);
            Data outputData = new Data.Builder().putString(Constants.KEY_IMAGE_URI, outputUri.toString()).build();
            return Result.success(outputData);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "doWork: ", e);
            return Result.failure();
        }
    }
}
