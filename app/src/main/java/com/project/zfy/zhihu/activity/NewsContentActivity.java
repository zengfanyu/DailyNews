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
import com.project.zfy.zhihu.event.NewsFragmentEvent;
import com.project.zfy.zhihu.fragment.NewsContentFragment;
import com.project.zfy.zhihu.moudle.StoriesEntity;
import com.project.zfy.zhihu.utils.UIUtils;
import com.project.zfy.zhihu.view.RevealBackgroundView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 某一主题日报的具体某一条item的activity
 * Created by zfy on 2016/8/6.
 */
public class NewsContentActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {

    private int[] mStartingLocation;

    private StoriesEntity mEntity;

    public RevealBackgroundView mBackgroundView;

    private NewsContentFragment mNewsContentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_content);

        EventBus.getDefault().register(this);

        mBackgroundView = (RevealBackgroundView) findViewById(R.id.rbv_view);

//        mStartingLocation = getIntent().getIntArrayExtra(Constant.START_LOCATION);
//
//        mEntity = (StoriesEntity) getIntent().getSerializableExtra("entity");

        FragmentManager fm = getSupportFragmentManager();

        mNewsContentFragment = (NewsContentFragment) fm.findFragmentById(R.id.id_fragment_container);

        if (mNewsContentFragment == null) {
            mNewsContentFragment = NewsContentFragment.newInstance(mEntity);
            fm.beginTransaction().add(R.id.id_fragment_container, mNewsContentFragment).commit();

        }

        initAnimation(savedInstanceState);

    }

    @Subscribe(threadMode = ThreadMode.MAIN , sticky = true)
    public void onMessageEventMainThread(NewsFragmentEvent event) {
        mStartingLocation=event.getStartingLocation();
        mEntity=event.getEntity();

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
        if (mNewsContentFragment != null) {
            if (RevealBackgroundView.STATE_FINISHED == state) {
                //stateChangeShowView
                mNewsContentFragment.coordinatorLayout.setVisibility(View.VISIBLE);
                mNewsContentFragment.fab_float.setVisibility(View.VISIBLE);

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
