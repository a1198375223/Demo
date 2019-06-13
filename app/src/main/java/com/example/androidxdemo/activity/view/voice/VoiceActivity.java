package com.example.androidxdemo.activity.view.voice;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;
import com.example.androidxdemo.view.VoiceAnimView;

public class VoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        VoiceAnimView voiceAnimView = findViewById(R.id.voice_animator_view);

        findViewById(R.id.start_animator).setOnClickListener(v -> voiceAnimView.startAnimation());
    }
}
