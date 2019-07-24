package com.example.androidxdemo.activity.anim;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;

public class ViewAnimationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_animation);

        findViewById(R.id.show_toasty).setOnClickListener(view -> {
            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.show_toasty).setAnimation(AnimationUtils.loadAnimation(this, R.anim.test_anim));
    }
}
