package com.example.androidxdemo.activity.image.qr_code;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;

import com.example.commonlibrary.utils.QRCodeUtils;
import com.example.commonlibrary.view.ShortcutShareView;
import com.example.javalib.BindView;

public class QrCodeActivity extends AppCompatActivity {
    private static final String TAG = "QrCodeActivity";
    private ShortcutShareView mShareView;


    @BindView(R.id.show)
    Button button;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        mShareView = new ShortcutShareView(this);

        findViewById(R.id.show).setOnClickListener(view -> {
//            qrCode.saveInformationView(mShareView);
            mShareView.createShortcutShareView(BitmapFactory.decodeResource(getResources(), R.drawable.photo),
                    QRCodeUtils.createQRCodeBitmap("测试二维码", ((int) getResources().getDimension(R.dimen.view_dimen_203)), ((int) getResources().getDimension(R.dimen.view_dimen_203))), "长按识别二维码, 查看更多精彩内容这里是一句Slogan!");
            addContentView(mShareView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        });
    }
}
