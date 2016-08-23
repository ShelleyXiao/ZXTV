package com.zx.zxtvsettings.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import com.zx.zxtvsettings.R;
import com.zx.zxtvsettings.view.ViewStatusTitleView;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    @BindView(R.id.status_bar)
    ViewStatusTitleView mStatusBar;

    @BindView(R.id.setting_net)
    ImageButton mSettingNet;
    @BindView(R.id.setting_display)
    ImageButton mSettingDisplay;
    @BindView(R.id.setting_bluethee)
    ImageButton mSettingBluethee;
    @BindView(R.id.setting_uninstall)
    ImageButton mSettingUninstall;
    @BindView(R.id.setting_more)
    ImageButton mSettingMore;
    @BindView(R.id.setting_about)
    ImageButton mSettingAbout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main2;
    }

    @Override
    protected void setupViews() {

        mSettingNet.setOnFocusChangeListener(mFocusChangeListener);
        mSettingDisplay.setOnFocusChangeListener(mFocusChangeListener);
        mSettingBluethee.setOnFocusChangeListener(mFocusChangeListener);
        mSettingMore.setOnFocusChangeListener(mFocusChangeListener);
        mSettingUninstall.setOnFocusChangeListener(mFocusChangeListener);
        mSettingAbout.setOnFocusChangeListener(mFocusChangeListener);

    }

    @Override
    protected void initialized() {

    }

    @OnClick({R.id.setting_net, R.id.setting_display, R.id.setting_bluethee, R.id.setting_uninstall, R.id.setting_more, R.id.setting_about})
    public void onClick(View view) {
        Intent intent = new Intent();   
        switch (view.getId()) {
            case R.id.setting_net:
//                intent.setClass(this, NetSetting.class);
                startActivity(NetSetting.class);
                break;
            case R.id.setting_display:
                break;
            case R.id.setting_bluethee:
                startActivity(BluethoothActivity.class);
                break;
            case R.id.setting_uninstall:
                startActivity(AppUninstallActivity.class);
                break;
            case R.id.setting_more:
                break;
            case R.id.setting_about:
                startActivity(AboutActivity.class);
                break;
        }
//        startActivity(intent);

    }

}
