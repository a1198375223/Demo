package com.example.opengles.gles;

public class OffscreenSurface extends EglSurfaceBase {

    public OffscreenSurface(EGLCore eglCore, int width, int height) {
        super(eglCore);
        createOffscreenSurface(width, height);
    }

    public void release() {
        releaseEglSurface();
    }
}
