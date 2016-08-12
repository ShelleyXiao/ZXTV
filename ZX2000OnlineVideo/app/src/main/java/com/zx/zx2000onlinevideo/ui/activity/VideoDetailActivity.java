package com.zx.zx2000onlinevideo.ui.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.aaron.library.MLog;
import com.open.androidtvwidget.bridge.EffectNoDrawBridge;
import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.recycle.LinearLayoutManagerTV;
import com.open.androidtvwidget.recycle.RecyclerViewTV;
import com.open.androidtvwidget.view.MainLayout;
import com.open.androidtvwidget.view.MainUpView;
import com.open.androidtvwidget.view.ReflectItemView;
import com.zx.zx2000onlinevideo.R;
import com.zx.zx2000onlinevideo.adapter.RelatedProgramRecyclerViewAdapter;
import com.zx.zx2000onlinevideo.bean.youku.program.ProgramShow;
import com.zx.zx2000onlinevideo.bean.youku.program.RelatedProgram;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.presenter.IProgramRelatedPresenter;
import com.zx.zx2000onlinevideo.presenter.ISingleProgramGetInfoPresenter;
import com.zx.zx2000onlinevideo.presenter.impl.ProgramRelatedPresenter;
import com.zx.zx2000onlinevideo.presenter.impl.ProgramShowItemInfoPresenterImpl;
import com.zx.zx2000onlinevideo.ui.IView.IProgramShowItemInfoActivity;
import com.zx.zx2000onlinevideo.ui.view.ViewStatusTitleView;
import com.zx.zx2000onlinevideo.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * User: ShaudXiao
 * Date: 2016-08-05
 * Time: 11:15
 * Company: zx
 * Description:
 * FIXME
 */

public class VideoDetailActivity extends BaseActivity implements IProgramShowItemInfoActivity {


    @BindView(R.id.status_bar)
    ViewStatusTitleView mStatusBar;
    @BindView(R.id.movie_details_img_id)
    ImageView mMovieDetailsImgId;
    @BindView(R.id.movie_details_name)
    TextView mMovieDetailsName;
    @BindView(R.id.movie_details_type)
    TextView mMovieDetailsType;
    @BindView(R.id.movie_details_grade)
    TextView mMovieDetailsGrade;
    @BindView(R.id.movie_details_direct)
    TextView mMovieDetailsDirect;
    @BindView(R.id.movie_details_act)
    TextView mMovieDetailsAct;
    @BindView(R.id.movie_details_descri)
    TextView mMovieDetailsDescri;
    @BindView(R.id.details_play)
    ReflectItemView mDetailsPlay;
//    @BindView(R.id.details_sc)
//    ReflectItemView mDetailsSc;
    @BindView(R.id.details_serial_xq)
    ReflectItemView mDetailsSerialXq;
    @BindView(R.id.main_lay)
    MainLayout mMainLay;
    @BindView(R.id.movie_details_main)
    LinearLayout mMovieDetailsMain;
    @BindView(R.id.movie_details_pro)
    ProgressBar mMovieDetailsPro;
    @BindView(R.id.mainUpView2)
    MainUpView mainUpView2;
    @BindView(R.id.related_video)
    RecyclerViewTV mRelatedVideo;
//    @BindView(R.id.mainUpView1)
//    MainUpView mMainUpView1;


    private View mOldFocus; // 4.3以下版本需要自己保存.
    private OpenEffectBridge mOpenEffectBridge;

    private ISingleProgramGetInfoPresenter singleProgramGetInfoPresenter;
    private IProgramRelatedPresenter mProgramRelatedPresenter;
    private String showId;
    private String category;
    private String thumUrl;
    private int eposidCount;

    private RelatedProgramRecyclerViewAdapter mRelatedProgramRecyclerViewAdapter;
    private List<RelatedProgram.ShowsBean> mRelatedProgrameDatas = new ArrayList<>();
    private RecyclerViewBridge mRecyclerViewBridge;
    private View oldView;

    private boolean updated = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_moviedetails;
    }

    @Override
    protected void setupViews() {
        initViewMove();
        initRelatedRecyleView();
        initSearchBtn();
    }

    @Override
    protected void initialized() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            showId = bundle.getString(YoukuConfig.INTENT_VIDEO_ID);
            category = bundle.getString(YoukuConfig.INTENT_CATEGROY_KEY);
        }

        if(YoukuConfig.MOVIE.equals(category)) {
            mDetailsSerialXq.setVisibility(View.GONE);
        } else /*if(YoukuConfig.SERIALS.equals(category))*/ {
            mDetailsSerialXq.setVisibility(View.VISIBLE);
        }

        singleProgramGetInfoPresenter = new ProgramShowItemInfoPresenterImpl(this, this);
        singleProgramGetInfoPresenter.getSingleProgramInfo(showId);

        mProgramRelatedPresenter = new ProgramRelatedPresenter(this, this);
        mProgramRelatedPresenter.getRelatedProgram(showId);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        singleProgramGetInfoPresenter.unsubcrible();
    }

    private void initSearchBtn() {
        mStatusBar.setCategory(category);
        mStatusBar.setBtnSearchVisibility(false);
        mStatusBar.setLogoVisibilty(false);
    }

    @Override
    public void updateProgramShowInfo(ProgramShow data) {
        if(data == null) {
            return;
        }


        ImageLoader.loadVideoThumbImage(this, data.getThumbnail(), mMovieDetailsImgId);
        mMovieDetailsName.setText(data.getName());
        mMovieDetailsType.setText(getString(R.string.program_detail_type) + data.getGenre());
        mMovieDetailsGrade.setText(getString(R.string.program_detail_grade) + data.getScore());
        List<ProgramShow.AttrBean.DirectorBean> directorBeans = data.getAttr().getDirector();
        StringBuilder director = new StringBuilder();
        if (directorBeans.size() >= 1) {
            for (int i = 0; i < directorBeans.size(); i++) {
                director.append(directorBeans.get(i).getName() + " ");
            }
        }
        mMovieDetailsDirect.setText(getString(R.string.program_detail_director) + director.toString());

        StringBuilder actor = new StringBuilder();
        List<ProgramShow.AttrBean.PerformerBean> actors = data.getAttr().getPerformer();
        int actorsNum = actors.size();
        for (int i = 0; i < actorsNum; i++) {
            if (i < actorsNum) {
                actor.append(actors.get(i).getName() + "、");
            } else {
                actor.append(actors.get(i).getName());
            }
        }
        mMovieDetailsAct.setText(getString(R.string.program_detail_actors) + actor.toString());
        mMovieDetailsDescri.setText(getString(R.string.program_detail_descrpition) + data.getDescription());

        thumUrl = data.getThumbnail();
        MLog.e(" " + data.getEpisode_count() + " " + data.getEpisode_updated() );
        eposidCount = Integer.valueOf(data.getEpisode_updated());

        updated = true;
    }

    /**
     * 显示相关影视
     *
     * @param datas
     */
    @Override
    public void updateRelatedProgramShowDatas(List<RelatedProgram.ShowsBean> datas) {
        if (datas != null && datas.size() > 0) {
            mRelatedProgrameDatas.addAll(datas);
            mRelatedProgramRecyclerViewAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void showProgressDialog() {
        mMovieDetailsPro.setVisibility(View.VISIBLE);
        mMovieDetailsMain.setVisibility(View.GONE);

    }

    @Override
    public void hidProgressDialog() {
        mMovieDetailsPro.setVisibility(View.GONE);
        mMovieDetailsMain.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String error) {

    }

    @OnClick({R.id.details_play,  R.id.details_serial_xq})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.details_play:
                if(YoukuConfig.MOVIE.equals(category)) {
                    Intent intent = new Intent(this, PlayYoukuActivity.class);
                    intent.putExtra(YoukuConfig.CONSTANCE_VID, showId);
                    startActivity(intent);
                }
                break;

            case R.id.details_serial_xq:
                if(!updated) return;

                if(YoukuConfig.SERIALS.equals(category)
                        || YoukuConfig.ANIM.equals(category)
                        || YoukuConfig.DOCUMENTARY.equals(category)) {
                    Intent intent = new Intent(this, SerialActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(YoukuConfig.INTENT_VIDEO_ID, showId);
                    bundle.putString(YoukuConfig.INTENT_THUMB_URL, thumUrl);
                    bundle.putInt(YoukuConfig.INTENT_EMPOID_COUNT, eposidCount);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    MLog.e(thumUrl);
                }
                break;
        }
    }

    public void initViewMove() {
        mOpenEffectBridge = (OpenEffectBridge) mainUpView2.getEffectBridge();
        switchNoDrawBridgeVersion();

        MainLayout main_lay11 = (MainLayout) findViewById(R.id.main_lay);
        main_lay11.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(final View oldFocus, final View newFocus) {
                if (newFocus != null)
                    newFocus.bringToFront(); // 防止放大的view被压在下面. (建议使用MainLayout)
                float scale = 1.1f;
                mainUpView2.setFocusView(newFocus, mOldFocus, scale);
                mOldFocus = newFocus; // 4.3以下需要自己保存.
                // 测试是否让边框绘制在下面，还是上面. (建议不要使用此函数)
                if (newFocus != null) {
                    topDemo(newFocus, scale);
                }
            }
        });
        //初始化焦点
        mainUpView2.setFocusView(mDetailsPlay, mOldFocus, 1.1f);
        mOldFocus = mDetailsPlay;
        mDetailsPlay.setFocusableInTouchMode(true);
        mDetailsPlay.requestFocus();
        mDetailsPlay.setFocusable(true);
        mOpenEffectBridge.setVisibleWidget(false);
        mainUpView2.setUpRectResource(R.drawable.test_rectangle); // 设置移动边框的图片.
    }

    private void initRelatedRecyleView() {
//        mMainUpView1.setEffectBridge(new RecyclerViewBridge());

        LinearLayoutManagerTV layoutManager = new LinearLayoutManagerTV(this);
        layoutManager.setLeftPadding((int) getResources().getDimension(R.dimen.px250));
        layoutManager.setRightPadding((int) getResources().getDimension(R.dimen.px150));

//        mRecyclerViewBridge = (RecyclerViewBridge) mMainUpView1.getEffectBridge();
//        mRecyclerViewBridge.setUpRectResource(R.drawable.test_rectangle);

//        layoutManager.setOnChildSelectedListener(new OnChildSelectedListener() {
//
//            @Override
//            public void onChildSelected(RecyclerView parent, View focusView, int position, int dy) {
//                focusView.bringToFront();
////                mRecyclerViewBridge.setFocusView(focusView, oldView, 1.2f);
//                oldView = focusView;
//            }
//        });

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRelatedVideo.setLayoutManager(layoutManager);
        mRelatedVideo.setFocusable(false);
        mRelatedProgramRecyclerViewAdapter = new RelatedProgramRecyclerViewAdapter(this, mRelatedProgrameDatas);
        mRelatedVideo.setAdapter(mRelatedProgramRecyclerViewAdapter);
    }


    private void switchNoDrawBridgeVersion() {
        EffectNoDrawBridge effectNoDrawBridge = new EffectNoDrawBridge();
        effectNoDrawBridge.setTranDurAnimTime(10);
        mainUpView2.setEffectBridge(effectNoDrawBridge); // 4.3以下版本边框移动.
        mainUpView2.setUpRectResource(R.drawable.health_focus_border); // 设置移动边框的图片.
        mainUpView2.setDrawUpRectPadding(new Rect(10, 10, 8, -28)); // 边框图片设置间距.
    }

    public void topDemo(View newView, float scale) {
        if (newView.getId() == R.id.details_play ||  newView.getId() == R.id.details_serial_xq) { // 小人在外面的测试.
            mOpenEffectBridge.setVisibleWidget(false);
            mainUpView2.setUpRectResource(R.drawable.test_rectangle); // 设置移动边框的图片.
        } else {
            mOpenEffectBridge.setVisibleWidget(true);
            mainUpView2.setUpRectResource(R.drawable.health_focus_border); // 设置移动边框的图片.
            if (newView instanceof TextView) {
                ((TextView) newView).setEllipsize(TextUtils.TruncateAt.MARQUEE);
                ((TextView) newView).setSingleLine(true);
                ((TextView) newView).setMarqueeRepeatLimit(6);
            }
        }
    }

}
