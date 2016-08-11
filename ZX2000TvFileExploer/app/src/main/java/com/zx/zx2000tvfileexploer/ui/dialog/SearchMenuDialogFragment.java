package com.zx.zx2000tvfileexploer.ui.dialog;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.interfaces.IMenuItemSelectListener;
import com.zx.zx2000tvfileexploer.ui.base.BaseMenuDialogFragment;

/**
 * Created by ShaudXiao on 2016/7/22.
 */
public class SearchMenuDialogFragment extends BaseMenuDialogFragment {

    public SearchMenuDialogFragment(IMenuItemSelectListener listener) {
        super(listener);
    }

    @Override
    public int getLayoutId() {
        return R.layout.search_menu_grid_layout;
    }

    @Override
    public void init() {
        mRootView.findViewById(R.id.menu_filter_all).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_filter_video).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_filter_music).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_filter_picture).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_filter_apk).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_cancle).setOnClickListener(this);
    }

}
