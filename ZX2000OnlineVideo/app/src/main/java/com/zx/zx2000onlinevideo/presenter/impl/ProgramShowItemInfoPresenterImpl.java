package com.zx.zx2000onlinevideo.presenter.impl;

import android.content.Context;

import com.zx.zx2000onlinevideo.api.youku.YoukuRequest;
import com.zx.zx2000onlinevideo.bean.youku.program.ProgramShow;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.presenter.ISingleProgramGetInfoPresenter;
import com.zx.zx2000onlinevideo.ui.IView.IProgramShowItemInfoActivity;
import com.zx.zx2000onlinevideo.utils.CacheUtil;
import com.zx.zx2000onlinevideo.utils.Logger;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Shelley on 2016/8/7.
 */
public class ProgramShowItemInfoPresenterImpl extends BasePresenterImpl implements ISingleProgramGetInfoPresenter {

    private IProgramShowItemInfoActivity mProgramShowItemInfoActivity;
    private CacheUtil mCacheUtil;

    public ProgramShowItemInfoPresenterImpl(IProgramShowItemInfoActivity iProgramSetActivity, Context context) {
        if(iProgramSetActivity == null) {
            throw new IllegalArgumentException("IProgramShowItemInfoActivity must not be null");
        }

        mProgramShowItemInfoActivity = iProgramSetActivity;
        mCacheUtil = CacheUtil.get(context);
    }


    @Override
    public void getSingleProgramInfo(String videoId) {
        mProgramShowItemInfoActivity.showProgressDialog();
        Logger.getLogger().d("videoId " + videoId);
        Subscription subscription = YoukuRequest.getYoukuApi().getProgramShowVideoItem(YoukuConfig.CLIENT_ID, videoId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProgramShow>() {
                    @Override
                    public void onCompleted() {
                        mProgramShowItemInfoActivity.hidProgressDialog();
                        Logger.getLogger().d("Subscription onCompleted ");

                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgramShowItemInfoActivity.showError("");
                        Logger.getLogger().d("Subscription onError " + e.getMessage());
                    }

                    @Override
                    public void onNext(ProgramShow programShow) {
                        mProgramShowItemInfoActivity.hidProgressDialog();
                        Logger.getLogger().d("Subscription onNext " + programShow.toString());
                        mProgramShowItemInfoActivity.updateProgramShowInfo(programShow);
//                      缓存
//                        mCacheUtil.put(YoukuConfig.YOUKU_VIDEO_SET, new Gson().toJson(videoByCategory));
                    }
                });

        addSubscription(subscription);
    }
}
