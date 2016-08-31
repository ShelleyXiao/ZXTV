package com.zx.zx2000tvfileexploer.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zx.zx2000tvfileexploer.FileManagerApplication;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.view.FileViewStatusTitleView;

/**
 * Created by ShaudXiao on 2016/7/18.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    protected FileViewStatusTitleView mFileViewStatusTitleView;

    protected TextView tvPath;

    protected TextView tvFileNums;

    protected FrameLayout etSearchView;
    protected EditText mSearchEditText;
    protected TextView tvSearchHint;

    protected View mRootView;

    protected String mCurPath;

    public Context context;


    /**
     * 获取全局的Context
     *
     * @return {@link #context = this.getApplicationContext();}
     */
    public Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        mRootView = LayoutInflater.from(this).inflate(getLayoutResId(), null);
        setContentView(mRootView);
        initStatusBarView();
        init();

        initReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    protected void initStatusBarView() {
        mFileViewStatusTitleView = (FileViewStatusTitleView)findViewById(R.id.title_bar);
        mFileViewStatusTitleView.findViewById(R.id.back).setOnClickListener(this);
        tvPath = (TextView) mFileViewStatusTitleView.findViewById(R.id.title_path);
        tvFileNums = (TextView) mFileViewStatusTitleView.findViewById(R.id.title_file_num);

        etSearchView = (FrameLayout) mFileViewStatusTitleView.findViewById(R.id.serarh_input);
        mSearchEditText = (EditText) mFileViewStatusTitleView.findViewById(R.id.input);
        tvSearchHint = (TextView)  mFileViewStatusTitleView.findViewById(R.id.hint);
    }

    public abstract int getLayoutResId();

    public abstract void init();

    public abstract void updateDiskInfo();

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.back) {
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(0, R.anim.slide_bottom_out);
    }

    public void setCurPath(String curPath) {
        if(!TextUtils.isEmpty(curPath)) {
            mCurPath = curPath;
        }

        tvPath.setText(mCurPath);

    }

    protected void setFileNums(String nums) {
        tvFileNums.setText(nums + " " + getString(R.string.file_nums));
    }

    protected void showToast(String msg) {
        if(!TextUtils.isEmpty(msg)) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    protected void searchInputViewVisiblity(boolean show) {
        View view = mFileViewStatusTitleView.findViewById(R.id.serarh_input);
        if(null != view) {
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        view = mFileViewStatusTitleView.findViewById(R.id.search_category);
        if(null != view) {
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addDataScheme("file");
        this.registerReceiver(mReceiver, intentFilter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            FileManagerApplication.getInstance().setStorageInfo();
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
                || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
                    ||action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDiskInfo();
                    }
                });
            }
        }
    };
}
