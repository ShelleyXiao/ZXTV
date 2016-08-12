package com.zx.zx2000onlinevideo.ui.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.zx.zx2000onlinevideo.R;
import com.zx.zx2000onlinevideo.ui.fragment.LazyFragment;
import com.zx.zx2000onlinevideo.ui.fragment.showCategory.FragmentFactory;
import com.zx.zx2000onlinevideo.ui.view.ViewStatusTitleView;
import com.zx.zx2000onlinevideo.ui.view.indicator.IndicatorViewPager;
import com.zx.zx2000onlinevideo.ui.view.indicator.ScrollIndicatorView;
import com.zx.zx2000onlinevideo.ui.view.indicator.slidebar.DrawableBar;
import com.zx.zx2000onlinevideo.ui.view.indicator.slidebar.ScrollBar;
import com.zx.zx2000onlinevideo.ui.view.indicator.transition.OnTransitionTextListener;
import com.zx.zx2000onlinevideo.ui.view.widget.ConfirmDialog;
import com.zx.zx2000onlinevideo.utils.DensityUtil;

import butterknife.BindView;
import butterknife.Unbinder;


public class MainActivity extends BaseActivity {


    @BindView(R.id.status_bar)
    ViewStatusTitleView mStatusBar;
    @BindView(R.id.view_paper)
    ViewPager mViewPaper;
    @BindView(R.id.indicator)
    ScrollIndicatorView mIndicator;

    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    //    private String[] names = {"CUPCAKE", "DONUT", "FROYO", "GINGERBREAD", "HONEYCOMB", "ICE CREAM SANDWICH", "JELLY BEAN", "KITKAT" , "HONEYCOMB", "ICE CREAM SANDWICH", "JELLY BEAN", "KITKAT"};
    private int unSelectTextColor;

    private String[] mShowCategory;

    private Unbinder unbinder;

    private OpenEffectBridge mSavebridge;
    private View mOldFocus;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setupViews() {
        initView();
        initSearchBtn();
    }

    @Override
    protected void initialized() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ConfirmDialog.Builder dialog = new ConfirmDialog.Builder(MainActivity.this);
            dialog.setMessage(R.string.exit_app_promat);
            dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    dialog.dismiss();
                }
            });
            dialog.create().show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {

        mShowCategory = getResources().getStringArray(R.array.ShowCategory);

        mIndicator.setSplitAuto(true);
        mIndicator.setPinnedTabView(false);
        // 设置固定tab的shadow，这里不设置的话会使用默认的shadow绘制
//        mIndicator.setPinnedShadow(R.drawable.tabshadow, dipToPix(4));
        mIndicator.setPinnedTabBgColor(Color.RED);
        mIndicator.setScrollBar(new DrawableBar(this, R.drawable.round_border_white_selector, ScrollBar.Gravity.CENTENT_BACKGROUND) {
            @Override
            public int getHeight(int tabHeight) {
                return tabHeight - DensityUtil.dip2px(MainActivity.this, 12);
            }

            @Override
            public int getWidth(int tabWidth) {
                return tabWidth - DensityUtil.dip2px(MainActivity.this, 12);
            }
        });
        unSelectTextColor = Color.WHITE;
        // 设置滚动监听
        mIndicator.setOnTransitionListener(new OnTransitionTextListener().setColor(Color.RED, unSelectTextColor));

//        mViewPaper.setOffscreenPageLimit(2);
        indicatorViewPager = new IndicatorViewPager(mIndicator, mViewPaper);
        inflate = LayoutInflater.from(getApplicationContext());
        indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

//        mViewPaper.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
//            @Override
//            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
//                int pos = mViewPaper.getCurrentItem();
//                final MainUpView mainUpView = (MainUpView) FragmentFactory.createFragment(pos).findViewById(R.id.mainUpView1);
//                if(null == mainUpView) {
//                    return;
//                }
//                MLog.d("mainUpView = " + mainUpView);
//                final OpenEffectBridge bridge = (OpenEffectBridge) mainUpView.getEffectBridge();
//                if (!(newFocus instanceof ReflectItemView)) { // 不是 ReflectitemView 的话.
//                    MLog.i("onGlobalFocusChanged no ReflectItemView + " + (newFocus instanceof GridView));
//                    mainUpView.setUnFocusView(mOldFocus);
//                    bridge.setVisibleWidget(true); // 隐藏.
//                    mSavebridge = null;
//                } else {
//                    MLog.i(TAG, "onGlobalFocusChanged yes ReflectItemView");
//                    newFocus.bringToFront();
//                    mSavebridge = bridge;
//                    // 动画结束才设置边框显示，
//                    // 是位了防止翻页从另一边跑出来的问题.
//                    bridge.setOnAnimatorListener(new OpenEffectBridge.NewAnimatorListener() {
//                        @Override
//                        public void onAnimationStart(OpenEffectBridge bridge, View view, Animator animation) {
//                        }
//
//                        @Override
//                        public void onAnimationEnd(OpenEffectBridge bridge1, View view, Animator animation) {
//                            if (mSavebridge == bridge1)
//                                bridge.setVisibleWidget(false);
//                        }
//                    });
//                    float scale = 1.05f;
//
//                    mainUpView.setFocusView(newFocus, mOldFocus, scale);
//                }
//                mOldFocus = newFocus;
//            }
//        });

    }

    private void initSearchBtn() {
//        mStatusBar.setBtnSearchVisibility(false);
//        mStatusBar.getBtnSearchView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        mStatusBar.setLogoVisibilty(true);
    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return mShowCategory.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_top, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(mShowCategory[position % mShowCategory.length]);
            int padding = dipToPix(10);
            textView.setPadding(padding, 0, padding, 0);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
//            MoreFragment fragment = new MoreFragment();
//            Bundle bundle = new Bundle();
//            bundle.putInt(MoreFragment.INTENT_INT_INDEX, position);
//            fragment.setArguments(bundle);
//            TVFragment fragment = new TVFragment();
            LazyFragment fragment = FragmentFactory.createFragment(position);
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
            // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
            return PagerAdapter.POSITION_NONE;
        }

    }

    /**
     * 根据dip值转化成px值
     *
     * @param dip
     * @return
     */
    private int dipToPix(float dip) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
        return size;
    }
}
