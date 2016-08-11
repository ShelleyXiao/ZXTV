package com.zx.zx2000onlinevideo.bean.youku.program;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-01
 * Time: 16:28
 * Company: zx
 * Description: 节目信息（综艺、电影等）
 * FIXME
 */

public class ProgramBean {

    private double reputation;
    private Object update_notice;
    private String showname;
    private String showsubtitle;
    private String pk_odshow;
    private String state;
    private String copyright_status;
    private String releasedate;
    private String releasedate_mainland;
    private String releasedate_youku;
    private String onlinetime;
    private String offlinetime;
    private String douban_num;
    private String showcategory;
    private String showcreatetime;
    private int id;
    private int showseq;
    private int seriesid;
    private int price;
    private int permit_duration;
    private int showtotal_down;
    private int showtotal_up;
    private int showtotal_fav;
    private int showtotal_comment;
    private int showweek_vv;
    private int showday_vv;
    private int showtotal_vv;
    private int video_collected;
    private int episode_last;
    private int episode_total;
    private int paid;
    private String showid;
    private Object series;
    private String showdesc;
    private String show_thumburl;
    private List<String> site;
    private List<?> pay_type;
    private List<String> hasvideotype;
    private List<String> streamtypes;
    private List<String> movie_genre;
    private List<?> tv_genre;
    private List<?> anime_genre;
    private List<?> mv_genre;
    private List<String> area;
    private List<?> variety_genre;
    private List<?> sports_genre;
    private List<?> edu_genre;
    private List<?> news_genre;
    private List<?> doc_genre;
    /**
     * type : 0
     * alias : 如果·爱
     */

    private List<ShowaliasBean> showalias;
    /**
     * id : 59725
     * name : 陈可辛
     * thumburl : http://r1.ykimg.com/05130000532AA31667379F188508C57D
     */

    private List<DirectorBean> director;
    /**
     * id : 17344
     * name : 周迅
     * thumburl : http://r4.ykimg.com/051300004F96446F9792737BD50C5180
     * character : ["孙纳"]
     */

    private List<PerformerBean> performer;
    /**
     * id : 104347
     * name : 林爱华
     * thumburl : http://r1.ykimg.com/05130000533E260A67379F17AB0E016E
     */

    private List<ScreenwriterBean> screenwriter;
    /**
     * id : 59725
     * name : 陈可辛
     * thumburl : http://r1.ykimg.com/05130000532AA31667379F188508C57D
     */

    private List<ProducerBean> producer;
    /**
     * id : 10410
     * name : 天映娱乐
     * type : distributor
     */

    private List<DistributorBean> distributor;
    /**
     * id : 14156
     * name : 甲上娱乐有限公司
     * type : production
     */

    private List<ProductionBean> production;

    public double getReputation() {
        return reputation;
    }

    public void setReputation(double reputation) {
        this.reputation = reputation;
    }

    public Object getUpdate_notice() {
        return update_notice;
    }

    public void setUpdate_notice(Object update_notice) {
        this.update_notice = update_notice;
    }

    public String getShowname() {
        return showname;
    }

    public void setShowname(String showname) {
        this.showname = showname;
    }

    public String getShowsubtitle() {
        return showsubtitle;
    }

    public void setShowsubtitle(String showsubtitle) {
        this.showsubtitle = showsubtitle;
    }

    public String getPk_odshow() {
        return pk_odshow;
    }

    public void setPk_odshow(String pk_odshow) {
        this.pk_odshow = pk_odshow;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCopyright_status() {
        return copyright_status;
    }

    public void setCopyright_status(String copyright_status) {
        this.copyright_status = copyright_status;
    }

    public String getReleasedate() {
        return releasedate;
    }

    public void setReleasedate(String releasedate) {
        this.releasedate = releasedate;
    }

    public String getReleasedate_mainland() {
        return releasedate_mainland;
    }

    public void setReleasedate_mainland(String releasedate_mainland) {
        this.releasedate_mainland = releasedate_mainland;
    }

    public String getReleasedate_youku() {
        return releasedate_youku;
    }

    public void setReleasedate_youku(String releasedate_youku) {
        this.releasedate_youku = releasedate_youku;
    }

    public String getOnlinetime() {
        return onlinetime;
    }

    public void setOnlinetime(String onlinetime) {
        this.onlinetime = onlinetime;
    }

    public String getOfflinetime() {
        return offlinetime;
    }

    public void setOfflinetime(String offlinetime) {
        this.offlinetime = offlinetime;
    }

    public String getDouban_num() {
        return douban_num;
    }

    public void setDouban_num(String douban_num) {
        this.douban_num = douban_num;
    }

    public String getShowcategory() {
        return showcategory;
    }

    public void setShowcategory(String showcategory) {
        this.showcategory = showcategory;
    }

    public String getShowcreatetime() {
        return showcreatetime;
    }

    public void setShowcreatetime(String showcreatetime) {
        this.showcreatetime = showcreatetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShowseq() {
        return showseq;
    }

    public void setShowseq(int showseq) {
        this.showseq = showseq;
    }

    public int getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(int seriesid) {
        this.seriesid = seriesid;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPermit_duration() {
        return permit_duration;
    }

    public void setPermit_duration(int permit_duration) {
        this.permit_duration = permit_duration;
    }

    public int getShowtotal_down() {
        return showtotal_down;
    }

    public void setShowtotal_down(int showtotal_down) {
        this.showtotal_down = showtotal_down;
    }

    public int getShowtotal_up() {
        return showtotal_up;
    }

    public void setShowtotal_up(int showtotal_up) {
        this.showtotal_up = showtotal_up;
    }

    public int getShowtotal_fav() {
        return showtotal_fav;
    }

    public void setShowtotal_fav(int showtotal_fav) {
        this.showtotal_fav = showtotal_fav;
    }

    public int getShowtotal_comment() {
        return showtotal_comment;
    }

    public void setShowtotal_comment(int showtotal_comment) {
        this.showtotal_comment = showtotal_comment;
    }

    public int getShowweek_vv() {
        return showweek_vv;
    }

    public void setShowweek_vv(int showweek_vv) {
        this.showweek_vv = showweek_vv;
    }

    public int getShowday_vv() {
        return showday_vv;
    }

    public void setShowday_vv(int showday_vv) {
        this.showday_vv = showday_vv;
    }

    public int getShowtotal_vv() {
        return showtotal_vv;
    }

    public void setShowtotal_vv(int showtotal_vv) {
        this.showtotal_vv = showtotal_vv;
    }

    public int getVideo_collected() {
        return video_collected;
    }

    public void setVideo_collected(int video_collected) {
        this.video_collected = video_collected;
    }

    public int getEpisode_last() {
        return episode_last;
    }

    public void setEpisode_last(int episode_last) {
        this.episode_last = episode_last;
    }

    public int getEpisode_total() {
        return episode_total;
    }

    public void setEpisode_total(int episode_total) {
        this.episode_total = episode_total;
    }

    public int getPaid() {
        return paid;
    }

    public void setPaid(int paid) {
        this.paid = paid;
    }

    public String getShowid() {
        return showid;
    }

    public void setShowid(String showid) {
        this.showid = showid;
    }

    public Object getSeries() {
        return series;
    }

    public void setSeries(Object series) {
        this.series = series;
    }

    public String getShowdesc() {
        return showdesc;
    }

    public void setShowdesc(String showdesc) {
        this.showdesc = showdesc;
    }

    public String getShow_thumburl() {
        return show_thumburl;
    }

    public void setShow_thumburl(String show_thumburl) {
        this.show_thumburl = show_thumburl;
    }

    public List<String> getSite() {
        return site;
    }

    public void setSite(List<String> site) {
        this.site = site;
    }

    public List<?> getPay_type() {
        return pay_type;
    }

    public void setPay_type(List<?> pay_type) {
        this.pay_type = pay_type;
    }

    public List<String> getHasvideotype() {
        return hasvideotype;
    }

    public void setHasvideotype(List<String> hasvideotype) {
        this.hasvideotype = hasvideotype;
    }

    public List<String> getStreamtypes() {
        return streamtypes;
    }

    public void setStreamtypes(List<String> streamtypes) {
        this.streamtypes = streamtypes;
    }

    public List<String> getMovie_genre() {
        return movie_genre;
    }

    public void setMovie_genre(List<String> movie_genre) {
        this.movie_genre = movie_genre;
    }

    public List<?> getTv_genre() {
        return tv_genre;
    }

    public void setTv_genre(List<?> tv_genre) {
        this.tv_genre = tv_genre;
    }

    public List<?> getAnime_genre() {
        return anime_genre;
    }

    public void setAnime_genre(List<?> anime_genre) {
        this.anime_genre = anime_genre;
    }

    public List<?> getMv_genre() {
        return mv_genre;
    }

    public void setMv_genre(List<?> mv_genre) {
        this.mv_genre = mv_genre;
    }

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }

    public List<?> getVariety_genre() {
        return variety_genre;
    }

    public void setVariety_genre(List<?> variety_genre) {
        this.variety_genre = variety_genre;
    }

    public List<?> getSports_genre() {
        return sports_genre;
    }

    public void setSports_genre(List<?> sports_genre) {
        this.sports_genre = sports_genre;
    }

    public List<?> getEdu_genre() {
        return edu_genre;
    }

    public void setEdu_genre(List<?> edu_genre) {
        this.edu_genre = edu_genre;
    }

    public List<?> getNews_genre() {
        return news_genre;
    }

    public void setNews_genre(List<?> news_genre) {
        this.news_genre = news_genre;
    }

    public List<?> getDoc_genre() {
        return doc_genre;
    }

    public void setDoc_genre(List<?> doc_genre) {
        this.doc_genre = doc_genre;
    }

    public List<ShowaliasBean> getShowalias() {
        return showalias;
    }

    public void setShowalias(List<ShowaliasBean> showalias) {
        this.showalias = showalias;
    }

    public List<DirectorBean> getDirector() {
        return director;
    }

    public void setDirector(List<DirectorBean> director) {
        this.director = director;
    }

    public List<PerformerBean> getPerformer() {
        return performer;
    }

    public void setPerformer(List<PerformerBean> performer) {
        this.performer = performer;
    }

    public List<ScreenwriterBean> getScreenwriter() {
        return screenwriter;
    }

    public void setScreenwriter(List<ScreenwriterBean> screenwriter) {
        this.screenwriter = screenwriter;
    }

    public List<ProducerBean> getProducer() {
        return producer;
    }

    public void setProducer(List<ProducerBean> producer) {
        this.producer = producer;
    }

    public List<DistributorBean> getDistributor() {
        return distributor;
    }

    public void setDistributor(List<DistributorBean> distributor) {
        this.distributor = distributor;
    }

    public List<ProductionBean> getProduction() {
        return production;
    }

    public void setProduction(List<ProductionBean> production) {
        this.production = production;
    }

    public static class ProducerBean {
        private String id;
        private String name;
        private String thumburl;

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

        public String getThumburl() {
            return thumburl;
        }

        public void setThumburl(String thumburl) {
            this.thumburl = thumburl;
        }
    }

    public static class DistributorBean {
        private String id;
        private String name;
        private String type;

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class ProductionBean {
        private String id;
        private String name;
        private String type;

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
