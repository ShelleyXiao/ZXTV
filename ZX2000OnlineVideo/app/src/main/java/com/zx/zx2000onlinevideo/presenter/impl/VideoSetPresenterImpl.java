package com.zx.zx2000onlinevideo.presenter.impl;

import android.content.Context;

import com.zx.zx2000onlinevideo.api.youku.YoukuRequest;
import com.zx.zx2000onlinevideo.bean.youku.QueryConditions;
import com.zx.zx2000onlinevideo.bean.youku.category.VideoByCategory;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.presenter.IVideoSetPresenter;
import com.zx.zx2000onlinevideo.ui.IView.IVideoSetActivity;
import com.zx.zx2000onlinevideo.utils.CacheUtil;
import com.zx.zx2000onlinevideo.utils.Logger;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * User: ShaudXiao
 * Date: 2016-08-04
 * Time: 14:51
 * Company: zx
 * Description:
 * FIXME
 */

public class VideoSetPresenterImpl extends BasePresenterImpl implements IVideoSetPresenter {

    private IVideoSetActivity mVideoSetActivity;
    private CacheUtil mCacheUtil;

    public VideoSetPresenterImpl(IVideoSetActivity iVideoSetActivity, Context context) {
        if(iVideoSetActivity == null) {
            throw new IllegalArgumentException("iVideoSetActivity must not be null");
        }

        mVideoSetActivity = iVideoSetActivity;
        mCacheUtil = CacheUtil.get(context);
    }

    @Override
    public void getVideoSetByCategory(String category, String genre, QueryConditions conditions, int page, int count) {
        mVideoSetActivity.showProgressDialog();
        Logger.getLogger().e("category: " + category + " genre: " + genre + "period: " + conditions.getPeiodCondition()
                        + " ordeyBy: " + conditions.getOrderbyCondition());
        Subscription subscription = YoukuRequest.getYoukuApi().getVideoByCategory(YoukuConfig.CLIENT_ID, category, genre,
                        conditions.getPeiodCondition(), conditions.getOrderbyCondition(), page, count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VideoByCategory>() {
                    @Override
                    public void onCompleted() {
                        mVideoSetActivity.hidProgressDialog();
                        Logger.getLogger().d("Subscription onCompleted ");

                    }

                    @Override
                    public void onError(Throwable e) {
                        mVideoSetActivity.showError("");
                        Logger.getLogger().d("Subscription onError " + e.getMessage());
                    }

                    @Override
                    public void onNext(VideoByCategory videoByCategory) {
                        mVideoSetActivity.hidProgressDialog();
                        Logger.getLogger().d("Subscription onNext " + videoByCategory.getVideos().get(0).toString());
                        mVideoSetActivity.updateVideoTotal(videoByCategory.getTotal());
                        mVideoSetActivity.updateList(videoByCategory.getVideos());
//                      缓存
//                        mCacheUtil.put(YoukuConfig.YOUKU_VIDEO_SET, new Gson().toJson(videoByCategory));
                    }
                });

        addSubscription(subscription);
    }

}
