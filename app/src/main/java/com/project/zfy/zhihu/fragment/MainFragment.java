package com.project.zfy.zhihu.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.activity.LatestContentActivity;
import com.project.zfy.zhihu.activity.MainActivity;
import com.project.zfy.zhihu.global.Constant;
import com.project.zfy.zhihu.moudle.Before;
import com.project.zfy.zhihu.moudle.Latest;
import com.project.zfy.zhihu.moudle.StoriesEntity;
import com.project.zfy.zhihu.utils.HttpUtils;
import com.project.zfy.zhihu.utils.SharedPreferenceUtils;
import com.project.zfy.zhihu.utils.UIUtils;
import com.project.zfy.zhihu.view.Kanner;

import org.apache.http.Header;

import java.util.ArrayList;
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

    /**
     * 标记当前listView的滑动方向
     *
     * @author zfy
     * @created at 2016/8/5 16:55
     */
    private boolean isScrollDown;
    /**
     * 用于记录ListView中第一个可见item的位置，和其top值
     *
     * @author zfy
     * @created at 2016/8/5 16:56
     */
    private int mFirstPosition, mFirstTop;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        //设置toolbar的标题
        ((MainActivity) mActivity).setStatusBarTitle("今日热闻");
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        lv_news = (ListView) view.findViewById(R.id.lv_news);


        View header = View.inflate(UIUtils.getContext(), R.layout.kanner, null);

        kanner = (Kanner) header.findViewById(R.id.kanner);

        //给轮播条设置监听
        kanner.setOnItemClickListener(new Kanner.OnItemClickListener() {
            @Override
            public void click(View v, Latest.TopStoriesEntity entity) {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;

                //将id和title传给新闻详情activity
                //id用于获取新闻详情的数据 title用于在tooabar上面显示
                StoriesEntity storiesEntity = new StoriesEntity();
                storiesEntity.setId(entity.getId());
                storiesEntity.setTitle(entity.getTitle());

                Intent intent = new Intent(mActivity, LatestContentActivity.class);
                intent.putExtra(Constant.START_LOCATION, startingLocation);
                intent.putExtra("entity", storiesEntity);
                intent.putExtra("isLight", ((MainActivity) mActivity).isLight());
                startActivity(intent);

                //取消Activity之间的跳转效果
                mActivity.overridePendingTransition(0, 0);
            }
        });

        //给ListView设置头布局
        lv_news.addHeaderView(header);


        //监听listView 的滑动事件
        lv_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //listView存在,并且有item项
                if (lv_news != null && lv_news.getChildCount() > 0) {

                    //判断是否正在向下滑动
                    View firstChild = view.getChildAt(0);
                    if (firstChild == null) return;
                    int top = firstChild.getTop();
                    /**
                     * firstVisibleItem > mFirstPosition表示向下滑动一整个Item
                     * mFirstTop > top表示在当前这个item中滑动
                     */
                    isScrollDown = firstVisibleItem > mFirstPosition || mFirstTop > top;
                    mFirstTop = top;
                    mFirstPosition = firstVisibleItem;


                    //解决listView个swipeRefreshLayout的滑动冲突
                    //只有当可以看到的第一个item编号是0,并且第一个可以看到的item完全可见!!!!!!!!!!! swipe才可以刷新
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);

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


        //对listView 的item做监听
        lv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] startingLoacation = new int[2];
                view.getLocationOnScreen(startingLoacation);
                startingLoacation[0] += view.getWidth() / 2;

                //parent is listView ,through listview get adapter then get item bean
                StoriesEntity entity = (StoriesEntity) parent.getAdapter().getItem(position);
                Intent intent = new Intent(mActivity, LatestContentActivity.class);
                intent.putExtra(Constant.START_LOCATION, startingLoacation);
                intent.putExtra("entity", entity);

                TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
                tv_title.setTextColor(UIUtils.getColor(R.color.clicked_tv_textcolor));

                startActivity(intent);
                mActivity.overridePendingTransition(0, 0);

            }
        });

        mAdapter = new MainNewsItemAdapter();


        lv_news.setAdapter(mAdapter);


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


    class MainNewsItemAdapter extends BaseAdapter {
        private List<StoriesEntity> mEntities;
        private ImageLoader mImageLoader;
        private DisplayImageOptions mOptions;
        private Animation mAnimation;


        /**
         * 构造方法
         *
         * @author zfy
         * @created at 2016/8/2 14:13
         */
        public MainNewsItemAdapter() {
            this.mEntities = new ArrayList<StoriesEntity>();
            mImageLoader = ImageLoader.getInstance();
            mOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            mAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.bottom_in_anim);


        }


        //刷新添加数据的方法
        public void addList(List<StoriesEntity> items) {
            this.mEntities.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mEntities.size();
        }

        @Override
        public StoriesEntity getItem(int position) {
            return mEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewHolder holder;
            if (convertView == null) {
                holder = new viewHolder();
                convertView = View.inflate(UIUtils.getContext(), R.layout.main_list_news_item, null);
                holder.iv_title = (ImageView) convertView.findViewById(R.id.iv_title);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_topic = (TextView) convertView.findViewById(R.id.tv_topic);
                holder.ll_root = (LinearLayout) convertView.findViewById(R.id.ll_root);
                holder.fl_container = (FrameLayout) convertView.findViewById(R.id.fl_container);
                holder.rl_root = (RelativeLayout) convertView.findViewById(R.id.rl_root);
                convertView.setTag(holder);
            } else {
                holder = (viewHolder) convertView.getTag();
            }


            /*
            * 在每一个item在添加动画前，都把当前显示区域内所有item动画给取消，
            * 然后给当前convertView添加上动画；当listview滚动到最后一个Item的时候，
            * 自然，同样也是先把所有动画取消，然后给他自己添加上动画，
            * 所以这样看起来就好像是只给他自己添加了动画，之前滚动的item是没有动画的。
            * */
            for (int i = 0; i < lv_news.getChildCount(); i++) {
                View view = lv_news.getChildAt(i);
                view.clearAnimation();
            }

            //如果现在是向下滑，我们才给convertView加上动画！
            if (isScrollDown) {
                convertView.startAnimation(mAnimation);
            }


            String readSeq = SharedPreferenceUtils.getString(UIUtils.getContext(), "read", "");
            if (readSeq.contains(mEntities.get(position).getId() + "")) {
                holder.tv_title.setTextColor(UIUtils.getColor(R.color.clicked_tv_textcolor));
            } else {
                holder.tv_title.setTextColor(UIUtils.getColor(R.color.light_news_topic));
            }

            holder.ll_root.setBackgroundColor(UIUtils.getColor(R.color.light_news_item));
            holder.tv_topic.setTextColor(UIUtils.getColor(R.color.light_news_topic));


            //判断是否是头item
            StoriesEntity entity = mEntities.get(position);
            if (entity.getType() == Constant.TOPIC) {
                //是头item,那么只显示一个tpic的TextView,其余的都隐藏掉
                holder.fl_container.setBackgroundColor(Color.TRANSPARENT);
                holder.tv_title.setVisibility(View.GONE);
                holder.iv_title.setVisibility(View.GONE);
                holder.tv_topic.setVisibility(View.VISIBLE);
                holder.tv_topic.setText(entity.getTitle());

            } else {
                //如果不是,现实普通的布局
                holder.fl_container.setBackgroundResource(R.drawable.item_background_selector_light);
                holder.tv_topic.setVisibility(View.GONE);
                holder.tv_title.setVisibility(View.VISIBLE);
                holder.iv_title.setVisibility(View.VISIBLE);
                holder.tv_title.setText(entity.getTitle());
                mImageLoader.displayImage(entity.getImages().get(0), holder.iv_title, mOptions);
            }

            return convertView;
        }


        class viewHolder {
            TextView tv_topic;
            TextView tv_title;
            ImageView iv_title;
            LinearLayout ll_root;
            FrameLayout fl_container;
            RelativeLayout rl_root;

        }
    }
}
