package com.example.opengles.shape;

import android.opengl.GLES20;

import com.example.opengles.SimpleExampleActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Cube {

    // 顶点着色器
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "varying  vec4 vColor;" +
                    "attribute vec4 aColor;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "   vColor=aColor;" +
                    "}";

    // 片源着色器
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "   gl_FragColor = vColor;" +
                    "}";

    // 顶点坐标
    private final float cubepos[] = {
            -1.0f, 1.0f, 1.0f,    //正面左上
            -1.0f, -1.0f, 1.0f,   //正面左下
            1.0f, -1.0f, 1.0f,    //正面右下
            1.0f, 1.0f, 1.0f,     //正面右上
            -1.0f, 1.0f, -1.0f,    //反面左上
            -1.0f, -1.0f, -1.0f,   //反面左下
            1.0f, -1.0f, -1.0f,    //反面右下
            1.0f, 1.0f, -1.0f     //反面右上
    };

    //索引坐标
    final short index[] = {
            0, 1, 3, 1, 2, 3,    //正面
            3, 2, 7, 2, 6, 7,    //右面
            7, 6, 4, 6, 4, 5,    //后面
            4, 5, 0, 5, 1, 0,    //左面
            1, 2, 5, 2, 6, 5,    //下面
            0, 3, 4, 3, 7, 4     //上面
    };

    //八个顶点的颜色，与顶点坐标一一对应
    float colors[] = {
            0f, 0.5f, 0f, 1f,
            0.5f, 0f, 0f, 1f,
            0f, 0f, 0.5f, 1f,
            0f, 0.5f, 0.5f, 1f,
            0.5f, 0.5f, 0f, 1f,
            0.5f, 0f, 0.5f, 1f,
            0.5f, 0.5f, 0.5f, 1f,
            0f, 0f, 0f, 1f,
    };

    // 句柄
    private int colorHandle;
    private int mvpMatrixHandle;
    private int positionHandle;
    // 程序
    private int mProgram;

    // 缓冲区
    private FloatBuffer mColorBuffer;
    private FloatBuffer mVertexBuffer;
    private ShortBuffer mIndexBuffer;


    public Cube() {
        // 初始化坐标缓冲区
        ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(cubepos.length * 4);
        vertexBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexBuffer.asFloatBuffer();
        mVertexBuffer.put(cubepos);
        mVertexBuffer.position(0);

        // 初始化颜色缓冲区
        ByteBuffer colorBuffer = ByteBuffer.allocateDirect(colors.length * 4);
        colorBuffer.order(ByteOrder.nativeOrder());
        mColorBuffer = colorBuffer.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);

        // 初始化索引缓冲区
        ByteBuffer indexBuffer = ByteBuffer.allocateDirect(index.length * 2);
        indexBuffer.order(ByteOrder.nativeOrder());
        mIndexBuffer = indexBuffer.asShortBuffer();
        mIndexBuffer.put(index);
        mIndexBuffer.position(0);

        // 获取着色器
        int vertexShader = SimpleExampleActivity.MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = SimpleExampleActivity.MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // 创建程序
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }


    public void draw(float[] mvpMatrix) {
        // 清除缓存
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // 使用程序
        GLES20.glUseProgram(mProgram);

        // 获取句柄
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        colorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // 启用句柄
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(colorHandle);

        // 准备数据
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, mColorBuffer);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);
        // 设置矩阵变化
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // 索引绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

        // 禁用句柄
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
