package com.project.zfy.zhihu.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.project.zfy.zhihu.utils.ToastUtils;
import com.project.zfy.zhihu.view.RevealBackgroundView;

import org.apache.http.Header;

/**
 * 某一主题日报的具体某一条item的fragment
 * Created by zfy on 2016/8/15.
 */
public class NewsContentFragment extends BaseFragment {

    public WebCacheDbHelper mDbHelper;
    public StoriesEntity mEntity;
    public RevealBackgroundView mBackgroundView;
    public WebView mWebView;
    private Toolbar mToolbar;
    public FloatingActionButton fab_float;
    private Content content;
    public CoordinatorLayout coordinatorLayout;

    public static NewsContentFragment newInstance(StoriesEntity entity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("entity", entity);
        NewsContentFragment newsContentFragment = new NewsContentFragment();
        newsContentFragment.setArguments(bundle);
        return newsContentFragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mEntity = (StoriesEntity) bundle.getSerializable("entity");
        }
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = View.inflate(getActivity(), R.layout.fragment_news_content, null);

        mDbHelper = new WebCacheDbHelper(getActivity(), 1);
        mBackgroundView = (RevealBackgroundView) view.findViewById(R.id.rbv_view);

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_layout);

        fab_float = (FloatingActionButton) view.findViewById(R.id.fab_float);

        fab_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showShare();
                ToastUtils.ToastUtils(getActivity(), "Share clicked");

            }
        });

        mToolbar = (Toolbar) view.findViewById(R.id.tb_bar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //对左上角的返回键做监听
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mWebView = (WebView) view.findViewById(R.id.wv_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启database storage API功能
        mWebView.getSettings().setDatabaseEnabled(true);
        // 开启Application Cache功能
        mWebView.getSettings().setAppCacheEnabled(true);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout.setVisibility(View.INVISIBLE);
        fab_float.setVisibility(View.INVISIBLE);


    }

    private void onBackPressed() {
        getActivity().finish();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(this).commit();
    }

    @Override
    public void initData() {
        if (HttpUtils.isNetworkConnected(getActivity())) {
            HttpUtils.get(Constant.CONTENT + mEntity.getId(), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    //请求数据成功，缓存到数据库
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    responseString = responseString.replaceAll("'", "''");
                    db.execSQL("replace into Cache(newsId,json) values(" + mEntity.getId() + ",'" + responseString + "')");
                    db.close();
                    parseJsonData(responseString);
                }
            });

        } else {

            //没有网络，则从数据库中拿数据
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

    public void parseJsonData(String responseString) {
        Gson gson = new Gson();
        content = gson.fromJson(responseString, Content.class);
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + content.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);

    }
}
