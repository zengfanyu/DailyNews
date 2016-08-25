package com.project.zfy.zhihu.event;

/**
 * 菜单页面条目点击之后的EventBus事件
 * Created by zfy on 2016/8/25.
 */
public class MenuItemEvent {
    private String title;
    private String id;

    public MenuItemEvent(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }
}
