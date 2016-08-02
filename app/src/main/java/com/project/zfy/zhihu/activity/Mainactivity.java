package com.project.zfy.zhihu.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.db.CacheDbHelper;
import com.project.zfy.zhihu.fragment.MainFragment;
import com.project.zfy.zhihu.utils.UIUtils;

/**
 * $desc
 * Created by zfy on 2016/8/2.
 */
public class MainActivity extends AppCompatActivity {

    //记录是夜间模式还是白天模式  true 白天; false夜间
    private boolean isLight;
    private CacheDbHelper mDBHelper; //数据库的帮助类
    private Toolbar toolBar;
    private FrameLayout fl_content;
    private DrawerLayout mDrawerLayout;

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
                .beginTransaction().
                setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                .replace(R.id.fl_content, new MainFragment(), "latest")
                .commit();

    }

    private void initView() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        //根据isLight来设置toolbar颜色
        toolBar.setBackgroundColor(UIUtils.getColor(R.color.light_toolbar));
        //用toolBar替代ActionBar
        setSupportActionBar(toolBar);
        //根据isLight来设置状态栏颜色 API21以上的方法
        setStatusBarColor(UIUtils.getColor(R.color.light_toolbar));

        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolBar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();


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
}
