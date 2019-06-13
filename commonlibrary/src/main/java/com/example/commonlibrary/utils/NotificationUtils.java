package com.example.commonlibrary.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.example.commonlibrary.R;


/**
 * 封装通知
 */
public class NotificationUtils {
    public static final String CHANNEL_ID = "channel_id";
    public static final String BADGE_CHANNEL_ID = "badge_channel_id";
    public static final String GROUP_ID_1 = "group_id_1";


    public static final int SUMMARY_ID = 999;
    public static final int NOTIFY_ID_1 = 1;
    public static final int NOTIFY_ID_2 = 2;


    /*NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            // 设置channel组 这个不常用
            //channel.setGroup("");
            // 设置绕过免打扰模式(只能被原生系统修改)
            //channel.setBypassDnd(true);
            // 设置显示badge
            channel.setShowBadge(true);
            // 判断是否是显示badge
            //channel.canShowBadge();
            // 判断是否绕过免打扰模式
            //channel.canBypassDnd();
            // 判断是否允许呼吸灯
            //channel.shouldShowLights();
            // 设置呼吸灯颜色
            //channel.setLightColor(Color.BLUE);
            // 设置允许呼吸灯
            //channel.enableLights(true);
            // 获取呼吸灯的颜色
            //channel.getLightColor();
            // 设置振动的频率
            //channel.setVibrationPattern(new long[]{3000, 2000, 1000, 500, 1000, 2000, 3000});
            // 设置锁屏的显示方式(只能被原生系统修改)
            //channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            // 设置是否允许振动
            //channel.enableVibration(true);
            // 设置提示声音
            channel.setSound();*/


    /**
     * 最好在application进行初始化
     */
    public static void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = AppUtils.app().getString(R.string.channel_name);
            String description = AppUtils.app().getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            // 设置显示badge
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = AppUtils.app().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }


    /**
     * 初始化NotificationBadge
     */
    public static void createNotificationBadge() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(BADGE_CHANNEL_ID, "badge_channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("badge_channel_description");
            channel.setShowBadge(true);
            NotificationManager notificationManager = (NotificationManager) AppUtils.app().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 删除notification channel
     */
    public static void deleteNotificationChannel() {
        NotificationManager manager = (NotificationManager) AppUtils.app().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.deleteNotificationChannel(CHANNEL_ID);
        }
    }


    /**
     * 取消显示某个notification
     */
    public static void dismissNotification(int notificationId) {
        NotificationManagerCompat compat = NotificationManagerCompat.from(AppUtils.app());
        compat.cancel(notificationId);
    }


//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private void showNotification() {
//        Intent intent = new Intent();
//        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
//        intent.putExtra("android.provider.extra.APP_PACKAGE", "com.wali.live");
//        PendingIntent pendingIntent = PendingIntent.getActivity(PermissionActivity.this, 0, intent, 0);
//
//        RemoteInput input = new RemoteInput.Builder(RemoteInputReceiver.REMOTE_KEY)
//                .setLabel("回复")
//                .build();
//
//        Intent intent1 = new Intent();
//        intent1.setAction(FILTER);
//        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(PermissionActivity.this, 0,
//                intent1, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "回复L", pendingIntent1)
//                //.addRemoteInput(input)
//                .build();
//
//        NotificationCompat.MessagingStyle.Message message0 = new NotificationCompat.MessagingStyle.Message("message-0", System.currentTimeMillis(), "wo cao");
//        NotificationCompat.MessagingStyle.Message message1 = new NotificationCompat.MessagingStyle.Message(message0.getText(), message0.getTimestamp(), message0.getSender());
//        NotificationCompat.MessagingStyle.Message message2 = new NotificationCompat.MessagingStyle.Message(message1.getText(), message1.getTimestamp(), message1.getSender());
//
//        Intent resultIntent = new Intent(this, PermissionActivity.class);
//        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addNextIntentWithParentStack(resultIntent);
//        PendingIntent newIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        build = new NotificationCompat.Builder(PermissionActivity.this, NotificationUtils.BADGE_CHANNEL_ID)
//                // 设置小图标
//                .setSmallIcon(R.drawable.feeds_icon_heart_selected)
//                // 设置大图标
//                //.setLargeIcon(ImageUtils.loadBitmap(R.drawable.photo, new ImageUtils.OperatingBuilder().setCircle(true).build()))
//                // 设置通知标题
//                .setContentTitle("test title text")
//                // 设置通知内容
//                .setContentText("test content text")
//                // 设置是否能被取消, true为不会被取消
//                //.setOngoing(false)
//                // 设置当用户点击通知的时候是否会自动消失, 配合setContentIntent()一起使用
//                //.setAutoCancel(true)
//                // 设置点击事件
//                //.setContentIntent(newIntent)
//                // 设置优先级
//                //.setPriority(NotificationCompat.PRIORITY_HIGH)
//                // 添加按钮
//                //.addAction(action)
//                // 设置进度条 第三个参数来决定是否显示不确定进度的进度条false代表知道具体的进度, true代表不知道哦具体的进度
//                //.setProgress(max, current, false)
//                // 设置通知类别,当系统设置免打扰模式的时候,这个可以使用让系统来判断是否是面打扰的过滤类别
//                //.setCategory(NotificationCompat.CATEGORY_CALL)
//                // 设置是否只通知一次,就是说只有当第一次显示通知的时候会提行用户,之后更新的时候不会提醒用户
//                //.setOnlyAlertOnce(true)
//                // 设置锁屏通知
//                //.setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
//                // 设置扩展类型
//                // 图片扩展
//                //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(ImageUtils.loadBitmap(R.drawable.photo)).bigLargeIcon(null))
//                // Message内容扩展
//                //.setStyle(new NotificationCompat.BigTextStyle().bigText("Hey, do you have any plans for tonight? " +
//                //        "I was thinking a few of us could go watch a movie at the theater nearby since there won't" +
//                //        " be much going on for the next couple of weeks. There are some great options at 6 and 7pm" +
//                //        ", but whatever works best for you. If you have any suggestions for dinner beforehand hit reply!"))
//                // Message内容扩展
//                //.setStyle(new NotificationCompat.InboxStyle().addLine("I'am ok!").addLine("Sorry~ man I can't go there"))
//                // 设置回复消息的样式
//                //.setStyle(new NotificationCompat.MessagingStyle(Objects.requireNonNull(message0.getSender())).addMessage(message1).addMessage(message2))
//                // 设置媒体样式, 使用action来作为图标
//                //.addAction(R.drawable.ic_favorite_border_white_24dp, "喜欢", null)
//                //.addAction(R.drawable.ic_skip_previous_white_24dp, "前一首", null)
//                //.addAction(R.drawable.ic_pause_circle_outline_white_24dp, "暂停", null)
//                //.addAction(R.drawable.ic_skip_next_white_24dp, "下一首", null)
//                //.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(1, 2, 3 /* #1: pause button */))
//                // 设置分组
//                //.setGroup(NotificationUtils.GROUP_ID_1)
//                // 设置分组的排列顺序
//                //.setSortKey("")
//                // 设置分组的提醒模式
//                // GROUP_ALERT_SUMMARY:每个分组只会提醒用户一次
//                // GROUP_ALERT_CHILDREN:静音所有通知
//                // GROUP_ALERT_ALL:组里所有的通知都会发出震动或者声音
//                //.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
//                // 设置分组的parent
//                //.setGroupSummary(true)
//                // 设置未读消息的数量
//                .setNumber(1)
//                // 设置NotificationBadge的图标的大小
//                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
//        //.setLargeIcon(ImageUtils.loadBitmap(R.drawable.photo))
//
//        Notification notification = new NotificationCompat.Builder(PermissionActivity.this, CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("title1")
//                .setContentText("content1")
//                .setGroup(NotificationUtils.GROUP_ID_1)
//                .build();
//
//        Notification notification1 = new NotificationCompat.Builder(PermissionActivity.this, CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("title2")
//                .setContentText("content2")
//                .setGroup(NotificationUtils.GROUP_ID_1)
//                .build();
//
//        managerCompat = NotificationManagerCompat.from(PermissionActivity.this);
////        managerCompat.notify(NotificationUtils.NOTIFY_ID_1, notification);
////        managerCompat.notify(NotificationUtils.NOTIFY_ID_2, notification1);
//        managerCompat.notify(NotificationUtils.SUMMARY_ID, build.build());
//        //ThreadPool.runOnIOPool(mAddRunnable);
//    }

}
