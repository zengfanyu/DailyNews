package com.project.zfy.zhihu.activity;

import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import com.google.gson.Gson;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.moudle.Content;
import com.project.zfy.zhihu.view.RevealBackgroundView;

/**
 * 某一主题日报的具体某一条item的新闻详情页面
 * Created by zfy on 2016/8/6.
 */
public class NewsContentActivity extends BaseContentActivity{
    private CoordinatorLayout coordinatorLayout;
    private Content content;

    @Override
    public int loadContentView() {
        return R.layout.activity_news_content;
    }




    @Override
    public void initView() {
        super.initView();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_layout);
        //一跳转到activity的时候,要隐藏toolbar
        coordinatorLayout.setVisibility(View.INVISIBLE);





    }

    @Override
    public void parseJsonData(String responseString) {
        Gson gson = new Gson();
        content = gson.fromJson(responseString, Content.class);
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + content.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);

    }



    @Override
    public void stateChangeShowView(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            coordinatorLayout.setVisibility(View.VISIBLE);
        }

    }
}
