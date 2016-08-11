package com.zx.zx2000onlinevideo.api.youku;

import com.zx.zx2000onlinevideo.bean.youku.category.ProgramByCategory;
import com.zx.zx2000onlinevideo.bean.youku.category.VideoByCategory;
import com.zx.zx2000onlinevideo.bean.youku.program.ProgramShow;
import com.zx.zx2000onlinevideo.bean.youku.program.RelatedProgram;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by ShaudXiao on 2016/8/1.
 */
public interface YoukuApi {

    /*
    *
    * 根据视频分类获取视频信息
    *
    * */

    @GET("https://openapi.youku.com/v2/videos/by_category.json")
    Observable<VideoByCategory> getVideoByCategory(@Query("client_id") String clientId,
                                                   @Query("category") String category,
                                                   @Query("genre") String genre,
                                                   @Query("period") String period,
                                                   @Query("orderby") String orderby,
                                                   @Query("page") int page,
                                                   @Query("count") int count);

    /*
*
* 根据视频分类获取节目列表
*
* */
    @GET("https://openapi.youku.com/v2/shows/by_category.json")
    Observable<ProgramByCategory> getProgramByCategory(@Query("client_id") String clientId,
                                                       @Query("category") String category,
                                                       @Query("genre") String genre,
                                                       @Query("area") String area,
                                                       @Query("orderby") String orderby,
                                                       @Query("page") int page,
                                                       @Query("count") int count);
    /*
    *
    * 获取单个节目详细信息
    *
    * */
    /*
    /**
     * youku单条视频详细信息
     *
     * @param client_id
     * @param video_id
     * @return
     */
    @GET("https://openapi.youku.com/v2/shows/show.json")
    Observable<ProgramShow> getProgramShowVideoItem(@Query("client_id") String client_id,
                                            @Query("show_id") String video_id);



    /**
     * youku单条视频相节目信息
     *
     * @param client_id
     * @param show_id
     * @return
     */

    @GET("https://openapi.youku.com/v2/shows/by_related.json")
    Observable<RelatedProgram> getRelatedProgram(@Query("client_id")String client_id,
                                                 @Query("show_id") String video_id,
                                                 @Query("ext") String count);

}
