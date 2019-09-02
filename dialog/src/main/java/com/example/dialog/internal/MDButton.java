package com.example.dialog.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;


import androidx.appcompat.text.AllCapsTransformationMethod;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.dialog.GravityEnum;
import com.example.dialog.R;
import com.example.dialog.util.DialogUtils;

/**
 * TextView不常用的方法
 *
 * 1. setTextIsSelectable(boolean selectable)       : TextView中的内容是否可以选中
 * 2. setSelectAllOnFocus(boolean selectAllOnFocus) : 是否在获取焦点的时候选中全部内容
 * 3. setHighlightColor(int color)                  : 被选中的文字的高亮的颜色
 * 4. setAutoLinkMask(int mask)                     : 用于声明TextView可识别的链接类型
 * 5. setLinkTextColor(int color)                   : 设置TextView实例中链接的颜色
 * 6. setLinksClickable(boolean whether)            : 链接是否可以点击
 * 7. setTextScaleX(float size)                     : 设置TextView实例中文字横向拉伸的倍数
 * 8. setMinLines(int minlines)                     : 设置TextView最小高度的制定行高度
 * 9. setMaxLines(int maxlines)                     : 设置TextView最大高度为指定行高度
 * 10.setLines(int lines)                           : 设置TextView精确高度为指定行高度
 * 11.setShadowLayer(float radius, float dx, float dy, int color) : 用于设置文字阴影
 * 12.setEllipsize(TextUtils.TruncateAt where)      : 用于文字省略样式设置
 * 13.setCompoundDrawablePadding(int pad)           : 设置TextView中文字与四周drawable的间距
 * 14.setCompoundDrawableTintList(ColorStateList tint) : 设置TextView四周drawable的着色
 * 15.setLineSpacing(float add, float mult)         : 设置多行TextView中单行高度
 * 16.setLetterSpacing(float letterSpacing)         : 设置文本的字符间距
 * 17.setAllCaps(boolean allCaps)                   : 设置TextView中的字符是否全部显示为大写形式
 * 18.setTransformationMethod(TransformationMethod method) : 设置TextView展示内容与实际内容之间的转换规则
 */
public class MDButton extends AppCompatTextView {

    private boolean mStacked = false;
    private GravityEnum mStackedGravity;

    private int mStackedEndPadding;
    private Drawable mStackedBackground;
    private Drawable mDefaultBackground;

    public MDButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MDButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mStackedEndPadding = context.getResources().getDimensionPixelSize(R.dimen.md_dialog_frame_margin);
        mStackedGravity = GravityEnum.END;
    }

    /**
     * Set if the button should be displayed in stacked mode.
     * This should only be called from MDRootLayout's onMeasure, and we must be measured
     * after calling this.
     */
    /* package */ void setStacked(boolean stacked, boolean force) {
        if (mStacked != stacked || force) {

            // 设置布局的对齐规则
            setGravity(stacked ? (Gravity.CENTER_VERTICAL | mStackedGravity.getGravityInt()) : Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // 调整字体的对齐规则
                setTextAlignment(stacked ? mStackedGravity.getTextAlignment() : TEXT_ALIGNMENT_CENTER);
            }

            // 设置背景
            DialogUtils.setBackgroundCompat(this, stacked ? mStackedBackground : mDefaultBackground);
            if (stacked) {
                setPadding(mStackedEndPadding, getPaddingTop(), mStackedEndPadding, getPaddingBottom());
            } /* Else the padding was properly reset by the drawable */

            mStacked = stacked;
        }
    }

    public void setStackedGravity(GravityEnum gravity) {
        mStackedGravity = gravity;
    }

    public void setStackedSelector(Drawable d) {
        mStackedBackground = d;
        if (mStacked)
            setStacked(true, true);
    }

    public void setDefaultSelector(Drawable d) {
        mDefaultBackground = d;
        if (!mStacked)
            setStacked(false, true);
    }

    /**
     * 设置是否所有的字符都变成大写模式
     * @param allCaps 是否要变成大写
     */
    @SuppressLint("RestrictedApi")
    public void setAllCapsCompat(boolean allCaps) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setAllCaps(allCaps);
        } else {
            if (allCaps)
                setTransformationMethod(new AllCapsTransformationMethod(getContext()));
            else
                setTransformationMethod(null);
        }
    }
}