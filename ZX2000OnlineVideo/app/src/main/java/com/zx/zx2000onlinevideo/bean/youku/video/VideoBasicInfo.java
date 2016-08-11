package com.zx.zx2000onlinevideo.bean.youku.video;

import java.util.List;

/**
 * Created by ShaudXiao on 2016/8/1.
 */
public class VideoBasicInfo {

    /**
     * tokens : videoid:2029,2030
     * orderby :
     * offset : 1
     * total : 2
     * index_cost : 0
     * results : [{"pk_video":"2029","videoid":"XODExNg==","title":"电影如果爱预告片","thumburl":"http://r2.ykimg.com/05420408521C652D6A0A4710C9C87E30","thumburl_v2":"http://r4.ykimg.com/05420408521C652D6A0A4710C9C87E30","userid":"526","username":"小钢炮94","seconds":"71.47","total_comment":"26","total_fav":"3","total_up":"1989","total_down":"61","publishtime":"2006-05-22 09:14:52","isoriginal":"0","sharestate":"OPEN","state":"normal","streamtypes":["flvhd"],"Category":"音乐","total_vv":389951,"tags":"歌舞电影,如果爱,张学友","show_id":"30595","show_videotype":"预告片"},{"pk_video":"2030","videoid":"XODEyMA==","title":"肯德基食得轻盈广告","desc":"好像和我们大陆的不太一样啊","thumburl":"http://r2.ykimg.com/05420408521C652E6A0A481CF2C42D50","thumburl_v2":"http://r3.ykimg.com/05420408521C652E6A0A481CF2C42D50","userid":"88","username":"Melissa802","seconds":"23.87","total_comment":"0","total_fav":"6","total_up":"11","total_down":"2","publishtime":"2006-05-25 19:51:25","isoriginal":"0","sharestate":"OPEN","state":"normal","streamtypes":["flvhd"],"Category":"广告","total_vv":6743,"tags":"KFC,肯德基,广告"}]
     * total_cost : 0.012033939361572
     */

    private String tokens;
    private String orderby;
    private int offset;
    private int total;
    private int index_cost;
    private double total_cost;
    /**
     * pk_video : 2029
     * videoid : XODExNg==
     * title : 电影如果爱预告片
     * thumburl : http://r2.ykimg.com/05420408521C652D6A0A4710C9C87E30
     * thumburl_v2 : http://r4.ykimg.com/05420408521C652D6A0A4710C9C87E30
     * userid : 526
     * username : 小钢炮94
     * seconds : 71.47
     * total_comment : 26
     * total_fav : 3
     * total_up : 1989
     * total_down : 61
     * publishtime : 2006-05-22 09:14:52
     * isoriginal : 0
     * sharestate : OPEN
     * state : normal
     * streamtypes : ["flvhd"]
     * Category : 音乐
     * total_vv : 389951
     * tags : 歌舞电影,如果爱,张学友
     * show_id : 30595
     * show_videotype : 预告片
     */

    private List<ResultsBean> results;

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getIndex_cost() {
        return index_cost;
    }

    public void setIndex_cost(int index_cost) {
        this.index_cost = index_cost;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(double total_cost) {
        this.total_cost = total_cost;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        private String pk_video;
        private String videoid;
        private String title;
        private String thumburl;
        private String thumburl_v2;
        private String userid;
        private String username;
        private String seconds;
        private String total_comment;
        private String total_fav;
        private String total_up;
        private String total_down;
        private String publishtime;
        private String isoriginal;
        private String sharestate;
        private String state;
        private String category;
        private int total_vv;
        private String tags;
        private String show_id;
        private String show_videotype;
        private List<String> streamtypes;

        public String getPk_video() {
            return pk_video;
        }

        public void setPk_video(String pk_video) {
            this.pk_video = pk_video;
        }

        public String getVideoid() {
            return videoid;
        }

        public void setVideoid(String videoid) {
            this.videoid = videoid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getThumburl() {
            return thumburl;
        }

        public void setThumburl(String thumburl) {
            this.thumburl = thumburl;
        }

        public String getThumburl_v2() {
            return thumburl_v2;
        }

        public void setThumburl_v2(String thumburl_v2) {
            this.thumburl_v2 = thumburl_v2;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSeconds() {
            return seconds;
        }

        public void setSeconds(String seconds) {
            this.seconds = seconds;
        }

        public String getTotal_comment() {
            return total_comment;
        }

        public void setTotal_comment(String total_comment) {
            this.total_comment = total_comment;
        }

        public String getTotal_fav() {
            return total_fav;
        }

        public void setTotal_fav(String total_fav) {
            this.total_fav = total_fav;
        }

        public String getTotal_up() {
            return total_up;
        }

        public void setTotal_up(String total_up) {
            this.total_up = total_up;
        }

        public String getTotal_down() {
            return total_down;
        }

        public void setTotal_down(String total_down) {
            this.total_down = total_down;
        }

        public String getPublishtime() {
            return publishtime;
        }

        public void setPublishtime(String publishtime) {
            this.publishtime = publishtime;
        }

        public String getIsoriginal() {
            return isoriginal;
        }

        public void setIsoriginal(String isoriginal) {
            this.isoriginal = isoriginal;
        }

        public String getSharestate() {
            return sharestate;
        }

        public void setSharestate(String sharestate) {
            this.sharestate = sharestate;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public int getTotal_vv() {
            return total_vv;
        }

        public void setTotal_vv(int total_vv) {
            this.total_vv = total_vv;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getShow_id() {
            return show_id;
        }

        public void setShow_id(String show_id) {
            this.show_id = show_id;
        }

        public String getShow_videotype() {
            return show_videotype;
        }

        public void setShow_videotype(String show_videotype) {
            this.show_videotype = show_videotype;
        }

        public List<String> getStreamtypes() {
            return streamtypes;
        }

        public void setStreamtypes(List<String> streamtypes) {
            this.streamtypes = streamtypes;
        }
    }
}
