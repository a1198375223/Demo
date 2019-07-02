package com.example.opengles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opengles.shape.Texture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyGLSurfaceView surfaceView = new MyGLSurfaceView(this);
        setContentView(surfaceView);
    }


    class MyGLSurfaceView extends GLSurfaceView {
        private MyGLRenderer renderer;

        public MyGLSurfaceView(Context context) {
            super(context);

            setEGLContextClientVersion(2);

            renderer = new MyGLRenderer();
            setRenderer(renderer);

            setRenderMode(RENDERMODE_WHEN_DIRTY);
        }
    }


    class MyGLRenderer implements GLSurfaceView.Renderer {
        private Texture texture;
        private float[] mPMatrix = new float[16];
        private float[] mMVPMatrix = new float[16];
        private float[] mVMatrix = new float[16];

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            // 设置背景颜色
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            texture = new Texture();
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            GLES20.glViewport(0, 0, width, height);

            //计算屏幕宽高比
            float ratio = (float)width/height;
            Matrix.orthoM(mPMatrix, 0, -ratio, ratio, -1, 1, 3, 6);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shale);
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            float ratioB = (float) w / h;

            if(width>height){
                if (ratioB > ratio) {
                    Matrix.orthoM(mPMatrix, 0, -ratio * ratioB, ratio * ratioB, -1, 1, 3, 7);
                } else {
                    Matrix.orthoM(mPMatrix, 0, -ratio / ratioB, ratio / ratioB, -1, 1, 3, 7);
                }
            }else {
                if (ratioB > ratio) {
                    Matrix.orthoM(mPMatrix, 0, -1, 1, -1 / ratio * ratioB, 1 / ratio * ratioB, 3, 7);
                } else {
                    Matrix.orthoM(mPMatrix, 0, -1, 1, -ratioB / ratio, ratioB / ratio, 3, 7);
                }
            }
//             投影
//            Matrix.frustumM(mPMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            // 相机
            Matrix.setLookAtM(mVMatrix, 0, 0, 0, 7, 0, 0, 0, 0, 1, 0);
            // 结果
            Matrix.multiplyMM(mMVPMatrix, 0, mPMatrix, 0, mVMatrix, 0);

            // 旋转一下
//            Matrix.rotateM(mMVPMatrix, 0, mMVPMatrix, 0, 1, 1, 1, 0);

            texture.draw(mMVPMatrix);
        }
    }
}
