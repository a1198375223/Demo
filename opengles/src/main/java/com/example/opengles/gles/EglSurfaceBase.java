package com.example.opengles.gles;

import android.graphics.Bitmap;
import android.opengl.EGL14;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EglSurfaceBase {
    private static final String TAG = "EglSurfaceBase";

    protected EGLCore mEglCore;

    private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;
    private int mWidth = -1;
    private int mHeight = -1;

    public EglSurfaceBase(EGLCore eglCore) {
        mEglCore = eglCore;
    }


    public void createWindowSurface(Object surface) {
        if (mEGLSurface != EGL14.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }

        mEGLSurface = mEglCore.createWindowSurface(surface);
    }

    public void createOffscreenSurface(int width, int height) {
        if (mEGLSurface != EGL14.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }

        mEGLSurface = mEglCore.createOffScreenSurface(width, height);
        mWidth = width;
        mHeight = height;
    }


    public int getHeight() {
        if (mHeight < 0) {
            return mEglCore.querySurface(mEGLSurface, EGL14.EGL_HEIGHT);
        } else  {
            return mHeight;
        }
    }


    public int getWidth() {
        if (mWidth < 0) {
            return mEglCore.querySurface(mEGLSurface, EGL14.EGL_WIDTH);
        } else {
            return mWidth;
        }
    }


    public void releaseEglSurface() {
        mEglCore.releaseSurface(mEGLSurface);
        mEGLSurface = EGL14.EGL_NO_SURFACE;
        mWidth = mHeight = -1;
    }


    public void makeCurrent() {
        mEglCore.makeCurrent(mEGLSurface);
    }


    public void makeCurrentReadFrom(EglSurfaceBase readSurface) {
        mEglCore.makeCurrent(mEGLSurface, readSurface.mEGLSurface);
    }

    public boolean swapBuffers() {
        boolean result = mEglCore.swapBuffer(mEGLSurface);
        if (!result) {
            Log.d(TAG, "WARNING: swapBuffers() failed");
        }
        return result;
    }

    public void setPresentationTime(long nsecs) {
        mEglCore.setPresentationTime(mEGLSurface, nsecs);
    }


    public void saveFrame(File file) throws IOException {
        if (!mEglCore.isCurrent(mEGLSurface)) {
            throw new RuntimeException("Expected EGL context/surface is not current");
        }

        String fileName = file.toString();
        int width = getWidth();
        int height = getHeight();
        ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);
        GLESUtils.checkGlError("glReadPixels");
        buf.rewind();

        BufferedOutputStream bos = null;
        bos = new BufferedOutputStream(new FileOutputStream(fileName));
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buf);
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, bos);
        bitmap.recycle();
        bos.close();

        Log.d(TAG, "Saved " + width + "x" + height + " frame as '" + fileName + "'");
    }

}
