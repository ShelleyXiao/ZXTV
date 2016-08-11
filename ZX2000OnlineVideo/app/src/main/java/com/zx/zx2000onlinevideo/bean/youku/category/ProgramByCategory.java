package com.zx.zx2000onlinevideo.bean.youku.category;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-01
 * Time: 16:59
 * Company: zx
 * Description: 根据分类获取节目
 * FIXME
 */

public class ProgramByCategory {

    /**
     * total : 100
     * shows : [{"id":"2a7260de1faa11e097c0","name":"裸婚时代","link":"http://www.youku.com/show_page/id_z2a7260de1faa11e097c0.html","play_link":"http://v.youku.com/v_show/id_XMjc0MzM1OTgw.html","poster":"http://g4.ykimg.com/0100641F464E1FC...","thumbnail":"http://g4.ykimg.com/0100641F464E1FC...","streamtypes":["hd2,flvhd,flv,hd,3gp,3gphd"],"episode_count":30,"episode_updated":30,"view_count":"345037980","score":9.3,"paid":0,"published":"0000-00-00","released":"2011-06-11"}]
     */

    private int total;
    /**
     * id : 2a7260de1faa11e097c0
     * name : 裸婚时代
     * link : http://www.youku.com/show_page/id_z2a7260de1faa11e097c0.html
     * play_link : http://v.youku.com/v_show/id_XMjc0MzM1OTgw.html
     * poster : http://g4.ykimg.com/0100641F464E1FC...
     * thumbnail : http://g4.ykimg.com/0100641F464E1FC...
     * streamtypes : ["hd2,flvhd,flv,hd,3gp,3gphd"]
     * episode_count : 30
     * episode_updated : 30
     * view_count : 345037980
     * score : 9.3
     * paid : 0
     * published : 0000-00-00
     * released : 2011-06-11
     */

    private List<ShowsBean> shows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ShowsBean> getShows() {
        return shows;
    }

    public void setShows(List<ShowsBean> shows) {
        this.shows = shows;
    }

    public static class ShowsBean {
        private String id;
        private String name;
        private String link;
        private String play_link;
        private String poster;
        private String thumbnail;
        private int episode_count;
        private int episode_updated;
        private String view_count;
        private double score;
        private int paid;
        private String published;
        private String released;
        private List<String> streamtypes;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getPlay_link() {
            return play_link;
        }

        public void setPlay_link(String play_link) {
            this.play_link = play_link;
        }

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public int getEpisode_count() {
            return episode_count;
        }

        public void setEpisode_count(int episode_count) {
            this.episode_count = episode_count;
        }

        public int getEpisode_updated() {
            return episode_updated;
        }

        public void setEpisode_updated(int episode_updated) {
            this.episode_updated = episode_updated;
        }

        public String getView_count() {
            return view_count;
        }

        public void setView_count(String view_count) {
            this.view_count = view_count;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getPaid() {
            return paid;
        }

        public void setPaid(int paid) {
            this.paid = paid;
        }

        public String getPublished() {
            return published;
        }

        public void setPublished(String published) {
            this.published = published;
        }

        public String getReleased() {
            return released;
        }

        public void setReleased(String released) {
            this.released = released;
        }

        public List<String> getStreamtypes() {
            return streamtypes;
        }

        public void setStreamtypes(List<String> streamtypes) {
            this.streamtypes = streamtypes;
        }

        @Override
        public String toString() {
            return "ShowsBean{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", link='" + link + '\'' +
                    ", play_link='" + play_link + '\'' +
                    ", poster='" + poster + '\'' +
                    ", thumbnail='" + thumbnail + '\'' +
                    ", episode_count=" + episode_count +
                    ", episode_updated=" + episode_updated +
                    ", view_count='" + view_count + '\'' +
                    ", score=" + score +
                    ", paid=" + paid +
                    ", published='" + published + '\'' +
                    ", released='" + released + '\'' +
                    ", streamtypes=" + streamtypes +
                    '}';
        }
    }
}
