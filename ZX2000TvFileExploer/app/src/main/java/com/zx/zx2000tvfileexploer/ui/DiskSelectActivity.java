package com.zx.zx2000tvfileexploer.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.view.MainUpView;
import com.open.androidtvwidget.view.RelativeMainLayout;
import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.ui.base.BaseActivity;
import com.zx.zx2000tvfileexploer.utils.Logger;
import com.zx.zx2000tvfileexploer.utils.SDCardUtils;
import com.zx.zx2000tvfileexploer.view.RoundProgressBar;

/**
 * Created by ShaudXiao on 2016/7/19.
 */
public class DiskSelectActivity extends BaseActivity  implements View.OnClickListener {

    private MainUpView mAnimationFrame;
    private OpenEffectBridge mOpenEffectBridge;
    private View mOldFocus;

    private RoundProgressBar mFlashProgress;
    private RoundProgressBar mTFSDProgress;
    private RoundProgressBar mUSBProgess;

    private TextView tvFlashStatus;
    private TextView tvTFSDStatus;
    private TextView tvUSBStatus;

    private TextView tvFlashUseSpace;
    private TextView tvFlashTotalSpace;
    private TextView tvTFSDUseSpace;
    private TextView tvTFSDTotalSpace;
    private TextView tvUSBUseSpace;
    private TextView tvUSBTotalSpace;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initInfo();
                    }
                });
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                initInfo();
            } else if (action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {
                initInfo();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        initInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();;
        unregisterReceiver(mReceiver);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.select_disk_layout;
    }

    @Override
    public void init() {
        setCurPath(getString(R.string.disk_select_promat));

        initView();

        initReceiver();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        String path = null;
        Intent intent = new Intent(DiskSelectActivity.this, FileListActivity.class);
        intent.putExtra("category", GlobalConsts.INTENT_EXTRA_ALL_VLAUE);
        switch (view.getId()) {
            case R.id.disk_flash:
                path = SDCardUtils.getFlashDirectory();
                if(path != null && SDCardUtils.isMounted(path)) {
                    intent.putExtra("path", path);
                }
                break;
            case R.id.disk_tf:
                path = SDCardUtils.getSdcardDirectory();
                if(path != null && SDCardUtils.isMounted(path)) {
                    intent.putExtra("path", path);
                }
                break;
            case R.id.disk_usb:
                path = SDCardUtils.getOTADirectory();
                if(path != null && SDCardUtils.isMounted(path)) {
                    intent.putExtra("path", path);
                }
                break;
        }

        if(path != null) {
            startActivity(intent);
        } else {
            showToast(getString(R.string.disk_unmounted_msg));
        }
    }

    private void initView() {
        mAnimationFrame = (MainUpView) findViewById(R.id.main_up_view);
        mOpenEffectBridge = (OpenEffectBridge) mAnimationFrame.getEffectBridge();

        mAnimationFrame.setUpRectResource(R.drawable.test_rectangle);
        mAnimationFrame.setShadowResource(R.drawable.item_shadow);

        RelativeMainLayout mainLayout = (RelativeMainLayout) findViewById(R.id.main_layout);
        mainLayout.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(final View oldFocus, final View newFocus) {

                if (newFocus != null) {
                    newFocus.bringToFront();
                }
                float scale = 1.2f;
                mAnimationFrame.setFocusView(newFocus, mOldFocus, scale);
                mOldFocus = newFocus;

            }
        });

        findViewById(R.id.disk_flash).setOnClickListener(this);
        findViewById(R.id.disk_tf).setOnClickListener(this);
        findViewById(R.id.disk_usb).setOnClickListener(this);

        mFlashProgress = (RoundProgressBar)findViewById(R.id.flash_progress);
        mTFSDProgress = (RoundProgressBar)findViewById(R.id.tf_progress);
        mUSBProgess = (RoundProgressBar)findViewById(R.id.usb_progress);

        tvFlashStatus = (TextView) findViewById(R.id.disk_flash_status);
        tvTFSDStatus = (TextView) findViewById(R.id.disk_tf_status);
        tvUSBStatus = (TextView) findViewById(R.id.disk_usb_status);

        tvFlashTotalSpace = (TextView) findViewById(R.id.disk_flash_total);
        tvFlashUseSpace = (TextView) findViewById(R.id.disk_flash_use);
        tvTFSDTotalSpace = (TextView) findViewById(R.id.disk_tf_total);
        tvTFSDUseSpace = (TextView) findViewById(R.id.disk_tf_use);
        tvUSBTotalSpace = (TextView) findViewById(R.id.disk_usb_total);
        tvUSBUseSpace = (TextView) findViewById(R.id.disk_usb_use);
    }

    private void initInfo() {
        String flashPath = SDCardUtils.getFlashDirectory();
        String TFSDPath = SDCardUtils.getSdcardDirectory();
        String USBPath = SDCardUtils.getOTADirectory();

//        Logger.getLogger().d("flashPath: " + flashPath + " " + TFSDPath + " " + USBPath);

        SDCardUtils.SDCardInfo unmountInfo = new SDCardUtils.SDCardInfo();
        unmountInfo.free = 0;
        unmountInfo.total = 0;

        if(SDCardUtils.isMounted(flashPath)) {
            SDCardUtils.SDCardInfo Info = SDCardUtils.getDiskInfo(flashPath);
            Logger.getLogger().d("Info: " + Info.free + " " + Info.total);
            setProgress(mFlashProgress, Info);
            setDiskUseInfo(tvFlashTotalSpace, tvFlashUseSpace, Info);
            setDiskMountInfo(tvFlashStatus, true);
        } else {
            setDiskMountInfo(tvFlashStatus, false);
            setProgress(mFlashProgress, unmountInfo);
            setDiskUseInfo(tvFlashTotalSpace, tvFlashUseSpace, unmountInfo);
        }

        if(SDCardUtils.isMounted(TFSDPath)) {
            SDCardUtils.SDCardInfo Info = SDCardUtils.getDiskInfo(TFSDPath);
            setProgress(mTFSDProgress, Info);
            setDiskUseInfo(tvTFSDTotalSpace, tvTFSDUseSpace, Info);
            setDiskMountInfo(tvTFSDStatus, true);
        } else {
            setDiskMountInfo(tvTFSDStatus, false);
            setProgress(mTFSDProgress, unmountInfo);
            setDiskUseInfo(tvTFSDTotalSpace, tvTFSDUseSpace, unmountInfo);
        }

        if(SDCardUtils.isMounted(USBPath)) {
            SDCardUtils.SDCardInfo Info = SDCardUtils.getDiskInfo(USBPath);
            setProgress(mUSBProgess, Info);
            setDiskUseInfo(tvUSBTotalSpace, tvUSBUseSpace, Info);
            setDiskMountInfo(tvUSBStatus, true);
        } else {
            setDiskMountInfo(tvUSBStatus, false);
            setProgress(mUSBProgess, unmountInfo);
            setDiskUseInfo(tvUSBTotalSpace, tvUSBUseSpace, unmountInfo);
        }
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addDataScheme("file");
        this.registerReceiver(mReceiver, intentFilter);
    }

    private void setProgress(RoundProgressBar bar, SDCardUtils.SDCardInfo info) {
        if(null == bar) {
            return;
        }
        if(info.total == 0) {
            bar.setMax(100);
            bar.setProgress(100);
            bar.runAnimate(100, Long.valueOf(5000));
            return;
        }
        Logger.getLogger().d("info.total: " + info.total + " " + (info.total - info.free));
        bar.setMax(info.total / 1024 / 1024);
        bar.setProgress((info.total - info.free) / 1024 / 1024);
        bar.runAnimate((info.total - info.free) / 1024 / 1024, Long.valueOf(5000));
    }

    private void setDiskUseInfo(TextView total, TextView use, SDCardUtils.SDCardInfo info) {
        if(null == info) {
            return;
        }

        total.setText(getString(R.string.disk_size_total) + SDCardUtils.convertStorage(info.total));
        use.setText(getString(R.string.disk_size_use) + SDCardUtils.convertStorage(info.total - info.free));
    }

    private void setDiskMountInfo(TextView view, boolean isMount) {
        if(!isMount) {
            view.setText(getString(R.string.disk_umount));
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    private void processDiskInfo(String path) {

    }
}


