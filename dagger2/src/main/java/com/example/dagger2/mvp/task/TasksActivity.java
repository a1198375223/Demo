package com.example.dagger2.mvp.task;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.dagger2.R;
import com.google.android.material.navigation.NavigationView;

import dagger.android.support.DaggerAppCompatActivity;

public class TasksActivity extends DaggerAppCompatActivity {
    private static final String TAG = "TasksActivity";

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
//    @Inject
//    TasksPresenter mTasksPresenter;
//    @Inject
//    Lazy<TasksFragment> taskFragmentProvider;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_act);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        // 更改返回图标
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        // 显示返回图标
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            // 为NavigationView设置点击事件
            setupDrawerContent(navigationView);
        }


        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            TasksFilterType currentFiltering = (TasksFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            //mTasksPresenter.setFiltering(currentFiltering);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putSerializable(CURRENT_FILTERING_KEY, mTasksPresenter.getFiltering());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.home) {
            // 点击左上角的事件
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        // NavigationView的菜单点击事件
        navigationView.setNavigationItemSelectedListener(menuItem -> {
                    int i = menuItem.getItemId();
                    if (i == R.id.list_navigation_menu_item) {


                    } else if (i == R.id.statistics_navigation_menu_item) {
//                        Intent intent = new Intent(TasksActivity.this, StatisticsActivity.class);
//                        startActivity(intent);
                    } else {
                    }
                    // 手动关闭drawer
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                });
    }
}
