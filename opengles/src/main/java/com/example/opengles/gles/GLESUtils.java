package com.example.opengles.gles;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLESUtils {
    private static final String TAG = "GLESUtils";

    /**
     * 创建程序
     * @param vertexSource 顶点着色器的代码
     * @param fragmentSource 片元着色器的代码
     * @return 程序句柄
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        if (program == 0) {
            Log.e(TAG, "Could not create program");
        } else {
            Log.d(TAG, "Create program=" + program);
        }
        GLES20.glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(program, pixelShader);
        checkGlError("glAttachShader");
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        } else {
            Log.d(TAG, "Create program=" + program);
            Log.d(TAG, "Current program status=" + GLES20.glGetProgramInfoLog(program));
        }
        return program;
    }


    /**
     * 创建着色器
     * @param shaderType 着色器的类型
     * @param source 创建的代码
     * @return 着色器
     */
    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        checkGlError("glCreateShader type=" + shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + shaderType + ":");
            Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        } else {
            Log.d(TAG, "Current shader status=" + compiled[0]);
            Log.d(TAG, " " + GLES20.glGetShaderInfoLog(shader));
        }
        return shader;
    }


    // 检查是否出错
    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, msg);
            throw new RuntimeException(msg);
        }
    }


    /**
     * 创建纹理
     * @param data 图片数据
     * @param width 纹理宽度
     * @param height 纹理高度
     * @param format 图像数据格式
     * @return 纹理句柄
     */
    public static int createImageTexture(ByteBuffer data, int width, int height, int format) {
        int[] textureHandles = new int[1];
        int textureHandle;

        // 获取纹理
        GLES20.glGenTextures(1, textureHandles, 0);
        textureHandle = textureHandles[0];
        checkGlError("glGenTextures");
        // 绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);

        // 设置缩放
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        checkGlError("loadImageTexture");

        // 设置环绕方式 最后一个参数
        // GL_REPEAT : 坐标的整数部分被忽略，重复纹理，这是OpenGL纹理默认的处理方式.
        // GL_MIRRORED_REPEAT : 纹理也会被重复，但是当纹理坐标的整数部分是奇数时会使用镜像重复。
        // GL_CLAMP_TO_EDGE : 坐标会被截断到[0,1]之间。结果是坐标值大的被截断到纹理的边缘部分，形成了一个拉伸的边缘(stretched edge pattern)。
        // GL_CLAMP_TO_BORDER : 不在[0,1]范围内的纹理坐标会使用用户指定的边缘颜色。
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);


        // 绘制
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, format, width, height, 0, format, GLES20.GL_UNSIGNED_BYTE, data);
        checkGlError("drawImageTexture");

        return textureHandle;
    }


    /**
     * 创建一个FloatBuffer
     * @param coords 坐标
     * @return 含有坐标信息的FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(float[] coords) {
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(coords);
        fb.position(0);
        return fb;
    }


    /**
     * 打印OpenGL ES的版本信息
     */
    public static void logVersionInfo() {
        Log.i(TAG, "vendor  :" + GLES20.glGetString(GLES20.GL_VENDOR));
        Log.i(TAG, "renderer:" + GLES20.glGetString(GLES20.GL_RENDERER));
        Log.i(TAG, "version :" + GLES20.glGetString(GLES20.GL_VERSION));

        int[] values = new int[1];
        GLES30.glGetIntegerv(GLES30.GL_MAJOR_VERSION, values, 0);
        int majorVersion = values[0];
        GLES30.glGetIntegerv(GLES30.GL_MINOR_VERSION, values, 0);
        int minorVersion = values[0];
        if (GLES30.glGetError() == GLES30.GL_NO_ERROR) {
            Log.i(TAG, "i version: " + majorVersion + "." + minorVersion);
        }
    }


    /**
     * 检查location是否是无效的. 如果我们找不到label, GLES会返回-1
     * @param location 返回的location
     * @param label 标签的名称
     */
    public static void checkLocation(int location, String label) {
        if (location < 0) {
            throw new RuntimeException("Unable to locate '" + label + "' in program");
        }
    }
}
