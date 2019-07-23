package com.example.media.activity.list;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.media.R;
import com.example.media.adapter.RotateRecyclerViewAdapter;
import com.example.media.common.VideoViewManager;
import com.example.media.utils.DataUtil;
import com.example.media.view.VideoView;

import org.jetbrains.annotations.NotNull;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class RotateRecyclerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_rotate_in_fullscreen);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.rv);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new RotateRecyclerViewAdapter(DataUtil.getVideoList(), this));
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NotNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NotNull View view) {
                VideoView videoView = view.findViewById(R.id.video_player);
                if (videoView != null && !videoView.isFullScreen()) {
//                    Log.d("@@@@@@", "onChildViewDetachedFromWindow: called");
//                    int tag = (int) videoView.getTag();
//                    Log.d("@@@@@@", "onChildViewDetachedFromWindow: position: " + tag);
                    videoView.release();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int firstVisibleItem, lastVisibleItem, visibleCount;

            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                switch (newState) {
                    case SCROLL_STATE_IDLE: //滚动停止
                        autoPlayVideo(recyclerView);
                        break;
                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                visibleCount = lastVisibleItem - firstVisibleItem;//记录可视区域item个数
            }

            private void autoPlayVideo(RecyclerView view) {
                //循环遍历可视区域videoview,如果完全可见就开始播放
                for (int i = 0; i < visibleCount; i++) {
                    if (view == null || view.getChildAt(i) == null) continue;
                    VideoView videoView = view.getChildAt(i).findViewById(R.id.video_player);
                    if (videoView != null) {
                        Rect rect = new Rect();
                        videoView.getLocalVisibleRect(rect);
                        int videoHeight = videoView.getHeight();
                        if (rect.top == 0 && rect.bottom == videoHeight) {
                            videoView.start();
                            return;
                        }
                    }
                }
            }
        });

        recyclerView.post(() -> {
            //自动播放第一个
            View view = recyclerView.getChildAt(0);
            VideoView videoView = view.findViewById(R.id.video_player);
            videoView.start();
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoViewManager.getInstance().release();
    }

    @Override
    public void onBackPressed() {
        if (!VideoViewManager.getInstance().onBackPressed()){
            super.onBackPressed();
        }
    }
}
