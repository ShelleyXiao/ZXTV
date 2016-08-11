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

public class DocumentaryFragment extends LazyFragment {

    private static final String CATEGORY = "纪录片";


    OpenEffectBridge mOpenEffectBridge;
    Unbinder mUnbinder;
    @BindView(R.id.documentary_nature)
    ReflectItemView mDocumentaryNature;
    @BindView(R.id.documentary_biography)
    ReflectItemView mDocumentaryBiography;
    @BindView(R.id.documentary_cosmos)
    ReflectItemView mDocumentaryCosmos;
    @BindView(R.id.documentary_history)
    ReflectItemView mDocumentaryHistory;
    @BindView(R.id.documentary_culture)
    ReflectItemView mDocumentaryCulture;
    @BindView(R.id.documentary_military)
    ReflectItemView mDocumentaryMilitary;
    @BindView(R.id.documentary_scienceandtechnology)
    ReflectItemView mDocumentaryScienceandtechnology;
    @BindView(R.id.documentary_adventure)
    ReflectItemView mDocumentaryAdventure;
    @BindView(R.id.documentary_travel)
    ReflectItemView mDocumentaryTravel;
    @BindView(R.id.documentary_crime)
    ReflectItemView mDocumentaryCrime;
    @BindView(R.id.documentary_historical)
    ReflectItemView mDocumentaryHistorical;
    @BindView(R.id.main_lay)
    RelativeMainLayout mMainLay;
    @BindView(R.id.hscroll_view)
    SmoothHorizontalScrollView mHscrollView;
    @BindView(R.id.mainUpView1)
    MainUpView mMainUpView1;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_category_documentary, layout, false);
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

    @OnClick({R.id.documentary_nature, R.id.documentary_biography, R.id.documentary_cosmos, R.id.documentary_history, R.id.documentary_culture, R.id.documentary_military, R.id.documentary_scienceandtechnology, R.id.documentary_adventure, R.id.documentary_travel, R.id.documentary_crime, R.id.documentary_historical})
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), ProgramSetActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(YoukuConfig.INTENT_CATEGROY_KEY, CATEGORY);
        switch (view.getId()) {
            case R.id.documentary_nature:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.documentary_nature));
                break;
            case R.id.documentary_biography:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.documentary_biography));
                break;
            case R.id.documentary_cosmos:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.documentary_cosmos));
                break;
            case R.id.documentary_history:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.documentary_history));
                break;
            case R.id.documentary_culture:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.documentary_culture));
                break;
            case R.id.documentary_military:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.documentary_military));
                break;
            case R.id.documentary_scienceandtechnology:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.documentary_scienceandtechnology));
                break;
            case R.id.documentary_adventure:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.documentary_adventure));
                break;
            case R.id.documentary_travel:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.documentary_travel));
                break;
            case R.id.documentary_crime:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.documentary_crime));
                break;
            case R.id.documentary_historical:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.documentary_historical));
                break;
        }

        intent.putExtras(bundle);
        startActivity(intent);
    }
}
