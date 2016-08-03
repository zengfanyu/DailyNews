package com.project.zfy.zhihu.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.db.CacheDbHelper;
import com.project.zfy.zhihu.fragment.MainFragment;
import com.project.zfy.zhihu.utils.UIUtils;

/**
 * 主界面
 * Created by zfy on 2016/8/2.
 */
public class MainActivity extends AppCompatActivity {

    //记录是夜间模式还是白天模式  true 白天; false夜间
    private boolean isLight;
    private CacheDbHelper mDBHelper; //数据库的帮助类
    private Toolbar toolBar;
    private FrameLayout fl_content;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout srl_swipe;
    private String mCurentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDBHelper = new CacheDbHelper(this, 1);

        initView();
        loadLatest();
    }

    private void loadLatest() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                .replace(R.id.fl_content, new MainFragment(), "latest")
                .commit();
        mCurentId = "latest";

    }

    private void initView() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        //根据isLight来设置toolbar颜色
        toolBar.setBackgroundColor(UIUtils.getColor(R.color.light_toolbar));
        //用toolBar替代ActionBar
        setSupportActionBar(toolBar);
        //根据isLight来设置状态栏颜色 API21以上的方法
        setStatusBarColor(UIUtils.getColor(R.color.light_toolbar));

        //下拉刷新控件
        srl_swipe = (SwipeRefreshLayout) findViewById(R.id.srl_swipe);

        //设置刷新时候动画的颜色
        srl_swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //对刷新控件做监听
        srl_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新之后,由右向左将fragment做一个平移,给用户一个刷新过了的体验
                refreshFragment();
                srl_swipe.setRefreshing(false);
            }
        });

        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolBar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();


    }

    private void refreshFragment() {
        if (mCurentId.equals("latest")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                    .replace(R.id.fl_content, new MainFragment(), "latest")
                    .commit();
        }


    }

    public boolean isLight() {

        return isLight;
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

    public void setStatusBarTitle(String title) {
        toolBar.setTitle(title);

    }

    public CacheDbHelper getCacheDBHelper() {
        return mDBHelper;
    }


    private long firstTime;

    @Override
    public void onBackPressed() {
        //当抽屉打开的时候按返回键,是收起抽屉
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            closeMenu();
        } else {
            //抽屉关闭的时候 ,计算两次按下返回键的时间
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Snackbar sb = Snackbar.make(fl_content, "再按一次退出", Snackbar.LENGTH_SHORT);
                sb.getView().setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                sb.show();
                firstTime = secondTime;
            } else {
                finish();
            }
        }

    }

    private void closeMenu() {
        mDrawerLayout.closeDrawers();
    }

    public void setSwipeRefreshLayoutEnable(boolean enable) {
        srl_swipe.setEnabled(enable);



    }
}
