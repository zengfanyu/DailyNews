package com.project.zfy.zhihu.activity;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.db.WebCacheDbHelper;
import com.project.zfy.zhihu.global.Constant;
import com.project.zfy.zhihu.moudle.Content;
import com.project.zfy.zhihu.moudle.StoriesEntity;
import com.project.zfy.zhihu.utils.HttpUtils;
import com.project.zfy.zhihu.utils.LogUtils;
import com.project.zfy.zhihu.utils.UIUtils;
import com.project.zfy.zhihu.view.RevealBackgroundView;

import org.apache.http.Header;

/**
 * 最新新闻的内容详情页
 * Created by zfy on 2016/8/5.
 */
public class LatestContentActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {

    private WebCacheDbHelper mDbHelper;
    private AppBarLayout app_bar_layout;
    private RevealBackgroundView mBackgroundView;
    private StoriesEntity mEntity;
    private ImageView iv_header;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private WebView mWebView;
    private Content mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_latest);
        //数据库的帮助类
        mDbHelper = new WebCacheDbHelper(getApplicationContext(), 1);

        initView();
        initData();
        initAnimation(savedInstanceState);


    }

    private void initAnimation(Bundle bundle) {
        setupRevealBackground(bundle);
        setStatusBarColor(UIUtils.getColor(R.color.light_toolbar));

    }

    private void setupRevealBackground(Bundle bundle) {
        mBackgroundView.setOnStateChangeListener(this);
        if (bundle == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(Constant.START_LOCATION);

            //view树完成测量并且分配空间而绘制过程还没有开始的时候播放动画
            mBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mBackgroundView.startFromLocation(startingLocation);
                    return true;
                }
            });


        } else {
            mBackgroundView.setToFinishedFrame();
        }

    }

    private void initData() {
        if (HttpUtils.isNetworkConnected(this)) {
            HttpUtils.get(Constant.CONTENT + mEntity.getId(), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
//                    responseString = responseString.replaceAll("'", "''");

                    LogUtils.d("responseString--->" + responseString);
                    db.execSQL("replace into Cache(newsId,json) values(" + mEntity.getId() + ",'" + responseString + "')");
                    db.close();
                    parseJsonData(responseString);
                }
            });

        } else {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from Cache where newsId = " + mEntity.getId(), null);
            if (cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseJsonData(json);
            }
            cursor.close();
            db.close();

        }

    }

    /**
     * 解析json数据的方法
     *
     * @author zfy
     * @created at 2016/8/5 10:18
     */
    private void parseJsonData(String responseString) {
        Gson gson = new Gson();
        mContent = gson.fromJson(responseString, Content.class);
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
        imageLoader.displayImage(mContent.getImage(), iv_header, options);

        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + mContent.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);


    }

    private void initView() {

        app_bar_layout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mBackgroundView = (RevealBackgroundView) findViewById(R.id.revealBackgroundView);
        iv_header = (ImageView) findViewById(R.id.iv_header);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mWebView = (WebView) findViewById(R.id.webview);

        app_bar_layout.setVisibility(View.INVISIBLE);

        //从mainFragment中传递过来的title和id信息
        mEntity = (StoriesEntity) getIntent().getSerializableExtra("entity");

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //对左上角的返回键做监听
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mCollapsingToolbarLayout.setTitle(mEntity.getTitle());
        mCollapsingToolbarLayout.setContentScrimColor(UIUtils.getColor(R.color.light_toolbar));
        mCollapsingToolbarLayout.setStatusBarScrimColor(UIUtils.getColor(R.color.light_toolbar));

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启database storage API功能
        mWebView.getSettings().setDatabaseEnabled(true);
        // 开启Application Cache功能
        mWebView.getSettings().setAppCacheEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.slide_out_to_left);
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            app_bar_layout.setVisibility(View.VISIBLE);
            setStatusBarColor(Color.TRANSPARENT);
        }

    }

    @TargetApi(21)
    private void setStatusBarColor(int statusBarColor) {
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
}
