package com.zx.zx2000onlinevideo.bean.youku.video;

import java.util.List;

/**
 * Created by ShaudXiao on 2016/8/1.
 */
public class DvdBean {

    private PointBean point;
    private String tv_starttime;
    private String desc;
    /**
     * id : 317206
     * name : 余筱萍
     * start : 112494
     * type : guest
     * link : http://www.youku.com/star_page/uid_UMTI2ODgyNA==
     */

    private List<Actor> person;

    public PointBean getPoint() {
        return point;
    }

    public void setPoint(PointBean point) {
        this.point = point;
    }

    public String getTv_starttime() {
        return tv_starttime;
    }

    public void setTv_starttime(String tv_starttime) {
        this.tv_starttime = tv_starttime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Actor> getPerson() {
        return person;
    }

    public void setPerson(List<Actor> person) {
        this.person = person;
    }

    public static class PointBean {
        /**
         * start : 307043
         * title : 张艾亚大秀台步 蹩脚姿势被嘲笑
         * desc :
         */

        private List<StoryBean> story;

        public List<StoryBean> getStory() {
            return story;
        }

        public void setStory(List<StoryBean> story) {
            this.story = story;
        }

        public static class StoryBean {
            private String start;
            private String title;
            private String desc;

            public String getStart() {
                return start;
            }

            public void setStart(String start) {
                this.start = start;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }
        }
    }

}
