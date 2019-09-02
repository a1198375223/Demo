package com.example.dialog;

import androidx.annotation.NonNull;

// Action按钮的点击事件
public interface SingleButtonCallback {
    void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which);
}
