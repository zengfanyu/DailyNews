package com.project.zfy.zhihu.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;
import com.orhanobut.logger.Logger;
import com.project.zfy.zhihu.global.Constant;

/**
 * AsyncHttp库的方法封装
 * Created by zfy on 2016/8/1.
 */
public class HttpUtils {

    private static AsyncHttpClient client = new AsyncHttpClient();


    /**
     * AsyncHttp的get方法
     *
     * @param url             BaseUrl之后的内容
     * @param responseHandler 用于接收请求结果
     * @return void
     * @author zfy
     * @created at 2016/8/1 21:54
     */
    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(Constant.BASEURL + url, responseHandler);
    }


    /**
     * AsyncHttp的get请求,用于拿到图片
     *
     * @param url             链接
     * @param responseHandler 用于接收请求结果
     * @return void
     * @author zfy
     * @created at 2016/8/1 21:55
     */
    public static void getImage(String url, ResponseHandlerInterface responseHandler) {
        client.get(url, responseHandler);
    }

    /**
     * 判断网络连接是否成功的方法
     *
     * @author zfy
     * @created at 2016/8/1 21:53
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            //若果没有网络连接,此时返回值NetworkInfo 为空
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

            Logger.d("网络状况:" + mNetworkInfo);

            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


}
