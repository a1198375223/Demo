package com.example.androidxdemo.activity.image.qr_code;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.QRCodeUtils;
import com.example.commonlibrary.utils.Toasty;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class QRCodeTestActivity extends AppCompatActivity {
    private static final String TAG = "QRCodeTestActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_test);

        findViewById(R.id.create_qrcode).setOnClickListener(view -> {
            ((ImageView) findViewById(R.id.qrcode_image)).setImageBitmap(QRCodeUtils.createQRCodeBitmap("https://www.baidu.com", 600, 600, 0));
        });

        findViewById(R.id.recognized_qrcode).setOnClickListener(view -> {
            // 创建IntentIntegrator对象
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);
            // 设置被扫描的二维码图片是否可以保存在本地
            intentIntegrator.setBarcodeImageEnabled(true);
            // 设置扫码成功后是否有提示音 默认true
            intentIntegrator.setBeepEnabled(false);
            // 设置使用的摄像头, 0->后置摄像头(默认)  1->前置摄像头
            intentIntegrator.setCameraId(0);

            // 设置可以识别的种类
            // IntentIntegrator.ALL_CODE_TYPES      : 支持所有类型的码
            // IntentIntegrator.PRODUCT_CODE_TYPES  : 商品类型码
            // IntentIntegrator.ONE_D_CODE_TYPES    : 一维码
            // IntentIntegrator.QR_CODE             : 二维码
            // IntentIntegrator.DATA_MATRIX         : 数据矩阵
            // IntentIntegrator.PDF_417             :
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

            // 设置是否锁定方向, 默认是true
            // 如果要使false生效 需要在manifest中的CaptureActivity添加
            // tools:replace="screenOrientation"
            intentIntegrator.setOrientationLocked(true);

            // 设置二维码扫描界面的提示信息
            intentIntegrator.setPrompt("请将二维码置于取景框内扫描");

            // 设置超时时间, 超时之后会自动关闭扫码界面
//            intentIntegrator.setTimeout(5000);

            // 自定义扫描界面
            intentIntegrator.setCaptureActivity(MyCaptureActivity.class);
            // 开始扫描
            intentIntegrator.initiateScan();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toasty.showError("取消扫描");
            } else {
                ((TextView) findViewById(R.id.recognized_result)).setText("扫描结果为: " + result.getContents());
                if (result.getBarcodeImagePath() != null) {
                    ((TextView) findViewById(R.id.recognized_result)).append("\n保存的二维码path: " + result.getBarcodeImagePath());
                    FileInputStream fis = null;
                    try {
                         fis = new FileInputStream(result.getBarcodeImagePath());
                        ((ImageView) findViewById(R.id.recognized_and_save_qrcode)).setImageBitmap(BitmapFactory.decodeStream(fis));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                Toasty.showSuccess("扫描成功");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
