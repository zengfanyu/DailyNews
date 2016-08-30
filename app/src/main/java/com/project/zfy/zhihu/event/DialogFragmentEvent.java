package com.project.zfy.zhihu.event;

/**
 * $desc
 * Created by zfy on 2016/8/26.
 */
public class DialogFragmentEvent {
    private String imgUrl;

    public DialogFragmentEvent(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
