package com.zx.zx2000onlinevideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zx.zx2000onlinevideo.R;
import com.zx.zx2000onlinevideo.bean.youku.program.RelatedProgram;
import com.zx.zx2000onlinevideo.utils.ImageLoader;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-08
 * Time: 14:13
 * Company: zx
 * Description:
 * FIXME
 */

public class RelatedProgramRecyclerViewAdapter extends RecyclerView.Adapter<GridViewHolder> {

    List<RelatedProgram.ShowsBean> mVideoDatas;
    Context mContext;

    public RelatedProgramRecyclerViewAdapter(Context context, List<RelatedProgram.ShowsBean> datas) {
        mContext = context;
        mVideoDatas = datas;
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        RelatedProgram.ShowsBean data = mVideoDatas.get(position);
        holder.relatedProgramName.setText(data.getName());
        ImageLoader.loadVideoThumbImage(mContext, data.getThumbnail(), holder.relatedProgramThumb);
    }

    @Override
    public int getItemCount() {
        return mVideoDatas.size();
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.related_program_recyclerview_view, parent, false);
        return new GridViewHolder(view);
    }


}
