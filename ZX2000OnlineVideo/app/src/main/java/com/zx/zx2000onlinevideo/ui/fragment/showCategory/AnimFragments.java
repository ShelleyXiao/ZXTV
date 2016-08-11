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
 * Date: 2016-08-03
 * Time: 10:27
 * Company: zx
 * Description:
 * FIXME
 */

public class AnimFragments extends LazyFragment {

    public static final String CATEGORY = "动漫";


    OpenEffectBridge mOpenEffectBridge;
    Unbinder mUnbinder;
    @BindView(R.id.anime_family)
    ReflectItemView mAnimeFamily;
    @BindView(R.id.anime_intelligence)
    ReflectItemView mAnimeIntelligence;
    @BindView(R.id.anime_fairytales)
    ReflectItemView mAnimeFairytales;
    @BindView(R.id.anime_history)
    ReflectItemView mAnimeHistory;
    @BindView(R.id.anime_adventure)
    ReflectItemView mAnimeAdventure;
    @BindView(R.id.anime_sciencefiction)
    ReflectItemView mGenreMilanimeSciencefictionitary;
    @BindView(R.id.anime_srw)
    ReflectItemView mAnimeSrw;
    @BindView(R.id.main_lay)
    RelativeMainLayout mMainLay;
    @BindView(R.id.hscroll_view)
    SmoothHorizontalScrollView mHscrollView;
    @BindView(R.id.mainUpView1)
    MainUpView mMainUpView1;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_category_anim, layout, false);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @OnClick({R.id.anime_family, R.id.anime_intelligence, R.id.anime_fairytales, R.id.anime_history, R.id.anime_adventure, R.id.anime_sciencefiction, R.id.anime_srw})
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), ProgramSetActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(YoukuConfig.INTENT_CATEGROY_KEY, CATEGORY);
        switch (view.getId()) {
            case R.id.anime_family:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.anime_family));
                break;
            case R.id.anime_intelligence:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.anime_intelligence));
                break;
            case R.id.anime_fairytales:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.anime_fairytales));
                break;
            case R.id.anime_history:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.anime_history));
                break;
            case R.id.anime_adventure:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.anime_adventure));
                break;
            case R.id.anime_sciencefiction:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.anime_sciencefiction));
                break;
            case R.id.anime_srw:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.anime_srw));
                break;
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
