package com.zx.zx2000onlinevideo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zx.zx2000onlinevideo.R;

import java.util.List;


public class ConditionsAdapter extends BaseAdapter {

    private List<String> menuList;
    private Context context;

    public ConditionsAdapter(List<String> conditionsArrayList, Context context) {
        this.menuList = conditionsArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
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
            view = View.inflate(context, R.layout.video_menu_item, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
//        QueryConditions queryConditions = menuList.get(position);
        holder.lemon_video_tv.setText(menuList.get(position));
        if (position == 0) {
            holder.lemon_video_tv.setTextColor(Color.WHITE);
        }
        return view;
    }

    private static class ViewHolder {
        TextView lemon_video_tv;

        public ViewHolder(View view) {
            lemon_video_tv = (TextView) view.findViewById(R.id.lemon_video_tv);
        }
    }


}
