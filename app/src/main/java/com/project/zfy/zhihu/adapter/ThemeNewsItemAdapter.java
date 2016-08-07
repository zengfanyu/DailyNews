package com.project.zfy.zhihu.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.global.Constant;
import com.project.zfy.zhihu.moudle.StoriesEntity;
import com.project.zfy.zhihu.utils.SharedPreferenceUtils;
import com.project.zfy.zhihu.utils.UIUtils;

import java.util.List;

/**
 * 主题日报listView展示列表的adapter
 * Created by zfy on 2016/8/4.
 */
public class ThemeNewsItemAdapter extends BaseAdapter {
    private Context mContext;
    private List<StoriesEntity> mEntities;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;


    public ThemeNewsItemAdapter(Context context, List<StoriesEntity> items) {
        this.mContext = context;
        this.mEntities = items;
        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

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
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.theme_list_news_item, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.iv_title = (ImageView) convertView.findViewById(R.id.iv_title);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        StoriesEntity entity = getItem(position);

        holder.tv_title.setText(entity.getTitle());

        //根据SP中的标记,来判断此条新闻是否点击过,从而来改变title的颜色
        String readIds = SharedPreferenceUtils.getString(mContext, Constant.READ_IDS, "");
        if (readIds.contains(getItem(position).getId() + "")) {
            holder.tv_title.setTextColor(UIUtils.getColor(R.color.clicked_tv_textcolor));
        } else {
            holder.tv_title.setTextColor(UIUtils.getColor(R.color.light_news_topic));
        }

        if (entity.getImages() != null) {
            holder.iv_title.setVisibility(View.VISIBLE);
            mImageLoader.displayImage(entity.getImages().get(0), holder.iv_title, mOptions);
        } else {
            holder.iv_title.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        TextView tv_title;
        ImageView iv_title;
    }
}
