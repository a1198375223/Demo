package com.example.opengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opengles.shape.Square;
import com.example.opengles.shape.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SimpleExampleActivity extends AppCompatActivity {
    private static final String TAG = "SimpleExampleActivity";

    private GLSurfaceView mGLSurfaceView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new MyGLSurfaceView(this);
        setContentView(mGLSurfaceView);
    }



    class MyGLSurfaceView extends GLSurfaceView {
        private MyGLRenderer renderer;
        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        private float previousX;
        private float previousY;


        public MyGLSurfaceView(Context context) {
            super(context);

            // 创建openGl es 2.0的context
            setEGLContextClientVersion(2);

            renderer = new MyGLRenderer();

            // 设置渲染器
            setRenderer(renderer);

            // 仅在绘图数据发生更改时才渲染视图
            // 可以防止不断的重新绘制, 在调用requestRender()之后才会绘制
            // 如果需要旋转则需要吧这个注释掉
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float dx = x - previousX;
                    float dy = y - previousY;

                    if (y > getHeight() / 2) {
                        dx = dx * -1;
                    }

                    if (x < getWidth() / 2) {
                        dy = dy * -1;
                    }

                    renderer.setAngle(renderer.getAngle() + ((dx + dy) * TOUCH_SCALE_FACTOR));
                    requestRender();
            }
            previousY = y;
            previousX = x;
            return true;
        }
    }


    public static class MyGLRenderer implements GLSurfaceView.Renderer {
        private Triangle mTriangle;
        private Square mSquare;
        private final float[] vPMatrix = new float[16];
        private final float[] projectionMatrix = new float[16];
        private final float[] viewMatrix = new float[16];
        private final float[] rotationMatrix = new float[16];
        private final float[] scratch = new float[16];

        public volatile float mAngle;


        // 在OpenGL ES环境建立的时候被调用
        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            Log.d(TAG, "onSurfaceCreated: ");
            // 设置黑色背景
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            // 在这个方法中初始化需要绘制的图形
            mTriangle = new Triangle();
            mSquare = new Square();
        }

        // 视图发生改变的时候调用 例如：屏幕旋转
        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            Log.d(TAG, "onSurfaceChanged: width=" + width + " height=" + height);
            GLES20.glViewport(0, 0, width, height);

            float ratio = (float) width / height;

            // 作投影变化
            /*Matrix.orthoM (float[] m,           //接收正交投影的变换矩阵
                int mOffset,        //变换矩阵的起始位置（偏移量）
                float left,         //相对观察点近面的左边距
                float right,        //相对观察点近面的右边距
                float bottom,       //相对观察点近面的下边距
                float top,          //相对观察点近面的上边距
                float near,         //相对观察点近面距离
                float far)          //相对观察点远面距离*/
            /*
            Matrix.frustumM (float[] m,         //接收透视投影的变换矩阵
                int mOffset,        //变换矩阵的起始位置（偏移量）
                float left,         //相对观察点近面的左边距
                float right,        //相对观察点近面的右边距
                float bottom,       //相对观察点近面的下边距
                float top,          //相对观察点近面的上边距
                float near,         //相对观察点近面距离
                float far)          //相对观察点远面距离
             */
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        }

        // 绘制的时候调用
        @Override
        public void onDrawFrame(GL10 gl10) {
            Log.d(TAG, "onDrawFrame: ");
            // 重新绘制背景颜色
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            // 创建一个旋转矩阵
//            long time = SystemClock.uptimeMillis() % 4000L;
//            float angle = 0.090f * ((int) time);
            Matrix.setRotateM(rotationMatrix, 0, mAngle, 0, 0, -1.0f);

            // 设置相机的位置
            /*atrix.setLookAtM (float[] rm,      //接收相机变换矩阵
                int rmOffset,       //变换矩阵的起始位置（偏移量）
                float eyeX,float eyeY, float eyeZ,   //相机位置
                float centerX,float centerY,float centerZ,  //观测点位置
                float upX,float upY,float upZ)  //up向量在xyz上的分量*/
            Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            // 计算变换矩阵
            /*Matrix.multiplyMM (float[] result, //接收相乘结果
                int resultOffset,  //接收矩阵的起始位置（偏移量）
                float[] lhs,       //左矩阵
                int lhsOffset,     //左矩阵的起始位置（偏移量）
                float[] rhs,       //右矩阵
                int rhsOffset)     //右矩阵的起始位置（偏移量）*/
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

            Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);
            mTriangle.draw(scratch);
        }

        public static int loadShader(int type, String shaderCode) {
            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            int shader = GLES20.glCreateShader(type);

            // 添加创建着色器的代码
            GLES20.glShaderSource(shader, shaderCode);
            // 编译着色器
            GLES20.glCompileShader(shader);
            return shader;
        }


        public float getAngle() {
            return mAngle;
        }

        public void setAngle(float mAngle) {
            this.mAngle = mAngle;
        }
    }
}
