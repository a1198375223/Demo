package com.example.media.activity.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.activity.list.tiktok.TikTokActivity;

import org.jetbrains.annotations.NotNull;

public class ListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_list);
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

    public void list(View view) {
        startActivity(new Intent(this, ListViewActivity.class));
    }

    public void recyclerAutoPlay(View view) {
        startActivity(new Intent(this, AutoPlayRecyclerViewActivity.class));
    }

    public void listFragmentViewPager(View view) {
        startActivity(new Intent(this, ListFragmentViewPagerActivity.class));
    }

    public void recycler(View view) {
        startActivity(new Intent(this, RecyclerViewActivity.class));
    }

    public void tikTok(View view) {
        startActivity(new Intent(this, TikTokActivity.class));
    }

    public void rotateInFullscreen(View view) {
        startActivity(new Intent(this, RotateRecyclerViewActivity.class));
    }

    public void seamlessPlay(View view) {
        startActivity(new Intent(this, SeamlessPlayActivity.class));
    }
}
