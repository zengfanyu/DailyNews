package com.project.zfy.zhihu.event;

import com.project.zfy.zhihu.moudle.StoriesEntity;

/**
 * 主题日报的列表item点击的Eventbus事件
 * Created by zfy on 2016/8/25.
 */
public class NewsFragmentEvent {
    private int[] startingLocation;
    private StoriesEntity entity;

    public NewsFragmentEvent(int[] startingLocation, StoriesEntity entity) {
        this.startingLocation = startingLocation;
        this.entity = entity;
    }

    public int[] getStartingLocation() {
        return startingLocation;
    }

    public StoriesEntity getEntity() {
        return entity;
    }
}
