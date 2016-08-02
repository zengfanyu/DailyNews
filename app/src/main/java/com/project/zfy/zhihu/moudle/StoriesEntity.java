package com.project.zfy.zhihu.moudle;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wwjun.wang on 2015/8/14.
 */
public class StoriesEntity implements Serializable {

    /*  {
          "ga_prefix":"080214",
          "id":8635432,
          "images":["http://pic3.zhimg.com/7362cec70d515f6fff35c26a3731cdf6.jpg"],
          "title":"卖版权的盖蒂图片社犯了一个错，代价是 10 亿美元",
          "type":0
      }
  */
    private int id;
    private String title;
    //    private String ga_prefix;
    private List<String> images;
    private int type;

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public void setGa_prefix(String ga_prefix) {
//        this.ga_prefix = ga_prefix;
//    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

//    public String getGa_prefix() {
//        return ga_prefix;
//    }

    public List<String> getImages() {
        return images;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "StoriesEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                /*", ga_prefix='" + ga_prefix + '\'' +*/
                ", images=" + images +
                ", type=" + type +
                '}';
    }
}
