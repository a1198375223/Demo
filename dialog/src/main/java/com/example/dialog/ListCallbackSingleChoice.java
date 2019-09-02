package com.example.dialog;

import android.view.View;

public interface ListCallbackSingleChoice {
    /**
     * Radio button单选, if the alwaysCallSingleChoice() option is used.
     *
     * @param dialog dialog
     * @param itemView 选择的item的view
     * @param which 选择的item对应的索引
     * @param text 选择的item的文本内容
     */
    boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text);
}
