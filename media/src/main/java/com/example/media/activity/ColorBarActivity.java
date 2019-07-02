package com.example.media.activity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;

public class ColorBarActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "ColorBarActivity";
    private SurfaceView mSurfaceView;

    private static final String[] COLOR_NAMES = {
            "黑色", "红色", "绿色", "黄色", "蓝色", "品红", "青色", "白色"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_bar);
        mSurfaceView = findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(this);
        mSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // 在这个方法中启动绘制线程
        Log.d(TAG, "surfaceCreated: ");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
        // surface尺寸发生改变的时候调用
        Log.d(TAG, "surfaceChanged: format=" + format + " width=" + width + " height=" + height);
        Surface surface = holder.getSurface();
        drawColorBars(surface);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // surface被销毁的时候调用
        Log.d(TAG, "surfaceDestroyed: ");
    }


    private void drawColorBars(Surface surface) {
        Canvas canvas = surface.lockCanvas(null);

        try {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            int least = Math.min(width, height);

            Log.d(TAG, "Drawing color bars at " + width + "x" + height);

            Paint textPaint = new Paint();
            Typeface typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
            textPaint.setTypeface(typeface);
            textPaint.setTextSize((float) least / 20f);
            textPaint.setAntiAlias(true);


            Paint rectPaint = new Paint();
            for (int i = 0; i < 8; i++) {
                int color = 0xff000000;
                if ((i & 0x01) != 0) {
                    color |= 0x00ff0000;
                }

                if ((i & 0x02) != 0) {
                    color |= 0x0000ff00;
                }

                if ((i & 0x04) != 0) {
                    color |= 0x000000ff;
                }

                rectPaint.setColor(color);

                float sliceWidth = (float) width / 8f;
                canvas.drawRect(sliceWidth * i, 0, sliceWidth * (i + 1), height, rectPaint);
            }

            rectPaint.setColor(0x80808080);
            float sliceHeight = (float) height / 8f;
            int posn = 6;
            canvas.drawRect(0, sliceHeight * posn, width, sliceHeight * (posn + 1), rectPaint);
            for (int i = 0; i < 8; i++) {
                drawOutlineText(canvas, textPaint, COLOR_NAMES[i], ((float) width / 8f) * i + 4, (((float) height) / 8f) * ((i & 1) + 1));
            }
        } finally {
            surface.unlockCanvasAndPost(canvas);
        }
    }

    private static void drawOutlineText(Canvas canvas, Paint textPaint, String str, float x, float y) {
        textPaint.setColor(0xff000000);
        canvas.drawText(str, x - 1, y, textPaint);
        canvas.drawText(str, x + 1, y, textPaint);
        canvas.drawText(str, x, y - 1, textPaint);
        canvas.drawText(str, x, y + 1, textPaint);
        canvas.drawText(str, x - 0.7f, y - 0.7f, textPaint);
        canvas.drawText(str, x + 0.7f, y - 0.7f, textPaint);
        canvas.drawText(str, x - 0.7f, y + 0.f, textPaint);
        canvas.drawText(str, x + 0.7f, y + 0.7f, textPaint);
        textPaint.setColor(0xffffffff);
        canvas.drawText(str, x, y, textPaint);
    }
}
