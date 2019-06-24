package com.example.androidxdemo.activity.bubble;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;

public class FlowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);

        findViewById(R.id.bubbles).setOnClickListener(view -> {
            Intent target = new Intent(this, BubblesActivity.class);
            PendingIntent bubbleIntent = PendingIntent.getActivity(this, 0, target, 0);
        });
    }
}
