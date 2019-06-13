package com.example.commonlibrary.utils;


import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
    private static final String TAG = "DateTimeUtils";
    /**
     * y        Year                年
     * M        Month               月
     * d        Number              月份中的天数
     * D        Number              年中的天数
     * w        Number              年中的周数
     * W        Number              月份中的周数
     * F        Number              月份中的星期
     * E        Text                星期中的天数（周几）
     * a        Text                am/pm的标记
     * H        Number              一天中的小时数（0-23）
     * k        Number              一天中的小时数（1-24）
     * K        Number              am/pm的小时数（0-11）
     * h        Number              am/pm的小时数（1-12）
     * m        Number              小时中的分钟数
     * s        Number              分钟中的秒数
     * S        Number              分钟中的毫秒数
     * z        General time zone   时区
     * Z        RFC 822 time zone   时区
     */

    public static final long MILLIS_ONE_SECOND             = 1000;                        // 一秒的时间戳
    public static final long MILLIS_ONE_MINUTE             = 60 * MILLIS_ONE_SECOND;      // 一分钟时间戳
    public static final long MILLIS_ONE_HOUR               = 60 * MILLIS_ONE_MINUTE;      // 一小时的时间戳
    public static final long MILLIS_ONE_DAY                = 24 * MILLIS_ONE_HOUR;        // 一天的时间戳
    public static final long MILLIS_ONE_WEEK               = 7 * MILLIS_ONE_DAY;          // 一星期的时间戳
    public static final long MILLIS_ONE_MONTH              = 30 * MILLIS_ONE_DAY;         // 一个月的时间戳
    public static final long MILLIS_ONE_YEAR               = 365 * MILLIS_ONE_DAY;        // 一年的时间戳


    public static final String DATETIME_FORMAT_TYPE_1  = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_2  = "yyyy/MM/dd HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_3  = "yyyy.MM.dd HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_4  = "yyyy年MM月dd日 HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_5  = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT_TYPE_6  = "yyyy年MM月dd日";
    public static final String DATETIME_FORMAT_TYPE_7  = "yy年MM月dd日";
    public static final String DATETIME_FORMAT_TYPE_8  = "yy-MM-dd";
    public static final String DATETIME_FORMAT_TYPE_9  = "MM-dd";
    public static final String DATETIME_FORMAT_TYPE_10 = "MM月dd日";
    public static final String DATETIME_FORMAT_TYPE_11 = "yy";
    public static final String DATETIME_FORMAT_TYPE_12 = "yy年";
    public static final String DATETIME_FORMAT_TYPE_13 = "MM";
    public static final String DATETIME_FORMAT_TYPE_14 = "MM月";
    public static final String DATETIME_FORMAT_TYPE_15 = "dd";
    public static final String DATETIME_FORMAT_TYPE_16 = "dd日";
    public static final String DATETIME_FORMAT_TYPE_17 = "HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_18 = "HH:mm";
    public static final String DATETIME_FORMAT_TYPE_19 = "HH";
    public static final String DATETIME_FORMAT_TYPE_20 = "mm:ss";
    public static final String DATETIME_FORMAT_TYPE_21 = "mm";
    public static final String DATETIME_FORMAT_TYPE_22 = "ss";
    public static final String DATETIME_FORMAT_TYPE_23 = "MM月dd日 HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_24 = "MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_25 = "MM月dd日 HH:mm";
    public static final String DATETIME_FORMAT_TYPE_26 = "MM-dd HH:mm";
    public static final String DATETIME_FORMAT_TYPE_27 = "MM.dd.yyyy HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_28 = "MM.dd.yy HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_29 = "MM/dd/yyyy HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_30 = "MM/dd/yy HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_31 = "MM.dd.yyyy HH:mm";
    public static final String DATETIME_FORMAT_TYPE_32 = "MM.dd.yy HH:mm";
    public static final String DATETIME_FORMAT_TYPE_33 = "MM/dd/yy HH:mm";
    public static final String DATETIME_FORMAT_TYPE_34 = "yyyy-MM-dd E HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_35 = "HH:mm:ss E";
    public static final String DATETIME_FORMAT_TYPE_36 = "M/d/yyyy HH:mm";
    public static final String DATETIME_FORMAT_TYPE_37 = "HH:mm E";
    public static final String DATETIME_FORMAT_TYPE_38 = "MM/dd/yy E HH:mm:ss";
    public static final String DATETIME_FORMAT_TYPE_39 = "MM/dd/yy E HH:mm:ss a";
    public static final String DATETIME_FORMAT_TYPE_40 = "HH:mm:ss a E";
    public static final String DATETIME_FORMAT_TYPE_41 = "M/d/yyyy HH:mm:ss";


    /**
     * 将字符串解析成Date
     */
    public static Date parseString(String time) {
        return parseString(time, DATETIME_FORMAT_TYPE_1);
    }

    public static Date parseString(String time, String format) {
        return parseString(time, format, 0);
    }

    public static Date parseString(String time, String format, int begin) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(time, new ParsePosition(begin));
    }


    /**
     * 将时间戳转化成Date
     */
    public static Date parseTimestamp(long time) {
        return new Date(time);
    }


    /**
     * 将时间戳转化为字符串
     */
    public static String formatTimestamp(long time) {
        return formatTimestamp(time, DATETIME_FORMAT_TYPE_1);
    }

    public static String formatTimestamp(long time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date(time));

    }

    /**
     * 将字符串转换成时间戳
     */
    public static long parseStringToTimestamp(String time) {
        return parseString(time).getTime();
    }

    public static long parseStringToTimestamp(String time, String format) {
        return parseString(time, format).getTime();
    }

    public static long parseStringToTimestamp(String time, String format, int begin) {
        return parseString(time, format, begin).getTime();
    }


    /**
     * 将date转化成时间戳
     */
    public static long parseDate(Date date) {
        return date.getTime();
    }


    /**
     * 将date转化为字符串
     */
    public static String formatDate(Date date) {
        return formatDate(date);
    }

    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }


    /**
     * 通过Date来判断是否是昨天
     */
    public static boolean isYesterday(Date time) {
        return isYesterday(time.getTime());
    }

    /**
     * 判断时间戳是否是昨天
     */
    public static boolean isYesterday(long time) {
        if (time > 0) {
            long now = System.currentTimeMillis();
            long result = now / MILLIS_ONE_DAY - time / MILLIS_ONE_DAY;
            Log.d(TAG, "isYesterday: result = " + result);
            return result >= 1;
        }
        return false;
    }


    /**
     * 判断time是否是before天前
     */
    public static boolean isDayBefore(long time, int before) {
        return (System.currentTimeMillis() / MILLIS_ONE_DAY - time / MILLIS_ONE_DAY) >= before;
    }

    /**
     * 判断time是否是after天后
     */
    public static boolean isDayAfter(long time, int after) {
        return (time / MILLIS_ONE_DAY - System.currentTimeMillis() / MILLIS_ONE_DAY) >= after;
    }

    /**
     * 判断time是否是before周前
     */
    public static boolean isWeekBefore(long time, int before) {
        return (System.currentTimeMillis() / MILLIS_ONE_WEEK - time / MILLIS_ONE_WEEK) >= before;
    }

    /**
     * 判断time是否是after周后
     */
    public static boolean isWeekAfter(long time, int after) {
        return (time / MILLIS_ONE_WEEK - System.currentTimeMillis() / MILLIS_ONE_WEEK) >= after;
    }


    /**
     * 判断time是否是before月前
     */
    public static boolean isMonthBefore(long time, int before) {
        return (System.currentTimeMillis() / MILLIS_ONE_MONTH - time / MILLIS_ONE_MONTH) >= before;
    }

    /**
     * 判断time是否是after月后
     */
    public static boolean isMonthAfter(long time, int after) {
        return (time / MILLIS_ONE_MONTH - System.currentTimeMillis() / MILLIS_ONE_MONTH ) >= after;
    }

    /**
     * 判断time是否是before年前
     */
    public static boolean isYearBefore(long time, int before) {
        return (System.currentTimeMillis() / MILLIS_ONE_YEAR - time / MILLIS_ONE_YEAR) >= before;
    }

    /**
     * 判断time是否是after年后
     */
    public static boolean isYearAfter(long time, int after) {
        return (time / MILLIS_ONE_YEAR - System.currentTimeMillis() / MILLIS_ONE_YEAR) >= after;
    }

    /**
     * 判断time是否是before小时前
     */
    public static boolean isHourBefore(long time, int before) {
        return (System.currentTimeMillis()/ MILLIS_ONE_HOUR - time / MILLIS_ONE_HOUR) >= before;
    }

    /**
     * 判断time是否是after小时后
     */
    public static boolean isHourAfter(long time, int after) {
        return (time / MILLIS_ONE_HOUR - System.currentTimeMillis() / MILLIS_ONE_HOUR) >= after;
    }

    /**
     * 判断是否是今天
     */
    public static boolean isToday(long time) {
        return System.currentTimeMillis() / MILLIS_ONE_DAY - time / MILLIS_ONE_DAY == 0;
    }


    /**
     * 判断是否是今年
     */
    public static boolean isThisYear(long time) {
        return System.currentTimeMillis() / MILLIS_ONE_YEAR - time / MILLIS_ONE_YEAR == 0;
    }

    /**
     * 判断是否是这周
     */
    public static boolean isThisWeek(long time) {
        return System.currentTimeMillis() / MILLIS_ONE_WEEK - time / MILLIS_ONE_WEEK == 0;
    }


    /**
     * 判断是否是这个月
     */
    public static boolean isThisMonth(long time) {
        return System.currentTimeMillis() / MILLIS_ONE_MONTH - time / MILLIS_ONE_MONTH == 0;
    }
}
