package com.example.androidxdemo.activity.bar;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;

import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.BarUtils;

import java.util.Random;

public class BarActivity extends AppCompatActivity {
    private static final String viewTag = "viewTag";
    private static final String contentTag = "contentTag";
    private boolean doTransparent = false;

    private View barView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);

//        setSupportActionBar(findViewById(R.id.toolbar));


        ObservableInt statusBarHeight = new ObservableInt(BarUtils.getStatusBarHeight());
        ObservableBoolean isLiteMode = new ObservableBoolean(BarUtils.isStatusBarLightMode(this));

        String builder = "Status Bar height:" + statusBarHeight.get() + '\n' +
                "Status Bar is Light Mode:" + isLiteMode.get() + '\n' +
                "Activity has ActionBar:" + BarUtils.isActionBarVisibleOrHasCustomToolBar(this) + '\n' +
                "Is support NavigationBar:" + BarUtils.isSupportNavBar() + '\n' +
                "NavigationBar height:" + BarUtils.getNavBarHeight() + '\n' +
                "NavigationBar Color:" + BarUtils.getNavBarColor(this);

        ((TextView) findViewById(R.id.bar_info)).setText(builder);

        findViewById(R.id.update_info).setOnClickListener(view -> {
            String s = "Status Bar height:" + statusBarHeight.get() + '\n' +
                    "Status Bar is Light Mode:" + isLiteMode.get() + '\n' +
                    "Activity has ActionBar:" + BarUtils.isActionBarVisibleOrHasCustomToolBar(this) + '\n' +
                    "Is support NavigationBar:" + BarUtils.isSupportNavBar() + '\n' +
                    "NavigationBar height:" + BarUtils.getNavBarHeight() + '\n' +
                    "NavigationBar Color:" + BarUtils.getNavBarColor(this);
            ((TextView) findViewById(R.id.bar_info)).setText(s);
        });

        findViewById(R.id.show_bar).setOnClickListener(view -> BarUtils.setStatusBarVisibility(this, true));

        findViewById(R.id.hide_bar).setOnClickListener(view -> BarUtils.setStatusBarVisibility(this, false));

        findViewById(R.id.change_color).setOnClickListener(view -> {
            Random random = new Random();
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            int color = Color.rgb(r, g, b);
            BarUtils.setStatusBarColor(this, color, true);
//            BarUtils.addMarginTopEqualStatusBarHeight(findViewById(R.id.bar_info));
        });

        findViewById(R.id.set_lite_mode).setOnClickListener(view -> BarUtils.setStatusBarLightMode(this, true));

        findViewById(R.id.cancel_lite_mode).setOnClickListener(view -> BarUtils.setStatusBarLightMode(this, false));


        findViewById(R.id.add_view).setOnClickListener(view -> {
            if (!BarUtils.isStatusBarVisible(this)) {
                return;
            }
            ViewGroup parent = (ViewGroup) getWindow().getDecorView();
            View title = parent.findViewWithTag(viewTag);
            if (title == null) {
                title = new View(this);
                title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, BarUtils.getStatusBarHeight()));
                title.setTag(viewTag);
                Random random = new Random();
                int r = random.nextInt(256);
                int g = random.nextInt(256);
                int b = random.nextInt(256);
                int color = Color.rgb(r, g, b);
                title.setBackgroundColor(color);
                parent.addView(title);
            } else {
                if (title.getVisibility() == View.VISIBLE) {
                    title.setVisibility(View.GONE);
                } else {
                    title.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.add_content_view).setOnClickListener(view -> {
            ViewGroup parent = findViewById(android.R.id.content);
            View title = parent.findViewWithTag(contentTag);
            if (title == null) {
                title = new View(this);
                title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, BarUtils.getStatusBarHeight()));
                title.setTag(contentTag);
                Random random = new Random();
                int r = random.nextInt(256);
                int g = random.nextInt(256);
                int b = random.nextInt(256);
                int color = Color.rgb(r, g, b);
                title.setBackgroundColor(color);
                parent.addView(title);
            } else {
                if (title.getVisibility() == View.VISIBLE) {
                    title.setVisibility(View.GONE);
                } else {
                    title.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.transparent).setOnClickListener(view -> {
            if (doTransparent) {
                return;
            }
            doTransparent = true;
//            BarUtils.fitSystemWindows(this, true);
            if (!BarUtils.isActionBarVisibleOrHasCustomToolBar(this)) {
                BarUtils.addMarginTopEqualStatusBarHeight(findViewById(android.R.id.content));
            } else {
                BarUtils.fitSystemWindows(this, true);
            }
            BarUtils.transparentStatusBar(this);
        });


        findViewById(R.id.show_or_hide_action_bar).setOnClickListener(view -> {
            if (getSupportActionBar() != null) {
                if (getSupportActionBar().isShowing()) {
                    getSupportActionBar().hide();
                } else {
                    getSupportActionBar().show();
                }
            }
        });

        findViewById(R.id.immersion_bar).setOnClickListener(view -> {
            Random random = new Random();
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            int color = Color.rgb(r, g, b);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            }
            BarUtils.setStatusBarColor(this, color, true);
        });

        findViewById(R.id.custom_bar).setOnClickListener(view -> {
            if (barView == null) {
                barView = new View(this);
                barView.setBackground(getResources().getDrawable(R.drawable.custom_bar_background));
                BarUtils.setStatusBarCustom(barView);
            } else {
                Random random = new Random();
                int r = random.nextInt(256);
                int g = random.nextInt(256);
                int b = random.nextInt(256);
                int color = Color.rgb(r, g, b);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
                }
                BarUtils.setStatusBarColor(barView, color);
            }
        });


        findViewById(R.id.set_nav_color).setOnClickListener(view -> {
            Random random = new Random();
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            int color = Color.rgb(r, g, b);
            BarUtils.setNavBarColor(this, color);
        });

        findViewById(R.id.set_nav_visibility).setOnClickListener(view -> {
            if (BarUtils.isNavBarVisible(this)) {
                BarUtils.setNavBarVisibility(this, false);
            } else {
                BarUtils.setNavBarVisibility(this, true);
            }
        });
    }
}
