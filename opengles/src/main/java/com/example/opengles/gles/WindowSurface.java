package com.example.opengles.gles;

import android.graphics.SurfaceTexture;
import android.view.Surface;

public class WindowSurface extends EglSurfaceBase {

    private Surface mSurface;
    private boolean mReleaseSurface;

    public WindowSurface(EGLCore eglCore, Surface surface, boolean releaseSurface) {
        super(eglCore);
        createWindowSurface(surface);
        mSurface = surface;
        mReleaseSurface = releaseSurface;
    }

    public WindowSurface(EGLCore eglCore, SurfaceTexture surfaceTexture) {
        super(eglCore);
        createWindowSurface(surfaceTexture);
    }


    public void release() {
        releaseEglSurface();
        if (mSurface != null) {
            if (mReleaseSurface) {
                mSurface.release();
            }
            mSurface = null;
        }
    }


    public void recreate(EGLCore newEglCore) {
        if (mSurface == null) {
            throw new RuntimeException("not yet implemented for SurfaceTexture");
        }
        mEglCore = newEglCore;
        createWindowSurface(mSurface);
    }
}
