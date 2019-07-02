package com.example.opengles;

import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opengles.gles.EGLCore;
import com.example.opengles.gles.OffscreenSurface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class GLInfoActivity extends AppCompatActivity {
    private static final String TAG = "GLInfoActivity";
    private String mGlInfo;
    private File mOutputFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_gles_info);

        mOutputFile = new File(getFilesDir(), "gles-info.txt");
        ((TextView) findViewById(R.id.save_path_tv)).setText(mOutputFile.toString());

        findViewById(R.id.save_bn).setOnClickListener(view -> {
            try {
                FileWriter writer = new FileWriter(mOutputFile);
                writer.write(mGlInfo);
                writer.close();
                Log.d(TAG, "Output written to '" + mOutputFile + "'");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ((TextView) findViewById(R.id.info_tv)).setText((mGlInfo = gatherGlInfo()));
    }


    private String gatherGlInfo() {
        EGLCore eglCore = new EGLCore(null, EGLCore.FLAG_TRY_GLES3);
        OffscreenSurface surface = new OffscreenSurface(eglCore, 1, 1);
        surface.makeCurrent();

        StringBuilder sb = new StringBuilder();
        sb.append("===== GL Information =====");
        sb.append("\nvendor     : ");
        sb.append(GLES20.glGetString(GLES20.GL_VENDOR));
        sb.append("\nversion    : ");
        sb.append(GLES20.glGetString(GLES20.GL_VERSION));
        sb.append("\nrenderer   : ");
        sb.append(GLES20.glGetString(GLES20.GL_RENDERER));
        sb.append("\nextensions : ");
        sb.append(formatExtensions(GLES20.glGetString(GLES20.GL_EXTENSIONS)));

        sb.append("\n===== EGL Information =====");
        sb.append("\nvendor     : ");
        sb.append(eglCore.queryString(EGL14.EGL_VENDOR));
        sb.append("\nversion    : ");
        sb.append(eglCore.queryString(EGL14.EGL_VERSION));
        sb.append("\nclient API : ");
        sb.append(eglCore.queryString(EGL14.EGL_CLIENT_APIS));
        sb.append("\nextensions : ");
        sb.append(formatExtensions(eglCore.queryString(EGL14.EGL_EXTENSIONS)));

        surface.release();
        eglCore.release();

        sb.append("\n===== System Information =====");
        sb.append("\nmfqr       : ");
        sb.append(Build.MANUFACTURER);
        sb.append("\nbrand      : ");
        sb.append(Build.BRAND);
        sb.append("\nmodel      : ");
        sb.append(Build.MODEL);
        sb.append("\nrelease    : ");
        sb.append(Build.VERSION.RELEASE);
        sb.append("\nbuild      : ");
        sb.append(Build.DISPLAY);
        sb.append("\n");
        return sb.toString();
    }


    private String formatExtensions(String ext) {
        String[] values = ext.split(" ");
        Arrays.sort(values);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append("  ");
            sb.append(values[i]);
            sb.append("\n");
        }
        return sb.toString();
    }
}
