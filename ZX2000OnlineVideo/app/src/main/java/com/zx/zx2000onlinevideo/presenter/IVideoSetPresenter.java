package com.zx.zx2000onlinevideo.presenter;

import com.zx.zx2000onlinevideo.bean.youku.QueryConditions;

/**
 * User: ShaudXiao
 * Date: 2016-08-04
 * Time: 14:36
 * Company: zx
 * Description:
 * FIXME
 */

public interface IVideoSetPresenter extends BasePresenter{

    public  void getVideoSetByCategory(String category, String genre, QueryConditions conditions, int page, int count);
}
