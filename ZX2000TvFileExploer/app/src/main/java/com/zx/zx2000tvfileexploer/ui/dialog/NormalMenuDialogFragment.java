package com.zx.zx2000tvfileexploer.ui.dialog;

import android.view.View;

import com.zx.zx2000tvfileexploer.FileManagerApplication;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.interfaces.IMenuItemSelectListener;
import com.zx.zx2000tvfileexploer.ui.base.BaseMenuDialogFragment;

/**
 * Created by ShaudXiao on 2016/7/22.
 */

@SuppressWarnings("ValidFragment")
public class NormalMenuDialogFragment extends BaseMenuDialogFragment {

    private boolean normal = true;

    public NormalMenuDialogFragment(IMenuItemSelectListener listener) {
        super(listener);
    }

    @Override
    public int getLayoutId() {
        return R.layout.menu_grid_layout;
    }

    @Override
    public void init() {

        mRootView.findViewById(R.id.menu_refresh).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_select).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_new_folder).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_new_file).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_cancle).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_rename).setOnClickListener(this);

        if(FileManagerApplication.getInstance().getCopyHelper().isCopying()) {
            mRootView.findViewById(R.id.normal_menu_paste).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.normal_menu_copy_cancel).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.normal_menu_paste).setOnClickListener(this);
            mRootView.findViewById(R.id.normal_menu_copy_cancel).setOnClickListener(this);
        }

        if(!normal) {
            mRootView.findViewById(R.id.menu_new_folder).setVisibility(View.GONE);
        }

    }

    public void setNormal(boolean normal) {
        this.normal = normal;
    }

}
