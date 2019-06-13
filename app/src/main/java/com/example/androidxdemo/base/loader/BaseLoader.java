package com.example.androidxdemo.base.loader;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * 使用这个类能快速的获取到数据,并且适应ListView.
 * 但是好像一个Activity只能被注册一次.不然其他的页面无法获取到正确的数据,换不换id都没有什么用
 * 如果一定要在ViewPager多次加载,请使用FragmentViewPager来支持这个类
 */
public abstract class BaseLoader<T> implements LoaderManager.LoaderCallbacks<T> {
    public static final int DEFAULT_LOADER_ID = 0;
    public static final String EXTRA_URI = "extra_uri";
    public static final String EXTRA_PROJECTION = "extra_projection";
    public static final String EXTRA_SELECTION = "extra_selection";
    public static final String EXTRA_SELECTION_ARG = "extra_selection_arg";
    public static final String EXTRA_SORT_ORDER = "extra_sort_order";


    private int loaderId = DEFAULT_LOADER_ID;
    private Bundle bundle = null;
    private Activity activity;

    public BaseLoader(Activity activity) {
        this.activity = activity;
        activity.getLoaderManager().initLoader(loaderId, null, this);
    }
    public BaseLoader(Activity activity, int loaderId) {
        this.activity = activity;
        this.loaderId = loaderId;
        activity.getLoaderManager().initLoader(loaderId, null, this);
    }

    public BaseLoader(Activity activity, int loaderId, Bundle bundle) {
        /*第一个参数:用于标识加载器的唯一 ID
          第二个参数:在构建时提供给加载器的可选参数
          第三个参数:LoaderManager.LoaderCallbacks的实现*/
        this.activity = activity;
        this.loaderId = loaderId;
        this.bundle = bundle;
        activity.getLoaderManager().initLoader(loaderId, bundle, this);
    }

    /**
     * 重启Loader
     */
    public void restart() {
        activity.getLoaderManager().restartLoader(loaderId, bundle, this);
    }

    /**
     * 销毁Loader
     */
    public void destroy() {
        activity.getLoaderManager().destroyLoader(loaderId);
    }


    /**
     * 获取Loader
     */
    public Loader<T> getLoader() {
        return activity.getLoaderManager().getLoader(loaderId);
    }


    @NonNull
    @Override
    public Loader<T> onCreateLoader(int id, @Nullable Bundle bundle) {
        return onCreate(id, bundle);
    }

    @Override
    public void onLoadFinished(Loader<T> loader, T data) {
        onFinished(loader, data);
    }

    @Override
    public void onLoaderReset(Loader<T> loader) {
        onReset(loader);
    }


    /**
     * 针对指定的 ID 进行实例化并返回新的 Loader
     * 在调用 activity.getLoaderManager().initLoader() 时系统会检查该指定的id的加载器是否已经存在, 如果
     * 不会在对应的加载器就会调用下面这个方法来创建一个新的加载器, 通常会创建一个CursorLoader类,但是也可以实现
     * 自定义的加载器
     * @param id 指定的id
     * @param args 指定的bundle
     * @return 返回创建的加载器对象
     */
    public Loader<T> onCreate(int id, Bundle args) {
        if (Cursor.class.isAssignableFrom(getClazz())) {
            return genCursorLoader(id, args);
        }
        return null;
    }

    /**
     * 将在先前创建的加载器完成加载时调用
     * 如果在调用时，调用程序处于启动状态，且请求的加载器已存在并生成了数据，则系统将立即调用 onLoadFinished()（在 initLoader() 期间）
     * @param loader 之前创建的加载器
     * @param data
     */
    public abstract void onFinished(Loader<T> loader, T data);

    /**
     * 将在先前创建的加载器重置且其数据因此不可用时调用
     * @param loader 之前创建的加载器
     */
    public abstract void onReset(Loader<T> loader);

    /**
     * 判断如果当前类的泛型参数是Cursor类型就可以通过这个方法来生成CursorLoader
     * @param id 指定的id
     * @param args 指定的bundle
     * @return 返回创建的CursorLoader对象
     */
    public Loader<T> genCursorLoader(int id, Bundle args) {
        Uri uri = args == null ? null : Uri.parse(args.getString(EXTRA_URI));
        String[] projection = args == null ? null : args.getStringArray(EXTRA_PROJECTION);
        String selection = args == null ? null : args.getString(EXTRA_SELECTION);
        String[] selectionArgs = args == null ? null : args.getStringArray(EXTRA_SELECTION_ARG);
        String order = args == null ? null : args.getString(EXTRA_SORT_ORDER);
        return (Loader<T>) new CursorLoader(activity, uri,
                projection, selection, selectionArgs, order);
    }

    /**
     * 获取泛型的类型
     */
    private Class<T> getClazz() {
        Class<T> tClass = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            tClass = (Class<T>) ((ParameterizedType) Objects.requireNonNull(getClass().getGenericSuperclass())).getActualTypeArguments()[0];
        }
        return tClass;
    }
}
