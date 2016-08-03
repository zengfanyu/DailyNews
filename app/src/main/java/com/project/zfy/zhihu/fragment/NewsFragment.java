package com.project.zfy.zhihu.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 主题日报的Fragment
 * Created by zfy on 2016/8/3.
 */

@SuppressLint("ValidFragment")
public class NewsFragment extends BaseFragment {

    private String title;

    public NewsFragment(String title) {
        this.title = title;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        TextView textView = new TextView(mActivity);

        textView.setText(title);


        return textView;
    }

    @Override
    public void initData() {
        super.initData();
    }

    public void parseJsonData() {

    }
}
