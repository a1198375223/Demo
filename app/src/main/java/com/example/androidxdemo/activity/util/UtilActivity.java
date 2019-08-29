package com.example.androidxdemo.activity.util;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;

/**
 * 工具Activity
 */
public class UtilActivity extends AppCompatActivity {
    private final String packageName = "com.xwcc.ccc";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util);

        findViewById(R.id.logoActivity).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName(packageName, "com.iapp.app.logoActivity");
            startActivityForResult(intent, 0);
        });


        findViewById(R.id.MianActivity).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName(packageName, "com.iapp.app.run.mian");
            startActivityForResult(intent, 1);
        });

        findViewById(R.id.main1Activity).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName(packageName, "com.iapp.app.run.main");
            startActivityForResult(intent, 2);
        });

        findViewById(R.id.main2Activity).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName(packageName, "com.iapp.app.run.main2");
            startActivityForResult(intent, 3);
        });

        findViewById(R.id.main3Activity).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName(packageName, "com.iapp.app.run.main3");
            startActivityForResult(intent, 4);
        });

        findViewById(R.id.WebActivity).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName(packageName, "com.iapp.app.Webview");
            startActivityForResult(intent, 5);
        });

        findViewById(R.id.VideoActivity).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName(packageName, "com.iapp.app.Videoview");
            startActivityForResult(intent, 6);
        });

        findViewById(R.id.CaptureActivity).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName(packageName, "com.iapp.app.DownList");
            startActivityForResult(intent, 7);
        });

        findViewById(R.id.DownListActivity).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName(packageName, "cn.hugo.android.scanner.CaptureActivity");
            startActivityForResult(intent, 8);
        });
    }
}
