package com.zx.zxtvsettings.adapter;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zx.zxtvsettings.R;
import com.zx.zxtvsettings.Utils.Logger;
import com.zx.zxtvsettings.wifi.Wifi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shelley on 2016/8/21.
 */
public class WifiListAdpter extends BaseAdapter {

    private Activity mContext;
    private List<ScanResult> datas;
    private LayoutInflater mInflater;

    public WifiListAdpter(Activity contextt, List<ScanResult> datas) {
        this.mContext = contextt;
        this.datas = datas;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int i) {
        return null ;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.wifi_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScanResult scanResult = datas.get(i);

        holder.mWifiListItemWifiname.setText(scanResult.SSID);

        Logger.getLogger().e(scanResult.SSID);

        holder.ArrowTop=(ImageView)mContext.findViewById(R.id.wifi_arrowtop);
        holder.ArrowBottom=(ImageView)mContext.findViewById(R.id.wifi_arrowbottom);

        final String rawSecurity = Wifi.ConfigSec.getDisplaySecirityString(scanResult);
        if(Wifi.ConfigSec.isOpenNetwork(rawSecurity)) {
            holder.mItemWifiLock.setVisibility(View.INVISIBLE);
        } else {
            holder.mItemWifiLock.setVisibility(View.VISIBLE);
        }

        if(i == datas.size() - 1){
            holder.ArrowBottom.setVisibility(View.INVISIBLE);
        }else{
            holder.ArrowBottom.setVisibility(View.VISIBLE);
        }

        return convertView;
    }


     static class ViewHolder {
        @BindView(R.id.item_wifi_img)
        ImageView mItemWifiImg;
        @BindView(R.id.wifi_list_item_wifiname)
        TextView mWifiListItemWifiname;
        @BindView(R.id.item_wifi_lock)
        ImageView mItemWifiLock;

        public ImageView ArrowTop;
        public ImageView ArrowBottom;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
