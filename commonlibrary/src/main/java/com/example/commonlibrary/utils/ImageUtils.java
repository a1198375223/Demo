package com.example.commonlibrary.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.PostProcessor;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.commonlibrary.utils.AppUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 使用这个工具类来处理bitmap加载和webp图片还有gif图片的加载
 * 主要是ImageDecoder
 */
@RequiresApi(api = Build.VERSION_CODES.P)
public class ImageUtils {

    private static ImageDecoder.Source createSource(File file) {
        return ImageDecoder.createSource(file);
    }

    private static ImageDecoder.Source createSource(ByteBuffer buffer) {
        return ImageDecoder.createSource(buffer);
    }

    private static ImageDecoder.Source createSource(@DrawableRes int resId) {
        return ImageDecoder.createSource(AppUtils.app().getResources(), resId);
    }

    private static ImageDecoder.Source createSource(Uri uri) {
        return ImageDecoder.createSource(AppUtils.app().getContentResolver(), uri);
    }

    private static ImageDecoder.Source createSource(String assetFile) {
        return ImageDecoder.createSource(AppUtils.app().getAssets(), assetFile);
    }

    /**
     * 只是普通的加载bitmap
     */
    public static Bitmap loadBitmap(File file, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return ImageDecoder.decodeBitmap(createSource(file), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap loadBitmap(ByteBuffer buffer, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return ImageDecoder.decodeBitmap(createSource(buffer), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap loadBitmap(@DrawableRes int resId, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return ImageDecoder.decodeBitmap(createSource(resId), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap loadBitmap(Uri uri, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return ImageDecoder.decodeBitmap(createSource(uri), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap loadBitmap(String assetFile, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return ImageDecoder.decodeBitmap(createSource(assetFile), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Bitmap loadBitmap(File file) {
        try {
            return ImageDecoder.decodeBitmap(createSource(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap loadBitmap(ByteBuffer buffer) {
        try {
            return ImageDecoder.decodeBitmap(createSource(buffer));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap loadBitmap(@DrawableRes int resId) {
        try {
            return ImageDecoder.decodeBitmap(createSource(resId));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap loadBitmap(Uri uri) {
        try {
            return ImageDecoder.decodeBitmap(createSource(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap loadBitmap(String assetFile) {
        try {
            return ImageDecoder.decodeBitmap(createSource(assetFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 只是普通的加载drawable
     */
    public static Drawable loadDrawable(File file, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return ImageDecoder.decodeDrawable(createSource(file), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable loadDrawable(ByteBuffer buffer, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return ImageDecoder.decodeDrawable(createSource(buffer), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable loadDrawable(@DrawableRes int resId, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return ImageDecoder.decodeDrawable(createSource(resId), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable loadDrawable(Uri uri, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return ImageDecoder.decodeDrawable(createSource(uri), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable loadDrawable(String assetFile, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return ImageDecoder.decodeDrawable(createSource(assetFile), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable loadDrawable(File file) {
        try {
            return ImageDecoder.decodeDrawable(createSource(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable loadDrawable(ByteBuffer buffer) {
        try {
            return ImageDecoder.decodeDrawable(createSource(buffer));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable loadDrawable(@DrawableRes int resId) {
        try {
            return ImageDecoder.decodeDrawable(createSource(resId));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable loadDrawable(Uri uri) {
        try {
            return ImageDecoder.decodeDrawable(createSource(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable loadDrawable(String assetFile) {
        try {
            return ImageDecoder.decodeDrawable(createSource(assetFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 只是普通的加载webp和gif动图
     * 记得在ImageView.setImageDrawable(drawable);之后要开启动画
     * drawable.start();
     */
    public static AnimatedImageDrawable loadGifDrawable(File file, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return (AnimatedImageDrawable) ImageDecoder.decodeDrawable(createSource(file), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AnimatedImageDrawable loadGifDrawable(ByteBuffer buffer, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return (AnimatedImageDrawable) ImageDecoder.decodeDrawable(createSource(buffer), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AnimatedImageDrawable loadGifDrawable(@DrawableRes int resId, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return (AnimatedImageDrawable) ImageDecoder.decodeDrawable(createSource(resId), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AnimatedImageDrawable loadGifDrawable(Uri uri, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return (AnimatedImageDrawable) ImageDecoder.decodeDrawable(createSource(uri), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AnimatedImageDrawable loadGifDrawable(String assetFile, @NonNull ImageDecoder.OnHeaderDecodedListener listener) {
        try {
            return (AnimatedImageDrawable) ImageDecoder.decodeDrawable(createSource(assetFile), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AnimatedImageDrawable loadGifDrawable(File file) {
        try {
            return (AnimatedImageDrawable) ImageDecoder.decodeDrawable(createSource(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AnimatedImageDrawable loadGifDrawable(ByteBuffer buffer) {
        try {
            return (AnimatedImageDrawable) ImageDecoder.decodeDrawable(createSource(buffer));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AnimatedImageDrawable loadGifDrawable(@DrawableRes int resId) {
        try {
            return (AnimatedImageDrawable) ImageDecoder.decodeDrawable(createSource(resId));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AnimatedImageDrawable loadGifDrawable(Uri uri) {
        try {
            return (AnimatedImageDrawable) ImageDecoder.decodeDrawable(createSource(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AnimatedImageDrawable loadGifDrawable(String assetFile) {
        try {
            return (AnimatedImageDrawable) ImageDecoder.decodeDrawable(createSource(assetFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 使用这个类来构造一个OnHeaderDecodedListener
     * 注意resize和crop是不能同时触发的
     */
    public static class OperatingBuilder {
        private int width = -1;
        private int height = -1;
        private int cropWidth = -1;
        private int cropHeight = -1;
        private float ratio = 0;
        private boolean isResize = false;
        private boolean isRoundCorner = false;
        private boolean isCircle = false;
        private float radius = -1f;
        private float corner = -1f;
        private boolean isCrop = false;
        private boolean ifSavePartly = false;


        public OperatingBuilder isResize(boolean isResize) {
            this.isResize = true;
            return this;
        }

        public OperatingBuilder isCrop(boolean isCrop) {
            this.isCrop = true;
            return this;
        }

        public OperatingBuilder setWidth(int width) {
            this.width = width;
            this.isResize = true;
            return this;
        }

        public OperatingBuilder setHeight(int height) {
            this.height = height;
            this.isResize = true;
            return this;
        }

        public OperatingBuilder setCorner(float radius) {
            this.corner = radius;
            this.isRoundCorner = true;
            return this;
        }

        public OperatingBuilder setCropWidth(int cropWidth) {
            this.cropWidth = cropWidth;
            this.isCrop = true;
            return this;
        }

        public OperatingBuilder setCropHeight(int cropHeight) {
            this.cropHeight = cropHeight;
            this.isCrop = true;
            return this;
        }

        public OperatingBuilder setRatio(float ratio) {
            this.ratio = ratio;
            this.isCrop = true;
            return this;
        }

        public OperatingBuilder setCircle(boolean circle) {
            this.isCircle = circle;
            return this;
        }

        public OperatingBuilder setRadius(float radius) {
            this.radius = radius;
            isCircle = true;
            return this;
        }

        /**
         * 这个方法用来设置是否保存解码过程出现失败现象,是否保存正常解码的图像进行显示
         */
        public OperatingBuilder savePartly(boolean ifSavePartly) {
            this.ifSavePartly = ifSavePartly;
            return this;
        }


        public ImageDecoder.OnHeaderDecodedListener build() {
            return new ImageDecoder.OnHeaderDecodedListener(){
                /**
                 * 对图片解码过程的操作
                 * @param decoder 允许对解码过程进行设置
                 * @param info 未解码的图片信息
                 * @param source source对象
                 */
                @SuppressLint("NewApi")
                @Override
                public void onHeaderDecoded(@NonNull ImageDecoder decoder, @NonNull ImageDecoder.ImageInfo info, @NonNull ImageDecoder.Source source) {
                    decoder.setOnPartialImageListener(new ImageDecoder.OnPartialImageListener() {
                        @Override
                        public boolean onPartialImage(@NonNull ImageDecoder.DecodeException e) {
                            if (e.getError() == ImageDecoder.DecodeException.SOURCE_EXCEPTION) { // 在读source资源的时候出错
                                // todo
                            } else if (e.getError() == ImageDecoder.DecodeException.SOURCE_INCOMPLETE) { // 解码数据不完成, 图片数据不完整
                                // todo
                            } else if (e.getError() == ImageDecoder.DecodeException.SOURCE_MALFORMED_DATA) { // 解码数据包含错误信息
                                // todo
                            }
                            return ifSavePartly;
                        }
                    });

                    int imageHeight = info.getSize().getHeight();
                    int imageWidth = info.getSize().getWidth();
                    // 进行resize
                    if (isResize) {
                        int resizeWidth = width;
                        int resizeHeight = height;
                        if (width == -1 || width >= imageWidth) {
                            resizeWidth = imageWidth;
                        }
                        if (height == -1 || height >= imageHeight) {
                            resizeHeight = imageHeight;
                        }
                        width = resizeWidth;
                        height = resizeHeight;
                        decoder.setTargetSize(width, height);
                    } else if (isCrop) { // 进行裁切 resize和crop不能同时执行
                        float centerX = (float) imageWidth / 2f;
                        float centerY = (float) imageHeight / 2f;
                        float cw = 0;
                        float ch = 0;
                        // 优先按比例进行裁切
                        if (ratio != 0) {
                            // 如果用户对cropWidth和cropHeight都进行了设置, 则使用符合比例的值
                            if (cropWidth != -1 && cropHeight != -1) {
                                ch = cropHeight >= imageHeight ? imageHeight : cropHeight;
                                cw = cropWidth >= imageWidth ? imageWidth : cropWidth;
                            } else if (cropWidth == -1 && cropHeight == -1) { // 如果用户对两个值都没有进行设置, 则按比例进行裁切
                                cw = imageWidth;
                                ch = imageHeight;
                            } else { // 如果用户对两个中的其中一个进行赋值了
                                if (cropWidth == -1) {
                                    ch = cropHeight >= imageHeight ? imageHeight : cropHeight;
                                    cw = ch / ratio;
                                    cw = cw > imageWidth ? imageWidth : cw;
                                } else {
                                    cw = cropWidth >= imageWidth ? imageWidth : cropWidth;
                                    ch = cw * ratio;
                                    ch = ch > imageHeight ? imageHeight : ch;
                                }
                            }

                            if (ch / cw <= ratio) { // 这种情况默认是width大了, 需要对width进行重新赋值
                                cw = ch / ratio;
                            } else { // 这种情况默认是height大了, 需要对height进行重新赋值
                                ch = cw * ratio;
                            }
                        } else { // 如果没有设置比例就按用户设置的大小和图片的大小进行裁切
                            // 如果用户对cropWidth和cropHeight都进行了设置, 则使用符合比例的值
                            if (cropWidth != -1 && cropHeight != -1) {
                                ch = cropHeight >= imageHeight ? imageHeight : cropHeight;
                                cw = cropWidth >= imageWidth ? imageWidth : cropWidth;
                            } else { // 如果用户对两个中的其中一个进行赋值了
                                if (cropWidth == -1) {
                                    ch = cropHeight >= imageHeight ? imageHeight : cropHeight;
                                    cw = imageWidth;
                                } else {
                                    ch = imageHeight;
                                    cw = cropWidth >= imageWidth ? imageWidth : cropWidth;
                                }
                            }
                        }
                        width = (int) cw;
                        height = (int) ch;

                        // 进行裁切操作
                        decoder.setCrop(new Rect((int) (centerX - width / 2), (int) (centerY - height / 2),
                                (int) (centerX + width / 2), (int) (centerY + height / 2)));
                    }


                    if (!isResize && !isCrop) {
                        width = imageWidth;
                        height = imageHeight;
                    }

                    if (isCircle) { // 进行圆设置
                        final float centerX = width / 2f;
                        final float centerY = height / 2f;
                        final float r = radius == -1 ? (float) Math.min(width, height) / 2f : radius;
                        decoder.setPostProcessor(new PostProcessor() {
                            @Override
                            public int onPostProcess(@NonNull Canvas canvas) {
                                Path path = new Path();
                                path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
                                Paint paint = new Paint();
                                paint.setColor(Color.TRANSPARENT);
                                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                                paint.setAntiAlias(true);
                                path.addCircle(centerX, centerY, r, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                paint.reset();
                                path.reset();
                                return PixelFormat.TRANSLUCENT;
                            }
                        });
                    }else if (isRoundCorner) { // 进行圆角设置
                        decoder.setPostProcessor(new PostProcessor() {
                            @Override
                            public int onPostProcess(@NonNull Canvas canvas) {
                                Path path = new Path();
                                path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
                                Paint paint = new Paint();
                                paint.setColor(Color.TRANSPARENT);
                                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                                paint.setAntiAlias(true);
                                path.addRoundRect(0f, 0f, width, height, corner, corner, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                paint.reset();
                                path.reset();
                                return PixelFormat.TRANSLUCENT;
                            }
                        });
                    }
                }


            };
        }
    }
}
