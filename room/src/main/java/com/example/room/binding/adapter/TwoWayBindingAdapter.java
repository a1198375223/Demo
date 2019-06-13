package com.example.room.binding.adapter;

import android.widget.EditText;

import androidx.databinding.BindingAdapter;

public class TwoWayBindingAdapter {

    @BindingAdapter("numberOfSets")
    public static void setNumberOfSets(EditText editText, String value) {
        editText.setText(value);
    }

}
