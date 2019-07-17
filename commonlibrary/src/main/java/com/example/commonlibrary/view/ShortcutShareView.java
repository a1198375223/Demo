package com.example.commonlibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commonlibrary.R;

public class ShortcutShareView extends RelativeLayout {
    private static final String TAG = "ShortcutShareView";
    private ImageView mShortcutImage;
    private ImageView mQRCodeImage;
    private TextView mLogoTv;

    public ShortcutShareView(Context context) {
        this(context, null);
    }

    public ShortcutShareView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShortcutShareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_shortcut_view, this);

        mShortcutImage = findViewById(R.id.shortcut_image);
        mQRCodeImage = findViewById(R.id.qr_code_image);
        mLogoTv = findViewById(R.id.logo_text);
    }

    /**
     * 返回一个该view的截图
     * @param shortcut 屏幕截图图片
     * @param qrCode 生成二维码图片
     * @param logo logo文字
     */
    public void createShortcutShareView(Bitmap shortcut, Bitmap qrCode, String logo) {
        mShortcutImage.setImageBitmap(shortcut);
        mQRCodeImage.setImageBitmap(qrCode);
        mLogoTv.setText(logo);
    }
}
