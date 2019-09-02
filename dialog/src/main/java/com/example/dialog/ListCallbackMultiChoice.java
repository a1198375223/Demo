package com.example.dialog;


public interface ListCallbackMultiChoice {
    /**
     * Return true to allow the check box to be checked, if the alwaysCallSingleChoice() option is used.
     * @param dialog dialog
     * @param which 选择的item对应的索引数组
     * @param text 选择的item的文本内容数组
     * @return True 允许checkbox被选中
     */
    boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text);
}
