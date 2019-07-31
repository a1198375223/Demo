package com.example.media.gsy.common;

import com.example.media.gsy.render.GSYVideoGLViewBaseRender;

/**
 * GL渲染错误
 */
public interface GSYVideoGLRenderErrorListener {
    /**
     *
     * @param render
     * @param Error 错误文本
     * @param code 错误代码
     * @param byChangedRenderError 错误是因为切换effect导致的
     */
    void onError(GSYVideoGLViewBaseRender render, String Error, int code, boolean byChangedRenderError);
}
