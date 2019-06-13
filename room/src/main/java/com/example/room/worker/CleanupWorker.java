package com.example.room.worker;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.commonlibrary.utils.WorkerUtils;

import java.io.File;

public class CleanupWorker extends Worker {
    private static final String TAG = "CleanupWorker";

    public CleanupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: clean up worker.");
        Context applicationContext = getApplicationContext();

        WorkerUtils.makeStatusNotification(applicationContext, "清除老的暂存图片");

        File outputDirectory = new File(applicationContext.getFilesDir(), Constants.OUTPUT_PATH);
        if (outputDirectory.exists()) {
            File[] entries = outputDirectory.listFiles();
            if (entries != null && entries.length > 0) {
                for (File entry : entries) {
                    String name = entry.getName();
                    if (!TextUtils.isEmpty(name) && name.endsWith(".png")) {
                        boolean deleted = entry.delete();
                        Log.d(TAG, String.format("Deleted %s - %s", name, deleted));
                    }
                }
            }
        }
        return Result.success();
    }
}
