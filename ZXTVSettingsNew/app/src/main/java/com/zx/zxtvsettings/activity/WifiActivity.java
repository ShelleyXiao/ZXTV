package com.zx.zxtvsettings.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zx.zxtvsettings.GlobalConstants;
import com.zx.zxtvsettings.R;
import com.zx.zxtvsettings.Utils.Logger;
import com.zx.zxtvsettings.adapter.WifiListAdpter;
import com.zx.zxtvsettings.wifi.Wifi;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-08-19
 * Time: 15:17
 * Company: zx
 * Description:
 * FIXME
 */

public class WifiActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, AdapterView.OnItemClickListener {


    TextView mWifisetingTittle;
    TextView mWifisConnectedSSID;
    Switch mWifiSwitch;
    TextView mWifiStatedispaly;
    ImageView mWifiArrowtop;
    ListView mWifiListview;
    ImageView mWifiArrowbottom;
    ProgressBar mWifiScanProgress;

    private final int WIFI_OPEN_FINISH = 1;//开启完成
    private final int WIFI_FOUND_FINISH = 0;//查找完成
    private final int WIFI_SCAN = 2;//wifi扫描
    private final int WIFI_CLOSE = 3;//关闭wifi
    private final int WIFI_INFO = 4;
    private final int WIFI_STATE_INIT = 5;//加载页面


    private WifiManager mWifiManager;
    private List<ScanResult> mScanResults = new ArrayList<>();

    private WifiListAdpter mWififListAdapter;

//    private WifiManager.ActionListener mConnectListener;

    private  IntentFilter mFilter;
    private  BroadcastReceiver mReceiver;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WIFI_STATE_INIT:
                    int wifiState = mWifiManager.getWifiState();
                    if (wifiState == WifiManager.WIFI_STATE_DISABLED) {  //wifi不可用啊
                        mWifiStatedispaly.setText(R.string.wifi_state_closed);
                        mWifiScanProgress.setVisibility(View.GONE);
                        mWifiListview.setVisibility(View.GONE);
                    } else if (wifiState == WifiManager.WIFI_STATE_UNKNOWN) {//wifi 状态未知
                        mWifiStatedispaly.setText(R.string.wifi_state_unknowed);
                    } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {//OK 可用
                        mWifiSwitch.setChecked(true);

                        if (mWifiManager.isWifiEnabled()) {
                            showToastLong(getString(R.string.wifi_state_opened));

                        } else {
                            showToastLong(getString(R.string.wifi_open_msg));
                        }
                    }
                    break;
                case WIFI_SCAN:
                    Wifi.startScan(mWifiManager);
                    List<ScanResult> results = mWifiManager.getScanResults();
                    mWifiStatedispaly.setText(R.string.wifi_scan_msg);
                    if (results == null || results.size() == 0) {
                        mHandler.sendEmptyMessageDelayed(WIFI_SCAN, 1000);
                    } else {
                        mScanResults.clear();
                        mScanResults.addAll(results);
                        mWififListAdapter.notifyDataSetChanged();

                        mWifiStatedispaly.setText(R.string.wifi_nearby);
                        mWifiScanProgress.setProgress(View.GONE);
                        mWifiListview.setVisibility(View.VISIBLE);
                    }

                    break;
            }

            super.handleMessage(msg);
        }
    };

    private void initReceiver() {
        mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                handleEvent(intent);
            }
        };

        this.registerReceiver(mReceiver, mFilter);
    }

    private void handleEvent(Intent intent) {
        final String action = intent.getAction();

        Logger.getLogger().d(" action = " + action);

        if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                || WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action)
                || WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {

            List<ScanResult> temp = mWifiManager.getScanResults();
//                mScanResults = mWifiManager.getScanResults();
            mScanResults.clear();
            mScanResults.addAll(temp);

            mWifiScanProgress.setVisibility(View.GONE);
            mWifiStatedispaly.setText(R.string.wifi_nearby);
            mWifiListview.setVisibility(View.VISIBLE);

            mWififListAdapter.notifyDataSetChanged();

            if (mScanResults == null || mScanResults.size() == 0) {
                mWifiManager.startScan();
            }

            Logger.getLogger().d(mScanResults.size() + " " + mScanResults.get(0).SSID);
        } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {

        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wifi_setting;
    }

    @Override
    protected void setupViews() {

        mWifisetingTittle = (TextView) findViewById(R.id.wifiseting_tittle);
        mWifisConnectedSSID = (TextView) findViewById(R.id.wifiseting_connect_name);
        mWifiSwitch = (Switch) findViewById(R.id.wifi_switch);
        mWifiStatedispaly = (TextView) findViewById(R.id.wifi_statedispaly);
        mWifiArrowtop = (ImageView) findViewById(R.id.wifi_arrowtop);
        mWifiListview = (ListView) findViewById(R.id.wifi_listview);
        mWifiArrowbottom = (ImageView) findViewById(R.id.wifi_arrowbottom);
        mWifiScanProgress = (ProgressBar) findViewById(R.id.wifi_scan_progress);

        mWifiSwitch.setOnCheckedChangeListener(this);

        mWififListAdapter = new WifiListAdpter(this, mScanResults);
        mWifiListview.setAdapter(mWififListAdapter);
        mWifiListview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int postion, long l) {
                if (postion == 0) {
                    mWifiArrowtop.setVisibility(View.GONE);
                } else {
                    mWifiArrowtop.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mWifiListview.setOnItemClickListener(this);
    }

    @Override
    protected void initialized() {

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (Wifi.isWifiEnabled(this)) {
            String name = Wifi.getConnectWifiName(mWifiManager);
            if (null != name) {
                mWifisConnectedSSID.setText(getString(R.string.wifi_connect_name, name));
            } else {
                mWifisConnectedSSID.setVisibility(View.GONE);
            }
        }

//        mConnectListener = new WifiManager.ActionListener() {
//            @Override
//            public void onSuccess() {
//                String name = Wifi.getConnectWifiName(mWifiManager);
//                if (null != name) {
//                    mWifisConnectedSSID.setText(getString(R.string.wifi_connect_name, name));
//                } else {
//                    mWifisConnectedSSID.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(int reason) {
//                mWifisConnectedSSID.setVisibility(View.GONE);
//                Toast.makeText(this,
//                        R.string.wifi_failed_connect_message,
//                        Toast.LENGTH_SHORT).show();
//            }
//        };

        initReceiver();
    }

    public void onResume() {
        super.onResume();

//        final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        registerReceiver(mReceiver, filter);

        initReceiver();

        mHandler.sendEmptyMessageDelayed(WIFI_STATE_INIT, 200);
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        Logger.getLogger().d("onCheckedChanged " + checked);
        if (compoundButton instanceof Switch) {
            if (checked) {
                mWifiStatedispaly.setText(R.string.wifi_scan_msg);
                mWifiScanProgress.setVisibility(View.VISIBLE);
//                mWifiListview.setVisibility(View.GONE);

                Wifi.openWifi(mWifiManager);
                Wifi.startScan(mWifiManager);

                mHandler.sendEmptyMessageDelayed(WIFI_SCAN, 1000);

            } else {
                Wifi.closeWifi(mWifiManager);

                mWifiListview.setVisibility(View.GONE);
                mWifiScanProgress.setVisibility(View.GONE);
                mWifiArrowtop.setVisibility(View.GONE);

                mWifiStatedispaly.setText(R.string.wifi_close_msg);

            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        final ScanResult result = mScanResults.get(position);
        launchWifiConnecter(WifiActivity.this, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalConstants.WIFI_CONNECT_REQUEST_CODE) {
            if (resultCode == GlobalConstants.WIFI_CONNECT_REUSLT_CODE) {
                boolean connectResult = data.getBooleanExtra(GlobalConstants.WIFI_CONNECT_REQUEST_KEY, false);
                if (connectResult) {
                    String name = Wifi.getConnectWifiName(mWifiManager);
                    if (null != name) {
                        mWifisConnectedSSID.setText(getString(R.string.wifi_connect_name, name));
                    } else {
                        mWifisConnectedSSID.setVisibility(View.GONE);
                    }
                } else {
//                    mWifisConnectedSSID.setText(R.string.wifi_failed_connect_message);
                    mWifisConnectedSSID.setVisibility(View.GONE);
                    Toast.makeText(this, R.string.wifi_failed_connect_message, Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    private static void launchWifiConnecter(final Activity activity, final ScanResult hotspot) {
        final Intent intent = new Intent(GlobalConstants.WIFI_CONECT_ACTION);
        intent.putExtra(GlobalConstants.WIFI_CONECT_ACTION_EXTRA, hotspot);
        try {
            activity.startActivityForResult(intent, GlobalConstants.WIFI_CONNECT_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Wifi Connecter is not installed.", Toast.LENGTH_LONG).show();
        }
    }


}
