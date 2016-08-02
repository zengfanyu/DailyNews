package com.project.zfy.zhihu.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.activity.MainActivity;
import com.project.zfy.zhihu.adapter.MainNewsItemAdapter;
import com.project.zfy.zhihu.global.Constant;
import com.project.zfy.zhihu.moudle.Latest;
import com.project.zfy.zhihu.moudle.StoriesEntity;
import com.project.zfy.zhihu.utils.HttpUtils;
import com.project.zfy.zhihu.utils.UIUtils;
import com.project.zfy.zhihu.view.Kanner;

import org.apache.http.Header;

import java.util.List;

/**
 * 主页面的Fragment
 * Created by zfy on 2016/8/2.
 */
public class MainFragment extends BaseFragment {
    private ListView lv_news;
    private Kanner kanner;
    private MainNewsItemAdapter mAdapter;
    private boolean isLoading = false;
    private Latest mLatest;
    private String mDate;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        //设置toolbar的标题
        ((MainActivity) mActivity).setStatusBarTitle("今日热闻");
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        lv_news = (ListView) view.findViewById(R.id.lv_news);

        View header = View.inflate(UIUtils.getContext(), R.layout.kanner, null);

        kanner = (Kanner) header.findViewById(R.id.kanner);

        kanner.setOnItemClickListener(new Kanner.OnItemClickListener() {
            @Override
            public void click(View v, Latest.TopStoriesEntity entity) {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                StoriesEntity storiesEntity = new StoriesEntity();
                storiesEntity.setId(entity.getId());
                storiesEntity.setTitle(entity.getTitle());
                /*
                * 此处跳转
                * */

                mActivity.overridePendingTransition(0, 0);
            }
        });

        //给ListView设置头布局
        lv_news.addHeaderView(header);

        mAdapter = new MainNewsItemAdapter(UIUtils.getContext());

        lv_news.setAdapter(mAdapter);


        return view;
    }


    @Override
    public void initData() {
        super.initData();
        loadFirst();
    }


    /**
    *加载数据
    *@author zfy
    *@created at 2016/8/2 15:24
    */
    private void loadFirst() {
        isLoading=true;

        if (HttpUtils.isNetworkConnected(UIUtils.getContext())){
            HttpUtils.get(Constant.LATESTNEWS, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    //将JSON数据写入数据库做缓存
                    SQLiteDatabase db = ((MainActivity) mActivity).getCacheDBHelper().getWritableDatabase();
                    db.execSQL("replace into CacheList(date,json) values(" + Constant.LATEST_COLUMN + ",' " + responseString + "')");
                    db.close();
                    parseLatestJSONData(responseString);


                }
            });
        }else{
            //如果目前没有网络连接,就从数据库读取数据
            SQLiteDatabase db = ((MainActivity) mActivity).getCacheDBHelper().getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from CacheList where date = " + Constant.LATEST_COLUMN, null);
            if (cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseLatestJSONData(json);
            } else {
                isLoading = false;
            }
            cursor.close();
            db.close();

        }

    }


    /**
    *解析"最新消息"的json数据  Gson
    *@author zfy
    *@created at 2016/8/2 15:36
    */
    private void parseLatestJSONData(String responseString) {
        Gson gson = new Gson();
        mLatest = gson.fromJson(responseString, Latest.class);
        mDate = mLatest.getDate();
        kanner.setTopEntities(mLatest.getTop_stories());
        UIUtils.getHandler().post(new Runnable() {
            @Override
            public void run() {

                List<StoriesEntity> storiesEntities = mLatest.getStories();
                StoriesEntity topic = new StoriesEntity();
                topic.setType(Constant.TOPIC);
                topic.setTitle("今日热闻");
                storiesEntities.add(0, topic);
                //在Adapter中已经刷新过了.!!
                mAdapter.addList(storiesEntities);
                isLoading = false;

            }
        });

    }
}
