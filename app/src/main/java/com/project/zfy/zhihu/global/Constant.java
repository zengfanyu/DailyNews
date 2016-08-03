package com.project.zfy.zhihu.global;

/**
 * 定义全局用到的字符串,包括Url链接,和KEY等..
 * 以下API均由https://github.com/izzyleung/ZhihuDailyPurify/wiki/%E7%9F%A5%E4%B9%8E%E6%97%A5%E6%8A%A5-API-%E5%88%86%E6%9E%90
 * Created by zfy on 2016/8/1.
 */
public class Constant {
    /**
     * API的基地址,其余绝大多数的地址均是在此地址后面拼接得到的
     *
     * @author zfy
     * @created at 2016/8/2 8:39
     */
    public static final String BASEURL = "http://news-at.zhihu.com/api/4/";


    /**
     * Splash界面图片的地址,不需要拼接基地址
     *
     * @author zfy
     * @created at 2016/8/2 8:45
     */
    public static final String START = "start-image/1080*1776";
    
    /**
    *主题日报列表URL
    *@author zfy
    *@created at 2016/8/2 12:47
    */
    public static final String THEMES ="themes";

    public static final int TOPIC = 131;
    
    /**
    *最新消息的URL
    *@author zfy
    *@created at 2016/8/2 15:27
    */
    public static final String LATESTNEWS = "news/latest";

    /**
    *最新消息的最大值
    *@author zfy
    *@created at 2016/8/2 15:34
    */
    public static final int LATEST_COLUMN = Integer.MAX_VALUE;

    /**
    *过往消息的url
    *@author zfy
    *@created at 2016/8/3 11:12
    */
    public static final String DEFORE = "news/before/";
}
