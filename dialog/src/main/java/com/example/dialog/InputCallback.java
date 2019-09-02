package com.example.dialog;

import androidx.annotation.NonNull;

// 输入事件的回调
public interface InputCallback {
    void onInput(@NonNull MaterialDialog dialog, CharSequence input);
}