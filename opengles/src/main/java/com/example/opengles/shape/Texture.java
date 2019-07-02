package com.example.opengles.shape;

import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.example.commonlibrary.utils.AppUtils;
import com.example.opengles.R;
import com.example.opengles.SimpleExampleActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Texture {
    //顶点着色器代码
    private final String vertexShaderCode = "" +
            "attribute vec4 vPosition;" +
            "attribute vec2 aCoord;" +
            "uniform mat4 vMatrix;" +
            "varying vec2 vCoord;" +
            "void main(){" +
            "   gl_Position = vMatrix * vPosition;" +
            "   vCoord = aCoord;" +
            "}";

    //片元着色器代码
    private final String fragmentShaderCode = "" +
            "precision mediump float;" +
            "uniform sampler2D vTexture;" +
            "varying vec2 vCoord;" +
            "void main(){" +
            "   gl_FragColor = texture2D(vTexture, vCoord);" +
            "}";


    //顶点坐标
    private final float[] pos = {
            -1.0f, 1.0f,
            1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f
    };

    //纹理坐标
    // 对应关系
    // 纹理坐标 -> 顶点坐标
    // (0, 0) -> (-1, 1)
    // (1, 0) -> (1, 1)
    // (0, 1) -> (-1, -1)
    // (1, 1) -> (1, -1)
    private final float[] coord = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
    };


    // 句柄
    private int coordHandle;
    private int mvpMatrixHandle;
    private int positionHandle;
    private int textureHandle;

    // 程序
    private int mProgram;

    // 缓冲区
    private FloatBuffer mCoordBuffer;
    private FloatBuffer mVertexBuffer;

    public Texture() {
        // 初始化坐标缓冲区
        ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(pos.length * 4);
        vertexBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexBuffer.asFloatBuffer();
        mVertexBuffer.put(pos);
        mVertexBuffer.position(0);

        // 初始化纹理缓冲区
        ByteBuffer colorBuffer = ByteBuffer.allocateDirect(coord.length * 4);
        colorBuffer.order(ByteOrder.nativeOrder());
        mCoordBuffer = colorBuffer.asFloatBuffer();
        mCoordBuffer.put(coord);
        mCoordBuffer.position(0);

        // 获取着色器
        int vertexShader = SimpleExampleActivity.MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = SimpleExampleActivity.MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // 创建程序
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    int[] texture = new int[1];
    public void draw(float[] mvpMatrix) {
        // 清除缓存
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // 使用程序
        GLES20.glUseProgram(mProgram);

        // 获取句柄
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        coordHandle = GLES20.glGetAttribLocation(mProgram, "aCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        textureHandle = GLES20.glGetUniformLocation(mProgram, "vTexture");

        // 启用句柄
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(coordHandle);

        // 准备数据
        GLES20.glVertexAttribPointer(coordHandle, 2, GLES20.GL_FLOAT, false, 8, mCoordBuffer);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 8, mVertexBuffer);
        // 设置矩阵变化
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // 使用纹理
        GLES20.glUniform1i(textureHandle, 0);
        // 创建纹理获得纹理索引 第一个参数是生成纹理数量因为定义的数组长度为1所以这里也是1.可以根据需要增加。
        GLES20.glGenTextures(1, texture, 0);
        // 绑定纹理 第一个参数是纹理类型，第二个参数纹理索引
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        // 设置缩放
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // 设置环绕方式 最后一个参数
        // GL_REPEAT : 坐标的整数部分被忽略，重复纹理，这是OpenGL纹理默认的处理方式.
        // GL_MIRRORED_REPEAT : 纹理也会被重复，但是当纹理坐标的整数部分是奇数时会使用镜像重复。
        // GL_CLAMP_TO_EDGE : 坐标会被截断到[0,1]之间。结果是坐标值大的被截断到纹理的边缘部分，形成了一个拉伸的边缘(stretched edge pattern)。
        // GL_CLAMP_TO_BORDER : 不在[0,1]范围内的纹理坐标会使用用户指定的边缘颜色。
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // 绘制
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, BitmapFactory.decodeResource(AppUtils.app().getResources(), R.drawable.shale), 0);

        // 索引绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // 禁用句柄
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
