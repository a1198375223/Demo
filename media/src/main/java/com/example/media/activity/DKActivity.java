package com.example.media.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.media.R;
import com.example.media.activity.api.ApiActivity;
import com.example.media.activity.api.PlayerActivity;
import com.example.media.common.VideoViewConfig;
import com.example.media.common.VideoViewManager;
import com.example.media.exo.ExoMediaPlayerFactory;
import com.example.media.factory.AbsPlayerFactory;
import com.example.media.factory.IjkPlayerFactory;
import com.example.media.media_player.AndroidMediaPlayerFactory;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class DKActivity extends AppCompatActivity {
    private static final String TAG = "DKActivity";
    private static final int REQUEST_CODE = 1;

    private EditText editText;
    private boolean isLive;

    private TextView mCurrentPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dk);

        editText = findViewById(R.id.et);
        mCurrentPlayer = findViewById(R.id.curr_player);
        VideoViewConfig config = VideoViewManager.getConfig();
        try {
            Field mPlayerFactoryField = config.getClass().getDeclaredField("mPlayerFactory");
            mPlayerFactoryField.setAccessible(true);
            Object playerFactory = mPlayerFactoryField.get(config);
            String msg = getString(R.string.str_current_player);
            if (playerFactory instanceof IjkPlayerFactory) {
                mCurrentPlayer.setText(msg + "IjkPlayer");
            } else if (playerFactory instanceof ExoMediaPlayerFactory) {
                mCurrentPlayer.setText(msg + "ExoPlayer");
            } else {
                mCurrentPlayer.setText(msg + "MediaPlayer");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((RadioGroup) findViewById(R.id.rg)).setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.vod) {
                isLive = false;
            } else if (checkedId == R.id.live) {
                isLive = true;
            }
        });
        addPermission();
    }

    // 请求添加权限
    private void addPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            // do nothing
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.close_float_window) {
//            PIPManager.getInstance().stopFloatWindow();
//            PIPManager.getInstance().reset();
        } else if (itemId == R.id.clear_cache) {
//            if (VideoCacheManager.clearAllCache(this)) {
//                Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
//            }
        }

        //切换播放核心，不推荐这么做，我这么写只是为了方便测试
        VideoViewConfig config = VideoViewManager.getConfig();
        try {
            Field mPlayerFactoryField = config.getClass().getDeclaredField("mPlayerFactory");
            mPlayerFactoryField.setAccessible(true);
            AbsPlayerFactory playerFactory = null;
            String msg = getString(R.string.str_current_player);
            if (itemId == R.id.ijk) {
                playerFactory = IjkPlayerFactory.create();
                mCurrentPlayer.setText(msg + "IjkPlayer");
            } else if (itemId == R.id.exo) {
                playerFactory = ExoMediaPlayerFactory.create();
                mCurrentPlayer.setText(msg + "ExoPlayer");
            } else if (itemId == R.id.media) {
                playerFactory = AndroidMediaPlayerFactory.create();
                mCurrentPlayer.setText(msg + "MediaPlayer");
            }
            mPlayerFactoryField.set(config, playerFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void playOther(View view) {
        String url = editText.getText().toString();
        if (TextUtils.isEmpty(url)) return;
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("isLive", isLive);
        startActivity(intent);
    }

    public void clearUrl(View view) {
        editText.setText("");
    }

    public void api(View view) {
        startActivity(new Intent(this, ApiActivity.class));
    }

    public void extend(View view) {
//        startActivity(new Intent(this, ExtendActivity.class));
    }

    public void list(View view) {
//        startActivity(new Intent(this, ListActivity.class));
    }

    public void pip(View view) {
//        startActivity(new Intent(this, PIPDemoActivity.class));
    }
}
