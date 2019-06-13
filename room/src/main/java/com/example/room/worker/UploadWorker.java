package com.example.room.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadWorker extends Worker {
    private static final String TAG = "UploadWorker";

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int count = 0;
        int sum = 0;
        while (count < 50) {
            int random = (int) (Math.round(Math.random() * 100) % 3) + 1;
            Log.d(TAG, "count=" + count + " doWork: random=" + random);
            sum += random;
            count++;
            try {
                // 休息3秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "final total=" + sum);
        if (sum > 120) {
            return Result.success();
        } else if (sum > 80) {
            return Result.retry();
        } else {
            return Result.failure();
        }
    }
}
