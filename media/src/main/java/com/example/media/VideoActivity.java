package com.example.media;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.activity.ColorBarActivity;
import com.example.media.activity.DKActivity;
import com.example.media.activity.ExoCastActivity;
import com.example.media.activity.ExoPlayerActivity;
import com.example.media.activity.MediaPlayerActivity;
import com.example.media.activity.MediaPlayerVideoActivity;
import com.example.media.activity.PlayMovieActivity;
import com.example.media.activity.TextureFromCameraActivity;
import com.example.media.gsy.GsyMainActivity;

import java.util.Arrays;

public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        findViewById(R.id.movie_play).setOnClickListener(view -> {
            Intent movieIntent = new Intent(VideoActivity.this, PlayMovieActivity.class);
            startActivity(movieIntent);
        });

        findViewById(R.id.color_bar_bn).setOnClickListener(view -> {
            Intent colorBarIntent = new Intent(VideoActivity.this, ColorBarActivity.class);
            startActivity(colorBarIntent);
        });

        findViewById(R.id.test_camera).setOnClickListener(view -> {
            Intent testCameraIntent = new Intent(VideoActivity.this, TextureFromCameraActivity.class);
            startActivity(testCameraIntent);
        });

        findViewById(R.id.media_player).setOnClickListener(view -> {
            Intent mediaPlayerIntent = new Intent(VideoActivity.this, MediaPlayerActivity.class);
            startActivity(mediaPlayerIntent);
        });

        findViewById(R.id.player_video).setOnClickListener(view -> {
            Intent mediaPlayerVideoIntent = new Intent(VideoActivity.this, MediaPlayerVideoActivity.class);
            startActivity(mediaPlayerVideoIntent);
        });

        findViewById(R.id.exo_player).setOnClickListener(view -> {
            Intent exoPlayerIntent = new Intent(VideoActivity.this, ExoPlayerActivity.class);
            startActivity(exoPlayerIntent);
        });

        findViewById(R.id.cast_exo_player).setOnClickListener(view -> {
            Intent castPlayerIntent = new Intent(VideoActivity.this, ExoCastActivity.class);
            startActivity(castPlayerIntent);
        });

        findViewById(R.id.dk_player).setOnClickListener(view -> {
            Intent dkPlayer = new Intent(VideoActivity.this, DKActivity.class);
            startActivity(dkPlayer);
        });

        findViewById(R.id.gsy_player).setOnClickListener(view -> {
            Intent gsyIntent = new Intent(VideoActivity.this, GsyMainActivity.class);
            startActivity(gsyIntent);
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Log.d(TAG, "测试文件路径：\n" +
//                    "getFilesDir=" + getFilesDir() + "\n" + // /data/user/0/com.example.androidxdemo/files
//                    "getCacheDir=" + getCacheDir() + "\n" + // /data/user/0/com.example.androidxdemo/cache
//                    "getDataDir=" + getDataDir() + "\n" + // /data/user/0/com.example.androidxdemo
//                    "getObbDir=" + getObbDir() + "\n" + // /storage/emulated/0/Android/obb/com.example.androidxdemo
//                    "getCodeCacheDir=" + getCodeCacheDir() + "\n" + // /data/user/0/com.example.androidxdemo/code_cache
//                    "getExternalCacheDir=" + getExternalCacheDir() + "\n" + // /storage/emulated/0/Android/data/com.example.androidxdemo/cache
//                    "getNoBackupFilesDir=" + getNoBackupFilesDir() + "\n" + // /data/user/0/com.example.androidxdemo/no_backup
//                    "getPackageCodePath=" + getPackageCodePath() + "\n" + // /data/app/com.example.androidxdemo-2/base.apk
//                    "getPackageResourcePath=" + getPackageResourcePath() + "\n" + // /data/app/com.example.androidxdemo-2/base.apk
//                    "getExternalFilesDir=" + getExternalFilesDir(null) + "\n" // /storage/emulated/0/Android/data/com.example.androidxdemo/files
//            );
//        }
    }
}
