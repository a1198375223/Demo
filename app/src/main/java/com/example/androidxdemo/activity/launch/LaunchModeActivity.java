package com.example.androidxdemo.activity.launch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;

public class LaunchModeActivity extends AppCompatActivity {
    private static final String TAG = "LaunchMode";
    private static final int STANDARD_CODE = 1;
    private static final int SINGLE_TOP_CODE = 2;
    private static final int SINGLE_TASK_CODE = 3;
    private static final int SINGLE_INSTANCE_CODE = 4;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        findViewById(R.id.button).setOnClickListener(view -> {
            Intent intent = new Intent(this, SingleTopActivity.class);
            startActivityForResult(intent, SINGLE_TOP_CODE);
        });

        findViewById(R.id.button_1).setOnClickListener(view -> {
            Intent intent = new Intent(this, SingleTaskActivity.class);
            startActivityForResult(intent, SINGLE_TASK_CODE);
        });

        findViewById(R.id.button_2).setOnClickListener(view -> {
            Intent intent = new Intent(this, SingleInstanceActivity.class);
            startActivityForResult(intent, SINGLE_INSTANCE_CODE);
        });

        findViewById(R.id.button_3).setOnClickListener(view -> {
            Intent intent = new Intent(this, StandardActivity.class);
            startActivityForResult(intent, STANDARD_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + " resultCode=" + resultCode);
    }
}
