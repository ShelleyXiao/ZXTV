package com.zx.zx2000onlinevideo.ui.IView;


import com.zx.zx2000onlinevideo.bean.youku.category.ProgramByCategory;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-04
 * Time: 14:48
 * Company: zx
 * Description:
 * FIXME
 */

public interface IProgramSetActivity extends IBaseActivity {
     void updateList(List<ProgramByCategory.ShowsBean> videos);

     void updateVideoTotal(int total);
}
