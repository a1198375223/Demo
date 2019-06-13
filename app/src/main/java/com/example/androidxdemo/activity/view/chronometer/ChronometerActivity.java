package com.example.androidxdemo.activity.view.chronometer;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;

public class ChronometerActivity extends AppCompatActivity {
    private static final String TAG = "ChronometerActivity";

    private Button mStartBn, mStopBn, mClearBn, mChangeFormatBn, mGetCurrentTimeBn, mCountDownBn, mCountDownTimer;
    private Chronometer mChronometer;
    private TextView mCurrentTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chronometer);
        mStartBn = findViewById(R.id.start_bn);
        mStopBn = findViewById(R.id.stop_bn);
        mClearBn = findViewById(R.id.clear_bn);
        mChangeFormatBn = findViewById(R.id.change_format_bn);
        mGetCurrentTimeBn = findViewById(R.id.current_time_bn);
        mCountDownBn = findViewById(R.id.count_down_bn);
        mChronometer = findViewById(R.id.chronometer);
        mCountDownTimer = findViewById(R.id.count_down_timer_bn);
        mCurrentTv = findViewById(R.id.current_time_tv);


        mStartBn.setOnClickListener(view -> mChronometer.start());

        mStopBn.setOnClickListener(view -> mChronometer.stop());

        mClearBn.setOnClickListener(view -> mChronometer.setBase(SystemClock.elapsedRealtime()));

        mChangeFormatBn.setOnClickListener(view -> {
            int hour = (int) ((SystemClock.elapsedRealtime() - mChronometer.getBase()) / 1000 / 60);
            mChronometer.setFormat("0"+ hour +":%s");
//            mChronometer.setFormat("H:MM:SS");
            mChronometer.setBase(SystemClock.elapsedRealtime());
        });

        mGetCurrentTimeBn.setOnClickListener(view -> mCurrentTv.setText(mChronometer.getText()));

        mCountDownBn.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (!mChronometer.isCountDown()) {
                    mChronometer.stop();
                    mChronometer.setCountDown(true);
                    mChronometer.setBase(SystemClock.elapsedRealtime() + 60000);
                    mChronometer.start();
                }
            }
        });

        // 待检测是否会出现内存泄漏
        mCountDownTimer.setOnClickListener(view -> new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mCurrentTv.setText("剩余" + (millisUntilFinished / 1000) + "秒");
            }

            @Override
            public void onFinish() {
                mCurrentTv.setText("计时完成");
            }
        }.start());
    }
}
