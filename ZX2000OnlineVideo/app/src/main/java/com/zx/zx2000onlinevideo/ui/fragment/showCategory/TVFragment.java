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
import com.zx.zx2000onlinevideo.ui.activity.ProgramSetActivity;
import com.zx.zx2000onlinevideo.ui.fragment.LazyFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * User: ShaudXiao
 * Date: 2016-08-05
 * Time: 13:55
 * Company: zx
 * Description:
 * FIXME
 */

public class TVFragment extends LazyFragment {

    private static final String CATEGORY = "电视剧";

    @BindView(R.id.program_genre_metrodrama)
    ReflectItemView mProgramGenreMetrodrama;
    @BindView(R.id.program_genre_family)
    ReflectItemView mProgramGenreFamily;
    @BindView(R.id.program_genre_drama)
    ReflectItemView mProgramGenreDrama;
    @BindView(R.id.program_genre_rural)
    ReflectItemView mProgramGenreRural;
    @BindView(R.id.program_genre_historical)
    ReflectItemView mProgramGenreHistorical;
    @BindView(R.id.program_genre_martial)
    ReflectItemView mProgramGenreMartial;
    @BindView(R.id.program_genre_history)
    ReflectItemView mProgramGenreHistory;
    @BindView(R.id.program_genre_soapopera)
    ReflectItemView mProgramGenreSoapopera;
    @BindView(R.id.program_genre_romance)
    ReflectItemView mProgramGenreRomance;
    @BindView(R.id.program_genre_fashon)
    ReflectItemView mProgramGenreFashon;
    @BindView(R.id.program_genre_cmdedy)
    ReflectItemView mProgramGenreCmdedy;
    @BindView(R.id.program_genre_police)
    ReflectItemView mProgramGenrePolice;
    @BindView(R.id.program_genre_mystery)
    ReflectItemView mProgramGenreMystery;
    @BindView(R.id.program_genre_military)
    ReflectItemView mProgramGenreMilitary;
    @BindView(R.id.program_genre_science)
    ReflectItemView mProgramGenreScience;
    @BindView(R.id.program_genre_fantasy)
    ReflectItemView mProgramGenreFantasy;
    @BindView(R.id.program_genre_other)
    ReflectItemView mProgramGenreOther;
    @BindView(R.id.main_lay)
    RelativeMainLayout mMainLay;
    @BindView(R.id.hscroll_view)
    SmoothHorizontalScrollView mHscrollView;
    @BindView(R.id.mainUpView1)
    MainUpView mMainUpView1;



    OpenEffectBridge mOpenEffectBridge;
    Unbinder mUnbinder;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_category_tv, layout, false);
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
                if(mMainUpView1 != null) {
//                    MLog.d("id = " + newFocus.getId());
                    if(newFocus instanceof  ReflectItemView) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @OnClick({R.id.program_genre_metrodrama, R.id.program_genre_family, R.id.program_genre_drama, R.id.program_genre_rural, R.id.program_genre_historical, R.id.program_genre_martial, R.id.program_genre_history, R.id.program_genre_soapopera, R.id.program_genre_romance, R.id.program_genre_fashon, R.id.program_genre_cmdedy, R.id.program_genre_police, R.id.program_genre_mystery, R.id.program_genre_military, R.id.program_genre_science, R.id.program_genre_fantasy, R.id.program_genre_other})
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), ProgramSetActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(YoukuConfig.INTENT_CATEGROY_KEY, CATEGORY);
        switch (view.getId()) {
            case R.id.program_genre_metrodrama:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_metrodrama));
                break;
            case R.id.program_genre_family:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_family));
                break;
            case R.id.program_genre_drama:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_drama));
                break;
            case R.id.program_genre_rural:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_rural));
                break;
            case R.id.program_genre_historical:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_historical));
                break;
            case R.id.program_genre_martial:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_martial));
                break;
            case R.id.program_genre_history:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_history));
                break;
            case R.id.program_genre_soapopera:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_soapopera));
                break;
            case R.id.program_genre_romance:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_romance));
                break;
            case R.id.program_genre_fashon:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_fashon));
                break;
            case R.id.program_genre_cmdedy:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_cmdedy));
                break;
            case R.id.program_genre_police:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_police));
                break;
            case R.id.program_genre_mystery:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_mystery));
                break;
            case R.id.program_genre_military:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_military));
                break;
            case R.id.program_genre_science:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_science));
                break;
            case R.id.program_genre_fantasy:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_fantasy));
                break;
            case R.id.program_genre_other:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.program_genre_other));
                break;
        }
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
