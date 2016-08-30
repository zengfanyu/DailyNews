package com.project.zfy.zhihu.activity;


import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.fragment.LatestContentFragment;
import com.project.zfy.zhihu.global.Constant;
import com.project.zfy.zhihu.moudle.StoriesEntity;
import com.project.zfy.zhihu.utils.UIUtils;
import com.project.zfy.zhihu.view.RevealBackgroundView;

/**
 * 最新新闻的activity
 * Created by zfy on 2016/8/5.
 */
public class LatestContentActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {

    private LatestContentFragment mLatestContentFragment;

    private int[] mStartingLocation;

    private StoriesEntity mEntity;

    public RevealBackgroundView mBackgroundView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_content);


        mBackgroundView = (RevealBackgroundView) findViewById(R.id.rbv_view);

        mStartingLocation = getIntent().getIntArrayExtra(Constant.START_LOCATION);

        mEntity = (StoriesEntity) getIntent().getSerializableExtra("entity");

        FragmentManager fm = getSupportFragmentManager();

        mLatestContentFragment = (LatestContentFragment) fm.findFragmentById(R.id.id_fragment_container);

        if (mLatestContentFragment == null) {

            mLatestContentFragment = LatestContentFragment.newInstance(mEntity);
            fm.beginTransaction().add(R.id.id_fragment_container, mLatestContentFragment).commit();

        }

        initAnimation(savedInstanceState);


    }

    public void initAnimation(Bundle bundle) {
        setupRevealBackground(bundle);
        setStatusBarColor(UIUtils.getColor(R.color.light_toolbar));

    }

    public void setupRevealBackground(Bundle bundle) {
        mBackgroundView.setOnStateChangeListener(this);
        if (bundle == null) {

            //view树完成测量并且分配空间而绘制过程还没有开始的时候播放动画
            mBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    //Callback method to be invoked when the view tree is about to be drawn.
                    // At this point, all views in the tree have been measured and given a frame.
                    mBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mBackgroundView.startFromLocation(mStartingLocation);
                    return true;
                }
            });


        } else {
            mBackgroundView.setToFinishedFrame();

        }

    }

    @TargetApi(21)
    public void setStatusBarColor(int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // If both system bars are black, we can remove these from our layout,
            // removing or shrinking the SurfaceFlinger overlay required for our views.
            Window window = this.getWindow();
            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }


    @Override
    public void onStateChange(int state) {


        if (mLatestContentFragment != null) {
            if (RevealBackgroundView.STATE_FINISHED == state) {
                mLatestContentFragment.app_bar_layout.setVisibility(View.VISIBLE);
                mLatestContentFragment.fab_float.setVisibility(View.VISIBLE);
                setStatusBarColor(Color.TRANSPARENT);
            }
        }


    }


}