package com.example.dialog;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dialog.internal.MDButton;
import com.example.dialog.util.DialogUtils;

import java.io.File;

public class DialogActivity extends AppCompatActivity {
    private static final String TAG = "DialogActivity";
    private MDButton mButton;
    int count = 0;

    // custom view dialog
    private EditText passwordInput;
    private View positiveAction;

    // color chooser dialog
    private int primaryPreselect;
    private int accentPreselect;

    // UTILITY METHODS

    private Toast mToast;
    private Thread mThread;
    private final static int STORAGE_PERMISSION_RC = 69;
    private Handler mHandler;

    private int chooserDialog;

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void startThread(Runnable run) {
        if (mThread != null)
            mThread.interrupt();
        mThread = new Thread(run);
        mThread.start();
    }

    private void showToast(@StringRes int message) {
        showToast(getString(message));
    }

    // BEGIN SAMPLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        mButton = findViewById(R.id.md_button);
//        mButton.setDefaultSelector(getResources().getDrawable(R.drawable.md_btn_selector, null));
        mButton.setOnClickListener(view -> {
            // 测试大写
            if (count % 4 == 0) {
//                mButton.setStackedGravity(GravityEnum.CENTER);
                mButton.setAllCapsCompat(true);
            } else if (count % 4 == 1) {
//                mButton.setStackedGravity(GravityEnum.END);
                mButton.setDefaultSelector(getResources().getDrawable(R.drawable.md_btn_selector, null));
            } else if (count % 4 == 2) {
//                mButton.setStackedGravity(GravityEnum.START);
                mButton.setStackedSelector(getResources().getDrawable(R.drawable.md_btn_selector_dark, null));
            } else {
                mButton.setAllCapsCompat(false);
            }
            count++;
        });

        mHandler = new Handler();
        primaryPreselect = DialogUtils.resolveColor(this, R.attr.colorPrimary);
        accentPreselect = DialogUtils.resolveColor(this, R.attr.colorAccent);

        // basic
        findViewById(R.id.basicNoTitle).setOnClickListener(view -> showBasicNoTitle());
        findViewById(R.id.basic).setOnClickListener(view -> showBasic());
        findViewById(R.id.basicLongContent).setOnClickListener(view -> showBasicLongContent());
        findViewById(R.id.basicLongContent).setOnClickListener(view -> showBasicLongContent());
        findViewById(R.id.basicIcon).setOnClickListener(view -> showBasicIcon());

        // stacked
        findViewById(R.id.stacked).setOnClickListener(view -> showStacked());

        // action button
        findViewById(R.id.neutral).setOnClickListener(view -> showNeutral());

        // callbacks
        findViewById(R.id.callbacks).setOnClickListener(view -> showCallbacks());
        findViewById(R.id.showCancelDismiss).setOnClickListener(view -> showShowCancelDismissCallbacks());

        // list
        findViewById(R.id.list).setOnClickListener(view -> showList());
        findViewById(R.id.listNoTitle).setOnClickListener(view -> showListNoTitle());
        findViewById(R.id.longList).setOnClickListener(view -> showLongList());
        findViewById(R.id.list_longItems).setOnClickListener(view -> showListLongItems());
        findViewById(R.id.simpleList).setOnClickListener(view -> showSimpleList());
        findViewById(R.id.customListItems).setOnClickListener(view -> showCustomList());

        // single
        findViewById(R.id.singleChoice).setOnClickListener(view -> showSingleChoice());
        findViewById(R.id.singleChoice_longItems).setOnClickListener(view -> showSingleChoiceLongItems());

        // multi
        findViewById(R.id.multiChoice).setOnClickListener(view -> showMultiChoice());
        findViewById(R.id.multiChoice_longItems).setOnClickListener(view -> showMultiChoiceLongItems());
        findViewById(R.id.multiChoiceLimited).setOnClickListener(view -> showMultiChoiceLimited());

        // custom view
        findViewById(R.id.customView).setOnClickListener(view -> showCustomView());
        findViewById(R.id.customView_webView).setOnClickListener(view -> showCustomWebView());
//        findViewById(R.id.customViewFrame).setOnClickListener(view -> );


        // color
        findViewById(R.id.colorChooser_primary).setOnClickListener(view -> showColorChooserPrimary());
        findViewById(R.id.colorChooser_accent).setOnClickListener(view -> showColorChooserAccent());
        findViewById(R.id.colorChooser_customColors).setOnClickListener(view -> showColorChooserCustomColors());
        findViewById(R.id.colorChooser_customColorsNoSub).setOnClickListener(view -> showColorChooserCustomColorsNoSub());

        // theme
        findViewById(R.id.themed).setOnClickListener(view -> showThemed());

        // file
        findViewById(R.id.file_chooser).setOnClickListener(view -> showFileChooser());

        // folder
        findViewById(R.id.folder_chooser).setOnClickListener(view -> showFolderChooser());

        // input
        findViewById(R.id.input).setOnClickListener(view -> showInputDialog());
        findViewById(R.id.input_custominvalidation).setOnClickListener(view -> showInputDialogCustomInvalidation());

        // progress
        findViewById(R.id.progress1).setOnClickListener(view -> showProgressDeterminateDialog());
        findViewById(R.id.progress2).setOnClickListener(view -> showProgressIndeterminateDialog());
        findViewById(R.id.progress3).setOnClickListener(view -> showProgressHorizontalIndeterminateDialog());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mThread != null && !mThread.isInterrupted() && mThread.isAlive())
            mThread.interrupt();
    }

    public void showBasicNoTitle() {
        new MaterialDialog.Builder(this)
                .content(R.string.shareLocationPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    public void showBasic() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    public void showBasicLongContent() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.loremIpsum)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    public void showBasicIcon() {
        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.ic_launcher)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    public void showStacked() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.speedBoost)
                .negativeText(R.string.noThanks)
                .btnStackedGravity(GravityEnum.END)
                .forceStacking(true)  // this generally should not be forced, but is used for demo purposes
                .show();
    }

    public void showNeutral() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .neutralText(R.string.more_info)
                .show();
    }

    public void showCallbacks() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .neutralText(R.string.more_info)
                .onAny((dialog, which) -> showToast(which.name() + "!"))
                .show();
    }

    public void showList() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallback((dialog, view, which, text) -> showToast(which + ": " + text))
                .show();
    }

    public void showListNoTitle() {
        new MaterialDialog.Builder(this)
                .items(R.array.socialNetworks)
                .itemsCallback((dialog, view, which, text) -> showToast(which + ": " + text))
                .show();
    }

    public void showLongList() {
        new MaterialDialog.Builder(this)
                .title(R.string.states)
                .items(R.array.states)
                .itemsCallback((dialog, view, which, text) -> showToast(which + ": " + text))
                .positiveText(android.R.string.cancel)
                .show();
    }

    public void showListLongItems() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks_longItems)
                .itemsCallback((dialog, view, which, text) -> showToast(which + ": " + text))
                .show();
    }

    public void showSingleChoice() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallbackSingleChoice(2, (dialog, view, which, text) -> {
                    showToast(which + ": " + text);
                    return true; // allow selection
                })
                .positiveText(R.string.md_choose_label)
                .show();
    }

    public void showSingleChoiceLongItems() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks_longItems)
                .itemsCallbackSingleChoice(2, (dialog, view, which, text) -> {
                    showToast(which + ": " + text);
                    return true; // allow selection
                })
                .positiveText(R.string.md_choose_label)
                .show();
    }

    public void showMultiChoice() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallbackMultiChoice(new Integer[]{1, 3}, (dialog, which, text) -> {
                    StringBuilder str = new StringBuilder();
                    for (int i = 0; i < which.length; i++) {
                        if (i > 0) str.append('\n');
                        str.append(which[i]);
                        str.append(": ");
                        str.append(text[i]);
                    }
                    showToast(str.toString());
                    return true; // allow selection
                })
                .onNeutral((dialog, which) -> dialog.clearSelectedIndices())
                .alwaysCallMultiChoiceCallback()
                .positiveText(R.string.md_choose_label)
                .autoDismiss(false)
                .neutralText(R.string.clear_selection)
                .show();
    }


    public void showMultiChoiceLimited() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallbackMultiChoice(new Integer[]{1}, (dialog, which, text) -> {
                    boolean allowSelection = which.length <= 2; // limit selection to 2, the new selection is included in the which array
                    if (!allowSelection) {
                        showToast(R.string.selection_limit_reached);
                    }
                    return allowSelection;
                })
                .positiveText(R.string.dismiss)
                .alwaysCallMultiChoiceCallback() // the callback will always be called, to check if selection is still allowed
                .show();
    }

    public void showMultiChoiceLongItems() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks_longItems)
                .itemsCallbackMultiChoice(new Integer[]{1, 3}, (dialog, which, text) -> {
                    StringBuilder str = new StringBuilder();
                    for (int i = 0; i < which.length; i++) {
                        if (i > 0) str.append('\n');
                        str.append(which[i]);
                        str.append(": ");
                        str.append(text[i]);
                    }
                    showToast(str.toString());
                    return true; // allow selection
                })
                .positiveText(R.string.md_choose_label)
                .show();
    }


    public void showSimpleList() {
//        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(this);
//        adapter.add(new MaterialSimpleListItem.Builder(this)
//                .content("username@gmail.com")
//                .icon(R.drawable.ic_account_circle)
//                .backgroundColor(Color.WHITE)
//                .build());
//        adapter.add(new MaterialSimpleListItem.Builder(this)
//                .content("user02@gmail.com")
//                .icon(R.drawable.ic_account_circle)
//                .backgroundColor(Color.WHITE)
//                .build());
//        adapter.add(new MaterialSimpleListItem.Builder(this)
//                .content(R.string.add_account)
//                .icon(R.drawable.ic_content_add)
//                .iconPaddingDp(8)
//                .build());
//
//        new MaterialDialog.Builder(this)
//                .title(R.string.set_backup)
//                .adapter(adapter, new MaterialDialog.ListCallback() {
//                    @Override
//                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
//                        MaterialSimpleListItem item = adapter.getItem(which);
//                        showToast(item.getContent().toString());
//                    }
//                })
//                .show();
    }

    public void showCustomList() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .adapter(new ButtonItemAdapter(this, R.array.socialNetworks),
                        (dialog, itemView, which, text) -> showToast("Clicked item " + which))
                .show();
    }

    @SuppressWarnings("ResourceAsColor")
    public void showCustomView() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.googleWifi)
                .customView(R.layout.dialog_customview, true)
                .positiveText(R.string.connect)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog1, which) -> showToast("Password: " + passwordInput.getText().toString())).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        //noinspection ConstantConditions
        passwordInput = dialog.getCustomView().findViewById(R.id.password);
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Toggling the show password CheckBox will mask or unmask the password input EditText
        CheckBox checkbox = dialog.getCustomView().findViewById(R.id.showPassword);
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            passwordInput.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
            passwordInput.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
        });

        int widgetColor = ThemeSingleton.get().widgetColor;
        MDTintHelper.setTint(checkbox,
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.material_teal_a400) : widgetColor);

        MDTintHelper.setTint(passwordInput,
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.material_teal_a400) : widgetColor);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }


    public void showCustomWebView() {
        int accentColor = ThemeSingleton.get().widgetColor;
        if (accentColor == 0)
            accentColor = ContextCompat.getColor(this, R.color.material_teal_a400);
        ChangelogDialog.create(false, accentColor)
                .show(getSupportFragmentManager(), "changelog");
    }

    public void showColorChooserPrimary() {
//        new ColorChooserDialog.Builder(this, R.string.color_palette)
//                .titleSub(R.string.colors)
//                .preselect(primaryPreselect)
//                .show();
    }

    public void showColorChooserAccent() {
//        new ColorChooserDialog.Builder(this, R.string.color_palette)
//                .titleSub(R.string.colors)
//                .accentMode(true)
//                .preselect(accentPreselect)
//                .show();
    }


    public void showColorChooserCustomColors() {
//        int[][] subColors = new int[][]{
//                new int[]{Color.parseColor("#EF5350"), Color.parseColor("#F44336"), Color.parseColor("#E53935")},
//                new int[]{Color.parseColor("#EC407A"), Color.parseColor("#E91E63"), Color.parseColor("#D81B60")},
//                new int[]{Color.parseColor("#AB47BC"), Color.parseColor("#9C27B0"), Color.parseColor("#8E24AA")},
//                new int[]{Color.parseColor("#7E57C2"), Color.parseColor("#673AB7"), Color.parseColor("#5E35B1")},
//                new int[]{Color.parseColor("#5C6BC0"), Color.parseColor("#3F51B5"), Color.parseColor("#3949AB")},
//                new int[]{Color.parseColor("#42A5F5"), Color.parseColor("#2196F3"), Color.parseColor("#1E88E5")}
//        };
//
//        new ColorChooserDialog.Builder(this, R.string.color_palette)
//                .titleSub(R.string.colors)
//                .preselect(primaryPreselect)
//                .customColors(R.array.custom_colors, subColors)
//                .show();
    }

    public void showColorChooserCustomColorsNoSub() {
//        new ColorChooserDialog.Builder(this, R.string.color_palette)
//                .titleSub(R.string.colors)
//                .preselect(primaryPreselect)
//                .customColors(R.array.custom_colors, null)
//                .show();
    }

    // Receives callback from color chooser dialog
//    @Override
//    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int color) {
//        if (dialog.isAccentMode()) {
//            accentPreselect = color;
//            ThemeSingleton.get().positiveColor = DialogUtils.getActionTextStateList(this, color);
//            ThemeSingleton.get().neutralColor = DialogUtils.getActionTextStateList(this, color);
//            ThemeSingleton.get().negativeColor = DialogUtils.getActionTextStateList(this, color);
//            ThemeSingleton.get().widgetColor = color;
//        } else {
//            primaryPreselect = color;
//            if (getSupportActionBar() != null)
//                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                getWindow().setStatusBarColor(CircleView.shiftColorDown(color));
//                getWindow().setNavigationBarColor(color);
//            }
//        }
//    }

    public void showThemed() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .positiveColorRes(R.color.material_red_400)
                .negativeColorRes(R.color.material_red_400)
                .titleGravity(GravityEnum.CENTER)
                .titleColorRes(R.color.material_red_400)
                .contentColorRes(android.R.color.white)
                .backgroundColorRes(R.color.material_blue_grey_800)
                .dividerColorRes(R.color.material_teal_a400)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .theme(Theme.DARK)
                .show();
    }

    public void showShowCancelDismissCallbacks() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .neutralText(R.string.more_info)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        showToast("onShow");
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        showToast("onCancel");
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        showToast("onDismiss");
                    }
                })
                .show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showFileChooser() {
//        chooserDialog = R.id.file_chooser;
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_RC);
//            return;
//        }
//        new FileChooserDialog.Builder(this)
//                .show();
    }

//    @Override
//    public void onFileSelection(@NonNull FileChooserDialog dialog, @NonNull File file) {
//        showToast(file.getAbsolutePath());
//    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showFolderChooser() {
//        chooserDialog = R.id.folder_chooser;
//        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_RC);
//            return;
//        }
//        new FolderChooserDialog.Builder(MainActivity.this)
//                .chooseButton(R.string.md_choose_label)
//                .show();
    }

//    @Override
//    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {
//        showToast(folder.getAbsolutePath());
//    }

    public void showInputDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.input)
                .content(R.string.input_content)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 16)
                .positiveText(R.string.submit)
                .input(R.string.input_hint, R.string.input_hint, false, (dialog, input) -> showToast("Hello, " + input.toString() + "!")).show();
    }

    public void showInputDialogCustomInvalidation() {
        new MaterialDialog.Builder(this)
                .title(R.string.input)
                .content(R.string.input_content_custominvalidation)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText(R.string.submit)
                .alwaysCallInputCallback() // this forces the callback to be invoked with every input change
                .input(R.string.input_hint, 0, false, (dialog, input) -> {
                    if (input.toString().equalsIgnoreCase("hello")) {
                        dialog.setContent("I told you not to type that!");
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    } else {
                        dialog.setContent(R.string.input_content_custominvalidation);
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                }).show();
    }


    public void showProgressDeterminateDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .contentGravity(GravityEnum.CENTER)
                .progress(false, 150, true)
                .cancelListener(dialog -> {
                    if (mThread != null)
                        mThread.interrupt();
                })
                .showListener(dialogInterface -> {
                    final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                    startThread(() -> {
                        while (dialog.getCurrentProgress() != dialog.getMaxProgress() &&
                                !Thread.currentThread().isInterrupted()) {
                            if (dialog.isCancelled())
                                break;
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                break;
                            }
                            dialog.incrementProgress(1);
                        }
                        runOnUiThread(() -> {
                            mThread = null;
                            dialog.setContent(getString(R.string.md_done_label));
                        });

                    });
                }).show();
    }

    public void showProgressIndeterminateDialog() {
        showIndeterminateProgressDialog(false);
    }

    public void showProgressHorizontalIndeterminateDialog() {
        showIndeterminateProgressDialog(true);
    }

    private void showIndeterminateProgressDialog(boolean horizontal) {
        new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(horizontal)
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
//            AboutDialog.show(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_RC) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mHandler.postDelayed(() -> findViewById(chooserDialog).performClick(), 1000);
            } else {
                Toast.makeText(this, "The folder or file chooser will not work without permission to read external storage.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
