package com.example.media.activity.list;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.adapter.VideoListViewAdapter;
import com.example.media.common.VideoViewManager;
import com.example.media.utils.DataUtil;
import com.example.media.view.VideoView;

import org.jetbrains.annotations.NotNull;

public class ListViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_list_view);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ListView listView = findViewById(R.id.lv);
        listView.setAdapter(new VideoListViewAdapter(DataUtil.getVideoList()));

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private View firstView; //记录当前屏幕中第一个可见的item对象
            private View lastView; //记录当前屏幕中最后个可见的item对象
            private int lastFirstVisibleItem; //记录当前屏幕中第一个可见的item的position
            private int lastVisibleItem; // 记录屏幕中最后一个可见的item的position
            private boolean scrollFlag;// 记录滑动状态

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        scrollFlag = false;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        scrollFlag = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        scrollFlag = true;
                        break;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollFlag) { // 避免不必要的执行
                    //如果记录的 屏幕中第一个可见的item的position 已经小于当前屏幕中第一个可见item的position，表示item已经完全滑出屏幕了
                    //这种情况一般出现在ListView上滑的时候，故此时我们可以把firstView上的播放器停止
                    if (lastFirstVisibleItem < firstVisibleItem) {
                        gcView(firstView);
                        //通过firstVisibleItem + visibleItemCount - 1我们可以得到当前屏幕上最后一个item的position
                        //如果屏幕中最后一个可见的item的position已经大于当前屏幕上最后一个item的position，表示item已经完全滑出屏幕了
                        //这种情况一般出现在ListView下滑的时候，故此时我们可以把lastView上的播放器停止
                    } else if (lastVisibleItem > firstVisibleItem + visibleItemCount - 1) {
                        gcView(lastView);
                    }
                    lastFirstVisibleItem = firstVisibleItem;
                    lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
                    firstView = view.getChildAt(0);
                    lastView = view.getChildAt(visibleItemCount - 1);
                }
            }

            private void gcView(View gcView) {
                if (gcView != null) {
                    VideoView videoView = gcView.findViewById(R.id.video_player);
                    if (videoView != null && !videoView.isFullScreen()) {
                        videoView.release();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoViewManager.getInstance().release();
    }

    @Override
    public void onBackPressed() {
        if (!VideoViewManager.getInstance().onBackPressed()) {
            super.onBackPressed();
        }
    }
}
