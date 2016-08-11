package com.zx.zx2000onlinevideo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zx.zx2000onlinevideo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * User: ShaudXiao
 * Date: 2016-08-08
 * Time: 14:22
 * Company: zx
 * Description:
 * FIXME
 */

public class GridViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.related_grid_img)
    ImageView relatedProgramThumb;
    @BindView(R.id.related_grid_textView)
    TextView relatedProgramName;

    public GridViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
