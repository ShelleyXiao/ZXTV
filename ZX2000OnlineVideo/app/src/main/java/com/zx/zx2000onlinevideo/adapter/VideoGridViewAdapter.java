package com.zx.zx2000onlinevideo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zx.zx2000onlinevideo.R;
import com.zx.zx2000onlinevideo.bean.youku.video.VideoBean;
import com.zx.zx2000onlinevideo.utils.ImageLoader;
import com.zx.zx2000onlinevideo.utils.StringUtils;

import java.util.List;


/**
 * User: ShaudXiao
 * Date: 2016-08-01
 * Time: 16:45
 * Company: zx
 * Description:
 * FIXME
 */
public class VideoGridViewAdapter extends BaseAdapter {

    List<VideoBean> videoBriefsList;
    private Context context;

    public VideoGridViewAdapter(List<VideoBean> videoBriefsList, Context context) {
        this.videoBriefsList = videoBriefsList;
        this.context = context;
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
            view = View.inflate(context, R.layout.item_gridview, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        VideoBean bean = videoBriefsList.get(position);
        ImageLoader.loadVideoThumbImage(context, bean.getThumbnail(), holder.lemon_grid_img);
        holder.lemon_grid_textView.setText(bean.getTitle());
        String title = bean.getTitle();
        if (!StringUtils.isEmpty(title)) {
//            holder.lemon_title.setText(title);
//            holder.lemon_title.setVisibility(View.VISIBLE);
        } else {
            holder.lemon_title.setVisibility(View.GONE);
        }
        return view;
    }

    private static class ViewHolder {
        TextView lemon_grid_textView;
        TextView lemon_title;
        ImageView lemon_grid_img;
        public ViewHolder(View view) {
            lemon_grid_textView = (TextView) view.findViewById(R.id.lemon_grid_textView);
            lemon_title = (TextView) view.findViewById(R.id.lemon_title);
            lemon_grid_img = (ImageView) view.findViewById(R.id.lemon_grid_img);
        }
    }
}
