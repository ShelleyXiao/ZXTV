package com.zx.zx2000onlinevideo.ui.IView;

import com.zx.zx2000onlinevideo.bean.youku.program.ProgramShow;
import com.zx.zx2000onlinevideo.bean.youku.program.RelatedProgram;

import java.util.List;

/**
 * Created by Shelley on 2016/8/7.
 */
public interface IProgramShowItemInfoActivity  extends IBaseActivity{

    public void updateProgramShowInfo(ProgramShow data);

    public void updateRelatedProgramShowDatas(List<RelatedProgram.ShowsBean> datas);
}
