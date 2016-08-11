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

public class VarietyFragment extends LazyFragment {

    private static final String CATEGORY = "综艺";

    OpenEffectBridge mOpenEffectBridge;
    Unbinder mUnbinder;
    @BindView(R.id.variety_crosstal)
    ReflectItemView mVarietyCrosstal;
    @BindView(R.id.variety_sketch)
    ReflectItemView mVarietySketch;
    @BindView(R.id.variety_reality)
    ReflectItemView mVarietyReality;
    @BindView(R.id.variety_talkshow)
    ReflectItemView mVarietyTalkshow;
    @BindView(R.id.variety_food)
    ReflectItemView mVarietyFood;
    @BindView(R.id.variety_interview)
    ReflectItemView mVarietyInterview;
    @BindView(R.id.variety_opera)
    ReflectItemView mVarietyOpera;
    @BindView(R.id.main_lay)
    RelativeMainLayout mMainLay;
    @BindView(R.id.hscroll_view)
    SmoothHorizontalScrollView mHscrollView;
    @BindView(R.id.mainUpView1)
    MainUpView mMainUpView1;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_category_variety, layout, false);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @OnClick({R.id.variety_crosstal, R.id.variety_sketch, R.id.variety_reality, R.id.variety_talkshow, R.id.variety_food, R.id.variety_interview, R.id.variety_opera})
    public void onClick(View view) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(YoukuConfig.INTENT_CATEGROY_KEY, CATEGORY);
        switch (view.getId()) {
            case R.id.variety_crosstal:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.variety_crosstal));
                intent.setClass(getActivity(), VideoSetActivity.class);
                break;
            case R.id.variety_sketch:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.variety_sketch));
                intent.setClass(getActivity(), VideoSetActivity.class);
                break;
            case R.id.variety_reality:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.variety_reality));
                intent.setClass(getActivity(), ProgramSetActivity.class);
                break;
            case R.id.variety_talkshow:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.variety_talkshow));
                intent.setClass(getActivity(), ProgramSetActivity.class);
                break;
            case R.id.variety_food:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.variety_food));
                intent.setClass(getActivity(), ProgramSetActivity.class);
                break;
            case R.id.variety_interview:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.variety_interview));
                intent.setClass(getActivity(), ProgramSetActivity.class);
                break;
            case R.id.variety_opera:
                bundle.putString(YoukuConfig.INTENT_GENRE_KEY, getString(R.string.variety_opera));
                intent.setClass(getActivity(), ProgramSetActivity.class);
                break;
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
