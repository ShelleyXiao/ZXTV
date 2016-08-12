package com.zx.zx2000onlinevideo.ui.activity;

import android.content.Intent;
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
import com.zx.zx2000onlinevideo.adapter.VideoGridViewAdapter;
import com.zx.zx2000onlinevideo.bean.youku.QueryConditions;
import com.zx.zx2000onlinevideo.bean.youku.video.VideoBean;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.presenter.IVideoSetPresenter;
import com.zx.zx2000onlinevideo.presenter.impl.VideoSetPresenterImpl;
import com.zx.zx2000onlinevideo.ui.IView.IVideoSetActivity;
import com.zx.zx2000onlinevideo.ui.view.ViewStatusTitleView;
import com.zx.zx2000onlinevideo.ui.view.widget.BrandTextView;
import com.zx.zx2000onlinevideo.ui.view.widget.FlowRadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class VideoSetActivity extends BaseActivity implements IVideoSetActivity, FlowRadioGroup.OnCheckedChangeListener{


    @BindView(R.id.status_bar)
    ViewStatusTitleView mStatusBar;
    @BindView(R.id.gridView)
    GridViewTV mGridView;
    @BindView(R.id.sz_movie_set_pro)
    ProgressBar mGetVideoPro;
    @BindView(R.id.mainUpView1)
    MainUpView mMainUpView1;
    @BindView(R.id.show_total)
    TextView mShowTotal;
    @BindView(R.id.show_category_period)
    BrandTextView mShowCategoryPeriod;
    @BindView(R.id.video_menu_period_id)
    ListViewTV mVideoMenuPeriodId;
    @BindView(R.id.show_category_orderby)
    BrandTextView mShowCategoryOrderby;
    @BindView(R.id.video_menu_orderby_id)
    ListViewTV mVideoMenuOrderbyId;


    private OpenEffectBridge mOpenEffectBridge;
    private View mOldGridView;
    private VideoGridViewAdapter mVideoGridViewAdapter;

    private IVideoSetPresenter mVideoSetPresenter;
    private ArrayList<VideoBean> mVideoDatas = new ArrayList<>();
    private QueryConditions mQueryConditions;
    private int page = 1;
    private int mTotalCount = 0;

    private boolean isPage;

    private String[] mPeroidArr;
    private String[] mOrderByArr;

    private ConditionsAdapter mPeroidAdapter;
    private ConditionsAdapter mOrderyAdpter;

    private View mOldListView;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_set;
    }

    @Override
    protected void setupViews() {
        initViewMove();
        mOpenEffectBridge.setVisibleWidget(true); // 隐藏
        mShowTotal.setFocusable(false);
        mVideoGridViewAdapter = new VideoGridViewAdapter(mVideoDatas, this);
        mGridView.setAdapter(mVideoGridViewAdapter);
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
                        mVideoSetPresenter.getVideoSetByCategory(category, genre, mQueryConditions, page, YoukuConfig.SHOW_PAGE_COUNT);
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
                VideoBean video = mVideoDatas.get(position);
                Bundle bundle = new Bundle();
                Intent intent = new Intent(VideoSetActivity.this, PlayYoukuActivity.class);
                intent.putExtra(YoukuConfig.CONSTANCE_VID, video.getId());
                startActivity(intent);
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
        mQueryConditions = new QueryConditions();
        mVideoSetPresenter = new VideoSetPresenterImpl(this, this);
        mVideoSetPresenter.getVideoSetByCategory(category, genre, mQueryConditions, page, YoukuConfig.SHOW_PAGE_COUNT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mVideoSetPresenter.unsubcrible();
    }

    public void initViewMove() {
        mOpenEffectBridge = (OpenEffectBridge) mMainUpView1.getEffectBridge();
        switchNoDrawBridgeVersion();
    }

    private void initSearchBtn() {
        mStatusBar.setCategory(category);
        mStatusBar.setBtnSearchVisibility(false);
    }

    private void initListMenu() {
        mPeroidArr = getResources().getStringArray(R.array.VideoPeriod);
        mPeroidAdapter = new ConditionsAdapter(Arrays.asList(mPeroidArr), this);

        mOrderByArr = getResources().getStringArray(R.array.VideoOrder);
        mOrderyAdpter = new ConditionsAdapter(Arrays.asList(mOrderByArr), this);

        mVideoMenuPeriodId.setAdapter(mPeroidAdapter);
        mVideoMenuOrderbyId.setAdapter(mOrderyAdpter);

        mOpenEffectBridge.setVisibleWidget(true); // 隐藏
        mVideoMenuPeriodId.requestFocus();

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

        mVideoMenuPeriodId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (view != null) {
                    // 子控件置顶，必需使用ListViewTV才行，
                    // 不然焦点会错乱.
                    // 不要忘记这句关键的话哦.
                    mGridView.setFocusable(true);
                    MLog.i(TAG, "离开焦点2");
                    view.bringToFront();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mVideoMenuPeriodId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mOpenEffectBridge.setVisibleWidget(true);
                    mVideoMenuPeriodId.setSelector(R.drawable.lemon_liangguang_03);
                } else {
                    mVideoMenuPeriodId.setSelector(R.color.listMenuFocusColor);
                }
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

        mVideoMenuPeriodId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                QueryConditions.PeiodCondition[] mPeroidArr = QueryConditions.PeiodCondition.values();
                mQueryConditions.setPeiodCondition(mPeroidArr[position]);

//                mVideoMenuPeriodId.setPoint(position);
                TextView textView = (TextView)view.findViewById(R.id.lemon_video_tv);
                textView.setTextColor(Color.WHITE);
                if (mOldListView != null) {
                    TextView textView2 = (TextView) mOldListView.findViewById(R.id.lemon_video_tv);
                    textView2.setTextColor(getResources().getColor(R.color.zx_b3aeae));
                } else {
                    TextView textView2 = (TextView)mVideoMenuPeriodId.getChildAt(0).findViewById(R.id.lemon_video_tv);
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

        mVideoMenuOrderbyId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                QueryConditions.OrdeyByCondition[] mOrdeyByArr = QueryConditions.OrdeyByCondition.values();
                mQueryConditions.setOrdeyByCondition(mOrdeyByArr[i]);

                TextView textView = (TextView)view.findViewById(R.id.lemon_video_tv);
                textView.setTextColor(Color.WHITE);
                if (mOldListView != null) {
                    TextView textView2 = (TextView) mOldListView.findViewById(R.id.lemon_video_tv);
                    textView2.setTextColor(getResources().getColor(R.color.zx_b3aeae));
                } else {
                    TextView textView2 = (TextView)mVideoMenuOrderbyId.getChildAt(0).findViewById(R.id.lemon_video_tv);
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
//        mMainUpView1.setDrawUpRectPadding(new Rect(25, 15, 40, -35)); // 边框图片设置间距.
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
    public void updateList(List<VideoBean> videos) {
        isPage = true;
        if (videos != null) {
            mVideoDatas.addAll(videos);
            mVideoGridViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateVideoTotal(int total) {
        mShowTotal.setText(getString(R.string.show_total_str, total));
        mTotalCount = total;
        mShowTotal.setVisibility(View.VISIBLE);
        mStatusBar.setLogoVisibilty(false);
    }

    @Override
    public void onCheckedChanged(FlowRadioGroup group, int checkedId) {

        switch (checkedId) {
//            case R.id.period_today:
//                mQueryConditions.setPeiodCondition(QueryConditions.PeiodCondition.Today);
//                break;
//            case R.id.period_week:
//                mQueryConditions.setPeiodCondition(QueryConditions.PeiodCondition.Week);
//                break;
//            case R.id.period_month:
//                mQueryConditions.setPeiodCondition(QueryConditions.PeiodCondition.Month);
//                break;
//            case R.id.period_history:
//                mQueryConditions.setPeiodCondition(QueryConditions.PeiodCondition.History);
//                break;
//            case R.id.orderby_comments:
//                mQueryConditions.setOrdeyByCondition(QueryConditions.OrdeyByCondition.ConmmentCount);
//                break;
//            case R.id.orderby_favoritecount:
//                mQueryConditions.setOrdeyByCondition(QueryConditions.OrdeyByCondition.FavoriteCount);
//                break;
//            case R.id.orderby_fravoritetime:
//                mQueryConditions.setOrdeyByCondition(QueryConditions.OrdeyByCondition.FavoriteTime);
//                break;
//            case R.id.orderby_published:
//                mQueryConditions.setOrdeyByCondition(QueryConditions.OrdeyByCondition.Published);
//                break;
//            case R.id.orderby_viewcount:
//                mQueryConditions.setOrdeyByCondition(QueryConditions.OrdeyByCondition.ViewCount);
//                break;
//            case R.id.orderby_referencecount:
//                mQueryConditions.setOrdeyByCondition(QueryConditions.OrdeyByCondition.ReferenceCount);
//                break;
//            default:
//                mQueryConditions.setOrdeyByCondition(QueryConditions.OrdeyByCondition.Empty);
//                mQueryConditions.setPeiodCondition(QueryConditions.PeiodCondition.Empty);
//                break;
        }

        refresh();
    }

    private void refresh() {
        page = 1;
        mVideoDatas.clear();
        mVideoSetPresenter.getVideoSetByCategory(category, genre, mQueryConditions, page, YoukuConfig.SHOW_PAGE_COUNT);
    }

}
