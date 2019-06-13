package com.example.androidxdemo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.core.content.PermissionChecker;


import com.example.androidxdemo.R;
import com.example.androidxdemo.receiver.RemoteInputReceiver;
import com.example.commonlibrary.utils.NotificationUtils;
import com.example.commonlibrary.utils.ThreadPool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.example.commonlibrary.utils.NotificationUtils.CHANNEL_ID;


public class PermissionActivity extends AppCompatActivity {
    private static final String TAG = "PermissionActivity";
    private RemoteInputReceiver mReceiver;
    private static final String FILTER = "com.example.utils.receiver.RemoteInputReceiver";
    private TextView mTextView;

    private int max = 100;
    private int current = 0;


    private NotificationCompat.Builder build;
    private NotificationManagerCompat managerCompat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        mTextView = (TextView) findViewById(R.id.text);

        mReceiver = new RemoteInputReceiver(text -> {
            mTextView.setText(text);
            showNotification();
        });

        findViewById(R.id.start).setOnClickListener(v -> {
            //startNotifyManager(PermissionActivity.this);
//                        try {
//                            AppOpsManager manager = (AppOpsManager) getSystemService(APP_OPS_SERVICE);
//                            PackageManager pm = getPackageManager();
//                            PackageInfo info = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
//                            int status = manager.checkOpNoThrow(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, info.applicationInfo.uid, info.packageName);
//                            Log.d(TAG, "run: status = " + status);
//                        } catch (PackageManager.NameNotFoundException e) {
//                            e.printStackTrace();
//                        }
//
//                        Log.d(TAG, "run: checkSystemAlertWindow 悬浮窗权限=" + checkSystemAlertWindow(PermissionActivity.this));
//                        try {
//                            AppOpsManager appOpsMgr = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
//                            Class<?> classType = android.app.AppOpsManager.class;
//                            Field f = classType.getField("OP_SYSTEM_ALERT_WINDOW");
//                            Method m = classType.getDeclaredMethod("checkOp", int.class, int.class, String.class);
//                            m.setAccessible(true);
//                            int status = (Integer) m.invoke(appOpsMgr, f.getInt(appOpsMgr), getApplicationInfo().uid, getPackageName());
//                            Log.d(TAG, "run: final result=" + status);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
            ThreadPool.runOnUiDelay(this::showNotification, 3000);

//                startNotifyManager(PermissionActivity.this);
        });
    }

    public boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context.getPackageName());
                return (mode == AppOpsManager.MODE_ALLOWED);
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean checkAlertWindowsPermission(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1));
            Log.d(TAG, "checkAlertWindowsPermission: mode=" + m);
            return m == AppOpsManager.MODE_ALLOWED;
        }
        catch (Exception ex) { }
        return false;
    }








    /**
     * 权限类型，枚举类型
     */
    public enum PermissionType {
        CAMERA,                 // 相机
        RECORD_AUDIO,           // 录音
        SYSTEM_ALERT_WINDOW,    // 悬浮窗
        WRITE_EXTERNAL_STORAGE, // sdcard
        ACCESS_COARSE_LOCATION, // 粗略定位
        ACCESS_FINE_LOCATION,   // 精细定位
        READ_PHONE_STATE,       // 手机信息
        GET_ACCOUNTS,           // 帐号
        FLOAT_NOTIFY,           // 浮动通知
    }

    /**
     * 检查悬浮窗权限
     */
    public static boolean checkSystemAlertWindow(Context context) {
        return checkPermissionWithType(context, PermissionType.SYSTEM_ALERT_WINDOW);
    }

    /**
     * 检查是否具有type指定的权限
     */
    private static boolean checkPermissionWithType(Context context, PermissionType type) {
        boolean result = true;
        String permission = "" + type;
        if (context != null) {
            if (type == PermissionType.SYSTEM_ALERT_WINDOW && Build.VERSION.SDK_INT >= 23) {
                result = Settings.canDrawOverlays(context);
            } else {
                result = selfPermissionGranted(context, "android.permission." + type);
            }
        } else {

            // 特定判断国内miui
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    AppOpsManager mgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                    PackageManager pm = context.getPackageManager();
                    PackageInfo info;
                    info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
                    Class<?> classType = AppOpsManager.class;
                    Field f = classType.getField("OP_" + permission);
                    Method m = classType.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                    m.setAccessible(true);
                    int status = (Integer) m.invoke(mgr, f.getInt(mgr), info.applicationInfo.uid, info.packageName);
                    // chenyong1 MIUI8用4来做权限标示
                    result = (status != AppOpsManager.MODE_ERRORED && status != AppOpsManager.MODE_IGNORED && status != 4);
                } catch (Exception e) {
                    Log.e(TAG, "权限检查出错时默认返回有权限，异常代码：" + e);
                }
            } else {
                result = checkPermission(context, "android.permission." + permission);
            }
        }
        Log.d(TAG, "call checkPermissionWithType: " + type + " = " + result);
        return result;
    }

    /**
     * 检查某个权限（属于静态检查，即该权限是否在manifest文件中声明）
     */
    private static boolean checkPermission(Context context, String permission) {
        if (null != context) {
            PackageManager pm = context.getPackageManager();
            return (PackageManager.PERMISSION_GRANTED ==
                    pm.checkPermission(permission, context.getPackageName()));
        }
        return false;
    }


    public static boolean selfPermissionGranted(Context context, String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }



    @TargetApi(value = Build.VERSION_CODES.KITKAT)
    private void showNotification() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", "com.wali.live");
        PendingIntent pendingIntent = PendingIntent.getActivity(PermissionActivity.this, 0, intent, 0);

        RemoteInput input = new RemoteInput.Builder(RemoteInputReceiver.REMOTE_KEY)
                .setLabel("回复")
                .build();

        Intent intent1 = new Intent();
        intent1.setAction(FILTER);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(PermissionActivity.this, 0,
                intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "回复L", pendingIntent1)
                //.addRemoteInput(input)
                .build();

        NotificationCompat.MessagingStyle.Message message0 = new NotificationCompat.MessagingStyle.Message("message-0", System.currentTimeMillis(), "wo cao");
        NotificationCompat.MessagingStyle.Message message1 = new NotificationCompat.MessagingStyle.Message(message0.getText(), message0.getTimestamp(), message0.getSender());
        NotificationCompat.MessagingStyle.Message message2 = new NotificationCompat.MessagingStyle.Message(message1.getText(), message1.getTimestamp(), message1.getSender());

        Intent resultIntent = new Intent(this, PermissionActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent newIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        build = new NotificationCompat.Builder(PermissionActivity.this, NotificationUtils.BADGE_CHANNEL_ID)
                // 设置小图标
                .setSmallIcon(R.drawable.feeds_icon_heart_selected)
                // 设置大图标
                //.setLargeIcon(ImageUtils.loadBitmap(R.drawable.photo, new ImageUtils.OperatingBuilder().setCircle(true).build()))
                // 设置通知标题
                .setContentTitle("test title text")
                // 设置通知内容
                .setContentText("test content text")
                // 设置是否能被取消, true为不会被取消
                //.setOngoing(false)
                // 设置当用户点击通知的时候是否会自动消失, 配合setContentIntent()一起使用
                //.setAutoCancel(true)
                // 设置点击事件
                //.setContentIntent(newIntent)
                // 设置优先级
                //.setPriority(NotificationCompat.PRIORITY_HIGH)
                // 添加按钮
                //.addAction(action)
                // 设置进度条 第三个参数来决定是否显示不确定进度的进度条false代表知道具体的进度, true代表不知道哦具体的进度
                //.setProgress(max, current, false)
                // 设置通知类别,当系统设置免打扰模式的时候,这个可以使用让系统来判断是否是面打扰的过滤类别
                //.setCategory(NotificationCompat.CATEGORY_CALL)
                // 设置是否只通知一次,就是说只有当第一次显示通知的时候会提行用户,之后更新的时候不会提醒用户
                //.setOnlyAlertOnce(true)
                // 设置锁屏通知
                //.setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                // 设置扩展类型
                // 图片扩展
                //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(ImageUtils.loadBitmap(R.drawable.photo)).bigLargeIcon(null))
                // Message内容扩展
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("Hey, do you have any plans for tonight? " +
                //        "I was thinking a few of us could go watch a movie at the theater nearby since there won't" +
                //        " be much going on for the next couple of weeks. There are some great options at 6 and 7pm" +
                //        ", but whatever works best for you. If you have any suggestions for dinner beforehand hit reply!"))
                // Message内容扩展
                //.setStyle(new NotificationCompat.InboxStyle().addLine("I'am ok!").addLine("Sorry~ man I can't go there"))
                // 设置回复消息的样式
                //.setStyle(new NotificationCompat.MessagingStyle(Objects.requireNonNull(message0.getSender())).addMessage(message1).addMessage(message2))
                // 设置媒体样式, 使用action来作为图标
                //.addAction(R.drawable.ic_favorite_border_white_24dp, "喜欢", null)
                //.addAction(R.drawable.ic_skip_previous_white_24dp, "前一首", null)
                //.addAction(R.drawable.ic_pause_circle_outline_white_24dp, "暂停", null)
                //.addAction(R.drawable.ic_skip_next_white_24dp, "下一首", null)
                //.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(1, 2, 3 /* #1: pause button */))
                // 设置分组
                //.setGroup(NotificationUtils.GROUP_ID_1)
                // 设置分组的排列顺序
                //.setSortKey("")
                // 设置分组的提醒模式
                // GROUP_ALERT_SUMMARY:每个分组只会提醒用户一次
                // GROUP_ALERT_CHILDREN:静音所有通知
                // GROUP_ALERT_ALL:组里所有的通知都会发出震动或者声音
                //.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
                // 设置分组的parent
                //.setGroupSummary(true)
                // 设置未读消息的数量
                .setNumber(1)
                // 设置NotificationBadge的图标的大小
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
                //.setLargeIcon(ImageUtils.loadBitmap(R.drawable.photo))

        Notification notification = new NotificationCompat.Builder(PermissionActivity.this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("title1")
                .setContentText("content1")
                .setGroup(NotificationUtils.GROUP_ID_1)
                .build();

        Notification notification1 = new NotificationCompat.Builder(PermissionActivity.this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("title2")
                .setContentText("content2")
                .setGroup(NotificationUtils.GROUP_ID_1)
                .build();

        managerCompat = NotificationManagerCompat.from(PermissionActivity.this);
//        managerCompat.notify(NotificationUtils.NOTIFY_ID_1, notification);
//        managerCompat.notify(NotificationUtils.NOTIFY_ID_2, notification1);
        managerCompat.notify(NotificationUtils.SUMMARY_ID, build.build());
        //ThreadPool.runOnIOPool(mAddRunnable);
    }


    private Runnable mAddRunnable = new Runnable() {
        @Override
        public void run() {
            if (current < max) {
                current += (int) (Math.random() * 10 % 5);
                Log.d(TAG, "run: current=" + current);
                ThreadPool.runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        build.setProgress(max, current, false);
                        managerCompat.notify(NotificationUtils.NOTIFY_ID_1, build.build());
                    }
                });
                ThreadPool.runOnWorkerDelay(mAddRunnable, 1000);
            } else {
                ThreadPool.runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        build.setProgress(0, 0, false);
                        managerCompat.notify(NotificationUtils.NOTIFY_ID_1, build.build());
                    }
                });
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(FILTER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    /**
     * 跳转到APP通知设置界面
     */
    public static void startNotifyManager(Activity refs) {
        PackageManager pm = refs.getPackageManager();
        PackageInfo info;
        try {
            info = pm.getPackageInfo(refs.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // android 8.0引导
//            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra("android.provider.extra.APP_PACKAGE", "com.wali.live");
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, "normal");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 由于小米5.0-7.0显示的并非正常app对应的通知界面, 所以改成拉起app详情界面
            // android 5.0-7.0
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", refs.getPackageName());
                intent.putExtra("app_uid", info.applicationInfo.uid);
//            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//            intent.setData(Uri.parse("package:" + "com.wali.live"));
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", refs.getPackageName(), null));
        }
        refs.startActivity(intent);
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ///< 8.0手机以上
            if (((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).getImportance() == NotificationManager.IMPORTANCE_NONE) {
                return false;
            }
        }

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
