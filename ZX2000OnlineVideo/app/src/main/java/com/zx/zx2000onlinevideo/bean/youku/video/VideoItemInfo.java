package com.zx.zx2000onlinevideo.bean.youku.video;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-01
 * Time: 16:27
 * Company: zx
 * Description: 视频条目信息
 * FIXME
 */
public class VideoItemInfo {


    VideoBean video;
    /**
     * id : 2ab71ff032cb11e196ac
     * name : 康熙来了 2012
     * link : http://www.youku.com/show_page/id_z2ab71ff032cb11e196ac.html
     * type : 正片
     * seq : 215
     * stage : 20121101
     */

    private ShowBean show;
    /**
     * point : {"story":[{"start":"307043","title":"张艾亚大秀台步 蹩脚姿势被嘲笑","desc":""},{"start":"1060907","title":"陈汉典野战造型 半裸狂跳骑马舞","desc":""},{"start":"1736034","title":"陈汉典大展身手 跳远似飞引欢呼","desc":""},{"start":"2046152","title":"小S教瘦身秘诀 超速竞走秀曲线","desc":""}]}
     * tv_starttime : 2012-11-01 22:00:00
     * desc : 本期《康熙来了》沈玉琳、许建国、王尹平、余筱萍和张艾亚纷纷挑战运动项目，他们每人在球场上表现如何呢？陈汉典为何现场大秀骑马舞？小S竟也放下身段，快速竞走传授她的曲线秘诀。敬请关注！
     * person : [{"id":"317206","name":"余筱萍","start":"112494","type":"guest","link":"http://www.youku.com/star_page/uid_UMTI2ODgyNA=="},{"id":"330092","name":"沈玉琳","start":"68347","type":"guest","link":"http://www.youku.com/star_page/uid_UMTMyMDM2OA=="},{"id":"419241","name":"许建国","start":"490796","type":"guest","link":"http://www.youku.com/star_page/uid_UMTY3Njk2NA=="},{"id":"421849","name":"张艾亚","start":"313980","type":"guest","link":"http://www.youku.com/star_page/uid_UMTY4NzM5Ng=="},{"id":"422446","name":"王尹平","start":"245714","type":"guest","link":"http://www.youku.com/star_page/uid_UMTY4OTc4NA=="}]
     */

    private DvdBean dvd;
    /**
     * id : 1
     * name : 优酷站内WEB上传
     * link : http://www.youku.com/v_up/
     */

    private SourceBean source;
    private List<?> operation_limit;
    private List<String> streamtypes;
    /**
     * seq : 1
     * url : http://g3.ykimg.com/01270F1F46509251F204590123193C80F22A26-EDE9-96A1-EB60-8FA55743C21C
     * is_cover : 0
     */

    private List<ThumbnailsBean> thumbnails;

    public ShowBean getShow() {
        return show;
    }

    public void setShow(ShowBean show) {
        this.show = show;
    }

    public DvdBean getDvd() {
        return dvd;
    }

    public void setDvd(DvdBean dvd) {
        this.dvd = dvd;
    }

    public SourceBean getSource() {
        return source;
    }

    public void setSource(SourceBean source) {
        this.source = source;
    }

    public List<?> getOperation_limit() {
        return operation_limit;
    }

    public void setOperation_limit(List<?> operation_limit) {
        this.operation_limit = operation_limit;
    }

    public List<String> getStreamtypes() {
        return streamtypes;
    }

    public void setStreamtypes(List<String> streamtypes) {
        this.streamtypes = streamtypes;
    }

    public List<ThumbnailsBean> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(List<ThumbnailsBean> thumbnails) {
        this.thumbnails = thumbnails;
    }
}
