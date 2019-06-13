package com.example.room.worker;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.commonlibrary.utils.WorkerUtils;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveImageToFileWorker extends Worker {
    private static final String TAG = "SaveImageToFileWorker";

    private final String TITLE = "模糊过的图片";
    private final SimpleDateFormat DATA_FORMATTER = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault());

    public SaveImageToFileWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: save  image to file.");
        Context applicationContext = getApplicationContext();

        WorkerUtils.makeStatusNotification(applicationContext, "保存图片");

        try {
            ContentResolver resolver = applicationContext.getContentResolver();
            String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI);
            Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)));
            String imageUrl = MediaStore.Images.Media.insertImage(resolver, bitmap, TITLE, DATA_FORMATTER.format(new Date()));
            if (TextUtils.isEmpty(imageUrl)) {
                Log.e(TAG, "Writing to MediaStore fialed");
                return Result.failure();
            }
            Data outputData = new Data.Builder()
                    .putString(Constants.KEY_IMAGE_URI, imageUrl)
                    .build();
            return Result.success(outputData);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "doWork: ", e);
            return Result.failure();
        }
    }
}
