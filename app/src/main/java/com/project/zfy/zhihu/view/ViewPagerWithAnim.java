package com.project.zfy.zhihu.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义的带动画效果的ViewPager 用于引导页面
 * Created by zfy on 2016/8/13.
 */
public class ViewPagerWithAnim extends ViewPager {


    private View mLeftView;
    private View mRightView;

    private float mTrans;
    private float mScale;
    private float mAlpha;

    private static final float MIN_SCALE = 0.6f;

    private Map<Integer, View> mChildren = new HashMap<>();

    public ViewPagerWithAnim(Context context) {
        super(context);
    }

    public ViewPagerWithAnim(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
    *将View机器对应的pisiton设置进HashMap
    *@author zfy
    *@created at 2016/8/13 17:14
    */
    public void setViewForPosition(View view, int position) {
        mChildren.put(position, view);

    }


    /**
    *将HashMap在position位置处的view移除掉
    *@author zfy
    *@created at 2016/8/13 17:15
    */
    public void removeViewForPosition(int position) {
        mChildren.remove(position);
    }


    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {

//        Log.d("onPageScrolled--->", "position:" + position + ",offset:" + offset + ",offsetPixels:" + offsetPixels);


        //通过属性动画,来达到动画的效果
        mLeftView = mChildren.get(position);
        mRightView = mChildren.get(position + 1);

        animStack(mLeftView, mRightView, offset, offsetPixels);

        super.onPageScrolled(position, offset, offsetPixels);
    }


    /**
     * 给ViewPager的View设置的动画
     *
     * @author zfy
     * @created at 2016/8/13 16:23
     */
    private void animStack(View leftView, View rightView, float offset, int offsetPixels) {
        if (rightView != null) {
            mScale = (1 - MIN_SCALE) * offset + MIN_SCALE;
            mTrans = -getWidth() - getPageMargin() + offsetPixels;

            rightView.setScaleX(mScale);
            rightView.setScaleY(mScale);

            rightView.setTranslationX(mTrans);
            rightView.setTranslationY(0);


        }

        if (leftView != null) {
            mAlpha = 1 - offset;

            leftView.bringToFront();

            leftView.setAlpha(mAlpha);
        }
    }


}
