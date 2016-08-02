package com.project.zfy.zhihu.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * $desc
 * Created by zfy on 2016/8/2.
 */
public class ToastUtils {

    private static Toast sToast;

    /**
     * 吐司的工具类 避免了重复延迟出现
     * @author zfy
     * @created at 2016/8/2 12:03
     */
    public static void ToastUtils(Context context, String toastWhat) {

        if (sToast == null) {
            sToast = Toast.makeText(context, toastWhat, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(toastWhat);
        }
        sToast.show();

    }
}
