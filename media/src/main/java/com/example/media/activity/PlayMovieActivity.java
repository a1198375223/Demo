package com.example.media.activity;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.commonlibrary.utils.FileUtils;
import com.example.commonlibrary.utils.Toasty;
import com.example.media.R;
import com.example.media.callback.SpeedControlCallback;
import com.example.media.common.Contants;
import com.example.media.media_player.MoviePlayer;

import java.io.File;
import java.lang.ref.WeakReference;

public class PlayMovieActivity extends AppCompatActivity {
    private static final String TAG = "PlayMovieActivity";

    private TextureView mTextureView;
    private boolean mSurfaceTextureReady = false;
    private boolean mShowStopLabel;

    private String[] mMovieFiles;
    // 选中要播放的视频
    private int mSelectedMovie;

    private PlayMoviePresenter mPresenter;
    private int fps;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_movie);

        mTextureView = findViewById(R.id.movie_texture_view);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                // 当这个方法回调的时候 textureView就初始化完成了
                Log.d(TAG, "SurfaceTexture ready (" + width + "x" + height + ")");
                mSurfaceTextureReady = true;
                updateControls();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                Log.d(TAG, "SurfaceTexture size change (" + width + "x" + height + ")");
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                Log.d(TAG, "SurfaceTexture destroy.");
                mSurfaceTextureReady = false;
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                Log.d(TAG, "SurfaceTexture change.");
            }
        });

        Spinner spinner = findViewById(R.id.playMovieFile_spinner);

        File dir = new File(Contants.MP4_VIDEO_PATH);
        if (!dir.exists()) {
            Log.e(TAG, "the path " + dir.getAbsolutePath() + " is not exists");
            Toasty.showError("目录不存在...");
            finish();
            return;
        }
        mMovieFiles = FileUtils.getFiles(dir, "*.mp4");
        if (mMovieFiles == null || mMovieFiles.length == 0) {
            Toasty.showError("没有视频文件...");
            finish();
            return;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mMovieFiles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                Log.d(TAG, "item selected pos=" + pos + " id=" + id);

                Spinner spinner1 = (Spinner) adapterView;
                mSelectedMovie = spinner1.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "nothing selected");
            }
        });


        findViewById(R.id.play_stop_button).setOnClickListener(this::clickPlayStop);

        updateControls();

        ((CheckBox) findViewById(R.id.locked60fps_checkbox)).setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                fps = 60;
            } else {
                fps = 30;
            }
        });

    }


    // 更新控件状态
    private void updateControls() {
        Button play = findViewById(R.id.play_stop_button);
        if (mShowStopLabel) {
            play.setText(R.string.stop_button_text);
        } else {
            play.setText(R.string.play_button_text);
        }

        play.setEnabled(mSurfaceTextureReady);

        CheckBox check = findViewById(R.id.locked60fps_checkbox);
        check.setEnabled(!mShowStopLabel);
        check = findViewById(R.id.loopPlayback_checkbox);
        check.setEnabled(!mShowStopLabel);
    }


    public void clickPlayStop(View unused) {
        if (mShowStopLabel) {
            Log.d(TAG, "stopping movie.");
            mPresenter.stopPlayback();
        } else {
            Log.d(TAG, "try to start movie.");
            if (mPresenter == null) {
                Surface surface = new Surface(mTextureView.getSurfaceTexture());
                mPresenter = new PlayMoviePresenter(this, new File(Contants.MP4_VIDEO_PATH, mMovieFiles[mSelectedMovie]), surface);
            }
            mPresenter.setFps(fps);
            mPresenter.startPlayback(mTextureView, ((CheckBox) findViewById(R.id.loopPlayback_checkbox)).isChecked(),
                    new WeakReference<>(mStartPlayCallback), new WeakReference<>(mStopPlayCallback));
        }
    }


    private PlayMoviePresenter.StartPlayCallback mStartPlayCallback = () -> {
        Log.d(TAG, "start callback");
        mShowStopLabel = true;
        updateControls();
    };

    private MoviePlayer.PlayerFeedback mStopPlayCallback = () -> {
        Log.d(TAG, "stop callback");
        mShowStopLabel = false;
        if (mPresenter != null) {
            mPresenter.release();
        }
        updateControls();
    };
}
