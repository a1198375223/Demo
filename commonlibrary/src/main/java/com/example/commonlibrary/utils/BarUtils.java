package com.example.commonlibrary.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;

import static android.Manifest.permission.EXPAND_STATUS_BAR;


public class BarUtils {
    private static final String TAG = "BarUtils";
    ///////////////////////////////////////////////////////////////////////////
    // status bar
    ///////////////////////////////////////////////////////////////////////////

    private static final String TAG_STATUS_BAR = "TAG_STATUS_BAR";
    private static final String TAG_OFFSET     = "TAG_OFFSET";
    private static final int    KEY_OFFSET     = -123;

    private BarUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    /**
     * 获取状态栏的高度
     * @return 返回状态栏的高度
     */
    public static int getStatusBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelOffset(resourceId);
    }


    /**
     * 设置状态栏是否可见
     * @param activity activity
     * @param isVisible 是否可见
     */
    public static void setStatusBarVisibility(Activity activity, boolean isVisible) {
        setStatusBarVisibility(activity.getWindow(), isVisible);
    }

    /**
     * 设置状态栏是否可见
     * @param window window
     * @param isVisible 是否可见
     */
    public static void setStatusBarVisibility(Window window, boolean isVisible) {
        if (isVisible) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            showStatusBarView(window);
            addMarginTopEqualStatusBarHeight(window);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hideStatusBarView(window);
            subtractMarginTopEqualStatusBarHeight(window);
        }
    }

    /**
     * 判断当前的Activity状态栏是否可见
     * @param activity 判断的Activity
     * @return 返回状态栏是否可见
     */
    public static boolean isStatusBarVisible(Activity activity) {
        int flags = activity.getWindow().getAttributes().flags;
        return (flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0;
    }


    /**
     * 设置状态栏的颜色
     * @param activity activity
     * @param color 颜色
     * @return 返回创建的一个虚拟状态栏
     */
    public static View setStatusBarColor(@NonNull Activity activity, @ColorInt int color) {
        return setStatusBarColor(activity, color, false);
    }


    /**
     * 设置状态栏的颜色
     * @param activity activity
     * @param color 颜色
     * @param isDecor 是否是DecorView
     * @return 返回创建的一个虚拟状态栏
     */
    public static View setStatusBarColor(@NonNull Activity activity,
                                         @ColorInt int color,
                                         boolean isDecor) {
        if (!isStatusBarVisible(activity)) {
            return null;
        }
//        fitSystemWindows(activity, true);
        if (isActionBarVisibleOrHasCustomToolBar(activity)) {
            fitSystemWindows(activity, true);
        } else {
            addMarginTopEqualStatusBarHeight(activity.getWindow());
        }
        transparentStatusBar(activity);
        setStatusBarLightMode(activity, isLightColor(color));
        return applyStatusBarColor(activity, color, isDecor);
    }

    /**
     * Set the status bar's color.
     *
     * @param fakeStatusBar The fake status bar view.
     * @param color         The status bar's color.
     */
    public static void setStatusBarColor(@NonNull final View fakeStatusBar,
                                         @ColorInt final int color) {
        Activity activity = getActivityByView(fakeStatusBar);
        if (activity == null || !isStatusBarVisible(activity)) return;
        transparentStatusBar(activity);
        fakeStatusBar.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = fakeStatusBar.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = getStatusBarHeight();
        fakeStatusBar.setBackgroundColor(color);
    }



    /**
     * 设置StatusBar是否为light mode.
     *
     * @param activity    The activity.
     * @param isLightMode True to set status bar light mode, false otherwise.
     */
    public static void setStatusBarLightMode(@NonNull final Activity activity,
                                             final boolean isLightMode) {
        setStatusBarLightMode(activity.getWindow(), isLightMode);
    }

    /**
     * 设置StatusBar是否为light mode.
     *
     * @param window      The window.
     * @param isLightMode True to set status bar light mode, false otherwise.
     */
    public static void setStatusBarLightMode(@NonNull final Window window,
                                             final boolean isLightMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = window.getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (isLightMode) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }


    /**
     * StatusBar是否是light mode.
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isStatusBarLightMode(@NonNull final Activity activity) {
        return isStatusBarLightMode(activity.getWindow());
    }

    /**
     * StatusBar是否是light mode.
     *
     * @param window The window.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isStatusBarLightMode(@NonNull final Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = window.getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                return (vis & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0;
            }
        }
        return false;
    }


    /**
     * Set the custom status bar.
     *
     * @param fakeStatusBar The fake status bar view.
     */
    public static void setStatusBarCustom(@NonNull final View fakeStatusBar) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        Activity activity = getActivityByView(fakeStatusBar);
        if (activity == null) return;
        if (isActionBarVisibleOrHasCustomToolBar(activity)) {
            fitSystemWindows(activity, true);
        } else {
            addMarginTopEqualStatusBarHeight(activity.getWindow());
        }
        transparentStatusBar(activity);
        fakeStatusBar.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = fakeStatusBar.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight()
            );
            fakeStatusBar.setLayoutParams(layoutParams);
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = getStatusBarHeight();
        }
        fakeStatusBar.setLayoutParams(layoutParams);
        ((ViewGroup) activity.getWindow().getDecorView()).addView(fakeStatusBar);
    }

    private static Activity getActivityByView(@NonNull final View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        Log.e("BarUtils", "the view's Context is not an Activity.");
        return null;
    }


    /**
     * 显示状态栏
     * @param window 需要显示状态栏的window
     */
    private static void showStatusBarView(Window window) {
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View fakeStatusBarView = decorView.findViewWithTag(TAG_STATUS_BAR);
        if (fakeStatusBarView == null) {
            return;
        }
        fakeStatusBarView.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏状态栏
     * @param window 需要隐藏状态栏的window
     */
    private static void hideStatusBarView(Window window) {
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View fakeStatusBarView = decorView.findViewWithTag(TAG_STATUS_BAR);
        if (fakeStatusBarView == null) {
            return;
        }
        fakeStatusBarView.setVisibility(View.GONE);
    }


    /**
     * 创建一个statusBar用来取代原先的statusBar
     * @param activity 需要替换StatusBar的Activity
     * @param color StatusBar的颜色
     * @return 返回一个状态栏view
     */
    private static View createStatusBarView(Activity activity, int color) {
        View statusBarView = new View(activity);
        statusBarView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight()));
        statusBarView.setBackgroundColor(color);
        statusBarView.setTag(TAG_STATUS_BAR);
        return statusBarView;
    }

    /**
     * 让状态栏变成透明的状态
     * @param activity 需要透明状态栏的activity
     */
    public static void transparentStatusBar(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 需要设置这个flag, window.setStatusBarColor(Color.TRANSPARENT)才会生效
            // 并且还不能有 FLAG_TRANSLUCENT_STATUS 这个flag
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // 这两个属性表示让主体内容沿用状态栏
            // SYSTEM_UI_FLAG_LAYOUT_STABLE: 保持整个View稳定, 常跟bar 悬浮, 隐藏共用, 使View不会因为SystemUI的变化而做layout
            // SYSTEM_UI_FLAG_FULLSCREEN: 状态栏隐藏(导航栏仍然显示). 同时在使用ActionBar的FEATURE_ACTION_BAR_OVERLAY时，启用SYSTEM_UI_FLAG_FULLSCREEN 会将ActionBar隐藏；该标志一般适用于短期的全屏状态而不是长期。
            // SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN: 状态栏上浮于Activity
            // SYSTEM_UI_FLAG_HIDE_NAVIGATION: 暂时隐藏导航栏, 由于导航栏的重要性，当产生细微的用户交互后，比如单击屏幕，都可能会导致navigation bar重新出现,源于系统clear掉该标志与SYSTEM_UI_FLAG_FULLSCREEN 标志，同SYSTEM_UI_FLAG_IMMERSIVE 标志一起使用可避免被clear
            // SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION: 导航栏上浮于Activity
            // SYSTEM_UI_FLAG_IMMERSIVE:  沉浸模式, 跟SYSTEM_UI_FLAG_HIDE_NAVIGATION一起使用才有意义，可以避免系统在产生细微用户交互时系统clear掉SYSTEM_UI_FLAG_HIDE_NAVIGATION标志。
            // SYSTEM_UI_FLAG_IMMERSIVE_STIKY: 如果同时指定SYSTEM_UI_FLAG_IMMERSIVE_STIKY 标志，那么对应标志将不会被清除，且呼出隐藏的bar后会自动再隐藏掉
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int vis = window.getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(option | vis);
            } else {
                window.getDecorView().setSystemUiVisibility(option);
            }
            // api >= 21 可以调用
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 让android.R.id.content设置fitSystemWindows
     * @param activity activity
     * @param fitSystemWindows 是否要fitSystemWindows
     */
    public static void fitSystemWindows(Activity activity, boolean fitSystemWindows) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        if (isActionBarVisibleOrHasCustomToolBar(activity)) {
            if (parent.getChildCount() > 0) {
                ViewGroup view = (ViewGroup) parent.getChildAt(0);
                if (view != null) {
                    view.setFitsSystemWindows(fitSystemWindows);
                }
            }
        } else {
            if (parent != null) {
                parent.setFitsSystemWindows(fitSystemWindows);
            }
        }
    }


    /**
     * 为状态栏设置颜色
     * @param activity 需要设置的Activity
     * @param color 设置的颜色
     * @param isDecor 是否是DecorView
     * @return 返回状态栏view
     */
    private static View applyStatusBarColor(Activity activity, int color, boolean isDecor) {
        ViewGroup parent = isDecor ?
                (ViewGroup) activity.getWindow().getDecorView() :
                activity.findViewById(android.R.id.content);

        View fakeStatusBarView = parent.findViewWithTag(TAG_STATUS_BAR);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(color);
        } else {
            fakeStatusBarView = createStatusBarView(activity, color);
            parent.addView(fakeStatusBarView);
        }
        return fakeStatusBarView;
    }


    /**
     * 为Window中的decorView添加一个高度为StatusBar高度的TopMargin
     * @param window 需要添加TopMargin的window
     */
    public static void addMarginTopEqualStatusBarHeight(Window window) {
        View withTag = window.getDecorView().findViewWithTag(TAG_OFFSET);
        if (withTag == null) {
            return;
        }
        addMarginTopEqualStatusBarHeight(withTag);
    }

    /**
     * 为view添加一个高度为StatusBar高度的TopMargin
     * @param view 需要添加TopMargin的view
     */
    public static void addMarginTopEqualStatusBarHeight(View view) {
        Log.d(TAG, "addMargin start");
        view.setTag(TAG_OFFSET);
        Object haveSetOffset = view.getTag(KEY_OFFSET);

        if (haveSetOffset != null && (boolean) haveSetOffset) {
            return;
        }

        Log.d(TAG, "addMargin go");

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        layoutParams.setMargins(layoutParams.leftMargin,
                layoutParams.topMargin + getStatusBarHeight(),
                layoutParams.rightMargin,
                layoutParams.bottomMargin);

        view.setLayoutParams(layoutParams);
        view.setTag(KEY_OFFSET, true);
    }

    /**
     * 去除TopMargin
     * @param window 去要取出DecorView的TopMargin的window
     */
    private static void subtractMarginTopEqualStatusBarHeight(Window window) {
        View withTag = window.getDecorView().findViewWithTag(TAG_OFFSET);
        if (withTag == null) {
            return;
        }
        subtractMarginTopEqualStatusBarHeight(withTag);
    }

    /**
     * 去除TopMargin
     * @param view 需要出去TopMargin的view
     */
    private static void subtractMarginTopEqualStatusBarHeight(View view) {
        Log.d(TAG, "subtractMargin start");
        Object haveSetOffset = view.getTag(KEY_OFFSET);

        if (haveSetOffset == null || !(boolean) haveSetOffset) {
            return;
        }

        Log.d(TAG, "subtractMargin go");

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        layoutParams.setMargins(layoutParams.leftMargin,
                layoutParams.topMargin - getStatusBarHeight(),
                layoutParams.rightMargin,
                layoutParams.bottomMargin);
        view.setTag(KEY_OFFSET, false);
    }

    /**
     * 当前activity是否拥有ActionBar或者是Toolbar
     * @param activity activity
     * @return 当前activity是否拥有ActionBar或者是Toolbar
     */
    public static boolean isActionBarVisibleOrHasCustomToolBar(Activity activity) {
        boolean result;
        // 判断是否有ActionBar或者是Toolbar
        if (activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            result = actionBar != null && actionBar.isShowing();
        } else {
            android.app.ActionBar actionBar = activity.getActionBar();
            result = actionBar != null && actionBar.isShowing();
        }
        return result;
    }


    /**
     * 判断颜色是否是亮色的
     * @param color 需要判断的颜色
     * @return 判断颜色是否是亮色的
     */
    private static boolean isLightColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }


    ///////////////////////////////////////////////////////////////////////////
    // navigation bar
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 返回NavigationBar的高度的
     * @return the navigation bar's height
     */
    public static int getNavBarHeight() {
        Resources res = Resources.getSystem();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId != 0) {
            return res.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    /**
     * Set the navigation bar's visibility.
     *
     * @param activity  The activity.
     * @param isVisible True to set navigation bar visible, false otherwise.
     */
    public static void setNavBarVisibility(@NonNull final Activity activity, boolean isVisible) {
        setNavBarVisibility(activity.getWindow(), isVisible);
    }

    /**
     * Set the navigation bar's visibility.
     *
     * @param window    The window.
     * @param isVisible True to set navigation bar visible, false otherwise.
     */
    public static void setNavBarVisibility(@NonNull final Window window, boolean isVisible) {
        final ViewGroup decorView = (ViewGroup) window.getDecorView();
        for (int i = 0, count = decorView.getChildCount(); i < count; i++) {
            final View child = decorView.getChildAt(i);
            final int id = child.getId();
            if (id != View.NO_ID) {
                String resourceEntryName = AppUtils.app()
                        .getResources()
                        .getResourceEntryName(id);
                if ("navigationBarBackground".equals(resourceEntryName)) {
                    child.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
                }
            }
        }
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (isVisible) {
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~uiOptions);
        } else {
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | uiOptions);
        }
    }

    /**
     * Return whether the navigation bar visible.
     * <p>Call it in onWindowFocusChanged will get right result.</p>
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isNavBarVisible(@NonNull final Activity activity) {
        return isNavBarVisible(activity.getWindow());
    }

    /**
     * Return whether the navigation bar visible.
     * <p>Call it in onWindowFocusChanged will get right result.</p>
     *
     * @param window The window.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isNavBarVisible(@NonNull final Window window) {
        boolean isVisible = false;
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        for (int i = 0, count = decorView.getChildCount(); i < count; i++) {
            final View child = decorView.getChildAt(i);
            final int id = child.getId();
            if (id != View.NO_ID) {
                String resourceEntryName = AppUtils.app()
                        .getResources()
                        .getResourceEntryName(id);
                if ("navigationBarBackground".equals(resourceEntryName)
                        && child.getVisibility() == View.VISIBLE) {
                    isVisible = true;
                    break;
                }
            }
        }
        if (isVisible) {
            int visibility = decorView.getSystemUiVisibility();
            isVisible = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
        }
        return isVisible;
    }

    /**
     * Set the navigation bar's color.
     *
     * @param activity The activity.
     * @param color    The navigation bar's color.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setNavBarColor(@NonNull final Activity activity, @ColorInt final int color) {
        setNavBarColor(activity.getWindow(), color);
    }

    /**
     * Set the navigation bar's color.
     *
     * @param window The window.
     * @param color  The navigation bar's color.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setNavBarColor(@NonNull final Window window, @ColorInt final int color) {
        window.setNavigationBarColor(color);
    }

    /**
     * Return the color of navigation bar.
     *
     * @param activity The activity.
     * @return the color of navigation bar
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static int getNavBarColor(@NonNull final Activity activity) {
        return getNavBarColor(activity.getWindow());
    }

    /**
     * Return the color of navigation bar.
     *
     * @param window The window.
     * @return the color of navigation bar
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static int getNavBarColor(@NonNull final Window window) {
        return window.getNavigationBarColor();
    }

    /**
     * 是否有虚拟按键
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSupportNavBar() {
        WindowManager wm = (WindowManager) AppUtils.app().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return false;
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        Point realSize = new Point();
        display.getSize(size);
        display.getRealSize(realSize);
        return realSize.y != size.y || realSize.x != size.x;
    }

    ///////////////////////////////////////////////////////////////////////////
    // notification bar
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Set the notification bar's visibility.
     * <p>Must hold {@code <uses-permission android:name="android.permission.EXPAND_STATUS_BAR" />}</p>
     *
     * @param isVisible True to set notification bar visible, false otherwise.
     */
    @RequiresPermission(EXPAND_STATUS_BAR)
    public static void setNotificationBarVisibility(final boolean isVisible) {
        String methodName;
        if (isVisible) {
            methodName = (Build.VERSION.SDK_INT <= 16) ? "expand" : "expandNotificationsPanel";
        } else {
            methodName = (Build.VERSION.SDK_INT <= 16) ? "collapse" : "collapsePanels";
        }
        invokePanels(methodName);
    }

    private static void invokePanels(final String methodName) {
        try {
            @SuppressLint("WrongConstant")
            Object service = AppUtils.app().getSystemService("statusbar");
            @SuppressLint("PrivateApi")
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusBarManager.getMethod(methodName);
            expand.invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
