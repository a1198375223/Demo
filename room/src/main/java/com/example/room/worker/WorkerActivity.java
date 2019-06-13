package com.example.room.worker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ArrayCreatingInputMerger;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.commonlibrary.utils.WorkerUtils;
import com.example.room.R;

import java.util.concurrent.TimeUnit;

/**
 * Constraints constraints = new Constraints.Builder()
 *                     // 设置网络约束
 *                     // NOT_REQUIRED 不需要网络
 *                     // CONNECTED    需要网络,任意
 *                     // UNMETERED    需要未计量的网络连接
 *                     // NOT_ROAMING  需要非漫游网络连接
 *                     // METERED      需要计量的网络连接
 *                     .setRequiredNetworkType(NetworkType.CONNECTED)
 *                     // 设置电池约束
 *                     // 电池是否处于可以接受的级别
 *                     .setRequiresBatteryNotLow(true)
 *                     // 是否是充电状态
 *                     .setRequiresCharging(true)
 *                     // 设备是否处于空闲状态, 需要api > 23
 *                     .setRequiresDeviceIdle(false)
 *                     // 设置是否存储空间处于可接受的级别
 *                     .setRequiresStorageNotLow(false)
 *                     // 设置从第一次content：Uri检测到改变到WorkRequest执行的最大延迟
 *                     .setTriggerContentMaxDelay(3, TimeUnit.SECONDS)
 *                     // 设置从第一次content：Uri检测到改变到WorkRequest执行的延迟
 *                     .setTriggerContentUpdateDelay(1, TimeUnit.SECONDS)
 *                     .build();
 */
public class WorkerActivity extends AppCompatActivity {
    private TextView mTv;
    private Button mBn, mStartSelect;
    private RadioGroup mNetGroup, mBatteryGroup, mChargingGroup, mStorageGroup, mIdleGroup;
    private Constraints.Builder builder;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
        mTv = findViewById(R.id.final_tv);
        mBn = findViewById(R.id.start_bn);
        mStartSelect = findViewById(R.id.start_select);
        mNetGroup = findViewById(R.id.net_group);
        mBatteryGroup = findViewById(R.id.battery_group);
        mChargingGroup = findViewById(R.id.charge_group);
        mStorageGroup = findViewById(R.id.storage_group);
        mIdleGroup = findViewById(R.id.idle_group);

        mStartSelect.setOnClickListener(view -> {
            Intent intent = new Intent(this, SelectActivity.class);
            startActivity(intent);
        });

        builder = new Constraints.Builder();

        mNetGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.net_not_required) {
                builder.setRequiredNetworkType(NetworkType.NOT_REQUIRED);
            } else if (checkedId == R.id.connected) {
                builder.setRequiredNetworkType(NetworkType.CONNECTED);
            } else if (checkedId == R.id.unmetered) {
                builder.setRequiredNetworkType(NetworkType.UNMETERED);
            } else if (checkedId == R.id.not_roaming) {
                builder.setRequiredNetworkType(NetworkType.NOT_ROAMING);
            } else if (checkedId == R.id.metered) {
                builder.setRequiredNetworkType(NetworkType.NOT_ROAMING);
            }
        });

        mBatteryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.battery_true) {
                builder.setRequiresBatteryNotLow(true);
            } else {
                builder.setRequiresBatteryNotLow(false);
            }
        });

        mChargingGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.charging_true) {
                builder.setRequiresCharging(true);
            } else {
                builder.setRequiresCharging(false);
            }
        });

        mStorageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.storage_true) {
                builder.setRequiresStorageNotLow(true);
            } else {
                builder.setRequiresStorageNotLow(false);
            }
        });

        mIdleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.idle_true) {
                builder.setRequiresDeviceIdle(true);
            } else {
                builder.setRequiresDeviceIdle(false);
            }
        });

        mBn.setOnClickListener(view -> {
            OneTimeWorkRequest request = WorkerUtils.createOneTimeWorker(UploadWorker.class,
                    builder.build(),
                    new Data.Builder().build(),
                    ArrayCreatingInputMerger.class,
                    1000,
                    3000,
                    "tag");

            WorkManager.getInstance(this).enqueue(request);
        });
    }
}
