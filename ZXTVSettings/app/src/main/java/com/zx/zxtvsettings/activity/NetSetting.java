package com.zx.zxtvsettings.activity;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zx.zxtvsettings.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * User: ShaudXiao
 * Date: 2016-08-19
 * Time: 13:56
 * Company: zx
 * Description:
 * FIXME
 */

public class NetSetting extends BaseActivity {

    @BindView(R.id.setting_custom_wifi)
    TextView mSettingCustomWifi;
    @BindView(R.id.setting_custom_ethernet)
    TextView mSettingCustomEthernet;
    @BindView(R.id.setting_custom_net_detection)
    TextView mSettingCustomNetDetection;
    @BindView(R.id.setting_custom_rl)
    RelativeLayout mSettingCustomRl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_net;
    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void initialized() {

    }


    @OnClick({R.id.setting_custom_wifi, R.id.setting_custom_ethernet, R.id.setting_custom_net_detection})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.setting_custom_wifi:
                intent.setClass(this, WifiActivity.class);
                break;
            case R.id.setting_custom_ethernet:
                break;
            case R.id.setting_custom_net_detection:
                break;
        }

        startActivity(intent);
    }
}
