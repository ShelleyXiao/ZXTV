package com.zx.zx2000onlinevideo.presenter.impl;

import android.content.Context;

import com.example.aaron.library.MLog;
import com.zx.zx2000onlinevideo.api.youku.YoukuRequest;
import com.zx.zx2000onlinevideo.bean.youku.program.SerialBean;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.presenter.ISerailProgramPresenter;
import com.zx.zx2000onlinevideo.ui.IView.ISerialActivity;
import com.zx.zx2000onlinevideo.utils.CacheUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * User: ShaudXiao
 * Date: 2016-08-11
 * Time: 16:23
 * Company: zx
 * Description:
 * FIXME
 */

public class SerailProgramPresenterImpl extends  BasePresenterImpl implements ISerailProgramPresenter {

    private ISerialActivity mSerialActivity;
    private CacheUtil mCacheUtil;

    public SerailProgramPresenterImpl(ISerialActivity iSerialActivity, Context context) {
        if(iSerialActivity == null) {
            throw new IllegalArgumentException("iSerialActivity must not be null");
        }

        mSerialActivity = iSerialActivity;
        mCacheUtil = CacheUtil.get(context);
    }

    @Override
    public void getSerailProgramInfo(String videoId, int episodeUpdated) {
        mSerialActivity.showProgressDialog();
        MLog.e("videoId: " + videoId + "e: " + episodeUpdated);
        Subscription subscription = YoukuRequest.getYoukuApi().getSerailProgramData(YoukuConfig.CLIENT_ID, videoId, episodeUpdated)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SerialBean>() {
                    @Override
                    public void onCompleted() {
                        mSerialActivity.hidProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SerialBean serialBean) {
                        MLog.d("getSerailProgramInfo " + serialBean.getTotal());
                        List<SerialBean.VideosBean> videosBeens = serialBean.getVideos();
                        List<String> ids = new ArrayList<String>();
                        for(SerialBean.VideosBean video : videosBeens) {
                            ids.add( video.getId());
                        }
                        mSerialActivity.updateSerialProgramShowDatas(ids);
                        MLog.d("seraila: " + serialBean.getTotal());
                    }
                });

        addSubscription(subscription);
    }
}
