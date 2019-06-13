package com.example.commonlibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BitmapUtils {

    public static Bitmap blurBitmap(Bitmap bitmap, Context applicationContext) {
        // 1. 创建一个RenderScript对象
        RenderScript renderScript = RenderScript.create(applicationContext, RenderScript.ContextType.DEBUG);
        // 2. copy一个bitmap用来操作模糊
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        // 3. 创建Allocation
        Allocation inAlloc = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation outAlloc = Allocation.createTyped(renderScript, inAlloc.getType());
        // 4. 创建script
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        // 5. 设置模糊半径（最大25.0f）
        theIntrinsic.setRadius(10.f);
        // 6. 为script设置input
        theIntrinsic.setInput(inAlloc);
        // 7. 调用script分配给outAlloc
        theIntrinsic.forEach(outAlloc);
        // 8. 将outAlloc应用到bitmap中
        outAlloc.copyTo(output);
        // 9. 释放内存
        renderScript.finish();
        inAlloc.destroy();
        outAlloc.destroy();
        theIntrinsic.destroy();
        return output;
    }
}
