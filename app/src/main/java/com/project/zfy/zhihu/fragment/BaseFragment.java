package com.project.zfy.zhihu.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment的基类 所有的Fragment必须继承该类
 * <p/>
 * 1.定义Activity常量,方便子类使用
 * 2.定义抽象方法initView(),初始化布局,必须实现的!!!!
 * 3.定义方法initData(),初始化数据,可以不实现
 * Created by zfy on 2016/8/2.
 */
public abstract class BaseFragment extends Fragment {


    public Activity mActivity;

    @Override //Fragment创建
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override   //Fragment填充布局
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();

        return initView(inflater, container, savedInstanceState);


    }

    @Override  //Fragment所依赖的Activity创建完成
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    public void initData() {

    }

    @Override //Fragment销毁
    public void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    /**
     * 初始化布局方法,所有的Fragment子类必须实现
     *
     * @author zfy
     * @created at 2016/8/2 10:36
     */
    public abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState);
}
