package com.zx.zx2000onlinevideo.presenter.impl;

import android.content.Context;

import com.zx.zx2000onlinevideo.api.youku.YoukuRequest;
import com.zx.zx2000onlinevideo.bean.youku.ProgramQueryConditions;
import com.zx.zx2000onlinevideo.bean.youku.category.ProgramByCategory;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.presenter.IProgramSetPresenter;
import com.zx.zx2000onlinevideo.ui.IView.IProgramSetActivity;
import com.zx.zx2000onlinevideo.utils.CacheUtil;
import com.zx.zx2000onlinevideo.utils.Logger;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * User: ShaudXiao
 * Date: 2016-08-05
 * Time: 15:55
 * Company: zx
 * Description:
 * FIXME
 */

public class ProgramSetPresenterImpl extends BasePresenterImpl implements IProgramSetPresenter {

    private IProgramSetActivity mProgramSetActivity;
    private CacheUtil mCacheUtil;

    public ProgramSetPresenterImpl(IProgramSetActivity iProgramSetActivity, Context context) {
        if(iProgramSetActivity == null) {
            throw new IllegalArgumentException("iProgramSetActivity must not be null");
        }

        mProgramSetActivity = iProgramSetActivity;
        mCacheUtil = CacheUtil.get(context);
    }

    @Override
    public void getProgramSetByCategory(String category, String genre, ProgramQueryConditions conditions, int page, int count) {
        mProgramSetActivity.showProgressDialog();
        Logger.getLogger().e("category: " + category + " genre: " + genre + "period: " + conditions.getAreaCondition()
                + " ordeyBy: " + conditions.getOrderbyCondition());
        Subscription subscription = YoukuRequest.getYoukuApi().getProgramByCategory(YoukuConfig.CLIENT_ID, category, genre,
                conditions.getAreaCondition(), conditions.getOrderbyCondition(), page, count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProgramByCategory>() {
                    @Override
                    public void onCompleted() {
                        mProgramSetActivity.hidProgressDialog();
                        Logger.getLogger().d("Subscription onCompleted ");

                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgramSetActivity.showError("");
                        Logger.getLogger().d("Subscription onError " + e.getMessage());
                    }

                    @Override
                    public void onNext(ProgramByCategory programByCategory) {
                        mProgramSetActivity.hidProgressDialog();
                        Logger.getLogger().d("Subscription onNext " + programByCategory.getShows().get(1).toString());
                        mProgramSetActivity.updateVideoTotal(programByCategory.getTotal());
                        mProgramSetActivity.updateList(programByCategory.getShows());
//                      缓存
//                        mCacheUtil.put(YoukuConfig.YOUKU_VIDEO_SET, new Gson().toJson(videoByCategory));
                    }
                });

        addSubscription(subscription);
    }
}
