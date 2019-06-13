package com.example.androidxdemo.activity.view.dot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;
import com.example.androidxdemo.view.dot_progress_view.LineView;


public class DotViewActivity extends AppCompatActivity {
    private static final String TAG = "DotViewActivity";

    private RelativeLayout mContainer;

    private LineView mLineView;
    private ImageView mDot;
    private ImageView mTransIv;
    private Button mTransBn1;
    private Button mTransBn2;
    private float lastX = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        mLineView = findViewById(R.id.line);
        mDot = findViewById(R.id.dot);
        mContainer = findViewById(R.id.container);
        mTransIv = findViewById(R.id.trans_iv);
        mTransBn1 = findViewById(R.id.trans_bn_1);
        mTransBn2 = findViewById(R.id.trans_bn_2);

        mLineView.post(() -> Log.d(TAG, "run: left=" + mLineView.getLeft() + "\nright=" + mLineView.getRight()
                + "\ntop=" + mLineView.getTop() + "\nbottom=" + mLineView.getBottom()
                + "\nwidth=" + mLineView.getMeasuredWidth() + "\nheight=" + mLineView.getMeasuredHeight()
                + "\nx=" + mLineView.getX() + "\ny=" + mLineView.getY()));


        mContainer.setOnTouchListener((v, event) -> {

            Log.d(TAG, "onTouch: X=" + event.getX() + " y=" + event.getY()
                    + "\nrawX=" + event.getRawX() + " rawY=" + event.getRawY());
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: {
                    Log.d(TAG, "onTouch: down");
                    lastX = event.getX();
                    //v.setTranslationX(v.getX() - lastX);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    Log.d(TAG, "onTouch: move distance=" + (event.getX() - lastX));
                    Log.d(TAG, "onCreate: container left=" + mContainer.getLeft() + " container right=" + mContainer.getRight()
                            + "\nx=" + event.getX() + " y=" + event.getY());
                    float x = event.getX();
                    if (x != lastX && event.getRawX() >= mContainer.getLeft() && event.getRawX() <= mContainer.getRight()) {
                        mDot.setTranslationX(x);
                        mLineView.updateLine(x);
                        lastX = x;
                    }
                    break;
                }
                case MotionEvent.ACTION_CANCEL: {
                    Log.d(TAG, "onTouch: cancel");
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    Log.d(TAG, "onTouch: up");
                    Log.d(TAG, "onTouch: progress=" + mLineView.getProgress());
                    break;
                }
            }
            return true;
        });

        mTransBn1.setOnClickListener(v -> mTransIv.setTranslationX(100f));

        mTransBn2.setOnClickListener(v -> {
            mTransIv.setTranslationX(100);
            mTransIv.setTranslationX(200);
        });
    }
}
