package com.example.androidxdemo.activity.image.image_decode;


import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.DisplayUtils;
import com.example.commonlibrary.utils.ImageUtils;


public class ImageDecoderActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        final ImageView imageView = findViewById(R.id.image);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            findViewById(R.id.button).setOnClickListener(v -> imageView.setImageBitmap(ImageUtils.loadBitmap(R.drawable.photo,
                    new ImageUtils.OperatingBuilder()
                            .setCorner(DisplayUtils.dip2px(1))
    //                                .setCropHeight(DisplayUtils.dip2px(100))
                            .setCropWidth(DisplayUtils.dip2px(200))
                            //.setWidth(DisplayUtils.dip2px(200))
                            //.setHeight(DisplayUtils.dip2px(100))
                            .setRatio(2f/4f)
                            .build())));
        }
    }
}
