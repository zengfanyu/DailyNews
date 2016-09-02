package com.project.zfy.zhihu.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.moudle.Latest;

import java.util.ArrayList;
import java.util.List;


public class Kanner extends FrameLayout implements OnClickListener {
    private List<Latest.TopStoriesEntity> topStoriesEntities;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private List<View> views;
    private Context context;
    private ViewPagerWithAnim vp;
    private boolean isAutoPlay;
    private int currentItem;
    private List<ImageView> iv_dots;
    private Handler handler = new Handler();
    private OnItemClickListener mItemClickListener;

    /*
    * 构造方法
    * */
    public Kanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        this.context = context;
        this.topStoriesEntities = new ArrayList<>();

        views = new ArrayList<>();
        iv_dots = new ArrayList<>();
    }


    /*
    * 最终回调的都是三个参数的构造方法
    * */
    public Kanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    /*
    * 最终回调的都是三个参数的构造方法
    * */
    public Kanner(Context context) {
        this(context, null);
    }

    public void setTopEntities(List<Latest.TopStoriesEntity> topEntities) {
        this.topStoriesEntities = topEntities;
        reset();
    }

    private void reset() {
        views.clear();
        initUI();
    }

    private void initUI() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.kanner_layout, this, true);
        vp = (ViewPagerWithAnim) view.findViewById(R.id.vp);
        LinearLayout ll_dot = (LinearLayout) view.findViewById(R.id.ll_dot);
//        ll_dot.removeAllViews();

        int len = topStoriesEntities.size();
        /*
        * 初始化ViewPagerIndicator
        * */
        for (int i = 0; i < len; i++) {
            ImageView iv_dot = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            ll_dot.addView(iv_dot, params);
            iv_dots.add(iv_dot);
        }

        /*
        * 此处把len长度+1,目的就是为了下面分情况设置Imageview和title
        * 当i==len+1的时候,
        * 此时实际上是没有展示的imageView和title的
        * 但是在此时加载位置为0处的imageview和title
        * 这样就达到了一个循环滚动的效果
        * */
        for (int i = 0; i <= len + 1; i++) {
            View pagerContentView = LayoutInflater.from(context).inflate(
                    R.layout.kanner_content_layout, null);
            ImageView iv = (ImageView) pagerContentView.findViewById(R.id.iv_title);
            TextView tv_title = (TextView) pagerContentView.findViewById(R.id.tv_title);
            iv.setScaleType(ScaleType.CENTER_CROP);
//            iv.setBackgroundResource(R.drawable.loading1);
            if (i == 0) {
                mImageLoader.displayImage(topStoriesEntities.get(len - 1).getImage(), iv, options);
                tv_title.setText(topStoriesEntities.get(len - 1).getTitle());
            } else if (i == len + 1) {
                mImageLoader.displayImage(topStoriesEntities.get(0).getImage(), iv, options);
                tv_title.setText(topStoriesEntities.get(0).getTitle());
            } else {
                mImageLoader.displayImage(topStoriesEntities.get(i - 1).getImage(), iv, options);
                tv_title.setText(topStoriesEntities.get(i - 1).getTitle());
            }
            //设置点击事件的监听
            pagerContentView.setOnClickListener(this);
            views.add(pagerContentView);
        }
        vp.setAdapter(new MyPagerAdapter());
        //设置ViewPage可以被点击
        vp.setFocusable(true);
        vp.setCurrentItem(1);
        currentItem = 1;
        vp.addOnPageChangeListener(new MyOnPageChangeListener());
        startPlay();
    }

    private void startPlay() {
        isAutoPlay = true;
        handler.postDelayed(task, 3000);
    }


    private final Runnable task = new Runnable() {

        @Override
        public void run() {
            if (isAutoPlay) {
                currentItem = currentItem % (topStoriesEntities.size() + 1) + 1;
                if (currentItem == 1) {
                    vp.setCurrentItem(currentItem, false);
                    handler.post(task);
                } else {
                    vp.setCurrentItem(currentItem);
                    handler.postDelayed(task, 5000);
                }
            } else {
                handler.postDelayed(task, 5000);
            }
        }
    };

    /*
    * ViewPager的Adapter
    * */
    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    /*
    * 监听ViewPager的滑动事件
    * */
    class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                // 1-->当前VP正在被用户拖动
                case 1:
                    isAutoPlay = false;
                    break;
                //2-->自动滑动的过程中
                case 2:
                    isAutoPlay = true;
                    break;
                //静止状态
                case 0:
                    if (vp.getCurrentItem() == 0) {
                        vp.setCurrentItem(topStoriesEntities.size(), false);
                    } else if (vp.getCurrentItem() == topStoriesEntities.size() + 1) {
                        vp.setCurrentItem(1, false);
                    }
                    currentItem = vp.getCurrentItem();
                    isAutoPlay = true;
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < iv_dots.size(); i++) {
                if (i == arg0 - 1) {
                    iv_dots.get(i).setImageResource(R.drawable.dot_focus);
                } else {
                    iv_dots.get(i).setImageResource(R.drawable.dot_blur);
                }
            }

        }

    }


    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void click(View v, Latest.TopStoriesEntity entity);
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            Latest.TopStoriesEntity entity = topStoriesEntities.get(vp.getCurrentItem() - 1);
            //接口回调
            mItemClickListener.click(v, entity);
        }
    }
}
