package com.zx.zx2000onlinevideo.bean.youku.program;

import java.util.List;

/**
 * Created by Shelley on 2016/8/7.
 */
public class ProgramShow {

    /**
     * id : 834adfc2368f11e1b52a
     * name : 宫锁珠帘
     * alias : [{"type":"0","alias":"宫2"},{"type":"0","alias":"宫锁心玉2"},{"type":"1","alias":"The Palace"}]
     * link : http://www.youku.com/show_page/id_z834adfc2368f11e1b52a.html
     * play_link : http://v.youku.com/v_show/id_XMzQ0ODQ4MzU2.html
     * poster : http://res.mfs.ykimg.com/050D00004FB317B60000015BB40555AE
     * thumbnail : http://g4.ykimg.com/0102641F464F14DBC4000000000000EED795E3-03F6-60B6-2F19-0301E4B671CF
     * streamtypes : ["hd2","flvhd","hd","3gphd"]
     * genre : 剧情,古装,言情
     * area : 大陆
     * completed : 1
     * episode_count : 37
     * episode_updated : 37
     * view_count : 188875880
     * score : 7.985
     * paid : 0
     * published : 2012-01-20
     * released : 2012-01-20
     * category : 电视剧
     * description : 雍正年间，候补四品典仪凌柱之女怜儿为父请命结识十七王爷胤礼，二人两情依依，本要结为夫妇，没想到胤礼为救老师阿灵阿，不得已娶了阿灵阿之女嘉嘉为妻，伤心欲绝的怜儿在深宫里步步为营，渴望能走出深宫过平凡的日子，不想却被宫中各股势力所利用，李卫要跟她结盟，大太监苏培盛要向她报恩，亲如姐妹的玉漱出卖她，表面和谐的深宫里埋藏着各种秘密，就在怜儿快喘不过气时，她意外地被雍正看中，成为了万千宠爱于一身的熹妃，而就在这时，她忽然发现所有的事情一下子都变复杂了，停下来就会被迫害，走下去可能连自己也不认识自己了，在三岔路口，她选择了相信阳光，相信暴风雨总会过去的，于是在她坚忍不拔的努力下，终于为自己闯出了一片天。
     * rank : 0
     * view_yesterday_count : 0
     * view_week_count : 291406
     * comment_count : 88272
     * favorite_count : 7160
     * up_count : 581262
     * down_count : 125799
     * attr : {"director":[{"id":"16924","name":"李慧珠","link":"http://www.youku.com/star_page/uid_UNjc2OTY=.html"}],"performer":[{"id":"22017","name":"杜淳","character":"胤礼","link":"http://www.youku.com/star_page/uid_UODgwNjg=.html"},{"id":"331783","name":"何晟铭","character":"雍正","link":"http://www.youku.com/star_page/uid_UMTMyNzEzMg==.html"},{"id":"382806","name":"袁姗姗","character":"怜儿","link":"http://www.youku.com/star_page/uid_UMTUzMTIyNA==.html"},{"id":"23163","name":"张嘉倪","character":"玉漱","link":"http://www.youku.com/star_page/uid_UOTI2NTI=.html"},{"id":"16669","name":"舒畅","character":"海棠","link":"http://www.youku.com/star_page/uid_UNjY2NzY=.html"}],"screenwriter":[{"id":"322397","name":"于正","link":"http://www.youku.com/star_page/uid_UMTI4OTU4OA==.html"}],"starring":null,"producer":null,"original":null}
     */

    private String id;
    private String name;
    private String link;
    private String play_link;
    private String poster;
    private String thumbnail;
    private String genre;
    private String area;
    private int completed;
    private String episode_count;
    private String episode_updated;
    private String view_count;
    private String score;
    private int paid;
    private String published;
    private String released;
    private String category;
    private String description;
    private String rank;
    private String view_yesterday_count;
    private String view_week_count;
    private String comment_count;
    private String favorite_count;
    private String up_count;
    private String down_count;
    /**
     * director : [{"id":"16924","name":"李慧珠","link":"http://www.youku.com/star_page/uid_UNjc2OTY=.html"}]
     * performer : [{"id":"22017","name":"杜淳","character":"胤礼","link":"http://www.youku.com/star_page/uid_UODgwNjg=.html"},{"id":"331783","name":"何晟铭","character":"雍正","link":"http://www.youku.com/star_page/uid_UMTMyNzEzMg==.html"},{"id":"382806","name":"袁姗姗","character":"怜儿","link":"http://www.youku.com/star_page/uid_UMTUzMTIyNA==.html"},{"id":"23163","name":"张嘉倪","character":"玉漱","link":"http://www.youku.com/star_page/uid_UOTI2NTI=.html"},{"id":"16669","name":"舒畅","character":"海棠","link":"http://www.youku.com/star_page/uid_UNjY2NzY=.html"}]
     * screenwriter : [{"id":"322397","name":"于正","link":"http://www.youku.com/star_page/uid_UMTI4OTU4OA==.html"}]
     * starring : null
     * producer : null
     * original : null
     */

    private AttrBean attr;
    /**
     * type : 0
     * alias : 宫2
     */

    private List<ShowaliasBean> alias;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public String getEpisode_count() {
        return episode_count;
    }

    public void setEpisode_count(String episode_count) {
        this.episode_count = episode_count;
    }

    public String getEpisode_updated() {
        return episode_updated;
    }

    public void setEpisode_updated(String episode_updated) {
        this.episode_updated = episode_updated;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getView_yesterday_count() {
        return view_yesterday_count;
    }

    public void setView_yesterday_count(String view_yesterday_count) {
        this.view_yesterday_count = view_yesterday_count;
    }

    public String getView_week_count() {
        return view_week_count;
    }

    public void setView_week_count(String view_week_count) {
        this.view_week_count = view_week_count;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getFavorite_count() {
        return favorite_count;
    }

    public void setFavorite_count(String favorite_count) {
        this.favorite_count = favorite_count;
    }

    public String getUp_count() {
        return up_count;
    }

    public void setUp_count(String up_count) {
        this.up_count = up_count;
    }

    public String getDown_count() {
        return down_count;
    }

    public void setDown_count(String down_count) {
        this.down_count = down_count;
    }

    public AttrBean getAttr() {
        return attr;
    }

    public void setAttr(AttrBean attr) {
        this.attr = attr;
    }

    public List<ShowaliasBean> getAlias() {
        return alias;
    }

    public void setAlias(List<ShowaliasBean> alias) {
        this.alias = alias;
    }

    public List<String> getStreamtypes() {
        return streamtypes;
    }

    public void setStreamtypes(List<String> streamtypes) {
        this.streamtypes = streamtypes;
    }

    public static class AttrBean {
        private Object starring;
        private Object producer;
        private Object original;
        /**
         * id : 16924
         * name : 李慧珠
         * link : http://www.youku.com/star_page/uid_UNjc2OTY=.html
         */

        private List<DirectorBean> director;
        /**
         * id : 22017
         * name : 杜淳
         * character : 胤礼
         * link : http://www.youku.com/star_page/uid_UODgwNjg=.html
         */

        private List<PerformerBean> performer;
        /**
         * id : 322397
         * name : 于正
         * link : http://www.youku.com/star_page/uid_UMTI4OTU4OA==.html
         */

        private List<ScreenwriterBean> screenwriter;

        public Object getStarring() {
            return starring;
        }

        public void setStarring(Object starring) {
            this.starring = starring;
        }

        public Object getProducer() {
            return producer;
        }

        public void setProducer(Object producer) {
            this.producer = producer;
        }

        public Object getOriginal() {
            return original;
        }

        public void setOriginal(Object original) {
            this.original = original;
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

        public static class DirectorBean {
            private String id;
            private String name;
            private String link;

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
        }

        public static class PerformerBean {
            private String id;
            private String name;
            private String character;
            private String link;

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

            public String getCharacter() {
                return character;
            }

            public void setCharacter(String character) {
                this.character = character;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }
        }

        public static class ScreenwriterBean {
            private String id;
            private String name;
            private String link;

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
        }
    }

    @Override
    public String toString() {
        return "ProgramShow{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", play_link='" + play_link + '\'' +
                ", poster='" + poster + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", genre='" + genre + '\'' +
                ", area='" + area + '\'' +
                ", completed=" + completed +
                ", episode_count='" + episode_count + '\'' +
                ", episode_updated='" + episode_updated + '\'' +
                ", view_count='" + view_count + '\'' +
                ", score='" + score + '\'' +
                ", paid=" + paid +
                ", published='" + published + '\'' +
                ", released='" + released + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", rank='" + rank + '\'' +
                ", view_yesterday_count='" + view_yesterday_count + '\'' +
                ", view_week_count='" + view_week_count + '\'' +
                ", comment_count='" + comment_count + '\'' +
                ", favorite_count='" + favorite_count + '\'' +
                ", up_count='" + up_count + '\'' +
                ", down_count='" + down_count + '\'' +
                ", attr=" + attr +
                ", alias=" + alias +
                ", streamtypes=" + streamtypes +
                '}';
    }
}
