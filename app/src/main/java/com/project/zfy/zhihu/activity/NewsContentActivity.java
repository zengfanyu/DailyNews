package com.project.zfy.zhihu.activity;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
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
 * 某一主题日报的具体某一条item的新闻详情页面
 * Created by zfy on 2016/8/6.
 */
public class NewsContentActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {

    private RevealBackgroundView backgroundView;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private WebView webView;
    private StoriesEntity mEntity;
    private int[] mStartingLocation;
    private WebCacheDbHelper mDBHelper;
    private Content content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);


        initView();
        initData();
        initAnimation(savedInstanceState);
    }

    private void initView() {

        mDBHelper = new WebCacheDbHelper(this, 1);

        backgroundView = (RevealBackgroundView) findViewById(R.id.rbv_view);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_layout);
        toolbar = (Toolbar) findViewById(R.id.tb_bar);
        webView = (WebView) findViewById(R.id.wv_view);

        mEntity = (StoriesEntity) getIntent().getSerializableExtra("entity");
        mStartingLocation = getIntent().getIntArrayExtra(Constant.START_LOCATION);

        //一跳转到activity的时候,要隐藏toolbar
        coordinatorLayout.setVisibility(View.INVISIBLE);

        toolbar.setBackgroundColor(UIUtils.getColor(R.color.light_toolbar));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            //显示返回键
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //对左上角的返回键做监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        // 开启database storage API功能
        webView.getSettings().setDatabaseEnabled(true);
        // 开启Application Cache功能
        webView.getSettings().setAppCacheEnabled(true);


    }

    private void initData() {


        if (HttpUtils.isNetworkConnected(this)) {
            HttpUtils.get(Constant.CONTENT + mEntity.getId(), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    //请求数据成功，缓存到数据库
                    SQLiteDatabase db = mDBHelper.getWritableDatabase();
//                    responseString = responseString.replaceAll("'", "''");

                    LogUtils.d("responseString--->" + responseString);
                    db.execSQL("replace into Cache(newsId,json) values(" + mEntity.getId() + ",'" + responseString + "')");
                    db.close();
                    parseJsonData(responseString);
                }
            });

        } else {

            //没有网络，则从数据库中拿数据
            SQLiteDatabase db = mDBHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from Cache where newsId = " + mEntity.getId(), null);
            if (cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseJsonData(json);
            }
            cursor.close();
            db.close();

        }

    }

    private void parseJsonData(String responseString) {
        Gson gson = new Gson();
        content = gson.fromJson(responseString, Content.class);
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + content.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        webView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }


    private void initAnimation(Bundle bundle) {
        setupRevealBackground(bundle);
        setStatusBarColor(UIUtils.getColor(R.color.light_toolbar));


    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        backgroundView.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(Constant.START_LOCATION);
            backgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    backgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    backgroundView.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            backgroundView.setToFinishedFrame();
        }
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.slide_out_to_left);
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            coordinatorLayout.setVisibility(View.VISIBLE);
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
