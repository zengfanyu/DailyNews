package com.project.zfy.zhihu.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.sharesdk.framework.ShareSDK;

/**
 * Activity的基类
 * Created by zfy on 2016/8/30.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在入口activity处初始化SharedSDK
        ShareSDK.initSDK(this, "15d2bf5fa8b3f");



    }
}
