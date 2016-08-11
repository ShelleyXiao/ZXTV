package com.zx.zx2000tvfileexploer.ui.base;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.zx.zx2000tvfileexploer.interfaces.IMenuItemSelectListener;
import com.zx.zx2000tvfileexploer.utils.Logger;

/**
 * Created by ShaudXiao on 2016/7/22.
 */
public class BaseMenuDialogFragment extends DialogFragment implements View.OnClickListener  {

    protected View mRootView;
    private IMenuItemSelectListener mIMenuItemSelectListener;

    public enum MenuMode {
        Normal, Edit
    }

    private MenuMode mCurMode;

    public BaseMenuDialogFragment() {}

    public BaseMenuDialogFragment(IMenuItemSelectListener listener) {
        this.mIMenuItemSelectListener = listener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mRootView = inflater.inflate(getLayoutId(), container);

        return mRootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onClick(View view) {
        if(mIMenuItemSelectListener != null) {
            mIMenuItemSelectListener.menuItemSelected(view.getId());
        }

        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if(activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    protected int getLayoutId() {
        return -1;
    }

    protected void init() {

    }

    public void setChildvisibility(int resId, boolean show) {
        View view = mRootView.findViewById(resId);
        if(null != view) {
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void setChildEnable(int resId, boolean enable) {
        View view = mRootView.findViewById(resId);
        if(null != view) {
            view.setFocusable(enable ? true : false);
//            view.setFocusableInTouchMode(enable ? true : false);
            view.setEnabled(enable ? true : false);

            Logger.getLogger().d("setChildEnable ----- " + resId);
        }
    }

}
