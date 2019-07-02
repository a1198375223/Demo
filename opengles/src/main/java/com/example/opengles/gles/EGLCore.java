package com.example.opengles.gles;


import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.util.Log;
import android.view.Surface;

/**
 * EGL的主要功能 (屏幕渲染)
 * 1. 和本地窗口系统通讯
 * 2. 查询可用的配置
 * 3. 创建OpenGL ES可用的绘图表面
 * 4. 同步不同类别的API之间的渲染, 比如在OpenGL ES和OpenVG之间同步, 或者在OpenGL和本地窗口的绘图命令之间
 * 5. 管理"渲染资源", 比如纹理映射
 *
 *
 * EGL包括Display  Surface Context
 * Display(EGLDisplay): 是对实际显示设备的抽象
 * Surface(EGLSurface): 是对用来存储图像的内存区域FrameBuffer的抽象, 包括Color Buffer, Stencil Buffer, Depth Buffer
 * Context(EGLContext): 存储OpenGL ES绘图的一些状态信息
 *
 * 初始化EGL过程
 * Display -> Config -> Surface
 * ^
 * |
 * Context
 * |
 * v
 * Application -> OpenGL Command
 *
 * 1. 使用EGL的第一步就是初始化一个EGLDisplay -------> EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY); // 创建一个diaplay
 * -------> EGL14.eglInitialize() // 初始化display
 *
 * 2. 挑选Config   EGL14.eglChooseConfig()
 * 3. 创建Context  EGL14.eglCreateContext()
 */
public class EGLCore {
    private static final String TAG = "EGLCore";
    // 使用这个flag来请求GLES3, 如果无法请求成功则使用GLES2, 如果没有这个标志则直接使用GLES2
    public static final int FLAG_TRY_GLES3 = 0x02;
    // surface是必须要是可渲染的
    public static final int FLAG_RECORDABLE = 0x01;
    public static final int EGL_RECORDABLE_ANDROID = 0x3142;
    private int mGlVersion = -1;


    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLConfig mEGLConfig = null;


    public EGLCore() {
        this(null, 0);
    }

    public EGLCore(EGLContext sharedContext, int flags) {
        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            Log.e(TAG, "EGL already set up");
            throw new RuntimeException("EGL already set up");
        }

        if (sharedContext == null) {
            sharedContext = EGL14.EGL_NO_CONTEXT;
        }

        // 获取display
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        }

        int[] version = new int[2];
        // 初始化display
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            Log.e(TAG, "Error to initialize EGL14");
            mEGLDisplay = null;
            throw new RuntimeException("unable to initialize EGL14");
        }
        Log.d(TAG, "create display version (" + version[0] + "." + version[1] + ")");


        // 构建Config
        if ((flags & FLAG_TRY_GLES3) != 0) {
            Log.d(TAG, "Trying GLES 3");
            EGLConfig config = getConfig(flags, 3);
            if (config != null) {
                int[] attrib3_list = {
                        EGL14.EGL_CONTEXT_CLIENT_VERSION, 3,
                        EGL14.EGL_NONE
                };
                /*EGLDisplay dpy,--------------------> display
                  EGLConfig config,------------------> config
                  EGLContext share_context,----------> share context
                  int[] attrib_list,-----------------> 属性list
                  int offset-------------------------> 偏移量*/
                EGLContext context = EGL14.eglCreateContext(mEGLDisplay, config, sharedContext, attrib3_list, 0);

                if (EGL14.eglGetError() == EGL14.EGL_SUCCESS) {
                    Log.d(TAG, "Got GLES 3 config");
                    mEGLConfig = config;
                    mEGLContext = context;
                    mGlVersion = 3;
                }
            }
        }

        if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
            Log.d(TAG, "Trying GLES 2");
            EGLConfig config = getConfig(flags, 2);
            if (config == null) {
                throw new RuntimeException("Unable to find a suitable EGLConfig");
            }

            int[] attrib2_list = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };

            EGLContext context = EGL14.eglCreateContext(mEGLDisplay, config, sharedContext, attrib2_list, 0);
            if (EGL14.eglGetError() == EGL14.EGL_SUCCESS) {
                mEGLConfig = config;
                mEGLContext = context;
                mGlVersion = 2;
            }
        }
        int[] values = new int[1];
        EGL14.eglQueryContext(mEGLDisplay, mEGLContext, EGL14.EGL_CONTEXT_CLIENT_VERSION, values, 0);
        Log.d(TAG, "EGLContext created, client version " + values[0]);
    }


    /**
     * 寻找合适的config
     * @param flags 标志
     * @param version 2 or 3
     * @return 返回选择的EGLConfig
     */
    private EGLConfig getConfig(int flags, int version) {
        int renderableType = EGL14.EGL_OPENGL_ES2_BIT;
        if (version >= 3) {
            renderableType |= EGLExt.EGL_OPENGL_ES3_BIT_KHR;
        }

        int[] attribList = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, renderableType,
                EGL14.EGL_NONE, 0,
                EGL14.EGL_NONE
        };

        if ((flags & FLAG_RECORDABLE) != 0) {
            attribList[attribList.length - 3] = EGL_RECORDABLE_ANDROID;
            attribList[attribList.length - 2] = 1;
        }

        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        /*EGLDisplay dpy, ----------------> display
        int[] attrib_list,----------------> 属性list 以EGL_NONE结束的参数数组 id,value依次存放
        int attrib_listOffset,------------> 属性偏移量
        EGLConfig[] configs,--------------> 返回的EGLConfig数组
        int configsOffset,----------------> 数组起始偏移量
        int config_size,------------------> 请求的数量
        int[] num_config,-----------------> config的个数
        int num_configOffset--------------> 偏移量       */
        if (!EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length, numConfigs, 0)) {
            Log.e(TAG, "unable to find RGB888 / " + version + " EGLConfig");
            return null;
        }
        return configs[0];
    }


    // 创建surface
    public EGLSurface createWindowSurface(Object surface) {
        if (!(surface instanceof Surface) && !(surface instanceof SurfaceTexture)) {
            throw new RuntimeException("invalid surface: " + surface);
        }

        int[] surfaceAttribs = {
                EGL14.EGL_NONE
        };

        /*EGLDisplay dpy,-----------------------> display
        EGLConfig config,-----------------------> config
        Object win,-----------------------------> surface对象
        int[] attrib_list,----------------------> 属性list
        int offset------------------------------> 偏移量*/
        EGLSurface eglSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, surface, surfaceAttribs, 0);
        checkEglError("eglCreateWindowSurface");
        if (eglSurface == null) {
            throw new RuntimeException("surface was null");
        }
        return eglSurface;
    }

    private void checkEglError(String msg) {
        int error;
        if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
            throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
        }
    }


    /**
     * 释放资源
     */
    public void release() {
        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(mEGLDisplay);
        }

        mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        mEGLContext = EGL14.EGL_NO_CONTEXT;
        mEGLConfig = null;
    }

    // 销毁指定的surface, 实际上这个surface如果还在context中的话是不会被销毁的
    public void releaseSurface(EGLSurface eglSurface) {
        EGL14.eglDestroySurface(mEGLDisplay, eglSurface);
    }


    // 创建一个离屏surface
    public EGLSurface createOffScreenSurface(int width, int height) {
        int[] surfaceAttribs = {
                EGL14.EGL_WIDTH, width,
                EGL14.EGL_HEIGHT, height,
                EGL14.EGL_NONE
        };

        /*EGLDisplay dpy,------------------> display
        EGLConfig config,------------------> config
        int[] attrib_list,-----------------> 属性list
        int offset-------------------------> 偏移量*/
        EGLSurface eglSurface = EGL14.eglCreatePbufferSurface(mEGLDisplay, mEGLConfig, surfaceAttribs, 0);
        checkEglError("eglCreatePbufferSurface");
        if (eglSurface == null) {
            throw new RuntimeException("surface was null");
        }
        return eglSurface;
    }


    // 使上下文context可用
    public void makeCurrent(EGLSurface eglSurface) {
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            Log.d(TAG, "makeCurrent w/o display");
        }
        /*EGLDisplay dpy,------------------> display
        EGLSurface draw,-------------------> draw surface
        EGLSurface read,-------------------> read surface
        EGLContext ctx---------------------> context*/
        if (!EGL14.eglMakeCurrent(mEGLDisplay, eglSurface, eglSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    public void makeCurrent(EGLSurface drawSurface, EGLSurface readSurface) {
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            Log.d(TAG, "makeCurrent w/o display.");
        }
        if (!EGL14.eglMakeCurrent(mEGLDisplay, drawSurface, readSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent(draw, read) failed");
        }
    }

    public void makeNothingCurrent() {
        if (!EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)) {
            throw new RuntimeException("eglMakeCurrent failed.");
        }
    }

    // 应用程序通过OpenGL API进行绘制，一帧完成之后，调用进行显示
    public boolean swapBuffer(EGLSurface eglSurface) {
        return EGL14.eglSwapBuffers(mEGLDisplay, eglSurface);
    }

    // 将屏幕的当前时间发送给EGL
    public void setPresentationTime(EGLSurface eglSurface, long nsecs) {
        EGLExt.eglPresentationTimeANDROID(mEGLDisplay, eglSurface, nsecs);
    }

    // 判断surface是否是在显示
    public boolean isCurrent(EGLSurface surface) {
        return mEGLContext.equals(EGL14.eglGetCurrentContext()) &&
                surface.equals(EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW));
    }


    @Override
    protected void finalize() throws Throwable {
        // 在回收之前如果mEGLDisplay没有被释放将会出现内存泄漏的问题
        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            Log.w(TAG, "WARNING: EglCore was not explicitly released -- state may be leaked");
            release();
        }
        super.finalize();
    }


    public int getGlVersion() {
        return mGlVersion;
    }

    // 查找信息
    // EGL_VENDOR 、 EGL_VERSION 、 EGL_EXTENSIONS
    public String queryString(int what) {
        return EGL14.eglQueryString(mEGLDisplay, what);
    }


    public int querySurface(EGLSurface eglSurface, int what) {
        int[] value = new int[1];
        EGL14.eglQuerySurface(mEGLDisplay, eglSurface, what, value, 0);
        return value[0];
    }


    // 输出log
    public static void logCurrent(String msg) {
        EGLDisplay display;
        EGLContext context;
        EGLSurface surface;
        display = EGL14.eglGetCurrentDisplay();
        context = EGL14.eglGetCurrentContext();
        surface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
        Log.i(TAG, "Current EGl(" + msg + "): display=" + display + ", context=" + context + ", surface=" + surface);
    }
}
