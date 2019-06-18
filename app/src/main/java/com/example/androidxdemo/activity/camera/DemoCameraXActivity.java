package com.example.androidxdemo.activity.camera;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.AppUtils;

import java.io.File;
import java.util.Arrays;

public class DemoCameraXActivity extends AppCompatActivity {
    private static final String TAG = "DemoCameraXActivity";
    private FrameLayout mContainer;
    private final int IMMERSIVE_FLAG_TIMEOUT = 500;
    public static final String KEY_EVENT_ACTION = "key_event_action";
    public static final String KEY_EVENT_EXTRA = "key_event_extra";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_camerax);

        mContainer = findViewById(R.id.fragment_container);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContainer.postDelayed(() -> mContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN), IMMERSIVE_FLAG_TIMEOUT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Intent intent = new Intent(KEY_EVENT_ACTION);
                intent.putExtra(KEY_EVENT_EXTRA, keyCode);
                // 发送一个局部广播
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static File getOutputDirectory(Context context) {
        File mediaDir;
        File[] files = context.getExternalMediaDirs();
        Log.d(TAG, "getOutputDirectory: files=" + Arrays.toString(files));
        if ((mediaDir = files[0]) != null) {
            File file = new File(context.getExternalMediaDirs()[0].getPath(), AppUtils.app().getResources().getString(R.string.app_name));
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        if (mediaDir != null && mediaDir.exists()) {
            return mediaDir;
        }
        return AppUtils.app().getFilesDir();
    }
}
