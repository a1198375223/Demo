package com.example.androidxdemo.activity.anim;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;

public class AnimationActivity extends AppCompatActivity {
    private static final String TAG = "AnimationActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim);

        findViewById(R.id.view_anim).setOnClickListener(view -> {
            Intent viewAnimationIntent = new Intent(this, ViewAnimationActivity.class);
            startActivity(viewAnimationIntent);
        });



    }
}
