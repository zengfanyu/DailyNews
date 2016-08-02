package com.project.zfy.zhihu.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 封装SP工具类
 * Created by zfy on 2016/8/2.
 */
public class SharedPreferenceUtils {

    private static final String SHARE_PREFS_NAME = "config";
    private static SharedPreferences sSharedPreferences;


    /**
     * 拿boolean数据的方法
     *
     * @author zfy
     * @created at 2016/8/2 12:55
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        if (sSharedPreferences == null) {
            sSharedPreferences = context.getSharedPreferences(SHARE_PREFS_NAME, context.MODE_PRIVATE);
        }
        return sSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * 存boolean数据的方法
     *
     * @author zfy
     * @created at 2016/8/2 12:55
     */
    public static void putBoolean(Context context, String key, boolean defValue) {
        if (sSharedPreferences == null) {
            sSharedPreferences = context.getSharedPreferences(SHARE_PREFS_NAME, context.MODE_PRIVATE);
        }
        sSharedPreferences.edit().putBoolean(key, defValue).commit();

    }

    public static String getString(Context context, String key, String defValue) {
        if (sSharedPreferences == null) {
            sSharedPreferences = context.getSharedPreferences(SHARE_PREFS_NAME, context.MODE_PRIVATE);
        }
        return sSharedPreferences.getString(key, defValue);
    }

    public static void putString(Context context, String key, String defValue) {
        if (sSharedPreferences == null) {
            sSharedPreferences = context.getSharedPreferences(SHARE_PREFS_NAME, context.MODE_PRIVATE);
        }
        sSharedPreferences.edit().putString(key, defValue).commit();

    }
}
