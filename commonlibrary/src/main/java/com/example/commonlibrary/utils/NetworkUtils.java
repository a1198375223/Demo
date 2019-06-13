package com.example.commonlibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 网络监控判断
 */
public class NetworkUtils {
    /**
     *     NET_NO       : 没有网络
     *     NET_2G       : 2G
     *     NET_3G       : 3G
     *     NET_4G       : 4G
     *     NET_4G_plus  : 4G+
     *     NET_WIFI     : wifi
     *     NET_UNKNOWN  : 无法识别网络
     */
    public static final int NET_STATUS_NET_NO       = 0;
    public static final int NET_STATUS_NET_2G       = 1;
    public static final int NET_STATUS_NET_3G       = 2;
    public static final int NET_STATUS_NET_4G       = 3;
    public static final int NET_STATUS_NET_4G_plus  = 4;
    public static final int NET_STATUS_NET_WIFI     = 5;
    public static final int NET_STATUS_NET_UNKNOWN  = 6;


    /**
     * 返回的type类型
     * ConnectivityManager.TYPE_WIFI //wifi
     * TelephonyManager.NETWORK_TYPE_GPRS //联通2g
     * TelephonyManager.NETWORK_TYPE_CDMA //电信2g
     * TelephonyManager.NETWORK_TYPE_EDGE //移动2g
     * TelephonyManager.NETWORK_TYPE_1xRTT
     * TelephonyManager.NETWORK_TYPE_IDEN
     *
     * TelephonyManager.NETWORK_TYPE_EVDO_A //电信3g
     * TelephonyManager.NETWORK_TYPE_UMTS
     * TelephonyManager.NETWORK_TYPE_EVDO_0
     * TelephonyManager.NETWORK_TYPE_HSDPA
     * TelephonyManager.NETWORK_TYPE_HSUPA
     * TelephonyManager.NETWORK_TYPE_HSPA
     * TelephonyManager.NETWORK_TYPE_EVDO_B
     * TelephonyManager.NETWORK_TYPE_EHRPD
     * TelephonyManager.NETWORK_TYPE_HSPAP
     * TelephonyManager.NETWORK_TYPE_LTE //4G
     * 19: //4g+
     */
    public static int getActiveNetworkType(Context context) {
        int defaultType = NET_STATUS_NET_UNKNOWN;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return defaultType;
        }
        // 调用cm.getActiveNetworkInfo需要加入permission
        //<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            return info.getType();
        }
        return defaultType;
    }


    /**
     * 将网络的类型转化成为Utils里的静态常量
     */
    public static int corvertActiveNetworkType(Context context) {
        int defaultType = NET_STATUS_NET_UNKNOWN;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return defaultType;
        }
        // 调用cm.getActiveNetworkInfo需要加入permission
        //<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            switch (info.getType()) {
                //wifi
                case ConnectivityManager.TYPE_WIFI:
                    return NET_STATUS_NET_WIFI;
                //mobile net
                case ConnectivityManager.TYPE_MOBILE:
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NET_STATUS_NET_2G;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NET_STATUS_NET_3G;
                        case TelephonyManager.NETWORK_TYPE_LTE: //4G
                            return NET_STATUS_NET_4G;
                        case 19: //4g+
                            return NET_STATUS_NET_4G_plus;
                        default:
                            return NET_STATUS_NET_UNKNOWN;
                    }
            }
        } else if (info != null && !info.isConnectedOrConnecting()) {
            return NET_STATUS_NET_NO;
        }
        return defaultType;
    }

    /**
     * 返回网络的名称
     */
    public static String corvertNetoworkName(int type) {
        if (type == NET_STATUS_NET_2G) {
            return "2G";
        } else if (type == NET_STATUS_NET_3G) {
            return "3G";
        } else if (type == NET_STATUS_NET_4G) {
            return "4G";
        } else if (type == NET_STATUS_NET_4G_plus) {
            return "4G+";
        } else if (type == NET_STATUS_NET_WIFI) {
            return "wifi";
        } else if (type == NET_STATUS_NET_NO) {
            return "没有网络连接";
        }
        return "无法识别的网络";
    }

    public static String getNetworkName(Context context) {
        String defaultValue = "null";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return defaultValue;
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null)
            return defaultValue;
        if (TextUtils.isEmpty(info.getSubtypeName()))
            return info.getTypeName();
        return String.format("%s-%s", info.getTypeName(), info.getSubtypeName());
    }


    /**
     * 判断是否是wifi网络
     */
    public static boolean isWifi(Context context) {
        return getActiveNetworkType(context) == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断是否是手机移动数据
     */
    public static boolean isMobile(Context context) {
        return getActiveNetworkType(context) == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 判断当前是否有网络
     */
    public static boolean hasNetwork(Context context) {
        return getActiveNetworkType(context) != NET_STATUS_NET_UNKNOWN;
    }
}
