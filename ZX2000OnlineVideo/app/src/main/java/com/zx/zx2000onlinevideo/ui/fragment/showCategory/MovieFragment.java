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

public class MovieFragment extends LazyFragment {

    private static final String CATEGORY = "电影";



    OpenEffectBridge mOpenEffectBridge;
    Unbinder mUnbinder;
    @BindView(R.id.movie_genre_action)
    ReflectItemView mMovieGenreAction;
    @BindView(R.id.movie_genre_drama)
    ReflectItemView mMovieGenreDrama;
    @BindView(R.id.movie_genre_comedy)
    ReflectItemView mMovieGenreComedy;
    @BindView(R.id.movie_genre_romance)
    ReflectItemView mMovieGenreRomance;
    @BindView(R.id.movie_genre_thriller)
    ReflectItemView mMovieGenreThriller;
    @BindView(R.id.movie_genre_war)
    ReflectItemView mMovieGenreWar;
    @BindView(R.id.movie_genre_animation)
    ReflectItemView mMovieGenreAnimation;
    @BindView(R.id.movie_genre_sciencefiction)
    ReflectItemView mMovieGenreSciencefiction;
    @BindView(R.id.movie_genre_martial)
    ReflectItemView mMovieGenreMartial;
    @BindView(R.id.movie_genre_policedrama)
    ReflectItemView mMovieGenrePolicedrama;
    @BindView(R.id.movie_genre_sports)
    ReflectItemView mMovieGenreSports;
    @BindView(R.id.movie_genre_crime)
    ReflectItemView mMovieGenreCrime;
    @BindView(R.id.main_lay)
    RelativeMainLayout mMainLay;
    @BindView(R.id.hscroll_view)
    SmoothHorizontalScrollView mHscrollView;
    @BindView(R.id.mainUpView1)
    MainUpView mMainUpView1;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_category_moive, layout, false);
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



    @OnClick({R.id.movie_genre_action, R.id.movie_genre_drama, R.id.movie_genre_comedy, R.id.movie_genre_romance, R.id.movie_genre_thriller, R.id.movie_genre_war, R.id.movie_genre_animation, R.id.movie_genre_sciencefiction, R.id.movie_genre_martial, R.id.movie_genre_policedrama, R.id.movie_genre_sports, R.id.movie_genre_crime})
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), ProgramSetActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(YoukuConfig.INTENT_CATEGROY_KEY, CATEGORY);
        switch (view.getId()) {
            case R.id.movie_genre_action:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.movice_genre_action));
                break;
            case R.id.movie_genre_drama:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.movice_genre_drama));
                break;
            case R.id.movie_genre_comedy:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.movice_genre_comedy));
                break;
            case R.id.movie_genre_romance:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.movice_genre_romance));
                break;
            case R.id.movie_genre_thriller:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.movice_genre_thriller));
                break;
            case R.id.movie_genre_war:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.movice_genre_war));
                break;
            case R.id.movie_genre_animation:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.movice_genre_animation));
                break;
            case R.id.movie_genre_sciencefiction:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.movice_genre_sciencefiction));
                break;
            case R.id.movie_genre_martial:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.movice_genre_martial));
                break;
            case R.id.movie_genre_policedrama:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.movice_genre_policedrama));
                break;
            case R.id.movie_genre_sports:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.movice_genre_sports));
                break;
            case R.id.movie_genre_crime:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.movice_genre_crime));
                break;
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
