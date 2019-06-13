package com.example.room.binding.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.room.R;

public class BindingActivity extends AppCompatActivity {
    private static final String TAG = "BindingActivity";

    private Button mStart1, mStart2, mStart3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);
        mStart1 = findViewById(R.id.start_bn1);
        mStart2 = findViewById(R.id.start_bn2);
        mStart3 = findViewById(R.id.start_bn3);

        mStart1.setOnClickListener(view -> {
            Intent intent = new Intent(BindingActivity.this, BindViewModelActivity.class);
            startActivity(intent);
        });


        mStart2.setOnClickListener(view -> {
            Intent intent = new Intent(BindingActivity.this, BindObservableActivity.class);
            startActivity(intent);
        });

        mStart3.setOnClickListener(view -> {
            Intent intent = new Intent(BindingActivity.this, TwoWayActivity.class);
            startActivity(intent);
        });
    }
}
