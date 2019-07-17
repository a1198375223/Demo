package com.example.androidxdemo.activity.image.qr_code;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.List;

public class MyViewFinderView extends ViewfinderView {
    private static final String TAG = "MyViewFinderView";

    /**
     * 边角线颜色
     */
    public int mLineColor = Color.WHITE;

    /**
     * "边角线长度/扫描边框长度"的占比 (比例越大，线越长)
     */
    public float mLineRate = 0.1F;

    /**
     * 边角线厚度 (建议使用dp)
     */
    public float mLineDepth =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());


    /**
     * 扫描线起始位置
     */
    public int mScanLinePosition = 0;

    /**
     * 扫描线厚度
     */
    public float mScanLineDepth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

    /**
     * 扫描线每次重绘的移动距离
     */
    public float mScanLineDy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());

    /**
     * 线性梯度
     */
    public LinearGradient mLinearGradient;

    /**
     * 线性梯度位置
     */
    public float[] mPositions = new float[]{0f, 0.5f, 1f};

    /**
     * 线性梯度各个位置对应的颜色值
     */
    public int[] mScanLineColor = new int[]{0x00FFFFFF, Color.WHITE, 0x00FFFFFF};




    public MyViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onDraw(Canvas canvas) {
        refreshSizes();
        if (framingRect == null || previewFramingRect == null) {
            return;
        }

        final Rect frame = framingRect;
        final Rect previewFrame = previewFramingRect;
        Log.d(TAG, "Framing Rect: " + framingRect);
        Log.d(TAG, "Preview Framing Rect: " + previewFramingRect);

        final int width = getWidth();
        final int height = getHeight();
        Log.d(TAG, "Size (" + width + "x" + height + ")");

        //绘制4个角
        paint.setColor(mLineColor); // 定义画笔的颜色
        canvas.drawRect(frame.left, frame.top, frame.left + frame.width() * mLineRate, frame.top + mLineDepth, paint);
        canvas.drawRect(frame.left, frame.top, frame.left + mLineDepth, frame.top + frame.height() * mLineRate, paint);

        canvas.drawRect(frame.right - frame.width() * mLineRate, frame.top, frame.right, frame.top + mLineDepth, paint);
        canvas.drawRect(frame.right - mLineDepth, frame.top, frame.right, frame.top + frame.height() * mLineRate, paint);

        canvas.drawRect(frame.left, frame.bottom - mLineDepth, frame.left + frame.width() * mLineRate, frame.bottom, paint);
        canvas.drawRect(frame.left, frame.bottom - frame.height() * mLineRate, frame.left + mLineDepth, frame.bottom, paint);

        canvas.drawRect(frame.right - frame.width() * mLineRate, frame.bottom - mLineDepth, frame.right, frame.bottom, paint);
        canvas.drawRect(frame.right - mLineDepth, frame.bottom - frame.height() * mLineRate, frame.right, frame.bottom, paint);


        // Draw the exterior (i.e. outside the framing rect) darkened
        // resultColor      : 69%黑色
        // maskColor        : 38%黑色
        // laserColor       : 100%红色 --> 激光
        // resultPointColor : 75%黄色
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {

            // Draw a red "laser scanner" line through the middle to show decoding is active
//            paint.setColor(laserColor);
//            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
//            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
//            final int middle = frame.height() / 2 + frame.top;
//            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

            mScanLinePosition += mScanLineDy;
            if(mScanLinePosition > frame.height()){
                mScanLinePosition = 0;
            }
            mLinearGradient = new LinearGradient(frame.left, frame.top + mScanLinePosition, frame.right, frame.top + mScanLinePosition, mScanLineColor, mPositions, Shader.TileMode.CLAMP);
            paint.setShader(mLinearGradient);
            canvas.drawRect(frame.left, frame.top + mScanLinePosition, frame.right, frame.top + mScanLinePosition + mScanLineDepth, paint);
            paint.setShader(null);

            final float scaleX = frame.width() / (float) previewFrame.width();
            final float scaleY = frame.height() / (float) previewFrame.height();

            final int frameLeft = frame.left;
            final int frameTop = frame.top;

            // draw the last possible result points
            if (!lastPossibleResultPoints.isEmpty()) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                float radius = POINT_SIZE / 2.0f;
                for (final ResultPoint point : lastPossibleResultPoints) {
                    canvas.drawCircle(
                            frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            radius, paint
                    );
                }
                lastPossibleResultPoints.clear();
            }

            // draw current possible result points
            if (!possibleResultPoints.isEmpty()) {
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                for (final ResultPoint point : possibleResultPoints) {
                    canvas.drawCircle(
                            frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            POINT_SIZE, paint
                    );
                }

                // swap and clear buffers
                final List<ResultPoint> temp = possibleResultPoints;
                possibleResultPoints = lastPossibleResultPoints;
                lastPossibleResultPoints = temp;
                possibleResultPoints.clear();
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }

    }
}
