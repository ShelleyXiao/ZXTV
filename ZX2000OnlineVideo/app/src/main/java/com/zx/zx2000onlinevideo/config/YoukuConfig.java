package com.zx.zx2000onlinevideo.config;

/**
 * Created by ShaudXiao on 2016/8/1.
 */
public class YoukuConfig {

    public static final String YOUKU_BASE_URL = "https://openapi.youku.com";

    public static final String CLIENT_ID = "8645c3062b918e02";
    public static final String CLIENT_SECRET = "28e7ff34f4d2dd888ccb9f1bd7c502ee";

    public static final String CONSTANCE_VID = "vid";

    public static final String API_VERSION = "v3";

    public static final String YOUKU_VIDEO_SET = "youku_video_set";

    public static final int SHOW_PAGE_COUNT = 28;

    public static final String INTENT_CATEGROY_KEY = "category";
    public static final String INTENT_GENRE_KEY = "genre";
    public static final String INTENT_VIDEO_TYPE = "type";
    public static final String INTENT_VIDEO_ID = "videoId";

    // show 类型
    public static final String MOVIE = "电影"; // 电影
    public static final String SERIALS = "电视剧"; // 电视剧
    public static final String VIDEO = "video";

    public static enum  TYPE {
        Moive, Serails, other
    };

}
