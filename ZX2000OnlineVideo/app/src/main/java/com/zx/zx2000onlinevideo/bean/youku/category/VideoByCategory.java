package com.zx.zx2000onlinevideo.bean.youku.category;

import com.zx.zx2000onlinevideo.bean.youku.video.VideoBean;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-01
 * Time: 16:27
 * Company: zx
 * Description: 通过分类获取视频
 * FIXME
 */
public class VideoByCategory {

    /**
     * total : 100
     * page : 1
     * count : 20
     * videos : [{"id":"XMjg1MTcyNDQ0","title":"泡芙小姐的灯泡 11","link":"http://v.youku.com/v_show/id_XMjg1MTcyNDQ0.html","thumbnail":"http://g4.ykimg.com/0100641F464E1FC...","duration":"910","category":"原创","state":"normal","view_count":2555843,"favorite_count":534,"comment_count":1000,"up_count":14859,"down_count":559,"published":"2011-07-15 09:00:42","user":{"id":58921428,"name":"泡芙小姐","link":"http://u.youku.com/user_show/id_UMjM1Njg1NzEy.html"},"operation_limit":["COMMENT_DISABLED"],"streamtypes":["flv","flvhd","hd"],"favorite_time":"2012-01-01 09:00:00"}]
     */

    private int total;
    private int page;
    private int count;
    /**
     * id : XMjg1MTcyNDQ0
     * title : 泡芙小姐的灯泡 11
     * link : http://v.youku.com/v_show/id_XMjg1MTcyNDQ0.html
     * thumbnail : http://g4.ykimg.com/0100641F464E1FC...
     * duration : 910
     * category : 原创
     * state : normal
     * view_count : 2555843
     * favorite_count : 534
     * comment_count : 1000
     * up_count : 14859
     * down_count : 559
     * published : 2011-07-15 09:00:42
     * user : {"id":58921428,"name":"泡芙小姐","link":"http://u.youku.com/user_show/id_UMjM1Njg1NzEy.html"}
     * operation_limit : ["COMMENT_DISABLED"]
     * streamtypes : ["flv","flvhd","hd"]
     * favorite_time : 2012-01-01 09:00:00
     */

    private List<VideoBean> videos;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<VideoBean> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoBean> videos) {
        this.videos = videos;
    }

}
