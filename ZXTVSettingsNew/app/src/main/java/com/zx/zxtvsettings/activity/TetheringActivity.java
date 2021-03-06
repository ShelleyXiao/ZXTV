package com.zx.zxtvsettings.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zx.zxtvsettings.R;
import com.zx.zxtvsettings.wifi.WifiApEnabler;

/**
 * User: ShaudXiao
 * Date: 2016-09-06
 * Time: 17:12
 * Company: zx
 * Description:
 * FIXME
 */

public class TetheringActivity extends BaseActivity {

    private final String TAG="TetheringFragment";
    // Views
    private LinearLayout tetherSwitch;
    private Switch tetherSwitch_switch;
    private TextView tetherSwitch_summary;
    private boolean tetherSwitch_checked=false;
    private boolean tetherConfig_opened=false;
    private TextView tetherConfig_summary;
    private Button tetherConfig_button;
    private int [] tetherConfig_button_NextFocusId = new int[4]; //up down left right
    private TextView tetherConfig_openmark;
    private LinearLayout tetherCon;

    private RadioGroup wifi_ap_security_group;

    private EditText wifi_ap_ssid;
    private LinearLayout wifi_ap_password;
    private LinearLayout wifi_ap_password_confirm;
    private EditText wifi_ap_password_icicle;
    private EditText wifi_ap_password_confirm_icicle;
    private Button wifi_ap_ok;


    // members
    private String[] mUsbRegexs;
    private String[] mWifiRegexs;
    private String[] mBluetoothRegexs;
    private WifiManager mWifiManager;
    private WifiConfiguration mWifiConfig = null;
    private BroadcastReceiver mTetherChangeReceiver;
    private WifiApEnabler mWifiApEnabler;


    public static final int OPEN_INDEX = 0;
    public static final int WPA_INDEX = 1;
    public static final int WPA2_INDEX = 2;
    private int mSecurityTypeIndex = OPEN_INDEX;
    private String[] mSecurityType;


    private View.OnKeyListener mEditTextKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (v == null) {
                return false;
            }
            boolean handled = false;
            String str;
            switch (keyCode) {
                case KeyEvent.KEYCODE_ENTER:
                    if(event.getAction() == KeyEvent.ACTION_DOWN){
                        EditText et = (EditText)v;
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                        handled = true;
                    }
                    break;
                default:
                    break;

            }
            return handled;
        }
    };

    private void initState(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        mUsbRegexs = cm.getTetherableUsbRegexs();
        mWifiRegexs = cm.getTetherableWifiRegexs();
        mBluetoothRegexs = cm.getTetherableBluetoothRegexs();

        final boolean usbAvailable = mUsbRegexs.length != 0;
        final boolean wifiAvailable = mWifiRegexs.length != 0;
        final boolean bluetoothAvailable = mBluetoothRegexs.length != 0;

        if (!usbAvailable || ActivityManager.isUserAMonkey()) {
            //getPreferenceScreen().removePreference(mUsbTether);
        }

        if (wifiAvailable && !ActivityManager.isUserAMonkey()) {
            mWifiApEnabler = new WifiApEnabler(this, tetherSwitch_switch, tetherSwitch_summary);
            initWifiTethering();
        } else {
            //getPreferenceScreen().removePreference(mEnableWifiAp);
            //getPreferenceScreen().removePreference(wifiApSettings);
        }

        if (!bluetoothAvailable) {
            //getPreferenceScreen().removePreference(mBluetoothTether);
        } else {
            /*
            BluetoothPan pan = mBluetoothPan.get();
            if (pan != null && pan.isTetheringOn()) {
                mBluetoothTether.setChecked(true);
            } else {
                mBluetoothTether.setChecked(false);
            }
            */
        }

        mTetherChangeReceiver = new TetherChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.ACTION_TETHER_STATE_CHANGED);
        Intent intent = registerReceiver(mTetherChangeReceiver, filter);

        if (intent != null) {
            mTetherChangeReceiver.onReceive(this, intent);
        }

        if(mWifiApEnabler!=null) {
            mWifiApEnabler.resume();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_tethering;
    }

    @Override
    protected void setupViews() {
        tetherSwitch=(LinearLayout)findViewById(R.id.tetherSwitch);
        tetherSwitch_switch = (Switch)findViewById(R.id.tetherSwitch_switch);
        tetherSwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tetherSwitch_switch.toggle();
                tetherSwitch_checked = tetherSwitch_switch.isChecked();
                switchTethering(tetherSwitch_checked);
            }
        });
        tetherSwitch_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchTethering(isChecked);
            }
        });

        tetherSwitch_summary = (TextView) findViewById(R.id.tetherSwitch_summary);

        tetherConfig_opened = false;
        tetherConfig_button = (Button)findViewById(R.id.tetherConfig_button);
        tetherConfig_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                toggleConfigWindow();
            }

        });
        tetherConfig_summary = (TextView) findViewById(R.id.tetherConfig_summary);
        // initial state is close config window, so set focus logic to tetherConfig_button
        tetherConfig_button.setNextFocusDownId(tetherConfig_button.getId());
        tetherConfig_button_NextFocusId[0]=tetherConfig_button.getNextFocusUpId();
        tetherConfig_button_NextFocusId[1]=tetherConfig_button.getNextFocusDownId();
        tetherConfig_button_NextFocusId[2]=tetherConfig_button.getNextFocusLeftId();
        tetherConfig_button_NextFocusId[3]=tetherConfig_button.getNextFocusRightId();

        tetherConfig_openmark = (TextView) findViewById(R.id.tetherConfig_openmark);
        tetherCon = (LinearLayout) findViewById(R.id.tetherCon);

        wifi_ap_ssid = (EditText) findViewById(R.id.wifi_ap_ssid);
        wifi_ap_ssid.setOnKeyListener(mEditTextKeyListener);
        wifi_ap_password = (LinearLayout) findViewById(R.id.wifi_ap_password);
        wifi_ap_password_confirm = (LinearLayout) findViewById(R.id.wifi_ap_password_confirm);
        wifi_ap_password_icicle = (EditText) findViewById(R.id.wifi_ap_password_icicle);
        wifi_ap_password_icicle.setOnKeyListener(mEditTextKeyListener);
        wifi_ap_password_confirm_icicle = (EditText) findViewById(R.id.wifi_ap_password_confirm_icicle);
        wifi_ap_password_confirm_icicle.setOnKeyListener(mEditTextKeyListener);
        wifi_ap_ok = (Button) findViewById(R.id.wifi_ap_ok);
        wifi_ap_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                submitWifiConfig();
            }
        });

        wifi_ap_security_group = (RadioGroup) findViewById(R.id.wifi_ap_security_group);
        wifi_ap_security_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.wifi_ap_security_open:
                        mSecurityTypeIndex=OPEN_INDEX;
                        wifi_ap_password.setVisibility(View.GONE);
                        wifi_ap_password_confirm.setVisibility(View.GONE);
                        break;
                    case R.id.wifi_ap_security_wpa_psk:
                        mSecurityTypeIndex=WPA_INDEX;
                        wifi_ap_password.setVisibility(View.VISIBLE);
                        wifi_ap_password_confirm.setVisibility(View.VISIBLE);
                        break;
                    case R.id.wifi_ap_security_wpa2_psk:
                        mSecurityTypeIndex=WPA2_INDEX;
                        wifi_ap_password.setVisibility(View.VISIBLE);
                        wifi_ap_password_confirm.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void initialized() {
        initState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mTetherChangeReceiver);
        if(mWifiApEnabler!=null)
            mWifiApEnabler.pause();
    }

    private void initWifiTethering() {

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //mWifiConfig = mWifiManager.getWifiApConfiguration();
        mSecurityType = getResources().getStringArray(R.array.wifi_ap_security);

        updateConfigSummary(mWifiManager.getWifiApConfiguration());
        /*
        mCreateNetwork = findPreference(WIFI_AP_SSID_AND_SECURITY);

        if (mWifiConfig == null) {
            final String s = activity.getString(
                    com.android.internal.R.string.wifi_tether_configure_ssid_default);
            mCreateNetwork.setSummary(String.format(activity.getString(CONFIG_SUBTEXT),
                    s, mSecurityType[WifiApDialog.OPEN_INDEX]));
        } else {
            int index = WifiApDialog.getSecurityTypeIndex(mWifiConfig);
            mCreateNetwork.setSummary(String.format(activity.getString(CONFIG_SUBTEXT),
                    mWifiConfig.SSID,
                    mSecurityType[index]));
        }
        */

    }

    private void updateConfigSummary(WifiConfiguration wifiConfig){
        if(wifiConfig!=null){
            int securetype = 0;
            if(wifiConfig!=null){
                securetype = getSecurityTypeIndex(wifiConfig);
                StringBuilder summary = new StringBuilder();
                summary.append(wifiConfig.SSID);
                summary.append(", ");
                summary.append(mSecurityType[securetype]);
                tetherConfig_summary.setText(summary.toString());
            }
        }
        else
            tetherConfig_summary.setText("");
    }

    private void validToast(String msg){
        Toast toast = Toast.makeText(this, msg,
                Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean validSubmit(){
        String pwd, pwdc, ssid;
        boolean isValid=true;
        String errorMsg;
        ssid = wifi_ap_ssid.getText().toString();
        pwdc = wifi_ap_password_confirm_icicle.getText().toString();
        pwd = wifi_ap_password_icicle.getText().toString();
        if(ssid.length()==0){
            errorMsg = getResources().getString(R.string.wifi_ap_config_err_msg_ssid);
            wifi_ap_ssid.setText("");
            wifi_ap_password_icicle.setText("");
            wifi_ap_password_confirm_icicle.setText("");
            wifi_ap_ssid.requestFocus();
            validToast(errorMsg);
            return false;
        }
        if(mSecurityTypeIndex!=OPEN_INDEX && pwd.length()<8){
            errorMsg = getResources().getString(R.string.wifi_ap_config_err_msg_pwd_short);
            wifi_ap_password_icicle.setText("");
            wifi_ap_password_confirm_icicle.setText("");
            wifi_ap_password_icicle.requestFocus();
            validToast(errorMsg);
            return false;
        }
        if(mSecurityTypeIndex!=OPEN_INDEX && pwd.equals(pwdc)==false){
            errorMsg = getResources().getString(R.string.wifi_ap_config_err_msg_pwd_match);
            wifi_ap_password_icicle.setText("");
            wifi_ap_password_confirm_icicle.setText("");
            wifi_ap_password_icicle.requestFocus();
            validToast(errorMsg);
            return false;
        }

        // close the config window
        tetherConfig_button.performClick();

        return true;
    }

    private void submitWifiConfig(){
        WifiConfiguration wifiConfig = this.getConfig();
        if(validSubmit()==false)
            return;
        if (wifiConfig != null) {
            /**
             * if soft AP is stopped, bring up
             * else restart with new config
             * TODO: update config on a running access point when framework support is added
             */
            if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
                mWifiManager.setWifiApEnabled(null, false);
                mWifiManager.setWifiApEnabled(wifiConfig, true);
            } else {
                mWifiManager.setWifiApConfiguration(wifiConfig);
            }
            /*
            int index = this.getSecurityTypeIndex(mWifiConfig);
            mCreateNetwork.setSummary(String.format(getActivity().getString(CONFIG_SUBTEXT),
                    mWifiConfig.SSID,
                    mSecurityType[index]));
                    */
            updateConfigSummary(wifiConfig);
        }
    }

    private void switchTethering(boolean isStartTethering){
        if(mWifiApEnabler!=null)
            mWifiApEnabler.setSoftapEnabled(isStartTethering);
        /* Tethering Service */

    }

    private void toggleConfigWindow(){
        tetherConfig_opened=tetherConfig_opened?false:true;

        if(tetherConfig_opened){
            tetherCon.setVisibility(View.VISIBLE);
            tetherConfig_openmark.setText("+");
            wifiConfigInit();
            tetherSwitch.setEnabled(false);
            tetherSwitch.setFocusable(false);
            tetherSwitch.setClickable(false);
            tetherConfig_button.setNextFocusUpId(tetherConfig_button.getId());
            tetherConfig_button.setNextFocusDownId(View.NO_ID);
            tetherConfig_button.setNextFocusLeftId(tetherConfig_button.getId());
            tetherConfig_button.setNextFocusRightId(tetherConfig_button.getId());
            if(mWifiApEnabler!=null)
                mWifiApEnabler.pause();
            tetherSwitch_switch.setEnabled(false);
        }
        else{
            tetherCon.setVisibility(View.GONE);
            tetherConfig_openmark.setText("");
            wifiConfigDestroy();
            tetherSwitch.setEnabled(true);
            tetherSwitch.setFocusable(true);
            tetherSwitch.setClickable(true);
            tetherConfig_button.setNextFocusUpId(tetherConfig_button_NextFocusId[0]);
            tetherConfig_button.setNextFocusDownId(tetherConfig_button_NextFocusId[1]);
            tetherConfig_button.setNextFocusLeftId(tetherConfig_button_NextFocusId[2]);
            tetherConfig_button.setNextFocusRightId(tetherConfig_button_NextFocusId[3]);
            if(mWifiApEnabler!=null)
                mWifiApEnabler.resume();
            //tetherSwitch_switch.setEnabled(true);
        }
    }

    static int getSecurityTypeIndex(WifiConfiguration wifiConfig){
        if(wifiConfig.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return WPA_INDEX;
        }
        else if (wifiConfig.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA2_PSK)) {
            return WPA2_INDEX;
        }
        return OPEN_INDEX;
    }

    private void wifiConfigInit(){
        WifiConfiguration wifiConfig = mWifiManager.getWifiApConfiguration();
        if(wifiConfig!=null)
            mSecurityTypeIndex = getSecurityTypeIndex(wifiConfig);
        wifi_ap_security_group.clearCheck();
        switch(mSecurityTypeIndex){
            case OPEN_INDEX:
                wifi_ap_security_group.check(R.id.wifi_ap_security_open);
                break;
            case WPA_INDEX:
                wifi_ap_security_group.check(R.id.wifi_ap_security_wpa_psk);
                break;
            case WPA2_INDEX:
                wifi_ap_security_group.check(R.id.wifi_ap_security_wpa2_psk);
                break;
            default:
                break;
        }
        if(wifiConfig!=null && wifiConfig.SSID.length()==0)
            return;
        wifi_ap_ssid.setText(wifiConfig.SSID);
    }

    private void wifiConfigDestroy(){
        mSecurityTypeIndex = OPEN_INDEX;
    }

    /* WifiApDialog.getConfig() */
    private WifiConfiguration getConfig() {
        WifiConfiguration config = new WifiConfiguration();
        /**
         * TODO: SSID in WifiConfiguration for soft ap
         * is being stored as a raw string without quotes.
         * This is not the case on the client side. We need to
         * make things consistent and clean it up
         */
        String ssid;
        ssid = wifi_ap_ssid.getText().toString();
        if(ssid.length()==0){
            ssid = wifi_ap_ssid.getHint().toString();
            wifi_ap_ssid.setText(ssid);
        }
        config.SSID = ssid;

        switch (mSecurityTypeIndex) {
            case OPEN_INDEX:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                return config;

            case WPA_INDEX:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                if (wifi_ap_password_icicle.length() != 0) {
                    String password = wifi_ap_password_icicle.getText().toString();
                    config.preSharedKey = password;
                }
                return config;

            case WPA2_INDEX:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA2_PSK);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                if (wifi_ap_password_icicle.length() != 0) {
                    String password = wifi_ap_password_icicle.getText().toString();
                    config.preSharedKey = password;
                }
                return config;
        }
        return null;
    }

    private class TetherChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context content, Intent intent) {

        }
    }

}
