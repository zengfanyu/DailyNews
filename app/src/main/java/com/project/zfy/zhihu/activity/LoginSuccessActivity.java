package com.project.zfy.zhihu.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Explode;

import com.project.zfy.zhihu.R;

/**
 * $desc
 * Created by zfy on 2016/8/30.
 */
public class LoginSuccessActivity extends BaseActivity {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginSuccessActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        };
        handler.postDelayed(runnable, 1000);
    }
}
