package com.example.dialog.other;

import android.view.WindowManager;

public class DialogException extends WindowManager.BadTokenException {
    public DialogException(@SuppressWarnings("SameParameterValue") String message) {
        super(message);
    }
}