package com.project.zfy.zhihu.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.activity.MainActivity;
import com.project.zfy.zhihu.global.Constant;
import com.project.zfy.zhihu.global.MyApplication;
import com.project.zfy.zhihu.moudle.ThemesListItem;
import com.project.zfy.zhihu.utils.HttpUtils;
import com.project.zfy.zhihu.utils.SharedPreferenceUtils;
import com.project.zfy.zhihu.utils.ToastUtils;
import com.project.zfy.zhihu.utils.UIUtils;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 侧边栏的Fragment
 * Created by zfy on 2016/8/2.
 */
public class MenuFragment extends BaseFragment {
    @InjectView(R.id.lv_item)
    ListView lv_item;
    @InjectView(R.id.tv_download)
    TextView tv_download;
    @InjectView(R.id.tv_login)
    TextView tv_login;
    @InjectView(R.id.tv_backup)
    TextView tv_backup;
    @InjectView(R.id.tv_main)
    TextView tv_main;
    @InjectView(R.id.ll_menu)
    LinearLayout ll_menu;

    private ArrayList<ThemesListItem> mItems;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.inject(this, view);

        //对返回首页的TextView设置监听事件
        tv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.ToastUtils(MyApplication.getContext(), "主页被点击了");
                //更新数据
                ((MainActivity) mActivity).loadLatest();
                //收起侧边栏
                ((MainActivity) mActivity).closeMenu();

            }
        });


        //对ListView设置点击事件
        lv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                        .replace(R.id.fl_content, new NewsFragment(mItems.get(position).getTitle(), mItems.get(position).getId()))
                        .commit();

                //更新当前显示界面的标记值
                ((MainActivity) mActivity).setCurrentId(mItems.get(position).getId());
                //主界面跳转到主题日报之后,收起侧边栏
                ((MainActivity) mActivity).closeMenu();


            }
        });


        return view;
    }


    @Override
    public void initData() {
        super.initData();
        mItems = new ArrayList<ThemesListItem>();
        if (HttpUtils.isNetworkConnected(UIUtils.getContext())) {
            HttpUtils.get(Constant.THEMES, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    //toString之后缓存到数据库
                    String jsonData = response.toString();
                    SharedPreferenceUtils.putString(UIUtils.getContext(), Constant.THEMES, jsonData);
                    //解析json数据
                    parseJsonData(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        } else {
            //如果没有网络,则从SP中拿缓存
            String json = SharedPreferenceUtils.getString(UIUtils.getContext(), Constant.THEMES, "");

            try {
                //将string类型的数据转换为JsonObject
                JSONObject jsonObject = new JSONObject(json);
                parseJsonData(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

    //解析服务器端传过来的json数据
    public void parseJsonData(JSONObject jsonData) {
        try {
            JSONArray themesItemArray = jsonData.getJSONArray("others");
            for (int i = 0; i < themesItemArray.length(); i++) {
                ThemesListItem themesListItem = new ThemesListItem();
                JSONObject item = themesItemArray.getJSONObject(i);
                themesListItem.setTitle(item.getString("name"));
                themesListItem.setId(item.getString("id"));
                mItems.add(themesListItem);
            }

            lv_item.setAdapter(new NewsTypeAdapter());


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //侧边栏ListView的Adapter
    class NewsTypeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public ThemesListItem getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            viewHolder holder;
            if (convertView == null) {
                holder = new viewHolder();
                convertView = View.inflate(UIUtils.getContext(), R.layout.menu_list_themes_item, null);
                holder.tv_item = (TextView) convertView.findViewById(R.id.tv_item);
                convertView.setTag(holder);
            } else {
                holder = (viewHolder) convertView.getTag();
            }

            holder.tv_item.setTextColor(UIUtils.getColor(R.color.light_menu_listview_textcolcr));
            holder.tv_item.setText(getItem(position).getTitle());


            return convertView;
        }
    }

    class viewHolder {
        TextView tv_item;
    }


}
