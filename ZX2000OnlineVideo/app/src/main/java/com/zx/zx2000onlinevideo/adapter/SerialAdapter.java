package com.zx.zx2000onlinevideo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zx.zx2000onlinevideo.R;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-08
 * Time: 17:11
 * Company: zx
 * Description:
 * FIXME
 */

public class SerialAdapter extends BaseAdapter {

    private List<String> datas;
    private Context context;

    public SerialAdapter(Context context, List<String> datas) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = View.inflate(context, R.layout.serial_item_servil, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.lemon_item_but.setText("第" + (i + 1) + "集");


        return view;
    }

    private static class ViewHolder {
        TextView lemon_item_but;

        public ViewHolder(View view) {
            lemon_item_but = (TextView) view.findViewById(R.id.serial_item_but);
        }
    }
}
