package com.zx.zx2000onlinevideo.ui.fragment.showCategory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.example.aaron.library.MLog;
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
 * Date: 2016-08-03
 * Time: 10:27
 * Company: zx
 * Description:
 * FIXME
 */

public class NewsFragments extends LazyFragment {

    public static final String CATEGORY = "资讯";

    @BindView(R.id.genre_life)
    ReflectItemView mGenreLife;
    @BindView(R.id.genre_social)
    ReflectItemView mGenreSocial;
    @BindView(R.id.genre_finance)
    ReflectItemView mGenreFinance;
    @BindView(R.id.genre_gov)
    ReflectItemView mGenreGov;
    @BindView(R.id.genre_tech)
    ReflectItemView mGenreTech;
    @BindView(R.id.genre_military)
    ReflectItemView mGenreMilitary;
    @BindView(R.id.genre_legal)
    ReflectItemView mGenreLegal;
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

        View view = inflater.inflate(R.layout.fragment_category_news, layout, false);
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
                    MLog.d("id = " + newFocus.getId());
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

    @OnClick({R.id.genre_life, R.id.genre_social, R.id.genre_finance, R.id.genre_gov, R.id.genre_tech, R.id.genre_military, R.id.genre_legal})
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), VideoSetActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(YoukuConfig.INTENT_CATEGROY_KEY, CATEGORY);
        switch (view.getId()) {
            case R.id.genre_life:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.genre_life));
                break;
            case R.id.genre_social:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.genre_social));
                break;
            case R.id.genre_finance:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.genre_finance));
                break;
            case R.id.genre_gov:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.genre_gov));
                break;
            case R.id.genre_tech:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.genre_tech));
                break;
            case R.id.genre_military:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.genre_military));
                break;
            case R.id.genre_legal:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.genre_legal));
                break;

        }
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
