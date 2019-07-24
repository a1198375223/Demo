package com.example.media.activity.extend;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;

import org.jetbrains.annotations.NotNull;

public class ExtendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_extend);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_extend);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void startFullScreen(View view) {
        startActivity(new Intent(this, FullScreenActivity.class));
    }

    public void danmaku(View view) {
        startActivity(new Intent(this, DanmakuActivity.class));
    }

    public void ad(View view) {
        startActivity(new Intent(this, ADActivity.class));
    }

    public void rotateInFullscreen(View view) {
        startActivity(new Intent(this, RotateInFullscreenActivity.class));
    }

    public void switchPlayer(View view) {
        startActivity(new Intent(this, SwitchPlayerActivity.class));
    }

    public void cache(View view) {
        startActivity(new Intent(this, CacheActivity.class));
    }

    public void playList(View view) {
        startActivity(new Intent(this, PlayListActivity.class));
    }

    public void pad(View view) {
        startActivity(new Intent(this, PadActivity.class));
    }
}
