package com.zx.zxtvsettings.activity;

import android.os.Build;
import android.widget.TextView;

import com.zx.zxtvsettings.R;

import butterknife.BindView;

/**
 * User: ShaudXiao
 * Date: 2016-08-23
 * Time: 16:36
 * Company: zx
 * Description:
 * FIXME
 */

public class AboutActivity extends BaseActivity {

    @BindView(R.id.device_name)
    TextView mDeviceName;
    @BindView(R.id.device_android)
    TextView mDeviceAndroid;
    @BindView(R.id.device_version)
    TextView mDeviceVersion;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void initialized() {
//        Logger.getLogger().d("DEVICE: " + Build.DEVICE + " DISPLAY: " + Build.DISPLAY + " MODEL:" + Build.MODEL + " PRODUCT:" + Build.PRODUCT);
        mDeviceAndroid.setText(Build.VERSION.RELEASE);
        mDeviceName.setText(Build.MODEL);
        mDeviceVersion.setText(Build.DISPLAY);
    }

}
