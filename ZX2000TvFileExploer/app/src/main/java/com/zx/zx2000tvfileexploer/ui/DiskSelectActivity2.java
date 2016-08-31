package com.zx.zx2000tvfileexploer.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.view.LinearMainLayout;
import com.open.androidtvwidget.view.MainUpView;
import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.ui.base.BaseActivity;
import com.zx.zx2000tvfileexploer.utils.Logger;
import com.zx.zx2000tvfileexploer.utils.SDCardUtils;
import com.zx.zx2000tvfileexploer.view.RoundProgressBar;

import java.util.List;

/**
 * Created by ShaudXiao on 2016/7/19.
 */
public class DiskSelectActivity2 extends BaseActivity  {

    private MainUpView mAnimationFrame;
    private OpenEffectBridge mOpenEffectBridge;
    private View mOldFocus;

    private LinearMainLayout mContentContainer;

    private LayoutInflater mLayoutInflater;



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.select_disk_layout_activity;
    }

    @Override
    public void init() {
        setCurPath(getString(R.string.disk_select_promat));

        mLayoutInflater = getLayoutInflater();

        initView();

        updateDiskInfo();


    }

    @Override
    public void updateDiskInfo() {
        if(mContentContainer.getChildCount() > 0) {
            mContentContainer.removeAllViews();
        }

//        for(int i = 0; i < 2; i++) {
//            View view =  mLayoutInflater.inflate(R.layout.disk_item_layout, null);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.setMargins(25, 50, 25, 50);
//
//            mContentContainer.addView(view, params);
//        }

        List<SDCardUtils.StorageInfo> storageInfos = SDCardUtils.listAvaliableStorage(this);

        for(int i = 0; i < storageInfos.size(); i++) {
            final SDCardUtils.StorageInfo info = storageInfos.get(i);
            Logger.getLogger().d("path " + info.path + " " + info.isMounted());
            if(info.isMounted()) {
                View view =  mLayoutInflater.inflate(R.layout.disk_item_layout, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(25, 50, 25, 50);

                mContentContainer.addView(view, params);

                updateItemView(info, view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DiskSelectActivity2.this, FileListActivity.class);
                        intent.putExtra("category", GlobalConsts.INTENT_EXTRA_ALL_VLAUE);
                        intent.putExtra("path", info.path);
                        startActivity(intent);
                    }
                });
            }

        }
    }


    private void initView() {
        mAnimationFrame = (MainUpView) findViewById(R.id.main_up_view);
        mOpenEffectBridge = (OpenEffectBridge) mAnimationFrame.getEffectBridge();

        mAnimationFrame.setUpRectResource(R.drawable.test_rectangle);
        mAnimationFrame.setShadowResource(R.drawable.item_shadow);

        mContentContainer = (LinearMainLayout) findViewById(R.id.container);

        mContentContainer.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
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

    }

    private void updateItemView(SDCardUtils.StorageInfo info, View layout) {

        RoundProgressBar progress = (RoundProgressBar)layout.findViewById(R.id.disk_progress);
        TextView tvTotalSpace = (TextView) layout.findViewById(R.id.disk_total);
        TextView tvUseSpace = (TextView) layout.findViewById(R.id.disk_use);
        TextView name = (TextView) layout.findViewById(R.id.disk_name);

        if(info.isMounted()) {
            Logger.getLogger().d("Info: " + info.free + " " + info.total);
            setProgress(progress, info);
            setDiskUseInfo(tvTotalSpace, tvUseSpace, info);
            name.setText(formatName(info.path));
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

    private String formatName(String path) {
        String lastP = path.substring(path.lastIndexOf("/"));
        path = path.substring(0, path.lastIndexOf("/"));
        String retPath = path.substring(path.lastIndexOf("/")) + lastP;
        return retPath;
    }

}


