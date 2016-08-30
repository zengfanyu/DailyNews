package com.project.zfy.zhihu.fragment;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.logger.Logger;
import com.project.zfy.zhihu.R;
import com.project.zfy.zhihu.event.DialogFragmentEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * $desc
 * Created by zfy on 2016/8/26.
 */
public class ImageViewDialogFragment extends DialogFragment {
    private String imgUrl;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        EventBus.getDefault().register(ImageViewDialogFragment.this);

        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Window window = getDialog().getWindow();
        View view = View.inflate(getActivity(), R.layout.fragment_imageview_dialog, null);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        window.setLayout(R.dimen.dimen_599_dip, R.dimen.dimen_599_dip);//这2行,和上面的一样,注意顺序就行;
        ImageView ziv_view = (ImageView) view.findViewById(R.id.ziv_view);

        mImageLoader.displayImage(imgUrl, ziv_view, mOptions);


        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEventMainThread(DialogFragmentEvent event) {

        this.imgUrl = event.getImgUrl();


        Logger.d("onMessageEventMainThread-DialogFragmentEvent" + "imgUrl:" + imgUrl);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(ImageViewDialogFragment.this);
    }
}
