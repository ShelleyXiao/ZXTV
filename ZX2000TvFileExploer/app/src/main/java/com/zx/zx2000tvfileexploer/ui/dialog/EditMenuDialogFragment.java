package com.zx.zx2000tvfileexploer.ui.dialog;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.interfaces.IMenuItemSelectListener;
import com.zx.zx2000tvfileexploer.ui.base.BaseMenuDialogFragment;
import com.zx.zx2000tvfileexploer.utils.Logger;

/**
 * Created by ShaudXiao on 2016/7/22.
 */
public class EditMenuDialogFragment extends BaseMenuDialogFragment {

    private boolean copying = false;
    private boolean moveing = false;

    public EditMenuDialogFragment(IMenuItemSelectListener listener) {
        super(listener);
    }

    @Override
    public int getLayoutId() {
        return R.layout.edit_menu_grid_layout;
    }

    @Override
    public void init() {

        mRootView.findViewById(R.id.menu_refresh).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_new_folder).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_copy).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_cancle).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_rename).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_select_all).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_select_all_cancle).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_paste).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_move).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_delete).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_copy_cancel).setOnClickListener(this);
        mRootView.findViewById(R.id.menu_move_cancel).setOnClickListener(this);

        if(isCopying()) {
            setMenuCoping();
        } else {
            setMenuMoving();
        }

    }

    public boolean isCopying() {
        return copying;
    }

    public void setCopying(boolean copying) {
        this.copying = copying;
    }

    public boolean isMoveing() {
        return moveing;
    }

    public void setMoveing(boolean moveing) {
        this.moveing = moveing;
    }

    private  void setMenuCoping() {
        Logger.getLogger().d("setMenuCoping ");
        if(isCopying()) {
            Logger.getLogger().d("setMenuCoping isCopying");
            setChildEnable(R.id.menu_refresh, false);
            setChildEnable(R.id.menu_new_folder, false);
            setChildEnable(R.id.menu_rename, false);
            setChildEnable(R.id.menu_select_all, false);
            setChildEnable(R.id.menu_select_all_cancle, false);
            setChildEnable(R.id.menu_move, false);
            setChildEnable(R.id.menu_delete, false);

            setChildEnable(R.id.menu_paste, true);

            setChildvisibility(R.id.menu_copy, false);
            setChildvisibility(R.id.menu_copy_cancel, true);

        } else {
            setChildEnable(R.id.menu_refresh, true);
            setChildEnable(R.id.menu_new_folder, true);
            setChildEnable(R.id.menu_rename, true);
            setChildEnable(R.id.menu_select_all, true);
            setChildEnable(R.id.menu_select_all_cancle, true);
            setChildEnable(R.id.menu_move, true);
            setChildEnable(R.id.menu_delete, true);

            setChildEnable(R.id.menu_paste, false);

            setChildvisibility(R.id.menu_copy, true);
            setChildvisibility(R.id.menu_copy_cancel, false);
        }

    }

    private void setMenuMoving() {
        if(isMoveing()) {
            setChildEnable(R.id.menu_refresh, false);
            setChildEnable(R.id.menu_new_folder, false);
            setChildEnable(R.id.menu_rename, false);
            setChildEnable(R.id.menu_select_all, false);
            setChildEnable(R.id.menu_select_all_cancle, false);
            setChildEnable(R.id.menu_delete, false);
            setChildEnable(R.id.menu_copy, false);

            setChildEnable(R.id.menu_paste, true);

            setChildvisibility(R.id.menu_move, false);
            setChildvisibility(R.id.menu_move_cancel, true);

        } else {
            setChildEnable(R.id.menu_refresh, true);
            setChildEnable(R.id.menu_new_folder, true);
            setChildEnable(R.id.menu_rename, true);
            setChildEnable(R.id.menu_select_all, true);
            setChildEnable(R.id.menu_select_all_cancle, true);
            setChildEnable(R.id.menu_delete, true);
            setChildEnable(R.id.menu_copy, true);

            setChildEnable(R.id.menu_paste, false);

            setChildvisibility(R.id.menu_move, true);
            setChildvisibility(R.id.menu_move_cancel, false);
        }
    }

}
