package com.project.zfy.zhihu.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.activity.LatestContentActivity;
import com.project.zfy.zhihu.activity.MainActivity;
import com.project.zfy.zhihu.adapter.MainNewsItemAdapter;
import com.project.zfy.zhihu.global.Constant;
import com.project.zfy.zhihu.moudle.Before;
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
    private boolean isLoading = false;  //是否正在加载数据的标记
    private Latest mLatest;
    /**
     * 从Json数据中解析出来的日期是当天的日期.
     * 这个日期贴在before的url后面,就可以查新到前一天的消息
     * 也就是说,要查询20160802的消息,url后拼接的应该是before/20160803,
     * 这个20160803刚好也就是latest消息中的date,也就是此处的date
     *
     * @author zfy
     * @created at 2016/8/3 11:06
     */
    private String mDate;
    private Before before;

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

                //将id和title传给新闻详情activity
                //id用于获取新闻详情的数据 title用于在tooabar上面显示

                storiesEntity.setId(entity.getId());
                storiesEntity.setTitle(entity.getTitle());

                Intent intent = new Intent(mActivity, LatestContentActivity.class);
                intent.putExtra(Constant.START_LOCATION, startingLocation);
                intent.putExtra("entity", storiesEntity);
                intent.putExtra("isLight", ((MainActivity) mActivity).isLight());
                startActivity(intent);


//                ((MainActivity) mActivity).startActivity(intent);

                //取消Activity之间的跳转效果
                mActivity.overridePendingTransition(0, 0);
            }
        });

        //给ListView设置头布局
        lv_news.addHeaderView(header);

        mAdapter = new MainNewsItemAdapter(UIUtils.getContext());

        lv_news.setAdapter(mAdapter);

        //监听listView 的滑动事件
        lv_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //listView存在,并且有item项
                if (lv_news != null && lv_news.getChildCount() > 0) {
                    //只有当可以看到的第一个item编号是0,并且第一个可以看到的item完全可见!!!!!!!!!!! swipe才可以刷新
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    /*LogUtils.d("enable-->" + enable +
                            ",firstVisibleItem-->" + firstVisibleItem +
                            ",top-->" + view.getChildAt(firstVisibleItem).getTop() +
                            ",visibleItemCount-->" + visibleItemCount +
                            ",totalItemCount-->" + totalItemCount);
                    LogUtils.i("firstVisibleItem-->" + firstVisibleItem +
                            ",visibleItemCount-->" + visibleItemCount +
                            ",totalItemCount-->" + totalItemCount);*/

                    ((MainActivity) mActivity).setSwipeRefreshLayoutEnable(enable);

                    //当滑动到最底部的时候,也就是第一个可以item的编号+可以看到的item数目==总items,并且现在没有在加载数据
                    // 那么,加载更多
                    if ((firstVisibleItem + visibleItemCount == totalItemCount) && !isLoading) {
                        //加载更多数据
                        loadMore();
                    }


                }

            }
        });


        return view;
    }

    /**
     * 加载更多数据的方法
     *
     * @author zfy
     * @created at 2016/8/3 11:10
     */
    private void loadMore() {
        //改变标记的状态
        isLoading = true;
        //拼接url
        String url = Constant.DEFORE + mDate;
        //使用AsyncHttp获取数据
        if (HttpUtils.isNetworkConnected(UIUtils.getContext())) {
            HttpUtils.get(url, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    //缓存到数据库
                    SQLiteDatabase db = ((MainActivity) mActivity).getCacheDBHelper().getWritableDatabase();
                    db.execSQL("replace into CacheList(date,json) values(" + mDate + ",' " + responseString + "')");
                    db.close();
                    //解析数据
                    parseBeforeJSONData(responseString);
                }
            });

        } else {
            //从数据库中拿缓存数据
            SQLiteDatabase db = ((MainActivity) mActivity).getCacheDBHelper().getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from CacheList where date = " + mDate, null);
            if (cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseBeforeJSONData(json);
            } else {
                db.delete("CacheList", "date < " + mDate, null);
                isLoading = false;
                Snackbar sb = Snackbar.make(lv_news, "没有更多的离线内容了~", Snackbar.LENGTH_SHORT);
                sb.getView().setBackgroundColor(UIUtils.getColor(android.R.color.holo_blue_dark));
                sb.show();
            }
            cursor.close();
            db.close();

        }


    }


    /**
     * 解析从服务器请求到的更多数据
     *
     * @param responseString 服务器返回的JSON数据
     * @return void
     * @author zfy
     * @created at 2016/8/3 12:33
     */
    private void parseBeforeJSONData(String responseString) {
        Gson gson = new Gson();
        before = gson.fromJson(responseString, Before.class);
        if (before == null) {
            isLoading = false;
            return;
        }

        //更新date值
        mDate = before.getDate();
        UIUtils.getHandler().post(new Runnable() {
            @Override
            public void run() {
                List<StoriesEntity> storiesEntities = before.getStories();
                StoriesEntity topic = new StoriesEntity();
                topic.setType(Constant.TOPIC);
                topic.setTitle(legalDate(mDate));
                storiesEntities.add(0, topic);
                mAdapter.addList(storiesEntities);
                isLoading = false;

            }
        });


    }


    /**
     * 产生合法title的方法
     *
     * @param date 新闻日期
     * @return result xxxx年xx月xx日 格式的数据
     * @author zfy
     * @created at 2016/8/3 11:42
     */
    private String legalDate(String date) {

        String result = date.substring(0, 4); //2016
        result += "年"; //2016年
        result += date.substring(4, 6);//2016年08
        result += "月";//2016年08月
        result += date.substring(6, 8);//2016年08月02
        result += "日";//2016年08月02日


        return result;
    }


    @Override
    public void initData() {
        super.initData();
        loadFirst();
    }


    /**
     * 加载数据
     *
     * @author zfy
     * @created at 2016/8/2 15:24
     */
    private void loadFirst() {
        isLoading = true;

        if (HttpUtils.isNetworkConnected(UIUtils.getContext())) {
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
        } else {
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
     * 解析"最新消息"的json数据  Gson
     *
     * @author zfy
     * @created at 2016/8/2 15:36
     */
    private void parseLatestJSONData(String responseString) {
        Gson gson = new Gson();
        mLatest = gson.fromJson(responseString, Latest.class);
        mDate = mLatest.getDate();
        kanner.setTopEntities(mLatest.getTop_stories());
        UIUtils.getHandler().post(new Runnable() {
            @Override
            public void run() {

                //手动添加新闻的头标题,用TOPIC区分开,在此处设定一个StoriesEntity的type==TOPIC,
                //然后添加到第0个位置,在MainNewsItemAdapter
                List<StoriesEntity> storiesEntities = mLatest.getStories();
                StoriesEntity topic = new StoriesEntity();
                topic.setType(Constant.TOPIC);
                topic.setTitle("今日热闻");
                storiesEntities.add(0, topic);
                //在addList()方法中已经刷新过了.!!
                mAdapter.addList(storiesEntities);
                isLoading = false;

            }
        });

    }
}
