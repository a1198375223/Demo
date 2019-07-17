package com.example.commonlibrary.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.commonlibrary.R;
import com.example.javalib.BindView;

public class TestAnnotationActivity extends Activity {

//    @BindView(R.id.tv)
//    @BindView(R.id.tv)
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_annotation);
    }
}
