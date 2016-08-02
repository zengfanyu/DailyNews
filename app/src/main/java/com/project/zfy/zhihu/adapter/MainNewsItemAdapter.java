package com.project.zfy.zhihu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.project.zfy.zhihu.R;
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

        String readSeq = SharedPreferenceUtils.getString(UIUtils.getContext(), "read", "");
        if (readSeq.contains(mEntities.get(position).getId() + "")) {
            holder.tv_title.setTextColor(UIUtils.getColor(R.color.clicked_tv_textcolor));
        } else {
            holder.tv_title.setTextColor(UIUtils.getColor(R.color.light_news_topic));
        }

        holder.ll_root.setBackgroundColor(UIUtils.getColor(R.color.light_news_item));
        holder.tv_topic.setTextColor(UIUtils.getColor(R.color.light_news_topic));

        StoriesEntity entity = mEntities.get(position);
        if (entity.getType() == Constant.TOPIC) {
            holder.fl_container.setBackgroundColor(Color.TRANSPARENT);
            holder.rl_root.setVisibility(View.GONE);
            holder.tv_topic.setText(entity.getTitle());

        } else {
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
