package com.example.opengles.shape;

import android.opengl.GLES20;

import com.example.opengles.SimpleExampleActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

// 三角形
public class Triangle {

    private final int mProgram;
    private FloatBuffer colorBuffer;
    private FloatBuffer vertexBuffer;

    // 每个定点使用多少个坐标
    static final int COORDS_PER_VERTEX = 3;

    // 三角形的坐标
    static float triangleCoords[] = {
            0.0f, 0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    // 定义颜色 red, green, blue, alpha
    float color[] = {
            0.63671875f, 0.76953125f, 0.22265625f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };


    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "varying  vec4 vColor;" +
                    "attribute vec4 aColor;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "   vColor=aColor;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "   gl_FragColor = vColor;" +
                    "}";

    // 位置句柄
    private int positionHandle;
    // 颜色句柄
    private int colorHandle;
    // 用于访问和设置视图转换的句柄
    private int vPMatrixHandle;
    // 每个顶点4个字节
    private final int vertexStride = COORDS_PER_VERTEX * 4;
    // 顶点的个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;



    public Triangle() {
        // 为形状坐标初始化顶点字节缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        // 使用设备硬件的本机字节顺序
        byteBuffer.order(ByteOrder.nativeOrder());

        // 将数据转化成FloatBuffer
        vertexBuffer = byteBuffer.asFloatBuffer();
        // 将坐标添加到FloatBuffer
        vertexBuffer.put(triangleCoords);
        // 读取第一个的坐标
        vertexBuffer.position(0);


        // 初始化颜色缓冲区
        ByteBuffer bb = ByteBuffer.allocateDirect(color.length * 4);
        bb.order(ByteOrder.nativeOrder());
        colorBuffer = bb.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);


        // 创建顶点着色器和片段着色器
        int vertexShader = SimpleExampleActivity.MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = SimpleExampleActivity.MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // 创建一个OpenGL ES程序
        mProgram = GLES20.glCreateProgram();
        // 为程序添加顶点着色器和片段着色器
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        // 创建链接程序
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        // 清空缓冲区
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // 将链接程序添加到OpenGL ES环境中
        GLES20.glUseProgram(mProgram);
        // 获取顶点着色器的vPosition成员的句柄
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 启用句柄
        GLES20.glEnableVertexAttribArray(positionHandle);

        // 获取颜色aColor句柄
        colorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        // 准备数据
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        // 获取片段着色器的vColor成员的句柄
//        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // 设置三角形的颜色
//        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        // 获取转化矩阵的句柄
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // 传入投影变化和view变化到着色器中
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
        // 禁止顶点数据的句柄
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
