package com.project.zfy.zhihu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.activity.MainActivity;
import com.project.zfy.zhihu.global.Constant;
import com.project.zfy.zhihu.moudle.StoriesEntity;
import com.project.zfy.zhihu.utils.SharedPreferenceUtils;
import com.project.zfy.zhihu.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面新闻列表的adapter
 * Created by zfy on 2016/8/2.
 */
public class MainNewsItemAdapter extends BaseAdapter {
    private List<StoriesEntity> mEntities;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private Animation mAnimation;
    private ListView lv_news;
    private Context mContext;


    /**
     * 构造方法
     *
     * @author zfy
     * @created at 2016/8/2 14:13
     */
    public MainNewsItemAdapter(Context context) {
        this.mEntities = new ArrayList<StoriesEntity>();
        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        mAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_in_anim);


    }

    private boolean isScrollDown;
    private int mFirstPosition, mFirstTop;
    AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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


            //listView存在,并且有item项
            if (lv_news != null && lv_news.getChildCount() > 0) {
                //只有当可以看到的第一个item编号是0,并且第一个可以看到的item完全可见!!!!!!!!!!! swipe才可以刷新
                boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);

                ((MainActivity) mContext).setSwipeRefreshLayoutEnable(enable);


            }
        }
    };


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
//            holder.tv_topic = (TextView) convertView.findViewById(R.id.tv_topic);
            holder.ll_root = (LinearLayout) convertView.findViewById(R.id.ll_root);
            holder.fl_container = (FrameLayout) convertView.findViewById(R.id.fl_container);
            holder.rl_root = (RelativeLayout) convertView.findViewById(R.id.rl_root);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }


        //清除当前显示区域中所有item的动画，保证只有最后一个item添加动画
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
//        holder.tv_topic.setTextColor(UIUtils.getColor(R.color.light_news_topic));


        //判断是否是头item
        StoriesEntity entity = mEntities.get(position);
        if (entity.getType() == Constant.TOPIC) {
            //是头item,那么只显示一个tpic的TextView,其余的都隐藏掉
            holder.fl_container.setBackgroundColor(Color.TRANSPARENT);
            holder.tv_title.setVisibility(View.GONE);
            holder.iv_title.setVisibility(View.GONE);
//            holder.tv_topic.setVisibility(View.VISIBLE);
//            holder.tv_topic.setText(entity.getTitle());

        } else {
            //如果不是,现实普通的布局
            holder.fl_container.setBackgroundResource(R.drawable.item_background_selector_light);
//            holder.tv_topic.setVisibility(View.GONE);
            holder.tv_title.setVisibility(View.VISIBLE);
            holder.iv_title.setVisibility(View.VISIBLE);
            holder.tv_title.setText(entity.getTitle());
            mImageLoader.displayImage(entity.getImages().get(0), holder.iv_title, mOptions);
        }

        return convertView;
    }


    class viewHolder {
//        TextView tv_topic;
        TextView tv_title;
        ImageView iv_title;
        LinearLayout ll_root;
        FrameLayout fl_container;
        RelativeLayout rl_root;

    }
}
