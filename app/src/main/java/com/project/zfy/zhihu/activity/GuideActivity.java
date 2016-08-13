package com.project.zfy.zhihu.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.global.Constant;
import com.project.zfy.zhihu.utils.SharedPreferenceUtils;
import com.project.zfy.zhihu.view.ViewPagerWithAnim;

import java.util.ArrayList;

/**
 * guide页面
 * Created by zfy on 2016/8/13.
 */
public class GuideActivity extends AppCompatActivity {

    private ViewPagerWithAnim mVp_pager;
    private int[] mImgIDs;
    private ArrayList<ImageView> mViewArrayList;
    private Button bt_start;
    private LinearLayout ll_container;
    private ImageView iv_red_point;
    private int mPointDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();

        initData();

        mVp_pager.setAdapter(new MyPagerAdapter());
    }

    private void initView() {

        ll_container = (LinearLayout) findViewById(R.id.ll_container);

        iv_red_point = (ImageView) findViewById(R.id.iv_red_point);

        mVp_pager = (ViewPagerWithAnim) findViewById(R.id.vp_pager);

        //监听ViewPager页面的滑动
        mVp_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //小红点的最新位置(leftMargin)==移动的百分比(positionOffset)*两圆点之间的距离(mPointDis)+当前位置

                int leftMargin = (int) (mPointDis * positionOffset + position * mPointDis);
                //拿到小红点的布局参数
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_red_point.getLayoutParams();

                RelativeLayout.LayoutParams params =
                        new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);

                //改变最新的位置
                params.leftMargin = leftMargin;
                //重新设置给小红点
                iv_red_point.setLayoutParams(params);


            }

            @Override //当某个页面被选中的时候,回调方法
            public void onPageSelected(int position) {
                //只有在最后一个guide页面的时候,才显示Start按钮
                if (position == mViewArrayList.size() - 1) {
                    bt_start.setVisibility(View.VISIBLE);
                } else {
                    bt_start.setVisibility(View.GONE);
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //监听layout方法结束,位置确定好了之后,再计算值
        iv_red_point.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                //layout方法结束后的回调

                //移除监听,避免重复回调
                iv_red_point.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                /*计算两个圆点的距离 第二个圆点的left值-第一个圆点的left值
                 * measures->layout(确定位置)->draw Acivity的onCreate方法结束后才会走这个流程
                 * */
                mPointDis = ll_container.getChildAt(1).getLeft() - ll_container.getChildAt(0).getLeft();


            }
        });

        bt_start = (Button) findViewById(R.id.bt_start);

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();

                SharedPreferenceUtils.putBoolean(GuideActivity.this, Constant.IS_FIRST_ENTER, false);
            }
        });


    }

    private void initData() {

        mImgIDs = new int[]{R.drawable.guide_01, R.drawable.guide_02, R.drawable.guide_03};

        mViewArrayList = new ArrayList<>();

        for (int i = 0; i < mImgIDs.length; i++) {
            ImageView imageView = new ImageView(GuideActivity.this);
            imageView.setImageResource(mImgIDs[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mViewArrayList.add(imageView);

            //初始化底部小灰点
            ImageView point_gray = new ImageView(GuideActivity.this);
            point_gray.setImageResource(R.drawable.shape_point_gray);
            //拿到布局参数
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

            if (i > 0) {
                //从第二个小灰点开始设置margin
                params.leftMargin = 10;
            }
            //设置布局参数给小灰点
            point_gray.setLayoutParams(params);
            //将小灰点添加到容器中
            ll_container.addView(point_gray);


        }
    }

    /**
     * 跳转Activity的方法
     *
     * @author zfy
     * @created at 2016/8/13 18:42
     */
    public void startActivity() {
        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
        startActivity(intent);
        //Activity之间跳转的动画效果
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mImgIDs.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {


            mVp_pager.setViewForPosition(mViewArrayList.get(position), position);

            container.addView(mViewArrayList.get(position));

            return mViewArrayList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView(mViewArrayList.get(position));
            mVp_pager.removeViewForPosition(position);

        }


    }
}
