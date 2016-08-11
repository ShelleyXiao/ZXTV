package com.zx.zx2000onlinevideo.bean.youku.video;

import java.util.List;
/**
 * User: ShaudXiao
 * Date: 2016-08-01
 * Time: 16:27
 * Company: zx
 * Description: 视频详细信息
 * FIXME
 */

public class VideoBean {

    /**
     * id : XNDY5Njc0MTA4
     * title : 康熙来了 121101
     * link : http://v.youku.com/v_show/id_XNDY5Njc0MTA4.html
     * thumbnail : http://g2.ykimg.com/01270F1F46509251F539D10123193CD2CB70CC-5896-F53E-B869-61E819861E71
     * duration : 2675.36
     * category : 综艺
     * state : normal
     * created : 2011-07-15 09:00:42
     * published : 2011-07-15 09:00:42
     * description : 康熙来了
     * player : http://player.youku.com/player.php/sid/XNDY5Njc0MTA4/v.swf
     * public_type : all
     * copyright_type : reproduced
     * user : {"id":58011986,"name":"康熙来了2010","link":"http://i.youku.com/u/UMjMyMDQ3OTQ0"}
     * tags : 康熙来了
     * view_count : 646437
     * favorite_count : 124
     * comment_count : 547
     * up_count : 3060
     * down_count : 724
     * reference_count : 0
     * operation_limit : []
     * streamtypes : ["hd2","flvhd","mp4","3gp","3gphd"]
     * thumbnails : [{"seq":1,"url":"http://g3.ykimg.com/01270F1F46509251F204590123193C80F22A26-EDE9-96A1-EB60-8FA55743C21C","is_cover":0},{"seq":2,"url":"http://g4.ykimg.com/01270F1F46509251F37D7D0123193C0E845A38-7A41-3CCA-00A9-2908EF58F301","is_cover":0},{"seq":3,"url":"http://g1.ykimg.com/01270F1F46509251F4E7800123193CCF7AB6E4-D027-1157-4F9F-D5783E051474","is_cover":0},{"seq":4,"url":"http://g2.ykimg.com/01270F1F46509251F539D10123193CD2CB70CC-5896-F53E-B869-61E819861E71","is_cover":1},{"seq":5,"url":"http://g3.ykimg.com/01270F1F46509251F625770123193C706B1FB5-4BAB-9077-2701-498C2E1DB452","is_cover":0},{"seq":6,"url":"http://g4.ykimg.com/01270F1F46509251F7DA0F0123193C43216832-6BF1-98C7-A93B-F5F003D06E3A","is_cover":0},{"seq":7,"url":"http://g1.ykimg.com/01270F1F46509251F8DC940123193C8074B788-FA97-3BAE-0273-AAECBCBAC873","is_cover":0},{"seq":8,"url":"http://g2.ykimg.com/01270F1F46509251F966270123193C863C7E46-5504-D446-3BAB-2A056EC1A550","is_cover":0}]
     */

    private String id;
    private String title;
    private String link;
    private String thumbnail;
    private String duration;
    private String category;
    private String state;
    private String created;
    private String published;
    private String description;
    private String player;
    private String public_type;
    private String copyright_type;
    /**
     * id : 58011986
     * name : 康熙来了2010
     * link : http://i.youku.com/u/UMjMyMDQ3OTQ0
     */

    private UserBean user;
    private String tags;
    private int view_count;
    private String favorite_count;
    private String comment_count;
    private int up_count;
    private int down_count;
    private int reference_count;
    private List<?> operation_limit;
    private List<String> streamtypes;
    /**
     * seq : 1
     * url : http://g3.ykimg.com/01270F1F46509251F204590123193C80F22A26-EDE9-96A1-EB60-8FA55743C21C
     * is_cover : 0
     */

    private List<ThumbnailsBean> thumbnails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getPublic_type() {
        return public_type;
    }

    public void setPublic_type(String public_type) {
        this.public_type = public_type;
    }

    public String getCopyright_type() {
        return copyright_type;
    }

    public void setCopyright_type(String copyright_type) {
        this.copyright_type = copyright_type;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public String getFavorite_count() {
        return favorite_count;
    }

    public void setFavorite_count(String favorite_count) {
        this.favorite_count = favorite_count;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public int getUp_count() {
        return up_count;
    }

    public void setUp_count(int up_count) {
        this.up_count = up_count;
    }

    public int getDown_count() {
        return down_count;
    }

    public void setDown_count(int down_count) {
        this.down_count = down_count;
    }

    public int getReference_count() {
        return reference_count;
    }

    public void setReference_count(int reference_count) {
        this.reference_count = reference_count;
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

    @Override
    public String toString() {
        return "VideoBean{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", duration='" + duration + '\'' +
                ", category='" + category + '\'' +
                ", state='" + state + '\'' +
                ", created='" + created + '\'' +
                ", published='" + published + '\'' +
                ", description='" + description + '\'' +
                ", player='" + player + '\'' +
                ", public_type='" + public_type + '\'' +
                ", copyright_type='" + copyright_type + '\'' +
                ", user=" + user +
                ", tags='" + tags + '\'' +
                ", view_count=" + view_count +
                ", favorite_count='" + favorite_count + '\'' +
                ", comment_count='" + comment_count + '\'' +
                ", up_count=" + up_count +
                ", down_count=" + down_count +
                ", reference_count=" + reference_count +
                ", operation_limit=" + operation_limit +
                ", streamtypes=" + streamtypes +
                ", thumbnails=" + thumbnails +
                '}';
    }
}
