package com.example.opengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opengles.shape.Square;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ShapeSquareActivity extends AppCompatActivity {
    private static final String TAG = "ShapeSquareActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyGLSurfaceView surfaceView = new MyGLSurfaceView(this);
        setContentView(surfaceView);
    }


    class MyGLSurfaceView extends GLSurfaceView {
        MyGLRenderer renderer;

        public MyGLSurfaceView(Context context) {
            super(context);
            // 设置版本
            setEGLContextClientVersion(2);

            // 创建渲染器
            renderer = new MyGLRenderer();
            setRenderer(renderer);

            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
    }


    class MyGLRenderer implements GLSurfaceView.Renderer {
        private Square mSquare;
        private float[] mPMatrix = new float[16];
        private float[] mVMatrix = new float[16];
        private float[] mVPMatrix = new float[16];

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            Log.d(TAG, "onSurfaceCreated: ");
            // 设置背景颜色
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            mSquare = new Square();
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            Log.d(TAG, "onSurfaceChanged: width=" + width + " height=" + height);
            // 设置
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            // 设置投影
            Matrix.frustumM(mPMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            Log.d(TAG, "onDrawFrame: ");
            // 设置相机
            Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            Matrix.multiplyMM(mVPMatrix, 0, mPMatrix, 0, mVMatrix, 0);
            // 绘制图形
            mSquare.draw(mVPMatrix);
        }
    }
}
