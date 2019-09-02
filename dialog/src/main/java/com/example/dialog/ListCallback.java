package com.example.dialog;

import android.view.View;

// 列表项的选择事件
public interface ListCallback {
    /**
     * @param dialog dialog
     * @param itemView 选择的item的view
     * @param which 选择的item对应的索引
     * @param text 选择的item的文本内容
     */
    void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text);
}
