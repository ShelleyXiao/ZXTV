package com.zx.zx2000tvfileexploer.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.utils.TimeUtils;

import java.util.Timer;

/**
 * Created by ShaudXiao on 2016/7/12.
 */
public class FileViewStatusTitleView extends RelativeLayout {

    private RelativeLayout lyContainerView;
    private TextView tvTime;
    private TextView tvPath;
    private TextView tvFileNums;
    private ImageView ivNetStatus;

    private Handler timeHandler = new Handler();
    private Timer timer;

    private Typeface typeface;

    private Context mContext;

    public FileViewStatusTitleView(Context context) {
        super(context);
        init(context);
    }

    public FileViewStatusTitleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public FileViewStatusTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        lyContainerView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.fileview_status_title_layout, this, true);
        tvTime = (TextView) lyContainerView.findViewById(R.id.title_time_hour);
        tvPath = (TextView) lyContainerView.findViewById(R.id.title_path);
        tvFileNums = (TextView) lyContainerView.findViewById(R.id.title_file_num);
        ivNetStatus = (ImageView) lyContainerView.findViewById(R.id.home_networkstate);

        typeface = Typeface.createFromAsset(context.getAssets(),
                "font/helvetica_neueltpro_thex.otf");
        tvTime.setTypeface(typeface);
        tvPath.setTypeface(typeface);
        tvFileNums.setTypeface(typeface);

        tvTime.setText(TimeUtils.getTime());
        tvTime.post(timeUpdateTask);

        this.mContext = context;


    }

    public void setPath(String text) {
        tvPath.setText(text);
    }

    public void setFileNums(String num) {
        tvPath.setText(num + mContext.getString(R.string.file_nums));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        mContext.registerReceiver(mConnReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
        mContext.registerReceiver(mWifiChange, new IntentFilter(
                WifiManager.RSSI_CHANGED_ACTION));
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mContext.unregisterReceiver(mConnReceiver);
        mContext.unregisterReceiver(mWifiChange);
    }

    private Runnable timeUpdateTask = new Runnable() {
        @Override
        public void run() {
            tvTime.setText(TimeUtils.getTime());

            timeHandler.postDelayed(this, 1000);
        }
    };

    BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            NetworkInfo connectInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if (connectInfo.isConnected()) {
                if (connectInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    ivNetStatus.setImageDrawable(getResources().getDrawable(R.drawable.networkstate_on));
                } else if (connectInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    ivNetStatus.setImageDrawable(getResources().getDrawable(R.drawable.networkstate_ethernet));
                } else if (!connectInfo.isConnected()) {
                    ivNetStatus.setImageDrawable(getResources().getDrawable(R.drawable.networkstate_off));
                }
            } else {

            }
        }
    };

    BroadcastReceiver mWifiChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wm.getConnectionInfo();
            if (wifiInfo.getBSSID() != null) {
                int signalLevel = WifiManager.calculateSignalLevel(
                        wifiInfo.getRssi(), 4);
                if (signalLevel == 0) {
                    ivNetStatus.setImageDrawable(getResources()
                            .getDrawable(R.drawable.wifi_1));
                } else if (signalLevel == 1) {
                    ivNetStatus.setImageDrawable(getResources()
                            .getDrawable(R.drawable.wifi_2));

                } else if (signalLevel == 2) {
                    ivNetStatus.setImageDrawable(getResources()
                            .getDrawable(R.drawable.wifi_3));

                } else if (signalLevel == 3) {
                    ivNetStatus.setImageDrawable(getResources()
                            .getDrawable(R.drawable.networkstate_on));
                }
            }
        }
    };

}