package com.example.media.gsy.effect;

import android.opengl.GLSurfaceView;

import com.example.media.gsy.render.GSYVideoGLView;

public class BlackAndWhiteEffect implements GSYVideoGLView.ShaderInterface {
    /**
     * Initialize Effect
     */
    public BlackAndWhiteEffect() {
    }

    @Override
    public String getShader(GLSurfaceView mGlSurfaceView) {
        return "#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "varying vec2 vTextureCoord;\n"
                + "uniform samplerExternalOES sTexture;\n" + "void main() {\n"
                + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
                + "  float colorR = (color.r + color.g + color.b) / 3.0;\n"
                + "  float colorG = (color.r + color.g + color.b) / 3.0;\n"
                + "  float colorB = (color.r + color.g + color.b) / 3.0;\n"
                + "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);\n"
                + "}\n";

    }
}

