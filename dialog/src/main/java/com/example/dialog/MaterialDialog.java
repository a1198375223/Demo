package com.example.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.core.content.res.ResourcesCompat;

import com.example.dialog.internal.MDButton;
import com.example.dialog.internal.MDRootLayout;
import com.example.dialog.other.DialogException;
import com.example.dialog.util.DialogUtils;
import com.example.dialog.util.TypefaceHelper;

import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

// 自定义一个dialog
public class MaterialDialog extends DialogBase implements View.OnClickListener, AdapterView.OnItemClickListener {
    protected Builder mBuilder;
    protected Handler mHandler;

    protected ListView listView;
    protected ImageView icon;
    protected TextView title;
    protected View titleFrame;
    protected FrameLayout customViewFrame;
    protected ProgressBar mProgress;
    protected TextView mProgressLabel;
    protected TextView mProgressMinMax;
    protected TextView content;
    protected EditText input;
    protected TextView inputMinMax;

    protected MDButton positiveButton;
    protected MDButton neutralButton;
    protected MDButton negativeButton;

    protected ListType listType;
    protected List<Integer> selectedIndicesList;

    protected MaterialDialog(Builder builder) {
        // 应用style
        super(builder.context, DialogInit.getTheme(builder));
        this.mBuilder = builder;
        this.mHandler = new Handler();

        // 加载布局
        LayoutInflater inflater = LayoutInflater.from(builder.context);
        view = (MDRootLayout) inflater.inflate(DialogInit.getInflateLayout(builder), null);
        // 初始化DialogInit
        DialogInit.init(this);

    }

    // 按钮的点击事件
    @Override
    public final void onClick(View v) {
        DialogAction tag = (DialogAction) v.getTag();
        switch (tag) {
            case POSITIVE: {
                if (mBuilder.onPositiveCallback != null)
                    mBuilder.onPositiveCallback.onClick(this, tag);
                if (!mBuilder.alwaysCallSingleChoiceCallback)
                    sendSingleChoiceCallback(v);
                if (!mBuilder.alwaysCallMultiChoiceCallback)
                    sendMultichoiceCallback();
                if (mBuilder.inputCallback != null && input != null && !mBuilder.alwaysCallInputCallback)
                    mBuilder.inputCallback.onInput(this, input.getText());
                if (mBuilder.autoDismiss) dismiss();
                break;
            }
            case NEGATIVE: {
                if (mBuilder.onNegativeCallback != null)
                    mBuilder.onNegativeCallback.onClick(this, tag);
                if (mBuilder.autoDismiss) dismiss();
                break;
            }
            case NEUTRAL: {
                if (mBuilder.onNeutralCallback != null)
                    mBuilder.onNeutralCallback.onClick(this, tag);
                if (mBuilder.autoDismiss) dismiss();
                break;
            }
        }
        if (mBuilder.onAnyCallback != null)
            mBuilder.onAnyCallback.onClick(this, tag);
    }

    // 回调单选事件
    private boolean sendSingleChoiceCallback(View v) {
        if (mBuilder.listCallbackSingleChoice == null) return false;
        CharSequence text = null;
        if (mBuilder.selectedIndex >= 0 && mBuilder.selectedIndex < mBuilder.items.length) {
            text = mBuilder.items[mBuilder.selectedIndex];
        }
        return mBuilder.listCallbackSingleChoice.onSelection(this, v, mBuilder.selectedIndex, text);
    }

    // 回调多选事件
    private boolean sendMultichoiceCallback() {
        if (mBuilder.listCallbackMultiChoice == null) return false;
        Collections.sort(selectedIndicesList); // make sure the indicies are in order
        List<CharSequence> selectedTitles = new ArrayList<>();
        for (Integer i : selectedIndicesList) {
            if (i < 0 || i > mBuilder.items.length - 1) continue;
            selectedTitles.add(mBuilder.items[i]);
        }
        return mBuilder.listCallbackMultiChoice.onSelection(this,
                selectedIndicesList.toArray(new Integer[selectedIndicesList.size()]),
                selectedTitles.toArray(new CharSequence[selectedTitles.size()]));
    }

    // 获取item的指示器
    protected final Drawable getListSelector() {
        if (mBuilder.listSelector != 0)
            return ResourcesCompat.getDrawable(mBuilder.context.getResources(), mBuilder.listSelector, null);
        final Drawable d = DialogUtils.resolveDrawable(mBuilder.context, R.attr.md_list_selector);
        if (d != null) return d;
        return DialogUtils.resolveDrawable(getContext(), R.attr.md_list_selector);
    }

    // 返回Builder对象
    public final Builder getBuilder() {
        return mBuilder;
    }

    // 为input设置回调
    protected void setInternalInputCallback() {
        if (input == null) return;
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final int length = s.toString().length();
                boolean emptyDisabled = false;
                if (!mBuilder.inputAllowEmpty) {
                    emptyDisabled = length == 0;
                    final View positiveAb = getActionButton(DialogAction.POSITIVE);
                    positiveAb.setEnabled(!emptyDisabled);
                }
                invalidateInputMinMaxIndicator(length, emptyDisabled);
                if (mBuilder.alwaysCallInputCallback)
                    mBuilder.inputCallback.onInput(MaterialDialog.this, s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    protected void invalidateInputMinMaxIndicator(int currentLength, boolean emptyDisabled) {
        if (inputMinMax != null) {
            if (mBuilder.inputMaxLength > 0) {
                inputMinMax.setText(String.format(Locale.getDefault(), "%d/%d", currentLength, mBuilder.inputMaxLength));
                inputMinMax.setVisibility(View.VISIBLE);
            } else inputMinMax.setVisibility(View.GONE);
            final boolean isDisabled = (emptyDisabled && currentLength == 0) ||
                    (mBuilder.inputMaxLength > 0 && currentLength > mBuilder.inputMaxLength) ||
                    currentLength < mBuilder.inputMinLength;
            final int colorText = isDisabled ? mBuilder.inputRangeErrorColor : mBuilder.contentColor;
            final int colorWidget = isDisabled ? mBuilder.inputRangeErrorColor : mBuilder.widgetColor;
            if (mBuilder.inputMaxLength > 0)
                inputMinMax.setTextColor(colorText);
            MDTintHelper.setTint(input, colorWidget);
            final View positiveAb = getActionButton(DialogAction.POSITIVE);
            positiveAb.setEnabled(!isDisabled);
        }
    }

    @Override
    public void dismiss() {
        if (input != null)
            DialogUtils.hideKeyboard(this, mBuilder);
        super.dismiss();
    }


    @UiThread
    @Override
    public void show() {
        try {
            super.show();
        } catch (WindowManager.BadTokenException e) {
            throw new DialogException("Bad window token, you cannot show a dialog before an Activity is created or after it's hidden.");
        }
    }

    @Override
    public final void onShow(DialogInterface dialog) {
        if (input != null) {
            DialogUtils.showKeyboard(this, mBuilder);
            if (input.getText().length() > 0)
                input.setSelection(input.getText().length());
        }
        super.onShow(dialog);
    }

    /**
     * Retrieves the view of an action button, allowing you to modify properties such as whether or not it's enabled.
     * Use {@link #setActionButton(DialogAction, int)} to change text, since the view returned here is not
     * the view that displays text.
     *
     * @param which The action button of which to get the view for.
     * @return The view from the dialog's layout representing this action button.
     */
    public final MDButton getActionButton(@NonNull DialogAction which) {
        switch (which) {
            default:
                return positiveButton;
            case NEUTRAL:
                return neutralButton;
            case NEGATIVE:
                return negativeButton;
        }
    }

    /**
     * 更新ActionButton的title
     *
     * @param which    The action button to update.
     * @param titleRes The string resource of the new title of the action button.
     */
    public final void setActionButton(DialogAction which, @StringRes int titleRes) {
        setActionButton(which, getContext().getText(titleRes));
    }


    /**
     * 更新ActionButton的title, 设置title为null, 将会隐藏button
     *
     * @param which The action button to update.
     * @param title The new title of the action button.
     */
    @UiThread
    public final void setActionButton(@NonNull final DialogAction which, final CharSequence title) {
        switch (which) {
            default:
                mBuilder.positiveText = title;
                positiveButton.setText(title);
                positiveButton.setVisibility(title == null ? View.GONE : View.VISIBLE);
                break;
            case NEUTRAL:
                mBuilder.neutralText = title;
                neutralButton.setText(title);
                neutralButton.setVisibility(title == null ? View.GONE : View.VISIBLE);
                break;
            case NEGATIVE:
                mBuilder.negativeText = title;
                negativeButton.setText(title);
                negativeButton.setVisibility(title == null ? View.GONE : View.VISIBLE);
                break;
        }
    }

    /**
     * 判断是否有ActionButton是可见的
     *
     * @return Whether or not 1 or more action buttons is visible.
     */
    public final boolean hasActionButtons() {
        return numberOfActionButtons() > 0;
    }


    /**
     * 得到可见的ActionButton的数量
     *
     * @return 0 through 3, depending on how many should be or are visible.
     */
    public final int numberOfActionButtons() {
        int number = 0;
        if (mBuilder.positiveText != null && positiveButton.getVisibility() == View.VISIBLE)
            number++;
        if (mBuilder.neutralText != null && neutralButton.getVisibility() == View.VISIBLE)
            number++;
        if (mBuilder.negativeText != null && negativeButton.getVisibility() == View.VISIBLE)
            number++;
        return number;
    }


    /**
     * 设置字体
     * @param target 需要设置字体的TextView
     * @param t 字体
     */
    public final void setTypeface(TextView target, Typeface t) {
        if (t == null) return;
        int flags = target.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG;
        target.setPaintFlags(flags);
        target.setTypeface(t);
    }


    /**
     * 获取按钮的指示器
     * @param which 哪一个Action按钮
     * @param isStacked 是否是Stacked模式
     * @return 返回指示器Drawable
     */
    Drawable getButtonSelector(DialogAction which, boolean isStacked) {
        if (isStacked) {
            if (mBuilder.btnSelectorStacked != 0)
                return ResourcesCompat.getDrawable(mBuilder.context.getResources(), mBuilder.btnSelectorStacked, null);
            final Drawable d = DialogUtils.resolveDrawable(mBuilder.context, R.attr.md_btn_stacked_selector);
            if (d != null) return d;
            return DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_stacked_selector);
        } else {
            switch (which) {
                default: {
                    if (mBuilder.btnSelectorPositive != 0)
                        return ResourcesCompat.getDrawable(mBuilder.context.getResources(), mBuilder.btnSelectorPositive, null);
                    Drawable d = DialogUtils.resolveDrawable(mBuilder.context, R.attr.md_btn_positive_selector);
                    if (d != null) return d;
                    d = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_positive_selector);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        RippleHelper.applyColor(d, mBuilder.buttonRippleColor);
                    return d;
                }
                case NEUTRAL: {
                    if (mBuilder.btnSelectorNeutral != 0)
                        return ResourcesCompat.getDrawable(mBuilder.context.getResources(), mBuilder.btnSelectorNeutral, null);
                    Drawable d = DialogUtils.resolveDrawable(mBuilder.context, R.attr.md_btn_neutral_selector);
                    if (d != null) return d;
                    d = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_neutral_selector);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        RippleHelper.applyColor(d, mBuilder.buttonRippleColor);
                    return d;
                }
                case NEGATIVE: {
                    if (mBuilder.btnSelectorNegative != 0)
                        return ResourcesCompat.getDrawable(mBuilder.context.getResources(), mBuilder.btnSelectorNegative, null);
                    Drawable d = DialogUtils.resolveDrawable(mBuilder.context, R.attr.md_btn_negative_selector);
                    if (d != null) return d;
                    d = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_negative_selector);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        RippleHelper.applyColor(d, mBuilder.buttonRippleColor);
                    return d;
                }
            }
        }
    }


    /**
     * 为ListView设置Adapter并且实现item的click监听器
     */
    protected final void invalidateList() {
        if (listView == null)
            return;
        else if ((mBuilder.items == null || mBuilder.items.length == 0) && mBuilder.adapter == null)
            return;
        // Set up list with adapter
        listView.setAdapter(mBuilder.adapter);
        if (listType != null || mBuilder.listCallbackCustom != null)
            listView.setOnItemClickListener(this);
    }


    /**
     * item的点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mBuilder.listCallbackCustom != null) {
            // Custom adapter
            CharSequence text = null;
            if (view instanceof TextView)
                text = ((TextView) view).getText();
            mBuilder.listCallbackCustom.onSelection(this, view, position, text);
        } else if (listType == null || listType == ListType.REGULAR) {
            // Default adapter, non choice mode
            if (mBuilder.autoDismiss) {
                // If auto dismiss is enabled, dismiss the dialog when a list item is selected
                dismiss();
            }
            if (mBuilder.listCallback != null) {
                mBuilder.listCallback.onSelection(this, view, position, mBuilder.items[position]);
            }
        } else {
            // Default adapter, choice mode
            if (listType == ListType.MULTI) {
                final boolean shouldBeChecked = !selectedIndicesList.contains(position);
                final CheckBox cb = view.findViewById(R.id.control);
                if (shouldBeChecked) {
                    // Add the selection to the states first so the callback includes it (when alwaysCallMultiChoiceCallback)
                    selectedIndicesList.add(position);
                    if (mBuilder.alwaysCallMultiChoiceCallback) {
                        // If the checkbox wasn't previously selected, and the callback returns true, add it to the states and check it
                        if (sendMultichoiceCallback()) {
                            cb.setChecked(true);
                        } else {
                            // The callback cancelled selection, remove it from the states
                            selectedIndicesList.remove(Integer.valueOf(position));
                        }
                    } else {
                        // The callback was not used to check if selection is allowed, just select it
                        cb.setChecked(true);
                    }
                } else {
                    // The checkbox was unchecked
                    selectedIndicesList.remove(Integer.valueOf(position));
                    cb.setChecked(false);
                    if (mBuilder.alwaysCallMultiChoiceCallback)
                        sendMultichoiceCallback();
                }
            } else if (listType == ListType.SINGLE) {
                boolean allowSelection = true;
                final DefaultAdapter adapter = (DefaultAdapter) mBuilder.adapter;
                final RadioButton radio = view.findViewById(R.id.control);

                if (mBuilder.autoDismiss && mBuilder.positiveText == null) {
                    // If auto dismiss is enabled, and no action button is visible to approve the selection, dismiss the dialog
                    dismiss();
                    // Don't allow the selection to be updated since the dialog is being dismissed anyways
                    allowSelection = false;
                    // Update selected index and send callback
                    mBuilder.selectedIndex = position;
                    sendSingleChoiceCallback(view);
                } else if (mBuilder.alwaysCallSingleChoiceCallback) {
                    int oldSelected = mBuilder.selectedIndex;
                    // Temporarily set the new index so the callback uses the right one
                    mBuilder.selectedIndex = position;
                    // Only allow the radio button to be checked if the callback returns true
                    allowSelection = sendSingleChoiceCallback(view);
                    // Restore the old selected index, so the state is updated below
                    mBuilder.selectedIndex = oldSelected;
                }
                // Update the checked states
                if (allowSelection) {
                    mBuilder.selectedIndex = position;
                    radio.setChecked(true);
                    adapter.notifyDataSetChanged();
                }
            }

        }
    }

    // 初始化的时候是否需要移动到被选中的位置
    protected final void checkIfListInitScroll() {
        if (listView == null)
            return;
        listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    //noinspection deprecation
                    listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                if (listType == ListType.SINGLE || listType == ListType.MULTI) {
                    int selectedIndex;
                    if (listType == ListType.SINGLE) {
                        if (mBuilder.selectedIndex < 0)
                            return;
                        selectedIndex = mBuilder.selectedIndex;
                    } else {
                        if (selectedIndicesList == null || selectedIndicesList.size() == 0)
                            return;
                        Collections.sort(selectedIndicesList);
                        selectedIndex = selectedIndicesList.get(0);
                    }
                    if (listView.getLastVisiblePosition() < selectedIndex) {
                        final int totalVisible = listView.getLastVisiblePosition() - listView.getFirstVisiblePosition();
                        // Scroll so that the selected index appears in the middle (vertically) of the ListView
                        int scrollIndex = selectedIndex - (totalVisible / 2);
                        if (scrollIndex < 0) scrollIndex = 0;
                        final int fScrollIndex = scrollIndex;
                        listView.post(() -> {
                            listView.requestFocus();
                            listView.setSelection(fScrollIndex);
                        });
                    }
                }
            }
        });
    }

    // 返回根布局
    public final View getView() {
        return view;
    }

    // 返回listView
    @Nullable
    public final ListView getListView() {
        return listView;
    }

    // 返回输入的EditText
    @Nullable
    public final EditText getInputEditText() {
        return input;
    }


    /**
     * 获取title, 如果需要更新title使用 #{@link #setTitle(CharSequence)}.
     */
    public final TextView getTitleView() {
        return title;
    }

    /**
     * 获取icon
     */
    public ImageView getIconView() {
        return icon;
    }

    /**
     * 获取内容的view
     */
    @Nullable
    public final TextView getContentView() {
        return content;
    }

    /**
     * 获取自定义的布局view
     */
    @Nullable
    public final View getCustomView() {
        return mBuilder.customView;
    }

    // 设置标题
    @UiThread
    @Override
    public final void setTitle(@NonNull CharSequence newTitle) {
        title.setText(newTitle);
    }

    // 设置标题
    @UiThread
    @Override
    public final void setTitle(@StringRes int newTitleRes) {
        setTitle(mBuilder.context.getString(newTitleRes));
    }

    // 设置标题
    @UiThread
    public final void setTitle(@StringRes int newTitleRes, @Nullable Object... formatArgs) {
        setTitle(mBuilder.context.getString(newTitleRes, formatArgs));
    }

    // 设置icon
    @UiThread
    public void setIcon(@DrawableRes final int resId) {
        icon.setImageResource(resId);
        icon.setVisibility(resId != 0 ? View.VISIBLE : View.GONE);
    }

    // 设置icon
    @UiThread
    public void setIcon(final Drawable d) {
        icon.setImageDrawable(d);
        icon.setVisibility(d != null ? View.VISIBLE : View.GONE);
    }

    // 设置icon
    @UiThread
    public void setIconAttribute(@AttrRes int attrId) {
        Drawable d = DialogUtils.resolveDrawable(mBuilder.context, attrId);
        setIcon(d);
    }

    // 设置内容
    @UiThread
    public final void setContent(CharSequence newContent) {
        content.setText(newContent);
        content.setVisibility(TextUtils.isEmpty(newContent) ? View.GONE : View.VISIBLE);
    }

    // 设置内容
    @UiThread
    public final void setContent(@StringRes int newContentRes) {
        setContent(mBuilder.context.getString(newContentRes));
    }

    // 设置内容
    @UiThread
    public final void setContent(@StringRes int newContentRes, @Nullable Object... formatArgs) {
        setContent(mBuilder.context.getString(newContentRes, formatArgs));
    }

    // 设置item
    @UiThread
    public final void setItems(CharSequence... items) {
        if (mBuilder.adapter == null)
            throw new IllegalStateException("This MaterialDialog instance does not yet have an adapter set to it. You cannot use setItems().");
        mBuilder.items = items;
        if (mBuilder.adapter instanceof DefaultAdapter) {
            mBuilder.adapter = new DefaultAdapter(this, ListType.getLayoutForType(listType));
        } else {
            throw new IllegalStateException("When using a custom adapter, setItems() cannot be used. Set items through the adapter instead.");
        }
        listView.setAdapter(mBuilder.adapter);
    }

    // 获取当前的进度
    public final int getCurrentProgress() {
        if (mProgress == null) return -1;
        return mProgress.getProgress();
    }

    // 获取进度条view
    public ProgressBar getProgressBar() {
        return mProgress;
    }

    // 增加进度
    public final void incrementProgress(final int by) {
        setProgress(getCurrentProgress() + by);
    }

    // 修改进度
    public final void setProgress(final int progress) {
        if (mBuilder.progress <= -2)
            throw new IllegalStateException("Cannot use setProgress() on this dialog.");
        mProgress.setProgress(progress);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mProgressLabel != null) {
//                    final int percentage = (int) (((float) getCurrentProgress() / (float) getMaxProgress()) * 100f);
                    mProgressLabel.setText(mBuilder.progressPercentFormat.format(
                            (float) getCurrentProgress() / (float) getMaxProgress()));
                }
                if (mProgressMinMax != null) {
                    mProgressMinMax.setText(String.format(mBuilder.progressNumberFormat,
                            getCurrentProgress(), getMaxProgress()));
                }
            }
        });
    }

    // 设置最大的进度条
    public final void setMaxProgress(final int max) {
        if (mBuilder.progress <= -2)
            throw new IllegalStateException("Cannot use setMaxProgress() on this dialog.");
        mProgress.setMax(max);
    }

    // 返回是否是不确定进度条
    public final boolean isIndeterminateProgress() {
        return mBuilder.indeterminateProgress;
    }

    // 获取最大的进度值
    public final int getMaxProgress() {
        if (mProgress == null) return -1;
        return mProgress.getMax();
    }

    /**
     * 使用百分比文字来显示进度
     */
    public final void setProgressPercentFormat(NumberFormat format) {
        mBuilder.progressPercentFormat = format;
        setProgress(getCurrentProgress()); // invalidates display
    }

    /**
     * 使用文字来显示进度
     */
    public final void setProgressNumberFormat(String format) {
        mBuilder.progressNumberFormat = format;
        setProgress(getCurrentProgress()); // invalidates display
    }

    // 是否取消掉了
    public final boolean isCancelled() {
        return !isShowing();
    }

    /**
     * 获取被选中的索引
     */
    public int getSelectedIndex() {
        if (mBuilder.listCallbackSingleChoice != null) {
            return mBuilder.selectedIndex;
        } else {
            return -1;
        }
    }

    /**
     * 获取被选中的索引数组
     */
    @Nullable
    public Integer[] getSelectedIndices() {
        if (mBuilder.listCallbackMultiChoice != null) {
            return selectedIndicesList.toArray(new Integer[selectedIndicesList.size()]);
        } else {
            return null;
        }
    }

    /**
     * Convenience method for setting the currently selected index of a single choice list.
     * This only works if you are not using a custom adapter; if you're using a custom adapter,
     * an IllegalStateException is thrown. Note that this does not call the respective single choice callback.
     *
     * @param index The index of the list item to check.
     */
    @UiThread
    public void setSelectedIndex(int index) {
        mBuilder.selectedIndex = index;
        if (mBuilder.adapter != null && mBuilder.adapter instanceof DefaultAdapter) {
            ((DefaultAdapter) mBuilder.adapter).notifyDataSetChanged();
        } else {
            throw new IllegalStateException("You can only use setSelectedIndex() with the default adapter implementation.");
        }
    }

    /**
     * Convenience method for setting the currently selected indices of a multi choice list.
     * This only works if you are not using a custom adapter; if you're using a custom adapter,
     * an IllegalStateException is thrown. Note that this does not call the respective multi choice callback.
     *
     * @param indices The indices of the list items to check.
     */
    @UiThread
    public void setSelectedIndices(@NonNull Integer[] indices) {
        selectedIndicesList = new ArrayList<>(Arrays.asList(indices));
        if (mBuilder.adapter != null && mBuilder.adapter instanceof DefaultAdapter) {
            ((DefaultAdapter) mBuilder.adapter).notifyDataSetChanged();
        } else {
            throw new IllegalStateException("You can only use setSelectedIndices() with the default adapter implementation.");
        }
    }

    /**
     * Clears all selected checkboxes from multi choice list dialogs.
     */
    public void clearSelectedIndices() {
        clearSelectedIndices(true);
    }

    /**
     * Clears all selected checkboxes from multi choice list dialogs.
     *
     * @param sendCallback Defaults to true. True will notify the multi-choice callback, if any.
     */
    public void clearSelectedIndices(boolean sendCallback) {
        if (listType == null || listType != ListType.MULTI)
            throw new IllegalStateException("You can only use clearSelectedIndices() with multi choice list dialogs.");
        if (mBuilder.adapter != null && mBuilder.adapter instanceof DefaultAdapter) {
            if (selectedIndicesList != null)
                selectedIndicesList.clear();
            ((DefaultAdapter) mBuilder.adapter).notifyDataSetChanged();
            if (sendCallback && mBuilder.listCallbackMultiChoice != null)
                sendMultichoiceCallback();
        } else {
            throw new IllegalStateException("You can only use clearSelectedIndices() with the default adapter implementation.");
        }
    }

    /**
     * Selects all checkboxes in multi choice list dialogs.
     */
    public void selectAllIndicies() {
        selectAllIndicies(true);
    }

    /**
     * Selects all checkboxes in multi choice list dialogs.
     *
     * @param sendCallback Defaults to true. True will notify the multi-choice callback, if any.
     */
    public void selectAllIndicies(boolean sendCallback) {
        if (listType == null || listType != ListType.MULTI)
            throw new IllegalStateException("You can only use selectAllIndicies() with multi choice list dialogs.");
        if (mBuilder.adapter != null && mBuilder.adapter instanceof DefaultAdapter) {
            if (selectedIndicesList == null)
                selectedIndicesList = new ArrayList<>();
            for (int i = 0; i < mBuilder.adapter.getCount(); i++) {
                if (!selectedIndicesList.contains(i))
                    selectedIndicesList.add(i);
            }
            ((DefaultAdapter) mBuilder.adapter).notifyDataSetChanged();
            if (sendCallback && mBuilder.listCallbackMultiChoice != null)
                sendMultichoiceCallback();
        } else {
            throw new IllegalStateException("You can only use selectAllIndicies() with the default adapter implementation.");
        }
    }

    public static class Builder {
        // 上下文对象
        protected final Context context;
        // 标题
        protected CharSequence title;
        // 标题字体颜色
        protected int titleColor = -1;
        // 标题是否设置了颜色
        protected boolean titleColorSet = false;
        // 标题对齐的规则
        protected GravityEnum titleGravity = GravityEnum.START;
        // 内容
        protected CharSequence content;
        // 内容字体颜色
        protected int contentColor = -1;
        // 是否设置了内容字体的颜色
        protected boolean contentColorSet = false;
        // 内容对齐规则
        protected GravityEnum contentGravity = GravityEnum.START;
        // 内容行间距
        protected float contentLineSpacingMultiplier = 1.2f;
        // 图标
        protected Drawable icon;
        // 标题和Action按钮的字体
        protected Typeface mediumFont;
        // 除了标题和Action按钮的字体
        protected Typeface regularFont;
        // 按钮波纹颜色
        protected int buttonRippleColor;
        // positive按钮的文字
        protected CharSequence positiveText;
        // positive按钮文字的颜色
        protected ColorStateList positiveColor;
        // 是否设置positive按钮文字的颜色
        protected boolean positiveColorSet = false;
        // neutral按钮的文字
        protected CharSequence neutralText;
        // neutral按钮文字的颜色
        protected ColorStateList negativeColor;
        // 是否设置neutral按钮文字的颜色
        protected boolean negativeColorSet = false;
        // negative按钮的文字
        protected CharSequence negativeText;
        // negative按钮文字颜色
        protected ColorStateList neutralColor;
        // 是否设置negative按钮文字颜色
        protected boolean neutralColorSet;
        // 链接的颜色
        protected ColorStateList linkColor;
        // 自定义布局view
        protected View customView;
        // 滑动的时候是否自适应
        protected boolean wrapCustomViewInScroll;
        // 是否使用文本显示进度
        protected boolean showMinMax;
        // 是否是一个不确定的进度条
        protected boolean indeterminateProgress;
        // 当前的进度条type -2：不确定圆形   -1：确定条形
        protected int progress = -2;
        // 进度条的最大值
        protected int progressMax = 0;
        // 数值文本解析格式
        protected String progressNumberFormat;
        // 百分比文本解析格式
        protected NumberFormat progressPercentFormat;
        // 是否让不确定进度条变成条形的
        protected boolean indeterminateIsHorizontalProgress;
        // 设置组件颜色
        protected int widgetColor;
        // 是否设置了组件颜色
        protected boolean widgetColorSet = false;
        // 设置分割线颜色
        protected int dividerColor;
        // 是否设置了分割线颜色
        protected boolean dividerColorSet = false;
        // 设置背景颜色
        protected int backgroundColor;
        // 点击positive按钮的回调
        protected SingleButtonCallback onPositiveCallback;
        // 点击negative按钮的回调
        protected SingleButtonCallback onNegativeCallback;
        // 点击neutral按钮的回调
        protected SingleButtonCallback onNeutralCallback;
        // 点击任意一个按钮的回调
        protected SingleButtonCallback onAnyCallback;
        // 调用show方法后的回调
        protected OnShowListener showListener;
        // 调用dismiss方法后的回调
        protected OnDismissListener dismissListener;
        // 调用cancel方法后的回调
        protected OnCancelListener cancelListener;
        // 处理虚拟按键的事件 例如按下back返回键
        protected OnKeyListener keyListener;
        // 主题
        protected Theme theme;
        // 是否可取消
        protected boolean cancelable = true;
        // 是否可以点击外部进行取消
        protected boolean canceledOnTouchOutside = true;
        // 当点击action按钮的时候是否可以自动取消dialog
        protected boolean autoDismiss = true;
        // 设置文字列表项
        protected CharSequence[] items;
        // 列表选择回调事件
        protected ListCallback listCallback;
        // 设置item文本的颜色
        protected int itemColor;
        // 是否设置了item文本的颜色
        protected boolean itemColorSet = false;
        // item文本的对齐规则
        protected GravityEnum itemsGravity = GravityEnum.START;
        // item列表对应的id
        protected int[] itemIds;
        // button的对齐规则
        protected GravityEnum buttonsGravity = GravityEnum.START;
        // 列表单选回调事件
        protected ListCallbackSingleChoice listCallbackSingleChoice;
        // 被选择的item索引
        protected int selectedIndex = -1;
        // 当有item被选中的时候是否总是回调(ListCallbackSingleChoice)接口
        protected boolean alwaysCallSingleChoiceCallback = false;
        // checkbox多选回调事件
        protected ListCallbackMultiChoice listCallbackMultiChoice;
        // 是否每一多选都回调接口
        protected boolean alwaysCallMultiChoiceCallback = false;
        // 被选中items的索引
        protected Integer[] selectedIndices = null;
        // item被选中的指示器
        protected int listSelector;
        // button被选中的指示器
        protected int btnSelectorStacked;
        protected int btnSelectorPositive;
        protected int btnSelectorNegative;
        protected int btnSelectorNeutral;
        // todo
        protected GravityEnum btnStackedGravity = GravityEnum.END;
        // 是否限制显示icon的图片大小为48dp
        protected boolean limitIconToDefaultSize;
        // 允许显示最大的icon数量
        protected int maxIconSize = -1;
        // 适配item的Adapter
        protected ListAdapter adapter;
        // item的选择回调接口
        protected ListCallback listCallbackCustom;
        // 是否强制为stacked模式
        protected boolean forceStacking;
        // 输入的提示
        protected CharSequence inputHint;
        // 是否允许输入空字符串
        protected boolean inputAllowEmpty;
        // 预输入的字符串
        protected CharSequence inputPrefill;
        // input的回调接口
        protected InputCallback inputCallback;
        // input类型
        protected int inputType = -1;
        // input的最小长度
        protected int inputMinLength = -1;
        // input的最大长度
        protected int inputMaxLength = -1;
        // input出错的提示颜色, 默认是红色
        protected int inputRangeErrorColor = 0;
        // 是否总是回调输入的接口
        protected boolean alwaysCallInputCallback;


        public Builder(Context context) {
            this.context = context;
            final int materialBlue = DialogUtils.getColor(context, R.color.md_material_blue_600);

            this.widgetColor = DialogUtils.resolveColor(context, R.attr.colorAccent, materialBlue);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.widgetColor = DialogUtils.resolveColor(context, android.R.attr.colorAccent, this.widgetColor);
            }

            this.positiveColor = DialogUtils.getActionTextStateList(context, this.widgetColor);
            this.negativeColor = DialogUtils.getActionTextStateList(context, this.widgetColor);
            this.neutralColor = DialogUtils.getActionTextStateList(context, this.widgetColor);
            this.linkColor = DialogUtils.getActionTextStateList(context,
                    DialogUtils.resolveColor(context, R.attr.md_link_color, this.widgetColor));

            int fallback = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                fallback = DialogUtils.resolveColor(context, android.R.attr.colorControlHighlight);
            this.buttonRippleColor = DialogUtils.resolveColor(context, R.attr.md_btn_ripple_color,
                    DialogUtils.resolveColor(context, R.attr.colorControlHighlight, fallback));

            this.progressPercentFormat = NumberFormat.getPercentInstance();
            this.progressNumberFormat = "%1d/%2d";

            // Set the default theme based on the Activity theme's primary color darkness (more white or more black)
            final int primaryTextColor = DialogUtils.resolveColor(context, android.R.attr.textColorPrimary);
            this.theme = DialogUtils.isColorDark(primaryTextColor) ? Theme.LIGHT : Theme.DARK;

            // Load theme values from the ThemeSingleton if needed
            checkSingleton();

            // Retrieve gravity settings from global theme attributes if needed
            this.titleGravity = DialogUtils.resolveGravityEnum(context, R.attr.md_title_gravity, this.titleGravity);
            this.contentGravity = DialogUtils.resolveGravityEnum(context, R.attr.md_content_gravity, this.contentGravity);
            this.btnStackedGravity = DialogUtils.resolveGravityEnum(context, R.attr.md_btnstacked_gravity, this.btnStackedGravity);
            this.itemsGravity = DialogUtils.resolveGravityEnum(context, R.attr.md_items_gravity, this.itemsGravity);
            this.buttonsGravity = DialogUtils.resolveGravityEnum(context, R.attr.md_buttons_gravity, this.buttonsGravity);

            final String mediumFont = DialogUtils.resolveString(context, R.attr.md_medium_font);
            final String regularFont = DialogUtils.resolveString(context, R.attr.md_regular_font);
            typeface(mediumFont, regularFont);

            if (this.mediumFont == null) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        this.mediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL);
                    else
                        this.mediumFont = Typeface.create("sans-serif", Typeface.BOLD);
                } catch (Exception ignored) {
                }
            }
            if (this.regularFont == null) {
                try {
                    this.regularFont = Typeface.create("sans-serif", Typeface.NORMAL);
                } catch (Exception ignored) {
                }
            }
        }

        // 通过单利来保存主题
        private void checkSingleton() {
            if (ThemeSingleton.get(false) == null) return;
            ThemeSingleton s = ThemeSingleton.get();
            if (s.darkTheme)
                this.theme = Theme.DARK;
            if (s.titleColor != 0)
                this.titleColor = s.titleColor;
            if (s.contentColor != 0)
                this.contentColor = s.contentColor;
            if (s.positiveColor != null)
                this.positiveColor = s.positiveColor;
            if (s.neutralColor != null)
                this.neutralColor = s.neutralColor;
            if (s.negativeColor != null)
                this.negativeColor = s.negativeColor;
            if (s.itemColor != 0)
                this.itemColor = s.itemColor;
            if (s.icon != null)
                this.icon = s.icon;
            if (s.backgroundColor != 0)
                this.backgroundColor = s.backgroundColor;
            if (s.dividerColor != 0)
                this.dividerColor = s.dividerColor;
            if (s.btnSelectorStacked != 0)
                this.btnSelectorStacked = s.btnSelectorStacked;
            if (s.listSelector != 0)
                this.listSelector = s.listSelector;
            if (s.btnSelectorPositive != 0)
                this.btnSelectorPositive = s.btnSelectorPositive;
            if (s.btnSelectorNeutral != 0)
                this.btnSelectorNeutral = s.btnSelectorNeutral;
            if (s.btnSelectorNegative != 0)
                this.btnSelectorNegative = s.btnSelectorNegative;
            if (s.widgetColor != 0)
                this.widgetColor = s.widgetColor;
            if (s.linkColor != null)
                this.linkColor = s.linkColor;
            this.titleGravity = s.titleGravity;
            this.contentGravity = s.contentGravity;
            this.btnStackedGravity = s.btnStackedGravity;
            this.itemsGravity = s.itemsGravity;
            this.buttonsGravity = s.buttonsGravity;
        }

        // 返回context对象
        public final Context getContext() {
            return context;
        }

        // 设置标题
        public Builder title(@StringRes int titleRes) {
            title(this.context.getText(titleRes));
            return this;
        }

        // 设置标题
        public Builder title(@NonNull CharSequence title) {
            this.title = title;
            return this;
        }

        // 设置标题的对齐规则
        public Builder titleGravity(@NonNull GravityEnum gravity) {
            this.titleGravity = gravity;
            return this;
        }

        // 设置标题字体颜色
        public Builder titleColor(@ColorInt int color) {
            this.titleColor = color;
            this.titleColorSet = true;
            return this;
        }

        // 设置标题字体颜色
        public Builder titleColorRes(@ColorRes int colorRes) {
            return titleColor(DialogUtils.getColor(this.context, colorRes));
        }

        // 设置标题字体颜色
        public Builder titleColorAttr(@AttrRes int colorAttr) {
            return titleColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        // 设置内容
        public Builder content(@StringRes int contentRes) {
            content(this.context.getText(contentRes));
            return this;
        }

        // 设置内容
        public Builder content(@NonNull CharSequence content) {
            if (this.customView != null)
                throw new IllegalStateException("You cannot set content() when you're using a custom view.");
            this.content = content;
            return this;
        }

        // 设置内容
        public Builder content(@StringRes int contentRes, Object... formatArgs) {
            content(this.context.getString(contentRes, formatArgs));
            return this;
        }

        // 设置内容字体颜色
        public Builder contentColor(@ColorInt int color) {
            this.contentColor = color;
            this.contentColorSet = true;
            return this;
        }

        // 设置内容字体颜色
        public Builder contentColorRes(@ColorRes int colorRes) {
            contentColor(DialogUtils.getColor(this.context, colorRes));
            return this;
        }

        // 设置内容字体颜色
        public Builder contentColorAttr(@AttrRes int colorAttr) {
            contentColor(DialogUtils.resolveColor(this.context, colorAttr));
            return this;
        }

        // 设置内容文字的对齐规则
        public Builder contentGravity(@NonNull GravityEnum gravity) {
            this.contentGravity = gravity;
            return this;
        }

        // 设置内容文字的行间距
        public Builder contentLineSpacing(float multiplier) {
            this.contentLineSpacingMultiplier = multiplier;
            return this;
        }

        // 设置图标
        public Builder icon(@NonNull Drawable icon) {
            this.icon = icon;
            return this;
        }

        // 设置图标
        public Builder iconRes(@DrawableRes int icon) {
            this.icon = ResourcesCompat.getDrawable(context.getResources(), icon, null);
            return this;
        }

        // 设置图标
        public Builder iconAttr(@AttrRes int iconAttr) {
            this.icon = DialogUtils.resolveDrawable(context, iconAttr);
            return this;
        }


        /**
         * 设置字体, 推荐使用下面的方法进行设置字体, 那样可以减少无意义创建Typeface的开销
         *
         * @param medium  字体运用到标题和action按钮上, 如果是null, 则使用默认字体
         * @param regular 字体大小运用到除了标题和action按钮之外的所有地方, 如果是null, 则使用默认字体
         */
        public Builder typeface(@Nullable Typeface medium, @Nullable Typeface regular) {
            this.mediumFont = medium;
            this.regularFont = regular;
            return this;
        }

        /**
         * 通过传入Asset目录下的文件名来创建字体, 通过这个方法创建字体可以减小内存开销
         *
         * @param medium  字体运用到标题和action按钮上, 如果是null, 则使用默认字体
         * @param regular 字体大小运用到除了标题和action按钮之外的所有地方, 如果是null, 则使用默认字体
         */
        public Builder typeface(@Nullable String medium, @Nullable String regular) {
            if (medium != null) {
                this.mediumFont = TypefaceHelper.get(this.context, medium);
                if (this.mediumFont == null)
                    throw new IllegalArgumentException("No font asset found for " + medium);
            }
            if (regular != null) {
                this.regularFont = TypefaceHelper.get(this.context, regular);
                if (this.regularFont == null)
                    throw new IllegalArgumentException("No font asset found for " + regular);
            }
            return this;
        }


        // 设置按钮波纹颜色
        public Builder buttonRippleColor(@ColorInt int color) {
            this.buttonRippleColor = color;
            return this;
        }

        // 设置按钮波纹颜色
        public Builder buttonRippleColorRes(@ColorRes int colorRes) {
            return buttonRippleColor(DialogUtils.getColor(this.context, colorRes));
        }

        // 设置按钮波纹颜色
        public Builder buttonRippleColorAttr(@AttrRes int colorAttr) {
            return buttonRippleColor(DialogUtils.resolveColor(this.context, colorAttr));
        }


        // 设置positive按钮的文字
        public Builder positiveText(@StringRes int postiveRes) {
            if (postiveRes == 0) return this;
            positiveText(this.context.getText(postiveRes));
            return this;
        }

        // 设置positive按钮的文字
        public Builder positiveText(@NonNull CharSequence message) {
            this.positiveText = message;
            return this;
        }

        // 设置positive按钮文字的颜色
        public Builder positiveColor(@ColorInt int color) {
            return positiveColor(DialogUtils.getActionTextStateList(context, color));
        }

        // 设置positive按钮文字的颜色
        public Builder positiveColorRes(@ColorRes int colorRes) {
            return positiveColor(DialogUtils.getActionTextColorStateList(this.context, colorRes));
        }

        // 设置positive按钮文字的颜色
        public Builder positiveColorAttr(@AttrRes int colorAttr) {
            return positiveColor(DialogUtils.resolveActionTextColorStateList(this.context, colorAttr, null));
        }

        // 设置positive按钮文字的颜色
        public Builder positiveColor(@NonNull ColorStateList colorStateList) {
            this.positiveColor = colorStateList;
            this.positiveColorSet = true;
            return this;
        }

        // 设置neutral按钮的文字
        public Builder neutralText(@StringRes int neutralRes) {
            if (neutralRes == 0) return this;
            return neutralText(this.context.getText(neutralRes));
        }

        // 设置neutral按钮的文字
        public Builder neutralText(@NonNull CharSequence message) {
            this.neutralText = message;
            return this;
        }

        // 设置neutral按钮文字的颜色
        public Builder negativeColor(@ColorInt int color) {
            return negativeColor(DialogUtils.getActionTextStateList(context, color));
        }

        // 设置neutral按钮文字的颜色
        public Builder negativeColorRes(@ColorRes int colorRes) {
            return negativeColor(DialogUtils.getActionTextColorStateList(this.context, colorRes));
        }

        // 设置neutral按钮文字的颜色
        public Builder negativeColorAttr(@AttrRes int colorAttr) {
            return negativeColor(DialogUtils.resolveActionTextColorStateList(this.context, colorAttr, null));
        }

        // 设置neutral按钮文字的颜色
        public Builder negativeColor(@NonNull ColorStateList colorStateList) {
            this.negativeColor = colorStateList;
            this.negativeColorSet = true;
            return this;
        }

        // 设置negative按钮的文字
        public Builder negativeText(@StringRes int negativeRes) {
            if (negativeRes == 0) return this;
            return negativeText(this.context.getText(negativeRes));
        }

        // 设置negative按钮的文字
        public Builder negativeText(@NonNull CharSequence message) {
            this.negativeText = message;
            return this;
        }

        // 设置negative按钮文字颜色
        public Builder neutralColor(@ColorInt int color) {
            return neutralColor(DialogUtils.getActionTextStateList(context, color));
        }

        // 设置negative按钮文字颜色
        public Builder neutralColorRes(@ColorRes int colorRes) {
            return neutralColor(DialogUtils.getActionTextColorStateList(this.context, colorRes));
        }

        // 设置negative按钮文字颜色
        public Builder neutralColorAttr(@AttrRes int colorAttr) {
            return neutralColor(DialogUtils.resolveActionTextColorStateList(this.context, colorAttr, null));
        }

        // 设置negative按钮文字颜色
        public Builder neutralColor(@NonNull ColorStateList colorStateList) {
            this.neutralColor = colorStateList;
            this.neutralColorSet = true;
            return this;
        }

        // 设置链接的颜色
        public Builder linkColor(@ColorInt int color) {
            return linkColor(DialogUtils.getActionTextStateList(context, color));
        }

        // 设置链接的颜色
        public Builder linkColorRes(@ColorRes int colorRes) {
            return linkColor(DialogUtils.getActionTextColorStateList(this.context, colorRes));
        }

        // 设置链接的颜色
        public Builder linkColorAttr(@AttrRes int colorAttr) {
            return linkColor(DialogUtils.resolveActionTextColorStateList(this.context, colorAttr, null));
        }

        // 设置链接的颜色
        public Builder linkColor(@NonNull ColorStateList colorStateList) {
            this.linkColor = colorStateList;
            return this;
        }


        // 自定义布局
        public Builder customView(@LayoutRes int layoutRes, boolean wrapInScrollView) {
            LayoutInflater li = LayoutInflater.from(this.context);
            return customView(li.inflate(layoutRes, null), wrapInScrollView);
        }

        // 自定义布局
        public Builder customView(@NonNull View view, boolean wrapInScrollView) {
            if (this.content != null)
                throw new IllegalStateException("You cannot use customView() when you have content set.");
            else if (this.items != null)
                throw new IllegalStateException("You cannot use customView() when you have items set.");
            else if (this.inputCallback != null)
                throw new IllegalStateException("You cannot use customView() with an input dialog");
            else if (this.progress > -2 || this.indeterminateProgress)
                throw new IllegalStateException("You cannot use customView() with a progress dialog");
            if (view.getParent() != null && view.getParent() instanceof ViewGroup)
                ((ViewGroup) view.getParent()).removeView(view);
            this.customView = view;
            this.wrapCustomViewInScroll = wrapInScrollView;
            return this;
        }


        /**
         * 使这个dialog变成一个进度条dialog
         *
         * @param indeterminate true -> 圆形不确定进度条, false -> 水平确定的进度条
         * @param max           上面为false才有效, 意味着进度条最大值
         */
        public Builder progress(boolean indeterminate, int max) {
            if (this.customView != null)
                throw new IllegalStateException("You cannot set progress() when you're using a custom view.");
            if (indeterminate) {
                this.indeterminateProgress = true;
                this.progress = -2;
            } else {
                this.indeterminateProgress = false;
                this.progress = -1;
                this.progressMax = max;
            }
            return this;
        }

        /**
         * 使这个dialog变成一个进度条dialog
         *
         * @param indeterminate true -> 圆形不确定进度条, false -> 水平确定的进度条
         * @param max           上面为false才有效, 意味着进度条最大值
         * @param showMinMax    是否使用文本显示进度, e.g. 50/100.
         */
        public Builder progress(boolean indeterminate, int max, boolean showMinMax) {
            this.showMinMax = showMinMax;
            return progress(indeterminate, max);
        }


        /**
         * 当使用文本显示进度的时候, 设置文本的格式
         * 默认 "%1d/%2d".
         */
        public Builder progressNumberFormat(@NonNull String format) {
            this.progressNumberFormat = format;
            return this;
        }

        /**
         * 如果使用的是百分比来显示进度, 修改显示的格式
         * 默认 NumberFormat.getPercentageInstance().
         */
        public Builder progressPercentFormat(@NonNull NumberFormat format) {
            this.progressPercentFormat = format;
            return this;
        }

        /**
         * 默认的不确定进度条是圆形的, 可以通过这个方法来改变它成为条行的
         */
        public Builder progressIndeterminateStyle(boolean horizontal) {
            this.indeterminateIsHorizontalProgress = horizontal;
            return this;
        }

        // 设置组件颜色
        public Builder widgetColor(@ColorInt int color) {
            this.widgetColor = color;
            this.widgetColorSet = true;
            return this;
        }

        // 设置组件颜色
        public Builder widgetColorRes(@ColorRes int colorRes) {
            return widgetColor(DialogUtils.getColor(this.context, colorRes));
        }

        // 设置组件颜色
        public Builder widgetColorAttr(@AttrRes int colorAttr) {
            return widgetColorRes(DialogUtils.resolveColor(this.context, colorAttr));
        }

        // 设置分割线颜色
        public Builder dividerColor(@ColorInt int color) {
            this.dividerColor = color;
            this.dividerColorSet = true;
            return this;
        }

        // 设置分割线颜色
        public Builder dividerColorRes(@ColorRes int colorRes) {
            return dividerColor(DialogUtils.getColor(this.context, colorRes));
        }

        // 设置分割线颜色
        public Builder dividerColorAttr(@AttrRes int colorAttr) {
            return dividerColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        // 设置背景颜色
        public Builder backgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        // 设置背景颜色
        public Builder backgroundColorRes(@ColorRes int colorRes) {
            return backgroundColor(DialogUtils.getColor(this.context, colorRes));
        }

        // 设置背景颜色
        public Builder backgroundColorAttr(@AttrRes int colorAttr) {
            return backgroundColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        // 点击positive按钮的回调
        public Builder onPositive(@NonNull SingleButtonCallback callback) {
            this.onPositiveCallback = callback;
            return this;
        }

        // 点击negative按钮的回调
        public Builder onNegative(@NonNull SingleButtonCallback callback) {
            this.onNegativeCallback = callback;
            return this;
        }

        // 点击neutral按钮的回调
        public Builder onNeutral(@NonNull SingleButtonCallback callback) {
            this.onNeutralCallback = callback;
            return this;
        }

        // 点击任意一个按钮的回调
        public Builder onAny(@NonNull SingleButtonCallback callback) {
            this.onAnyCallback = callback;
            return this;
        }

        // 调用show方法后的回调
        public Builder showListener(@NonNull OnShowListener listener) {
            this.showListener = listener;
            return this;
        }

        // 调用dismiss方法后的回调
        public Builder dismissListener(@NonNull OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        // 调用cancel方法后的回调
        public Builder cancelListener(@NonNull OnCancelListener listener) {
            this.cancelListener = listener;
            return this;
        }

        // 处理虚拟按键的事件 例如按下back返回键
        public Builder keyListener(@NonNull OnKeyListener listener) {
            this.keyListener = listener;
            return this;
        }

        // 设置主题
        public Builder theme(@NonNull Theme theme) {
            this.theme = theme;
            return this;
        }

        // 设置是否是可取消的
        public Builder cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            this.canceledOnTouchOutside = cancelable;
            return this;
        }

        // 是否可以通过点击外部取消dialog
        public Builder canceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        /**
         * 默认是true, 当它被置为false的时候, 点击action按钮是不会自动取消这个dialog的, 选择列表的时候也不会取消
         *
         * @param dismiss 是否自动dismiss这个dialog
         */
        public Builder autoDismiss(boolean dismiss) {
            this.autoDismiss = dismiss;
            return this;
        }


        // 设置文字列表项
        public Builder items(@NonNull Collection collection) {
            if (collection.size() > 0) {
                final String[] array = new String[collection.size()];
                int i = 0;
                for (Object obj : collection) {
                    array[i] = obj.toString();
                    i++;
                }
                items(array);
            }
            return this;
        }

        // 设置文字列表项
        public Builder items(@ArrayRes int itemsRes) {
            items(this.context.getResources().getTextArray(itemsRes));
            return this;
        }

        // 设置文字列表项
        public Builder items(@NonNull CharSequence... items) {
            if (this.customView != null)
                throw new IllegalStateException("You cannot set items() when you're using a custom view.");
            this.items = items;
            return this;
        }

        // 列表选择的回调事件
        public Builder itemsCallback(@NonNull ListCallback callback) {
            this.listCallback = callback;
            this.listCallbackSingleChoice = null;
            this.listCallbackMultiChoice = null;
            return this;
        }

        // 设置item文本的颜色
        public Builder itemsColor(@ColorInt int color) {
            this.itemColor = color;
            this.itemColorSet = true;
            return this;
        }

        // 设置item文本的颜色
        public Builder itemsColorRes(@ColorRes int colorRes) {
            return itemsColor(DialogUtils.getColor(this.context, colorRes));
        }

        // 设置item文本的颜色
        public Builder itemsColorAttr(@AttrRes int colorAttr) {
            return itemsColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        // 设置item文本的对齐规则
        public Builder itemsGravity(@NonNull GravityEnum gravity) {
            this.itemsGravity = gravity;
            return this;
        }

        // 设置item列表对应的id
        public Builder itemsIds(@NonNull int[] idsArray) {
            this.itemIds = idsArray;
            return this;
        }

        // 设置item列表对应的id
        public Builder itemsIds(@ArrayRes int idsArrayRes) {
            return itemsIds(context.getResources().getIntArray(idsArrayRes));
        }

        // 设置button的对齐规则
        public Builder buttonsGravity(@NonNull GravityEnum gravity) {
            this.buttonsGravity = gravity;
            return this;
        }

        /**
         * 如果传入小于0的索引, 默认是没有item被选择的, 如果想要在初始化的时候, 看到某个item被选中则需要传入对应
         * item的index值
         *
         * @param selectedIndex 想要初始化的item的索引值
         * @param callback      item被选中的回调
         */
        public Builder itemsCallbackSingleChoice(int selectedIndex, @NonNull ListCallbackSingleChoice callback) {
            this.selectedIndex = selectedIndex;
            this.listCallback = null;
            this.listCallbackSingleChoice = callback;
            this.listCallbackMultiChoice = null;
            return this;
        }

        /**
         * 默认当有positive按钮存在的情况下,只有点击positive按钮之后才会回调列表单选的回调接口(ListCallbackSingleChoice),
         * 如果想要无论有没有点击positive按钮都回调接口, 就可以调用这个方法来实现这个功能
         */
        public Builder alwaysCallSingleChoiceCallback() {
            this.alwaysCallSingleChoiceCallback = true;
            return this;
        }

        /**
         * 如果传入null 默认是没有item被选择的, 如果想要在初始化的时候, 看到某几个items被选中则需要传入对应
         * item的index值
         *
         * @param selectedIndices 被选中的items的索引数组
         * @param callback        多选回调接口
         */
        public Builder itemsCallbackMultiChoice(@Nullable Integer[] selectedIndices, @NonNull ListCallbackMultiChoice callback) {
            this.selectedIndices = selectedIndices;
            this.listCallback = null;
            this.listCallbackSingleChoice = null;
            this.listCallbackMultiChoice = callback;
            return this;
        }

        /**
         * 默认当有positive按钮存在的情况下,只有点击positive按钮之后才会回调列表单选的回调接口(ListCallbackMultiChoice),
         * 如果想要无论有没有点击positive按钮都回调接口, 就可以调用这个方法来实现这个功能. 每一次多选都会回调接口
         */
        public Builder alwaysCallMultiChoiceCallback() {
            this.alwaysCallMultiChoiceCallback = true;
            return this;
        }

        // 设置item被选中的指示器
        public Builder listSelector(@DrawableRes int selectorRes) {
            this.listSelector = selectorRes;
            return this;
        }

        // 设置button被选中的指示器
        public Builder btnSelectorStacked(@DrawableRes int selectorRes) {
            this.btnSelectorStacked = selectorRes;
            return this;
        }

        // 设置button被选中的指示器
        public Builder btnSelector(@DrawableRes int selectorRes) {
            this.btnSelectorPositive = selectorRes;
            this.btnSelectorNeutral = selectorRes;
            this.btnSelectorNegative = selectorRes;
            return this;
        }

        // button被选中的指示器
        public Builder btnSelector(@DrawableRes int selectorRes, @NonNull DialogAction which) {
            switch (which) {
                default:
                    this.btnSelectorPositive = selectorRes;
                    break;
                case NEUTRAL:
                    this.btnSelectorNeutral = selectorRes;
                    break;
                case NEGATIVE:
                    this.btnSelectorNegative = selectorRes;
                    break;
            }
            return this;
        }

        /**
         * todo
         * Sets the gravity used for the text in stacked action buttons. By default, it's #{@link GravityEnum#END}.
         *
         * @param gravity The gravity to use.
         * @return The Builder instance so calls can be chained.
         */
        public Builder btnStackedGravity(@NonNull GravityEnum gravity) {
            this.btnStackedGravity = gravity;
            return this;
        }


        /**
         * Sets a custom {@link android.widget.ListAdapter} for the dialog's list
         *
         * @param adapter  The adapter to set to the list.
         * @param callback The callback invoked when an item in the list is selected.
         */
        public Builder adapter(@NonNull ListAdapter adapter, @Nullable ListCallback callback) {
            if (this.customView != null)
                throw new IllegalStateException("You cannot set adapter() when you're using a custom view.");
            this.adapter = adapter;
            this.listCallbackCustom = callback;
            return this;
        }

        /**
         * 限制显示icon图片的大小为48dp
         */
        public Builder limitIconToDefaultSize() {
            this.limitIconToDefaultSize = true;
            return this;
        }

        // 限制设置最大的图片数量
        public Builder maxIconSize(int maxIconSize) {
            this.maxIconSize = maxIconSize;
            return this;
        }

        // 限制设置最大的图片数量
        public Builder maxIconSizeRes(@DimenRes int maxIconSizeRes) {
            return maxIconSize((int) this.context.getResources().getDimension(maxIconSizeRes));
        }

        // 是否强制为stacking模式
        public Builder forceStacking(boolean stacked) {
            this.forceStacking = stacked;
            return this;
        }

        // 设置输入
        public Builder input(@Nullable CharSequence hint, @Nullable CharSequence prefill, boolean allowEmptyInput, @NonNull InputCallback callback) {
            if (this.customView != null)
                throw new IllegalStateException("You cannot set content() when you're using a custom view.");
            this.inputCallback = callback;
            this.inputHint = hint;
            this.inputPrefill = prefill;
            this.inputAllowEmpty = allowEmptyInput;
            return this;
        }

        // 设置输入
        public Builder input(@Nullable CharSequence hint, @Nullable CharSequence prefill, @NonNull InputCallback callback) {
            return input(hint, prefill, true, callback);
        }

        // 设置输入
        public Builder input(@StringRes int hint, @StringRes int prefill, boolean allowEmptyInput, @NonNull InputCallback callback) {
            return input(hint == 0 ? null : context.getText(hint), prefill == 0 ? null : context.getText(prefill), allowEmptyInput, callback);
        }

        // 设置输入
        public Builder input(@StringRes int hint, @StringRes int prefill, @NonNull InputCallback callback) {
            return input(hint, prefill, true, callback);
        }

        // 设置输入类型
        public Builder inputType(int type) {
            this.inputType = type;
            return this;
        }

        // 设置输入范围
        public Builder inputRange(@IntRange(from = 0, to = Integer.MAX_VALUE) int minLength,
                                  @IntRange(from = -1, to = Integer.MAX_VALUE) int maxLength) {
            return inputRange(minLength, maxLength, 0);
        }

        /**
         * 设置输入范围
         * @param errorColor Pass in 0 for the default red error color (as specified in guidelines).
         */
        public Builder inputRange(@IntRange(from = 0, to = Integer.MAX_VALUE) int minLength,
                                  @IntRange(from = -1, to = Integer.MAX_VALUE) int maxLength,
                                  @ColorInt int errorColor) {
            if (minLength < 0)
                throw new IllegalArgumentException("Min length for input dialogs cannot be less than 0.");
            this.inputMinLength = minLength;
            this.inputMaxLength = maxLength;
            if (errorColor == 0) {
                this.inputRangeErrorColor = DialogUtils.getColor(context, R.color.md_edittext_error);
            } else {
                this.inputRangeErrorColor = errorColor;
            }
            return this;
        }

        /**
         * 设置输入范围
         * Same as #{@link #inputRange(int, int, int)}, but it takes a color resource ID for the error color.
         */
        public Builder inputRangeRes(@IntRange(from = 0, to = Integer.MAX_VALUE) int minLength,
                                     @IntRange(from = 1, to = Integer.MAX_VALUE) int maxLength,
                                     @ColorRes int errorColor) {
            return inputRange(minLength, maxLength, DialogUtils.getColor(context, errorColor));
        }

        // 是否总是回调输入的接口
        public Builder alwaysCallInputCallback() {
            this.alwaysCallInputCallback = true;
            return this;
        }

        @UiThread
        public MaterialDialog build() {
            return new MaterialDialog(this);
        }

        // 显示dialog
        @UiThread
        public MaterialDialog show() {
            MaterialDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }
}
