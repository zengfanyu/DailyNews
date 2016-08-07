package com.project.zfy.zhihu.activity;


import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.moudle.Content;
import com.project.zfy.zhihu.utils.UIUtils;
import com.project.zfy.zhihu.view.RevealBackgroundView;

/**
 * 最新新闻的内容详情页
 * Created by zfy on 2016/8/5.
 */
public class LatestContentActivity extends BaseContentActivity {
    private AppBarLayout app_bar_layout;
    private ImageView iv_header;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Content mContent;

    @Override
    public int loadContentView() {
        return R.layout.activity_latest;
    }


    @Override
    public void initView() {
        super.initView();

        app_bar_layout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        iv_header = (ImageView) findViewById(R.id.iv_header);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);

        app_bar_layout.setVisibility(View.INVISIBLE);

        mCollapsingToolbarLayout.setTitle(mEntity.getTitle());
        mCollapsingToolbarLayout.setContentScrimColor(UIUtils.getColor(R.color.light_toolbar));
        mCollapsingToolbarLayout.setStatusBarScrimColor(UIUtils.getColor(R.color.light_toolbar));


    }



    @Override
    public void parseJsonData(String responseString) {
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

    @Override
    public void stateChangeShowView(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            app_bar_layout.setVisibility(View.VISIBLE);
            setStatusBarColor(Color.TRANSPARENT);
        }

    }


}