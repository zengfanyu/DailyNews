package com.project.zfy.zhihu.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.orhanobut.logger.Logger;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.fragment.LatestContentFragment;
import com.project.zfy.zhihu.global.Constant;
import com.project.zfy.zhihu.moudle.StoriesEntity;
import com.project.zfy.zhihu.utils.UIUtils;
import com.project.zfy.zhihu.view.RevealBackgroundView;

import java.util.List;

/**
 * 主页新闻列表的可滑动Activity
 * Created by zfy on 2016/8/15.
 */
public class LatestContentPagerActivity extends BaseActivity implements RevealBackgroundView.OnStateChangeListener {

    private LatestContentFragment mLatestContentFragment;

    private int[] mStartingLocation;


    public RevealBackgroundView mBackgroundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_content);

        initView();

        initAnimation(savedInstanceState);

    }


    private void initView() {

        RelativeLayout rl_container = (RelativeLayout) findViewById(R.id.id_fragment_container);
        mBackgroundView = (RevealBackgroundView) findViewById(R.id.rbv_view);
        mStartingLocation = getIntent().getIntArrayExtra(Constant.START_LOCATION);
        final List<StoriesEntity> entities = (List<StoriesEntity>) getIntent().getSerializableExtra("entities");
        /*
        * ViewPager默认是第0页的,
        * 必须要将外界点击的是第几条Item传进来
        * 然后才可以一进来就让ViewPager显示对应的详情页面
        * */
        final int mCurrentPos = getIntent().getIntExtra("mCurrentPos", 0);

        //create a ViewPager by code
        ViewPager viewPager = new ViewPager(this);
        viewPager.setId(R.id.viewPager);
        rl_container.addView(viewPager);

//        mEntity = (StoriesEntity) getIntent().getSerializableExtra("entity");
//        mLatest = (Latest) getIntent().getSerializableExtra("entities");
//        Serializable adapter = getIntent().getSerializableExtra("adapter");
//        mItemCount = getIntent().getIntExtra("itemCount", 0);

        FragmentManager fm = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {

                //此处减掉的1是头item,
                int Pos = position + mCurrentPos-1 ;

                Logger.d("Pos:"+Pos);

                mLatestContentFragment = LatestContentFragment.newInstance(entities.get(Pos));

                return mLatestContentFragment;
            }

            @Override
            public int getCount() {
//                Log.d("Pos--->", "entities.size" + entities.size());

                /*
                * 此处要-mCurrentPos是因为:
                * 当我们点击第一条进到ViewPage的时候,ViewPager总共有多少页就是entities.size()
                * 但是当我们从第N条点进来的时候,ViewPager总共的页书,就不再是entities.size()
                * 而要在entities.size()的基础上减去第N条之前的条数
                *
                * */
                return entities.size() - mCurrentPos;
            }
        });


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
