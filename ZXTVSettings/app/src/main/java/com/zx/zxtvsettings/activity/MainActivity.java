package com.zx.zxtvsettings.activity;

import android.content.Intent;
import android.provider.Settings;
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
    @BindView(R.id.setting_clear)
    ImageButton mSettingClear;

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
        mSettingClear.setOnFocusChangeListener(mFocusChangeListener);

    }

    @Override
    protected void initialized() {

    }

    @OnClick({R.id.setting_net, R.id.setting_display, R.id.setting_bluethee, R.id.setting_uninstall, R.id.setting_more, R.id.setting_about, R.id.setting_clear})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.setting_net:
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
                Intent intent =  new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                break;
            case R.id.setting_about:
                startActivity(AboutActivity.class);
                break;
            case R.id.setting_clear:
                startActivity(ClearGarbageActivity.class);
                break;
        }

    }

}
