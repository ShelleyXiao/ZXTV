package com.zx.zx2000onlinevideo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zx.zx2000onlinevideo.R;
import com.zx.zx2000onlinevideo.bean.youku.category.ProgramByCategory;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.utils.ImageLoader;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-05
 * Time: 16:45
 * Company: zx
 * Description:
 * FIXME
 */
public class ProgramGridViewAdapter extends BaseAdapter {

    private List<ProgramByCategory.ShowsBean> videoBriefsList;
    private Context context;
    private YoukuConfig.TYPE type;

    public ProgramGridViewAdapter(List<ProgramByCategory.ShowsBean> videoBriefsList, Context context, YoukuConfig.TYPE type) {
        this.videoBriefsList = videoBriefsList;
        this.context = context;
        this.type = type;
    }

    @Override
    public int getCount() {
        return videoBriefsList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoBriefsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = View.inflate(context, R.layout.program_item_gridview, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        ProgramByCategory.ShowsBean videoBriefs = videoBriefsList.get(position);
        ImageLoader.loadVideoThumbImage(context, videoBriefs.getThumbnail(), holder.lemon_grid_img);
        int episodeCount = videoBriefs.getEpisode_count();
        int episodUpload = videoBriefs.getEpisode_updated();
        holder.tvName.setText(videoBriefs.getName());

        if(type == YoukuConfig.TYPE.Serails) {
            if (episodeCount >= episodUpload && episodeCount != 0) {
                if(episodeCount == episodUpload) {
                    holder.lemon_title.setText(context.getResources().getString(R.string.episode_count, episodeCount));

                } else {
                    holder.lemon_title.setText(context.getResources().getString(R.string.episode_upload, episodUpload));
                }

                holder.lemon_title.setVisibility(View.VISIBLE);
            } else {
                holder.lemon_title.setVisibility(View.GONE);
            }
        } else {
            holder.lemon_title.setVisibility(View.GONE);
        }

        return view;
    }

    private static class ViewHolder {
        TextView tvName;
        TextView lemon_title;
        ImageView lemon_grid_img;
        public ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.grid_tv_name);
            lemon_title = (TextView) view.findViewById(R.id.lemon_title);
            lemon_grid_img = (ImageView) view.findViewById(R.id.lemon_grid_img);
        }
    }
}
