package com.zx.zx2000onlinevideo.ui.fragment.showCategory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.view.MainUpView;
import com.open.androidtvwidget.view.ReflectItemView;
import com.open.androidtvwidget.view.RelativeMainLayout;
import com.open.androidtvwidget.view.SmoothHorizontalScrollView;
import com.zx.zx2000onlinevideo.R;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.ui.activity.VideoSetActivity;
import com.zx.zx2000onlinevideo.ui.fragment.LazyFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * User: ShaudXiao
 * Date: 2016-08-11
 * Time: 10:52
 * Company: zx
 * Description:
 * FIXME
 */

public class SportFragment extends LazyFragment {

    private static final String CATEGORY = "体育";

    OpenEffectBridge mOpenEffectBridge;
    Unbinder mUnbinder;
    @BindView(R.id.sports_info)
    ReflectItemView mSportsInfo;
    @BindView(R.id.sports_olympic)
    ReflectItemView mSportsOlympic;
    @BindView(R.id.sports_basketball)
    ReflectItemView mSportsBasketball;
    @BindView(R.id.sports_cba)
    ReflectItemView mSportsCba;
    @BindView(R.id.sports_football)
    ReflectItemView mSportsFootball;
    @BindView(R.id.sports_extremesports)
    ReflectItemView mSportsExtremesports;
    @BindView(R.id.sports_tennis)
    ReflectItemView mSportsTennis;
    @BindView(R.id.sports_fitness)
    ReflectItemView mSportsFitness;
    @BindView(R.id.sports_other)
    ReflectItemView mSportsOther;
    @BindView(R.id.main_lay)
    RelativeMainLayout mMainLay;
    @BindView(R.id.hscroll_view)
    SmoothHorizontalScrollView mHscrollView;
    @BindView(R.id.mainUpView1)
    MainUpView mMainUpView1;


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_category_sports, layout, false);
        setContentView(view);
        mUnbinder = ButterKnife.bind(this, view);

        mOpenEffectBridge = (OpenEffectBridge) mMainUpView1.getEffectBridge();

        mMainUpView1.setUpRectResource(R.drawable.test_rectangle); // 设置移动边框的图片.
        mMainUpView1.setShadowResource(R.drawable.item_shadow); // 设置移动边框的阴影.

        mMainLay.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(final View oldFocus, final View newFocus) {
                if (newFocus != null)
                    newFocus.bringToFront();
                float scale = 1.2f;
                if (mMainUpView1 != null) {
//                    MLog.d("id = " + newFocus.getId());
                    if (newFocus instanceof ReflectItemView) {
                        mOpenEffectBridge.setVisibleWidget(false);
                        mMainUpView1.setFocusView(newFocus, oldFocus, scale);
                    } else {
                        mOpenEffectBridge.setVisibleWidget(true);
                    }
                }
            }
        });

    }


    @Override
    public void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        mUnbinder.unbind();
    }


    @OnClick({R.id.sports_info, R.id.sports_olympic, R.id.sports_basketball, R.id.sports_cba, R.id.sports_football, R.id.sports_extremesports, R.id.sports_tennis, R.id.sports_fitness, R.id.sports_other})
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), VideoSetActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(YoukuConfig.INTENT_CATEGROY_KEY, CATEGORY);
        switch (view.getId()) {
            case R.id.sports_info:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.sports_info));
                break;
            case R.id.sports_olympic:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.sports_olympic));
                break;
            case R.id.sports_basketball:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.sports_basketball));
                break;
            case R.id.sports_cba:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.sports_cba));
                break;
            case R.id.sports_football:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.sports_football));
                break;
            case R.id.sports_extremesports:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.sports_extremesports));
                break;
            case R.id.sports_tennis:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.sports_tennis));
                break;
            case R.id.sports_fitness:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.sports_fitness));
                break;
            case R.id.sports_other:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.sports_other));
                break;
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
