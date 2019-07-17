package com.example.androidxdemo.activity.image.water_mark;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.PictureSynthesisUtils;

public class WatchMarkActivity extends AppCompatActivity {
    private static final String TAG = "WatchMarkActivity";

    private ImageView originImage;
    private ImageView waterMarkImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_mark);

        findViewById(R.id.add_water_mark).setOnClickListener(view -> {
            waterMarkImage.setImageBitmap(PictureSynthesisUtils.addWaterMarkRightBottom(originImage.getDrawable(), "测试水印"));
//            waterMarkImage.setImageBitmap(PictureSynthesisUtils.addWaterMarkLeftTop(originImage.getDrawable(), "测试水印"));
//            waterMarkImage.setImageBitmap(PictureSynthesisUtils.addWaterMarkLeftBottom(originImage.getDrawable(), BitmapFactory.decodeResource(getResources(), R.drawable.down)));
//            waterMarkImage.setImageBitmap(PictureSynthesisUtils.addWaterMarkRightTop(originImage.getDrawable(), BitmapFactory.decodeResource(getResources(), R.drawable.down)));
        });


        originImage = findViewById(R.id.origin_image);
        waterMarkImage = findViewById(R.id.water_mark_image);

        originImage.setImageResource(R.drawable.photo);
    }
}
