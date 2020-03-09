package com.example.androidxdemo.activity.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;

public class DialogActivityTest extends AppCompatActivity {
    private static final String TAG = "DialogActivityTest";
    private AlertDialog dialog1, dialog2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_test);
        dialog1 = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.leak_canary_icon)
                .setTitle("title1")
                .setMessage("message1")
                .create();
        dialog2 = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.leak_canary_icon)
                .setTitle("title2")
                .setMessage("message2")
                .create();

        findViewById(R.id.start_dialog).setOnClickListener(view -> {
            if (dialog2.isShowing())
                dialog2.dismiss();
            if (dialog1.isShowing())
                return;
            dialog1.show();
        });

        findViewById(R.id.start_dialog_1).setOnClickListener(view -> {
            if (dialog1.isShowing())
                dialog1.dismiss();
            if (dialog2.isShowing())
                return;
            if (dialog2.getWindow() != null) {
                Log.d(TAG, "setType");
                dialog2.getWindow().setType(5);
            }
            dialog2.show();
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        Log.d(TAG, "onSaveInstanceState: ");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: ");
        super.onNewIntent(intent);
    }
}
