package com.project.zfy.zhihu.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.global.Constant;
import com.project.zfy.zhihu.utils.HttpUtils;
import com.project.zfy.zhihu.utils.IOUtils;
import com.project.zfy.zhihu.utils.SharedPreferenceUtils;
import com.project.zfy.zhihu.utils.ToastUtils;
import com.project.zfy.zhihu.utils.UIUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.sharesdk.framework.ShareSDK;

public class SplashActivity extends BaseActivity {

    private ImageView iv_start;
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在入口activity出初始化SharedSDK
        ShareSDK.initSDK(this, "15d2bf5fa8b3f");

        setContentView(R.layout.activity_splash);


        initView();
        initAnimation();

    }


    /**
     * 初始化布局的方法
     *
     * @author zfy
     * @created at 2016/8/1 17:25
     */
    private void initView() {
        iv_start = (ImageView) findViewById(R.id.iv_start);
        //获取/data/data//files目录的绝对路径
        File dir = getFilesDir();
        //创建/data/data/files/
        imageFile = new File(dir, "start.jpg");
        if (imageFile.exists()) {
            iv_start.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        } else {
            iv_start.setBackgroundResource(R.mipmap.start);

        }
    }

    /**
     * 初始化动画的方法
     *
     * @author zfy
     * @created at 2016/8/1 17:25
     */
    private void initAnimation() {
        ScaleAnimation scaleAnimation =
                new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
        //设置动画时间
        scaleAnimation.setDuration(3000);
        //保持动画完成后的样子
        scaleAnimation.setFillAfter(true);
        //对动画设置监听
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (HttpUtils.isNetworkConnected(SplashActivity.this)) {
                    //拿到开始界面的数据
                    HttpUtils.get(Constant.START, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {

                                //解析Json数据,注意转换为String类型
                                JSONObject jsonObject = new JSONObject(new String(responseBody));
                                String imgUrl = jsonObject.getString("img");

                                //从开始界面的数据中,拿到图片的数据
                                HttpUtils.getImage(imgUrl, new BinaryHttpResponseHandler() {
                                    @Override //注意此处图片为字节文件
                                    public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                                        //将图片存入缓存文件夹中
                                        saveImage(imageFile, binaryData);

                                        boolean isFirstEnter = SharedPreferenceUtils.getBoolean(SplashActivity.this, Constant.IS_FIRST_ENTER, true);
                                        //跳转到MainActivity
                                        startActivity(isFirstEnter);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                                        ToastUtils.ToastUtils(getApplicationContext(), "网络开小差了...");

                                        boolean isFirstEnter = SharedPreferenceUtils.getBoolean(SplashActivity.this, Constant.IS_FIRST_ENTER, true);
                                        //跳转到MainActivity
                                        startActivity(isFirstEnter);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                } else {
                    ToastUtils.ToastUtils(UIUtils.getContext(), "没有网络连接!!!");

                    boolean isFirstEnter = SharedPreferenceUtils.getBoolean(SplashActivity.this, Constant.IS_FIRST_ENTER, true);
                    //跳转到MainActivity
                    startActivity(isFirstEnter);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //启动动画
        iv_start.startAnimation(scaleAnimation);
    }


    /**
     * 将二进制图片存入缓存文件夹中的方法
     *
     * @param file  缓存文件夹
     * @param bytes 图片的二进制数据
     * @return void
     * @author zfy
     * @created at 2016/8/2 9:16
     */
    public void saveImage(File file, byte[] bytes) {
        FileOutputStream outputStream = null;
        try {

            if (file.exists()) {
                file.delete();
            }

            outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(outputStream);
        }
    }


    /**
     * 跳转到下一个Activity的方法
     *
     * @author zfy
     * @created at 2016/8/2 9:24
     */
    public void startActivity(boolean isFirstEnter) {


        Intent intent;
        if (isFirstEnter) {
            intent = new Intent(SplashActivity.this, GuideActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        startActivity(intent);
        //Activity之间跳转的动画效果
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
