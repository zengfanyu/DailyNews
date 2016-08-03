package com.project.zfy.zhihu.moudle;

import java.util.List;

/**
 * 过往消息的model
 * Created by zfy on 2016/8/3.
 */
public class Before {

    /**
     * date : 20160802
     * stories : [{"ga_prefix":"080222","id":8640277,"images":["http://pic4.zhimg.com/3078e8e89d118ddbfbaa4c3cca7d51d3.jpg"],"title":"小事 · 骗子也在进步","type":0},{"ga_prefix":"080221","id":8640113,"images":["http://pic1.zhimg.com/8df70e7c790badecf7a2fbbca17d973c.jpg"],"title":"这部电影是一头怪物","type":0},{"ga_prefix":"080219","id":8639944,"images":["http://pic1.zhimg.com/c598f9d164023c3f9a4bfbbabeec94a0.jpg"],"title":"想让用户快速增长，数据分析很有用，但一定不是万能的","type":0},{"ga_prefix":"080218","id":8639629,"images":["http://pic4.zhimg.com/01a46ce8f916bef8c48d087c21fd6ebb.jpg"],"multipic":true,"title":"如何系统地学习毛笔书法？（多图）","type":0},{"ga_prefix":"080217","id":8635759,"images":["http://pic3.zhimg.com/272fc3ff3acc6ac13f7cc4b5a18fb722.jpg"],"title":"知乎好问题 · 跟别人聊天没有话题，怎么办？","type":0},{"ga_prefix":"080216","id":8639755,"images":["http://pic4.zhimg.com/c291ecc1ade6c7e8dc4e837cdbdbe5f7.jpg"],"multipic":true,"title":"怎么鉴别假钱，应该不会有比这更权威的答案了","type":0},{"ga_prefix":"080215","id":8634940,"images":["http://pic2.zhimg.com/fa026f9fb1fa0a1d8981e1041af34d99.jpg"],"title":"「病人、来访者、消费者」，你觉得哪个称呼更合适？","type":0},{"ga_prefix":"080214","id":8635432,"images":["http://pic3.zhimg.com/7362cec70d515f6fff35c26a3731cdf6.jpg"],"title":"卖版权的盖蒂图片社犯了一个错，代价是 10 亿美元","type":0},{"ga_prefix":"080213","id":8635129,"images":["http://pic3.zhimg.com/ff1850a48538580f68973cdd9ff0cdc6.jpg"],"title":"在创业公司待久了的你，工作还开心吗？","type":0},{"ga_prefix":"080212","id":8638583,"images":["http://pic3.zhimg.com/c20550ac061a1b008ea640e98435a9f2.jpg"],"title":"大误 · 单恋女神和我被困在电梯里","type":0},{"ga_prefix":"080211","id":8637039,"images":["http://pic2.zhimg.com/145a59c90cf217335c0e1e04880ba195.jpg"],"multipic":true,"title":"出了事故之后，老司机表示，我是真的没看到啊","type":0},{"ga_prefix":"080210","id":8636755,"images":["http://pic4.zhimg.com/04bf664188b13700cf7eae367c5d0d6b.jpg"],"title":"从大战到并购，灰色到合法，打车软件这一路是怎么走来的","type":0},{"ga_prefix":"080209","id":8639267,"images":["http://pic3.zhimg.com/752c99a86142d95496e5198fe26be4fe.jpg"],"title":"商务部：「等等，还没跟我申报，怎么就宣布合并了」","type":0},{"ga_prefix":"080209","id":8631053,"images":["http://pic4.zhimg.com/405365ad77791df8cdcca6604712ae4f.jpg"],"title":"为什么中国的 GDP 平减指数和消费者物价指数相差很大？","type":0},{"ga_prefix":"080208","id":8637688,"images":["http://pic1.zhimg.com/9e78fb8d9e2fbcce3b0a9a2c4311002c.jpg"],"title":"同样是阅读，「刷」和「搜索」，差异巨大","type":0},{"ga_prefix":"080207","id":8637621,"images":["http://pic1.zhimg.com/89a09d6b9ff1f0adb33a0f4c656769e8.jpg"],"title":"那些以为滴滴不会与 Uber 合并的人，还是低估了资本的力量","type":0},{"ga_prefix":"080207","id":8623801,"images":["http://pic2.zhimg.com/ee4517b834b8a51cf9ae37c2e82c7571.jpg"],"title":"天空为什么是蓝的？熟悉的「散射」并不是问题的全部","type":0},{"ga_prefix":"080207","id":8629958,"images":["http://pic1.zhimg.com/b0edbcf5b02e81dfb93750323feccd48.jpg"],"title":"小时候，我真的以为这是外星人在地球修建的基地","type":0},{"ga_prefix":"080207","id":8637343,"images":["http://pic4.zhimg.com/98867ac6733e0bcf3787f98ebde1e267.jpg"],"title":"读读日报 24 小时热门 TOP 5 · 卧底「教你连接宇宙能量」的心灵培训班","type":0},{"ga_prefix":"080206","id":8632997,"images":["http://pic1.zhimg.com/11507461011f7f666ba40af0c61a76b8.jpg"],"title":"瞎扯 · 如何正确地吐槽","type":0}]
     */

    public String date;
    /**
     * ga_prefix : 080222
     * id : 8640277
     * images : ["http://pic4.zhimg.com/3078e8e89d118ddbfbaa4c3cca7d51d3.jpg"]
     * title : 小事 · 骗子也在进步
     * type : 0
     */

    public List<StoriesEntity> stories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<StoriesEntity> getStories() {
        return stories;
    }

    public void setStories(List<StoriesEntity> stories) {
        this.stories = stories;
    }
}
