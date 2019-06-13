package com.example.commonlibrary.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.InputMerger;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.commonlibrary.R;
import com.example.commonlibrary.common.NotificationConstants;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 工具链
 * WorkManager.getInstance()
 *     // Candidates to run in parallel
 *     .beginWith(Arrays.asList(filter1, filter2, filter3))
 *     // Dependent work (only runs after all previous work in chain)
 *     .then(compress)
 *     .then(upload)
 *     // Don't forget to enqueue()
 *     .enqueue();
 */
public class WorkerUtils {

    // 当设置约束的时候, 只有满足所有的约束当前worker才能被执行
    // 当正在执行的worker的约束条件不满足的时候,当前worker会stop, 直到重新满足条件,worker会retry
    public static OneTimeWorkRequest createOneTimeWorker(Class<? extends ListenableWorker> workerClazz,
                                                         Constraints constraints,
                                                         Data inputData,
                                                         Class<? extends InputMerger> inputClazz,
                                                         long delay,
                                                         long retryDelay,
                                                         String tag) {
        return new OneTimeWorkRequest.Builder(workerClazz)
                .setConstraints(constraints) // 设置worker的约束
                .setInitialDelay(delay, TimeUnit.MILLISECONDS) // 设置延迟的起始时间
                .setBackoffCriteria( // 设置retry的标准
                        BackoffPolicy.LINEAR,
                        retryDelay,
                        TimeUnit.MILLISECONDS
                )
                .setInputData(inputData) // 设置input的data. worker可以使用Worker.getInputData()来获取data
                .addTag(tag) // 添加一个tag 方便管理
                .setInputMerger(inputClazz) // ArrayCreatingInputMerger.class or OverwritingInputMerger.class
                .build();
    }

    // PeriodicWorker不能被chained调用
    public static PeriodicWorkRequest createPeriodicWorker(Class<? extends ListenableWorker> workerClazz,
                                                           Constraints constraints,
                                                           Data inputData,
                                                           long repeatInterval, // 重复间隔时间, 最小为15分钟
                                                           long flexInterval, // 任务结束后的休息时间
                                                           String tag) throws IllegalAccessException {
        if (repeatInterval < 15) {
            throw new IllegalAccessException("the repeat interval should > 15");
        }
        return new PeriodicWorkRequest.Builder(workerClazz, repeatInterval, TimeUnit.MINUTES, flexInterval, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInputData(inputData)
                .addTag(tag)
                .build();
    }

    // 通过uid来获取WorkInfo
    public static LiveData<WorkInfo> getWorkInfoByIdLiveData(Context context, UUID uuid) {
        return WorkManager.getInstance(context).getWorkInfoByIdLiveData(uuid);
    }

    public static ListenableFuture<WorkInfo> getWorkInfoById(Context context, UUID uuid) {
        return WorkManager.getInstance(context).getWorkInfoById(uuid);
    }

    // 通过tag来获取workInfo
    public static LiveData<List<WorkInfo>> getWorkInfoByTagLiveData(Context context, String tag) {
        return WorkManager.getInstance(context).getWorkInfosByTagLiveData(tag);
    }

    public static ListenableFuture<List<WorkInfo>> getWorkInfoByTag(Context context, String tag) {
        return WorkManager.getInstance(context).getWorkInfosByTag(tag);
    }

    // 通过名称来获取workInfo
    public static LiveData<List<WorkInfo>> getWorkInfoByNameLiveData(Context context, String name) {
        return WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(name);
    }

    public static ListenableFuture<List<WorkInfo>> getWorkInfoByName(Context context, String name) {
        return WorkManager.getInstance(context).getWorkInfosForUniqueWork(name);
    }

    public static void cancelWorkById(Context context, UUID uuid) {
        WorkManager.getInstance(context).cancelWorkById(uuid);
    }

    public static void cancelAllWork(Context context) {
        WorkManager.getInstance(context).cancelAllWork();
    }

    public static void cancelAllWorkByTag(Context context, String tag) {
        WorkManager.getInstance(context).cancelAllWorkByTag(tag);
    }

    public static void cancelAllWorkByName(Context context, String name) {
        WorkManager.getInstance(context).cancelUniqueWork(name);
    }


    public static void makeStatusNotification(Context context, String message) {
        // api > 26
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = NotificationConstants.WORKER_NOTIFICATION_CHANNEL_NAME;
            String description = NotificationConstants.WORKER_NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = 0;
            importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NotificationConstants.WORKER_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationConstants.WORKER_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(NotificationConstants.WORKER_NOTIFICATION_TITLE)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0]);

        NotificationManagerCompat.from(context).notify(NotificationConstants.WORKER_NOTIFICATION_ID, builder.build());
    }
}
