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

import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.global.MyApplication;
import com.project.zfy.zhihu.utils.ToastUtils;

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

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.inject(this, view);

        //对返回首页的TextView设置监听事件
        tv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.ToastUtils(MyApplication.getContext(), "主页被点击了");

            }
        });


        //对ListView设置点击事件
        lv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });


        return view;
    }


    @Override
    public void initData() {
        super.initData();


    }

    //解析服务器端传过来的json数据
    public void parseJsonData(String jsonData) {

    }

    //侧边栏ListView的Adapter
    class NewsTypeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            return null;
        }
    }


}
