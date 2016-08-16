package com.zx.zx2000tvfileexploer.ui;

import android.content.Intent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.view.MainUpView;
import com.open.androidtvwidget.view.RelativeMainLayout;
import com.zx.zx2000tvfileexploer.FileManagerApplication;
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



    @Override
    public void onResume() {
        super.onResume();

        initInfo();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.select_disk_layout;
    }

    @Override
    public void init() {
        setCurPath(getString(R.string.disk_select_promat));

        initView();

    }

    @Override
    public void updateDiskInfo() {
        initInfo();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        boolean isMounted = false;
        String path = null;
        Intent intent = new Intent(DiskSelectActivity.this, FileListActivity.class);
        intent.putExtra("category", GlobalConsts.INTENT_EXTRA_ALL_VLAUE);
        switch (view.getId()) {
            case R.id.disk_flash:
//                path = SDCardUtils.getFlashDirectory();
//                if(path != null && SDCardUtils.isMounted(path)) {
//                    intent.putExtra("path", path);
//                }
                SDCardUtils.StorageInfo flashInfo = FileManagerApplication.getInstance().getFlash();
                if(null != flashInfo && flashInfo.isMounted()) {
                    intent.putExtra("path", flashInfo.path);
                    isMounted = true;
                }
                break;
            case R.id.disk_tf:
//                path = SDCardUtils.getSdcardDirectory();
//                if(path != null && SDCardUtils.isMounted(path)) {
//                    intent.putExtra("path", path);
//                }

                SDCardUtils.StorageInfo tfInfo = FileManagerApplication.getInstance().getTF();
                if(null != tfInfo && tfInfo.isMounted()) {
                    intent.putExtra("path", tfInfo.path);
                    isMounted = true;
                }
                break;
            case R.id.disk_usb:
//                path = SDCardUtils.getOTADirectory();
//                if(path != null && SDCardUtils.isMounted(path)) {
//                    intent.putExtra("path", path);
//                }

                SDCardUtils.StorageInfo usbInfo = FileManagerApplication.getInstance().getUSB();
                if(null != usbInfo && usbInfo.isMounted()) {
                    intent.putExtra("path", usbInfo.path);
                    isMounted = true;
                }
                break;
        }

        if(isMounted) {
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
//        String flashPath = SDCardUtils.getFlashDirectory();
//        String TFSDPath = SDCardUtils.getSdcardDirectory();
//        String USBPath = SDCardUtils.getOTADirectory();

        String flashPath = FileManagerApplication.getInstance().getFlashAbsolutePath();
        String TFSDPath = FileManagerApplication.getInstance().getTFAbsolutePath();
        String USBPath = FileManagerApplication.getInstance().getUSBAbsolutePath();

//        Logger.getLogger().d("flashPath: " + flashPath + " " + TFSDPath + " " + USBPath);

        SDCardUtils.StorageInfo unmountInfo = new SDCardUtils.StorageInfo();
        unmountInfo.free = 0;
        unmountInfo.total = 0;

        if(SDCardUtils.isMounted(flashPath)) {
            SDCardUtils.StorageInfo Info = SDCardUtils.getDiskInfo(flashPath);
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
            SDCardUtils.StorageInfo Info = SDCardUtils.getDiskInfo(TFSDPath);
            setProgress(mTFSDProgress, Info);
            setDiskUseInfo(tvTFSDTotalSpace, tvTFSDUseSpace, Info);
            setDiskMountInfo(tvTFSDStatus, true);
        } else {
            setDiskMountInfo(tvTFSDStatus, false);
            setProgress(mTFSDProgress, unmountInfo);
            setDiskUseInfo(tvTFSDTotalSpace, tvTFSDUseSpace, unmountInfo);
        }

        if(SDCardUtils.isMounted(USBPath)) {
            SDCardUtils.StorageInfo Info = SDCardUtils.getDiskInfo(USBPath);
            setProgress(mUSBProgess, Info);
            setDiskUseInfo(tvUSBTotalSpace, tvUSBUseSpace, Info);
            setDiskMountInfo(tvUSBStatus, true);
        } else {
            setDiskMountInfo(tvUSBStatus, false);
            setProgress(mUSBProgess, unmountInfo);
            setDiskUseInfo(tvUSBTotalSpace, tvUSBUseSpace, unmountInfo);
        }
    }

    private void setProgress(RoundProgressBar bar, SDCardUtils.StorageInfo info) {
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

    private void setDiskUseInfo(TextView total, TextView use, SDCardUtils.StorageInfo info) {
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


