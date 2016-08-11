package com.zx.zx2000onlinevideo.presenter.impl;

import com.zx.zx2000onlinevideo.presenter.BasePresenter;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * User: ShaudXiao
 * Date: 2016-08-04
 * Time: 14:36
 * Company: zx
 * Description:
 * FIXME
 */

public class BasePresenterImpl implements BasePresenter {

    private CompositeSubscription mCompositeSubscription;

    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    @Override
    public void unsubcrible() {
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }
}
