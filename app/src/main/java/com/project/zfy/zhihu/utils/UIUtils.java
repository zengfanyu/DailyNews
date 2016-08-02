package com.project.zfy.zhihu.utils;

import android.content.Context;
import android.os.Handler;

import com.project.zfy.zhihu.global.MyApplication;

/**
 * $desc
 * Created by zfy on 2016/8/2.
 */
public class UIUtils {

    public static Context getContext() {
        return MyApplication.getContext();
    }

    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    public static Handler getHandler() {
        return MyApplication.getHandler();
    }
}
