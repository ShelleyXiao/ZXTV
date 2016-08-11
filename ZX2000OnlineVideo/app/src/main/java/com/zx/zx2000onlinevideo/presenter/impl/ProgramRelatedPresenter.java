package com.zx.zx2000onlinevideo.presenter.impl;

import android.content.Context;

import com.zx.zx2000onlinevideo.api.youku.YoukuRequest;
import com.zx.zx2000onlinevideo.bean.youku.program.RelatedProgram;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.presenter.IProgramRelatedPresenter;
import com.zx.zx2000onlinevideo.ui.IView.IProgramShowItemInfoActivity;
import com.zx.zx2000onlinevideo.utils.Logger;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Shelley on 2016/8/7.
 */
public class ProgramRelatedPresenter extends  BasePresenterImpl implements IProgramRelatedPresenter {

    IProgramShowItemInfoActivity showItemInfoActivity;

    public ProgramRelatedPresenter(Context context, IProgramShowItemInfoActivity activity) {
        if(null == activity) {
            throw new IllegalArgumentException("iProgramSetActivity must not be null");
        }

        showItemInfoActivity = activity;
    }

    @Override
    public void getRelatedProgram(String videoId) {

        Subscription subscriptions = YoukuRequest.getYoukuApi().getRelatedProgram(YoukuConfig.CLIENT_ID, videoId, String.valueOf(20))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RelatedProgram>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(RelatedProgram data) {
                        Logger.getLogger().d(data.getTotal() + " " + data.getShows().get(0).getName());
                        showItemInfoActivity.updateRelatedProgramShowDatas(data.getShows());
                    }
                });

        addSubscription(subscriptions);
    }
}
