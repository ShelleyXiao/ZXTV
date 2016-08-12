package com.zx.zx2000onlinevideo.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.aaron.library.MLog;
import com.open.androidtvwidget.view.GridViewTV;
import com.zx.zx2000onlinevideo.R;
import com.zx.zx2000onlinevideo.adapter.SerialAdapter;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.presenter.ISerailProgramPresenter;
import com.zx.zx2000onlinevideo.presenter.impl.SerailProgramPresenterImpl;
import com.zx.zx2000onlinevideo.ui.IView.ISerialActivity;
import com.zx.zx2000onlinevideo.ui.view.ViewStatusTitleView;
import com.zx.zx2000onlinevideo.utils.ImageLoader;
import com.zx.zx2000onlinevideo.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * User: ShaudXiao
 * Date: 2016-08-08
 * Time: 16:54
 * Company: zx
 * Description:
 * FIXME
 */

public class SerialActivity extends BaseActivity implements ISerialActivity {


    @BindView(R.id.status_bar)
    ViewStatusTitleView mStatusBar;
    @BindView(R.id.movie_details_img_id)
    ImageView mMovieDetailsImgId;
    @BindView(R.id.serial_gridview)
    GridViewTV mSerialGridview;
    @BindView(R.id.serial_movie_details_main)
    RelativeLayout mSerialMovieDetailsMain;
    @BindView(R.id.zx_pro)
    ProgressBar mZxPro;

    private View oldView;

    private List<String> serialIds = new ArrayList<>();
    private SerialAdapter mSerialAdapter;

    private ISerailProgramPresenter mSerailProgramPresenter;

    private String videoId;
    private String thumUrl;
    private int eposidCount;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_serial;
    }

    @Override
    protected void setupViews() {
        mSerialGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String showId = serialIds.get((int)id);
                play(showId);
            }
        });
        mSerialGridview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView button = (TextView) view.findViewById(R.id.serial_item_but);
                button.setTextColor(Color.parseColor("#57fffa"));
                button.setBackgroundResource(R.drawable.lemon_servial_pree);
                if (oldView != null) {
                    TextView button2 = (TextView) oldView.findViewById(R.id.serial_item_but);
                    button2.setTextColor(Color.parseColor("#b3aeae"));
                    button2.setBackgroundResource(R.drawable.lemon_servial_none);
                }
                oldView = view;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSerialGridview.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                //初始化选择
                oldView = mSerialGridview.getChildAt(0 - mSerialGridview.getFirstVisiblePosition());
                if (oldView != null) {
                    TextView button = (TextView) oldView.findViewById(R.id.serial_item_but);
                    button.setTextColor(Color.parseColor("#57fffa"));
                    button.setBackgroundResource(R.drawable.lemon_servial_pree);
                }
            }
        });

        initSearchBtn();
    }

    @Override
    protected void initialized() {
        Bundle bundle = getIntent().getExtras();
        MLog.e("bundle = " + bundle);
        if(null != bundle) {
            videoId = bundle.getString(YoukuConfig.INTENT_VIDEO_ID);
            thumUrl = bundle.getString(YoukuConfig.INTENT_THUMB_URL);
            eposidCount = bundle.getInt(YoukuConfig.INTENT_EMPOID_COUNT);
            Logger.getLogger().e("thumUrl = " + thumUrl);
        }

        mSerailProgramPresenter = new SerailProgramPresenterImpl(this, this);
        mSerailProgramPresenter.getSerailProgramInfo(videoId, eposidCount);

        ImageLoader.loadVideoThumbImage(this, thumUrl, mMovieDetailsImgId);

        mSerialAdapter = new SerialAdapter(this, serialIds);
        mSerialGridview.setAdapter(mSerialAdapter);
    }

    private void initSearchBtn() {
        mStatusBar.setCategory(getString(R.string.serail_title));
        mStatusBar.setBtnSearchVisibility(false);
    }

    @Override
    public void showProgressDialog() {
        mZxPro.setVisibility(View.VISIBLE);
        mMovieDetailsImgId.setVisibility(View.GONE);
        mSerialGridview.setVisibility(View.GONE);
    }

    @Override
    public void hidProgressDialog() {
        mZxPro.setVisibility(View.GONE);
        mMovieDetailsImgId.setVisibility(View.VISIBLE);
        mSerialGridview.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void updateSerialProgramShowDatas(List<String> datas) {
        serialIds.addAll(datas);
        mSerialAdapter.notifyDataSetChanged();
    }

    private void play(String showId) {
        Intent intent = new Intent(this, PlayYoukuActivity.class);
        intent.putExtra(YoukuConfig.CONSTANCE_VID, showId);
        startActivity(intent);
    }
}
