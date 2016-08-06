package com.project.zfy.zhihu.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.activity.MainActivity;
import com.project.zfy.zhihu.activity.NewsContentActivity;
import com.project.zfy.zhihu.adapter.ThemeNewsItemAdapter;
import com.project.zfy.zhihu.global.Constant;
import com.project.zfy.zhihu.moudle.News;
import com.project.zfy.zhihu.moudle.StoriesEntity;
import com.project.zfy.zhihu.utils.HttpUtils;
import com.project.zfy.zhihu.utils.UIUtils;

import org.apache.http.Header;

/**
 * 主题日报的Fragment
 * Created by zfy on 2016/8/3.
 */

@SuppressLint("ValidFragment")
public class NewsFragment extends BaseFragment {

    private String title;
    private String urlId;
    private ListView lv_news;
    private ImageView iv_tile;
    private TextView tv_title;
    private ImageLoader mImageLoader;


    /**
     * 构造方法,用于传参
     *
     * @param title 在主题日报themes中,此处用于显示在statusBar
     * @param id    在主题日报themes中,此处是用于在基地址之后拼接Url地址的
     * @return NewsFragment
     * @author zfy
     * @created at 2016/8/4 8:37
     */
    public NewsFragment(String title, String id) {

        this.title = title;
        this.urlId = id;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        //初始化ImageLoader
        mImageLoader = ImageLoader.getInstance();

        //设置状态栏显示为此主题日报的标题
        ((MainActivity) mActivity).setStatusBarTitle(title);

        //ListView布局
        View view = View.inflate(mActivity, R.layout.fragment_news, null);

        lv_news = (ListView) view.findViewById(R.id.lv_news);

        //初始刷布局,作为头布局添加给ListView
        View header = View.inflate(mActivity, R.layout.theme_list_news_header, null);

        iv_tile = (ImageView) header.findViewById(R.id.iv_title);
        tv_title = (TextView) header.findViewById(R.id.tv_title);

        lv_news.addHeaderView(header);

        //监听ListView的滑动事件,解决和swioerefreshLayou的滑动冲突
        lv_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (lv_news != null && lv_news.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(0).getTop() == 0);
                    ((MainActivity) mActivity).setSwipeRefreshLayoutEnable(enable);
                }

            }
        });

        //对ListView的点击事件做监听 跳转到某一主题日报下面的某一具体的新闻的详情页
        lv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] startingLocation = new int[2];
                view.getLocationOnScreen(startingLocation);
                startingLocation[0] += view.getWidth() / 2;


                StoriesEntity entity = (StoriesEntity) parent.getAdapter().getItem(position);
                Intent intent = new Intent(mActivity, NewsContentActivity.class);
                intent.putExtra(Constant.START_LOCATION, startingLocation);
                intent.putExtra("entity", entity);

                TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
                tv_title.setTextColor(UIUtils.getColor(R.color.clicked_tv_textcolor));

                startActivity(intent);
                mActivity.overridePendingTransition(0, 0);


            }
        });

        return view;
    }


    @Override
    public void initData() {
        super.initData();
        if (HttpUtils.isNetworkConnected(mActivity)) {
            HttpUtils.get(Constant.THEMENEWS + urlId, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    SQLiteDatabase db = ((MainActivity) mActivity).getCacheDBHelper().getWritableDatabase();
                    db.execSQL("replace into CacheList(date,json) values(" + (Constant.BASE_COLUMN + Integer.parseInt(urlId)) + ",' " + responseString + "')");
                    db.close();
                    parseJsonData(responseString);
                }
            });

        } else {
            SQLiteDatabase db = ((MainActivity) mActivity).getCacheDBHelper().getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from CacheList where date = " + (Constant.BASE_COLUMN + Integer.parseInt(urlId)), null);
            if (cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseJsonData(json);
            }
            cursor.close();
            db.close();

        }
    }

    //解析Json数据
    public void parseJsonData(String jsonData) {
        Gson gson = new Gson();
        News news = gson.fromJson(jsonData, News.class);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        tv_title.setText(news.getDescription());
        mImageLoader.displayImage(news.getImage(), iv_tile);
        ThemeNewsItemAdapter adapter = new ThemeNewsItemAdapter(mActivity, news.getStories());
        lv_news.setAdapter(adapter);


    }
}
