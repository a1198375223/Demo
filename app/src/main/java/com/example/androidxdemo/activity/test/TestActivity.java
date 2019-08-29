package com.example.androidxdemo.activity.test;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;
import com.example.androidxdemo.activity.test.visibility.VisibleActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        findViewById(R.id.VisibleActivity).setOnClickListener(view -> {
            Intent visibleIntent = new Intent(this, VisibleActivity.class);
            startActivity(visibleIntent);
        });
    }
}
