package com.zx.zx2000onlinevideo.presenter;

import com.zx.zx2000onlinevideo.bean.youku.ProgramQueryConditions;

/**
 * User: ShaudXiao
 * Date: 2016-08-05
 * Time: 15:26
 * Company: zx
 * Description:
 * FIXME
 */

public interface IProgramSetPresenter extends BasePresenter {
    public  void getProgramSetByCategory(String category, String genre, ProgramQueryConditions conditions, int page, int count);
}
