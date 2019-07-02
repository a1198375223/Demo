package com.example.opengles;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * glClearColor(red, green, blue, alpha)                : 绘制背景颜色
 * glClear(mask)                                        :
 * glViewport(x, y, width, height)                      : 设置窗口视图
 * glCreateShader(int type)                             : 创建着色器 Shader  顶点着色器->GLES20.GL_VERTEX_SHADER   片段着色器->GLES20.GL_FRAGMENT_SHADER
 * glShaderSource(int shader, String shaderCode)        : 为着色器的创建指定创建的代码
 * glCompileShader(int shader)                          : 编译着色器
 * glCreateProgram()                                    : 创建一个OpenGL ES程序
 * glAttachShader(int program, int shader)              : 为程序添加着色器
 * glLinkProgram(int program)                           : 创建一个链接程序
 * glUseProgram(int program)                            : 将program添加到OpenGL ES环境中
 * glGetAttribLocation(int program, String member)      : 获取程序中member的句柄（顶点着色器）
 * glEnableVertexAttribArray(int handle)                : 启用句柄
 * glVertexAttribPointer(int handle, int count,
 *                      int type, boolean normalized,
 *                      int stride, Buffer ptr)         : 准备数据
 * glGetUniformLocation(int program, String member)     : 获取程序中member的句柄（片段着色器）
 * glUniform4fv(int location, int count,
 *              float[] color, int offset)              : 设置颜色
 * glDrawArrays(int mode, int first, int count)         : 绘制图形(模式图案 开始顶点 顶点数量)
 * glDisableVertexAttribArray(int index)                : 禁止顶点数据的句柄
 *
 *
 *
 *
 *
 * 绘制的方式 mode:
 * glDrawArrays(int mode, int first, int count)
 * int GL_POINTS       //将传入的顶点坐标作为单独的点绘制
 * int GL_LINES        //将传入的坐标作为单独线条绘制，ABCDEFG六个顶点，绘制AB、CD、EF三条线
 * int GL_LINE_STRIP   //将传入的顶点作为折线绘制，ABCD四个顶点，绘制AB、BC、CD三条线
 * int GL_LINE_LOOP    //将传入的顶点作为闭合折线绘制，ABCD四个顶点，绘制AB、BC、CD、DA四条线。
 * int GL_TRIANGLES    //将传入的顶点作为单独的三角形绘制，ABCDEF绘制ABC,DEF两个三角形
 * int GL_TRIANGLE_FAN    //将传入的顶点作为扇面绘制，ABCDEF绘制ABC、ACD、ADE、AEF四个三角形
 * int GL_TRIANGLE_STRIP   //将传入的顶点作为三角条带绘制，ABCDEF绘制ABC,BCD,CDE,DEF四个三角形
 *
 * 向量
 * vec2	包含了两个 float 的向量	ivec4	包含四个 int 的向量
 * vec3	包含了3个 float 的向量	bvec2	包含两个 bool 的向量
 * vec4	包含了4个 float 的向量	bvec3	包含三个 bool 的向量
 * ivec2 包含了两个 int 的向量	bvec4	包含四个 bool 的向量
 * ivec3 包含三个 int 的向量
 *
 * 矩阵
 * mat2	-> 2 x 2 的浮点数矩阵
 * mat3	-> 3 x 3 的浮点数矩阵
 * mat4	-> 4 x 4 的浮点数矩阵
 *
 * 采样器
 * sampler2D	用于访问二维纹理
 * sampler3D	用于访问三维纹理
 * samplerCube	包含四个 int 的向量
 *
 * 结构体
 * struct info{
 *  vec3 color;
 *  vec3 position;
 *  vec2 textureCoor;
 * }
 */
public class OpenGLESActivity extends AppCompatActivity {
    private static final String TAG = "OpenGLESActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gles);

        findViewById(R.id.bn1).setOnClickListener(view -> {
            Intent simpleIntent = new Intent(OpenGLESActivity.this, SimpleExampleActivity.class);
            startActivity(simpleIntent);
        });


        findViewById(R.id.bn2).setOnClickListener(view -> {
            Intent shapeSquareIntent = new Intent(OpenGLESActivity.this, ShapeSquareActivity.class);
            startActivity(shapeSquareIntent);
        });

        findViewById(R.id.bn3).setOnClickListener(view -> {
            Intent cubeIntent = new Intent(OpenGLESActivity.this, CubeActivity.class);
            startActivity(cubeIntent);
        });

        findViewById(R.id.bn4).setOnClickListener(view -> {
            Intent textureIntent = new Intent(OpenGLESActivity.this, TextureActivity.class);
            startActivity(textureIntent);
        });

        findViewById(R.id.bn5).setOnClickListener(view -> {
            Intent glInfoIntent = new Intent(OpenGLESActivity.this, GLInfoActivity.class);
            startActivity(glInfoIntent);
        });
    }
}
