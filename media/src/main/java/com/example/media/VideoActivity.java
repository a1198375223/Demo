package com.example.media;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.activity.PlayMovieActivity;

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
//                    "getPackageResourcePath=" + getPackageResourcePath()); // /data/app/com.example.androidxdemo-2/base.apk
//        }
    }
}
