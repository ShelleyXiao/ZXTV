package com.zx.zx2000onlinevideo.ui.IView;

import com.zx.zx2000onlinevideo.bean.youku.video.VideoBean;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-04
 * Time: 14:48
 * Company: zx
 * Description:
 * FIXME
 */

public interface IVideoSetActivity extends IBaseActivity {
     void updateList(List<VideoBean> videos);

     void updateVideoTotal(int total);
}
