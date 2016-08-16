package com.zx.zx2000tvfileexploer.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.view.MainUpView;
import com.open.androidtvwidget.view.RelativeMainLayout;
import com.zx.zx2000tvfileexploer.FileManagerApplication;
import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.fileutil.FileCategoryHelper;
import com.zx.zx2000tvfileexploer.utils.Logger;
import com.zx.zx2000tvfileexploer.utils.SDCardUtils;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MainUpView mAnimationFrame;
    private OpenEffectBridge mOpenEffectBridge;
    private View mOldFocus;

    private TextView tvAppCount;
    private TextView tvMusicCount;
    private TextView tvPictureCount;
    private TextView tvVideoCount;

    private RelativeLayout rlUsbDevice;
    private RelativeLayout rlTFDevice;
    private RelativeLayout rlLocalDevice;

    private ProgressBar pbLocalDevice;
    private ProgressBar pbUsbDevice;
    private ProgressBar pbTFDevice;
    private TextView tvLocalDeviceSize;
    private TextView tvUsbDeviceSize;
    private TextView tvTFDeviceSize;


    private FileCategoryHelper mFileCategoryHelper;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
                    || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
                    || action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, FileListActivity.class);
        Intent intentAll = new Intent(MainActivity.this, DiskSelectActivity.class);
        Intent intentSerach = new Intent(MainActivity.this, SearchActivity.class);

        switch (view.getId()) {
            case R.id.main_apk_lay:
                intent.putExtra("category", GlobalConsts.INTENT_EXTRA_APK_VLAUE);
                break;
            case R.id.main_video_lay:
                intent.putExtra("category", GlobalConsts.INTENT_EXTRA_VIDEO_VLAUE);
                break;
            case R.id.main_image_lay:
                intent.putExtra("category", GlobalConsts.INTENT_EXTRA_PICTURE_VLAUE);
                break;
            case R.id.main_music_lay:
                intent.putExtra("category", GlobalConsts.INTENT_EXTRA_MUSIC_VLAUE);
                break;
            case R.id.main_search_lay:

                break;
        }

//        ActivityOptionsCompat  compat = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_bottom_in, R.anim.slide_bottom_out);
//        ActivityCompat.startActivity(MainActivity.this, intent, compat.toBundle());
        if(view.getId() == R.id.main_allfile_lay) {
            startActivity(intentAll);
        } else if(view.getId() == R.id.main_search_lay){
            startActivity(intentSerach);
        } else {
            startActivity(intent);
        }
//        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_in_top);
//        overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_out);
    }

    private void init() {
        initView();
        initData();
        initMainMenu();

        initDiskInfo();

        initReceiver();
    }

    private void initData() {
        mFileCategoryHelper = new FileCategoryHelper(this);
        mFileCategoryHelper.refreshCategoryInfo();
    }

    private void initMainMenu() {
        HashMap<FileCategoryHelper.FileCategory, FileCategoryHelper.CategoryInfo> mCategoryMap = mFileCategoryHelper.getCategoryInfo();

        FileCategoryHelper.CategoryInfo musicCatgory = mCategoryMap.get(FileCategoryHelper.FileCategory.MUSIC);
        tvMusicCount.setText(musicCatgory.count + getString(R.string.main_music_nums));

        FileCategoryHelper.CategoryInfo apkCategory = mCategoryMap.get(FileCategoryHelper.FileCategory.MUSIC);
        tvAppCount.setText(apkCategory.count + getString(R.string.main_apk_nums));

        FileCategoryHelper.CategoryInfo pictureCategory = mCategoryMap.get(FileCategoryHelper.FileCategory.PICTURE);
        tvPictureCount.setText(pictureCategory.count + getString(R.string.main_image_nums));

        FileCategoryHelper.CategoryInfo videoCategory = mCategoryMap.get(FileCategoryHelper.FileCategory.VIDEO);
        tvVideoCount.setText(videoCategory.count + getString(R.string.main_vidio_nums));

//        SDCardUtils.SDCardInfo sdCardInfo = SDCardUtils.getSDCardInfo();
//        if(sdCardInfo != null) {
//            tvLocalDeviceSize.setText(SDCardUtils.convertStorage(sdCardInfo.total));
//
//            pbLocalDevice.setMax((int)sdCardInfo.total);
//            pbLocalDevice.setProgress((int)(sdCardInfo.total - sdCardInfo.free));
//        }


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

        findViewById(R.id.main_allfile_lay).setOnClickListener(this);
        findViewById(R.id.main_apk_lay).setOnClickListener(this);
        findViewById(R.id.main_image_lay).setOnClickListener(this);
        findViewById(R.id.main_music_lay).setOnClickListener(this);
        findViewById(R.id.main_video_lay).setOnClickListener(this);
        findViewById(R.id.main_search_lay).setOnClickListener(this);

        tvAppCount = (TextView) findViewById(R.id.apk_count);
        tvMusicCount = (TextView) findViewById(R.id.music_count);
        tvPictureCount = (TextView) findViewById(R.id.picture_count);
        tvVideoCount = (TextView) findViewById(R.id.video_count);

        rlLocalDevice = (RelativeLayout) findViewById(R.id.local_device);
        rlUsbDevice = (RelativeLayout) findViewById(R.id.usb_device);
        rlTFDevice = (RelativeLayout) findViewById(R.id.tf_device);

        pbLocalDevice = (ProgressBar) findViewById(R.id.local_device_size_progress);
        pbUsbDevice = (ProgressBar) findViewById(R.id.usb_device_size_progress);
        pbTFDevice = (ProgressBar) findViewById(R.id.tf_device_size_progress);
        tvLocalDeviceSize = (TextView) findViewById(R.id.local_device_size);
        tvUsbDeviceSize = (TextView) findViewById(R.id.usb_device_size);
        tvTFDeviceSize = (TextView) findViewById(R.id.tf_device_size);
    }

    private void initDiskInfo() {
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
            SDCardUtils.StorageInfo info = SDCardUtils.getSDCardInfo();
            Logger.getLogger().d("flash Info: " + info.free + " " + info.total);
            setDeviceSize(tvLocalDeviceSize, info.total);
            setProgress(pbLocalDevice, info);
        } else {
            setDeviceSize(tvLocalDeviceSize, 0);
            setProgress(pbLocalDevice, unmountInfo);
        }

        if(SDCardUtils.isMounted(TFSDPath)) {
            SDCardUtils.StorageInfo info = SDCardUtils.getDiskInfo(TFSDPath);
            setProgress(pbTFDevice, info);
            setDeviceSize(tvTFDeviceSize, info.total);
        } else {
            setDeviceSize(tvTFDeviceSize, 0);
            setProgress(pbTFDevice, unmountInfo);
        }

        if(SDCardUtils.isMounted(USBPath)) {
            SDCardUtils.StorageInfo info = SDCardUtils.getDiskInfo(USBPath);
            setDeviceSize(tvUsbDeviceSize, info.total);
            setProgress(pbUsbDevice, info);
        } else {
            setDeviceSize(tvUsbDeviceSize, 0);
            setProgress(pbUsbDevice, unmountInfo);
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

    private void updateUI() {
        initDiskInfo();
    }

    private void showUsbDeviceView(boolean isShow) {
        TextView udTitle = (TextView) findViewById(R.id.usb_device_title);
        TextView udSize = (TextView) findViewById(R.id.usb_device_size);
        ProgressBar udPb = (ProgressBar) findViewById(R.id.usb_device_size_progress);
        if(isShow) {
            udTitle.setVisibility(View.VISIBLE);
            udSize.setVisibility(View.VISIBLE);
            udPb.setVisibility(View.VISIBLE);
        } else{
            udTitle.setVisibility(View.GONE);
            udSize.setVisibility(View.GONE);
            udPb.setVisibility(View.GONE);
        }
    }

    private void setDeviceSize(TextView view, long size) {
        if(null != view) {
            view.setText(SDCardUtils.convertStorage(size));
        }
    }

    private void setProgress(ProgressBar bar, SDCardUtils.StorageInfo info) {
        if(null == bar) {
            return;
        }
        if(info.total == 0) {
            bar.setMax(100);
            bar.setProgress(100);
            return;
        }
        Logger.getLogger().d("info.total: " + (int)(info.total / 1024 /1024)+ " " + (int)((info.total - info.free) / 1024 / 1024));
        bar.setMax((int)info.total / 1024 / 1024);
        bar.setProgress((int)((info.total - info.free) / 1024 / 1024));
    }

}
