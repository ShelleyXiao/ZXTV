package com.zx.zx2000onlinevideo.ui.activity;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.aaron.library.MLog;
import com.open.androidtvwidget.bridge.EffectNoDrawBridge;
import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.view.GridViewTV;
import com.open.androidtvwidget.view.ListViewTV;
import com.open.androidtvwidget.view.MainUpView;
import com.zx.zx2000onlinevideo.R;
import com.zx.zx2000onlinevideo.adapter.ConditionsAdapter;
import com.zx.zx2000onlinevideo.adapter.ProgramGridViewAdapter;
import com.zx.zx2000onlinevideo.bean.youku.ProgramQueryConditions;
import com.zx.zx2000onlinevideo.bean.youku.category.ProgramByCategory;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.presenter.IProgramSetPresenter;
import com.zx.zx2000onlinevideo.presenter.impl.ProgramSetPresenterImpl;
import com.zx.zx2000onlinevideo.ui.IView.IProgramSetActivity;
import com.zx.zx2000onlinevideo.ui.view.ViewStatusTitleView;
import com.zx.zx2000onlinevideo.ui.view.widget.FlowRadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgramSetActivity extends BaseActivity implements IProgramSetActivity, FlowRadioGroup.OnCheckedChangeListener {


    @BindView(R.id.status_bar)
    ViewStatusTitleView mStatusBar;
    @BindView(R.id.video_menu_orderby_id)
    ListViewTV mVideoMenuOrderbyId;
    @BindView(R.id.show_total)
    TextView mShowTotal;
    @BindView(R.id.gridView)
    GridViewTV mGridView;
    @BindView(R.id.sz_movie_set_pro)
    ProgressBar mGetVideoPro;
    @BindView(R.id.mainUpView1)
    MainUpView mMainUpView1;

    private OpenEffectBridge mOpenEffectBridge;
    private View mOldGridView;
    private ProgramGridViewAdapter mGridViewAdapter;

    private IProgramSetPresenter mProgramSetPresenter;
    private ArrayList<ProgramByCategory.ShowsBean> mVideoDatas = new ArrayList<>();
    private ProgramQueryConditions mQueryConditions;
    private int page = 1;
    private int mTotalCount = 0;

    private boolean isPage;

    private String[] mOrderByArr;
    private ConditionsAdapter mOrderyAdpter;

    private View mOldListView;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_program_set;
    }

    @Override
    protected void setupViews() {
        initViewMove();
        mOpenEffectBridge.setVisibleWidget(true); // 隐藏
        mShowTotal.setFocusable(false);
        mGridViewAdapter = new ProgramGridViewAdapter(mVideoDatas, this, type);
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setFocusable(false);

        mGridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * 这里注意要加判断是否为NULL.
                 * 因为在重新加载数据以后会出问题.
                 */
                // mOpenEffectBridge.setVisibleWidget(false);
                if (view != null && mOldGridView != null) {
                    view.bringToFront();
                    mMainUpView1.setFocusView(view, mOldGridView, 1.1f);
                }
                mOldGridView = view;
                int size = mVideoDatas.size();
                if (size - (YoukuConfig.SHOW_PAGE_COUNT + 1) < position && size < mTotalCount) {
                    if (isPage) {
                        //翻页
                        page = page + 1;
                        mProgramSetPresenter.getProgramSetByCategory(category, genre, mQueryConditions, page, YoukuConfig.SHOW_PAGE_COUNT);
                        isPage = false;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProgramByCategory.ShowsBean video = mVideoDatas.get(position);
                Bundle bundle = new Bundle();
                bundle.putString(YoukuConfig.INTENT_VIDEO_ID, video.getId());
                bundle.putString(YoukuConfig.INTENT_CATEGROY_KEY, category);

                startActivity(VideoDetailActivity.class, bundle);
            }
        });
        mGridView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mOpenEffectBridge.setVisibleWidget(false);
                    if (mOldGridView == null) {
                        mMainUpView1.setFocusView(mGridView.getSelectedView(), 1.1f);
                        mOldGridView = mGridView.getSelectedView();
                    } else {
                        mMainUpView1.setFocusView(mOldGridView, 1.1f);
                    }
                } else {
                    mMainUpView1.setVisibility(View.GONE);
                    mOpenEffectBridge.setVisibleWidget(true); // 隐藏
                    mMainUpView1.setUnFocusView(mOldGridView);
                }
            }
        });

        initListMenu();
        initSearchBtn();
    }

    @Override
    protected void initialized() {
        mQueryConditions = new ProgramQueryConditions();
        mProgramSetPresenter = new ProgramSetPresenterImpl(this, this);
        mProgramSetPresenter.getProgramSetByCategory(category, genre, mQueryConditions, page, YoukuConfig.SHOW_PAGE_COUNT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mProgramSetPresenter.unsubcrible();
    }

    public void initViewMove() {
        mOpenEffectBridge = (OpenEffectBridge) mMainUpView1.getEffectBridge();
        switchNoDrawBridgeVersion();
    }

    private void initSearchBtn() {
        mStatusBar.setCategory(category);
        mStatusBar.setBtnSearchVisibility(false);
        mStatusBar.setLogoVisibilty(false);
    }

    private void initListMenu() {

        mOrderByArr = getResources().getStringArray(R.array.ProgramOrder);
        mOrderyAdpter = new ConditionsAdapter(Arrays.asList(mOrderByArr), this);
        mVideoMenuOrderbyId.setAdapter(mOrderyAdpter);

        mOpenEffectBridge.setVisibleWidget(true); // 隐藏
        mVideoMenuOrderbyId.requestFocus();

        mVideoMenuOrderbyId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (view != null) {
                    // 子控件置顶，必需使用ListViewTV才行，
                    // 不然焦点会错乱.
                    // 不要忘记这句关键的话哦.
                    mGridView.setFocusable(true);
                    MLog.i(TAG, "离开焦点1");
                    view.bringToFront();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mVideoMenuOrderbyId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mOpenEffectBridge.setVisibleWidget(true);
                    mVideoMenuOrderbyId.setSelector(R.drawable.lemon_liangguang_03);
                } else {
                    mVideoMenuOrderbyId.setSelector(R.color.listMenuFocusColor);
                }
            }
        });


        mVideoMenuOrderbyId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProgramQueryConditions.OrdeyByCondition[] mOrdeyByArr = ProgramQueryConditions.OrdeyByCondition.values();
                mQueryConditions.setOrdeyByCondition(mOrdeyByArr[i]);

                TextView textView = (TextView) view.findViewById(R.id.lemon_video_tv);
                textView.setTextColor(Color.WHITE);
                if (mOldListView != null) {
                    TextView textView2 = (TextView) mOldListView.findViewById(R.id.lemon_video_tv);
                    textView2.setTextColor(getResources().getColor(R.color.zx_b3aeae));
                } else {
                    TextView textView2 = (TextView) mVideoMenuOrderbyId.getChildAt(0).findViewById(R.id.lemon_video_tv);
                    textView2.setTextColor(getResources().getColor(R.color.zx_b3aeae));
                }
                mOldListView = view;

                mOldGridView = null;
                mGridView.setSelection(0);
                mOpenEffectBridge.setVisibleWidget(true); // 隐藏
                mMainUpView1.setUnFocusView(mGridView.getChildAt(0));

                refresh();
                showProgressDialog();
            }
        });


    }

    private void switchNoDrawBridgeVersion() {
        EffectNoDrawBridge effectNoDrawBridge = new EffectNoDrawBridge();
        effectNoDrawBridge.setTranDurAnimTime(20);
        mMainUpView1.setEffectBridge(effectNoDrawBridge); // 4.3以下版本边框移动.
        mMainUpView1.setUpRectResource(R.drawable.health_focus_border); // 设置移动边框的图片.
        mMainUpView1.setDrawUpRectPadding(new Rect(25, 15, -20, -50)); // 边框图片设置间距.
    }


    @Override
    public void showProgressDialog() {
        mGetVideoPro.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hidProgressDialog() {
        mGetVideoPro.setVisibility(View.INVISIBLE);
        mGridView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String error) {
        mGetVideoPro.setVisibility(View.INVISIBLE);
        mGridView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void updateList(List<ProgramByCategory.ShowsBean> videos) {
        isPage = true;
        if (videos != null) {
            mVideoDatas.addAll(videos);
            mGridViewAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void updateVideoTotal(int total) {
        mShowTotal.setText(getString(R.string.show_total_str, total));
        mTotalCount = total;
        mShowTotal.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCheckedChanged(FlowRadioGroup group, int checkedId) {


        refresh();
    }

    private void refresh() {
        page = 1;
        mVideoDatas.clear();
        mProgramSetPresenter.getProgramSetByCategory(category, genre, mQueryConditions, page, YoukuConfig.SHOW_PAGE_COUNT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
