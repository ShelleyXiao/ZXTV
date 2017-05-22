package com.zx.zx2000tvfileexploer.ui;

import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.adapter.SearchFileCursorAdaper;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.FileCategoryHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileIconHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileSortHelper;
import com.zx.zx2000tvfileexploer.fileutil.IntentBuilder;
import com.zx.zx2000tvfileexploer.interfaces.IFileInteractionListener;
import com.zx.zx2000tvfileexploer.interfaces.IMenuItemSelectListener;
import com.zx.zx2000tvfileexploer.ui.base.BaseFileOperationActivity;
import com.zx.zx2000tvfileexploer.ui.dialog.SearchMenuDialogFragment;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by ShaudXiao on 2016/7/27.
 */
public class SearchActivity extends BaseFileOperationActivity implements AdapterView.OnItemClickListener
        , IFileInteractionListener, IMenuItemSelectListener, DialogInterface.OnDismissListener {

    private GridView mFilePathGridView;
    private View mEmptyView;
    private ProgressBar mRefreshProgress;
    private TextView tvSelectInfo;
    private TextView tvSearchInputHint;
    private TextView tvSearchCategory;

    private FrameLayout mInputLayout;

    private FileCategoryHelper mFileCagetoryHelper;
    private FileIconHelper mFileIconHelper;
    private SearchFileCursorAdaper mAdapter;
    private SearchFileTask mSearchFileTask;
    private static String[] arraryFilterStrings;
    private String mCurrentFilter;
    private String mSearchFileName;

    private SearchMenuDialogFragment mSearchMenuDialog;


    private HashMap<String, FileCategoryHelper.FileCategory> filterTypeMap = new HashMap<String, FileCategoryHelper.FileCategory>();

    @Override
    protected int getLayoutId() {
        return R.layout.search_activity;
    }

    @Override
    protected void setupViews() {
        super.setupViews();

    }

    @Override
    protected void initialized() {
        super.initialized();
        initView();
        initData();

        setCurPath(getString(R.string.search_title));

        mSearchEditText.postDelayed(new SearchAction(this), 100);
    }

    @Override
    public void updateDiskInfo() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (parent.getAdapter() == mAdapter) {
            FileInfo fileInfo = getItem(position);
            if (!fileInfo.IsDir) {
                try {
                    IntentBuilder.viewFile(this, fileInfo.filePath, mFileCagetoryHelper.getCurFileCategory());
                } catch (ActivityNotFoundException e) {
                    Logger.getLogger().d("fail to view file: " + e.toString());
                }
            } else {
                Intent intent = new Intent(SearchActivity.this, FileListActivity.class);
                intent.putExtra("category", GlobalConsts.INTENT_EXTRA_ALL_VLAUE);
                intent.putExtra("path", fileInfo.filePath);
                startActivity(intent);
            }
        }
    }

    private void initView() {
        mFilePathGridView = (GridView) findViewById(R.id.file_path_list);
        mEmptyView = findViewById(R.id.empty_view_layout);
        mRefreshProgress = (ProgressBar) findViewById(R.id.refresh_progress);

        mInputLayout = (FrameLayout) findViewById(R.id.serarh_input);
        tvSearchInputHint = (TextView) findViewById(R.id.empty_view_hint);

        tvSearchCategory = (TextView) findViewById(R.id.search_category);

        mSearchEditText.addTextChangedListener(new SearchTextWatcher(this));
        mSearchEditText.setOnFocusChangeListener(new SearchFocusChange(this));

        showEmptyView(false);
        showProgressBar(false);
        searchInputViewVisiblity(true);
    }

    private void initData() {
        mFileCagetoryHelper = new FileCategoryHelper(this);
        mFileIconHelper = new FileIconHelper(this);

        mAdapter = new SearchFileCursorAdaper(this, null, mFileIconHelper);
        mFilePathGridView.setAdapter(mAdapter);
        mFilePathGridView.setOnItemClickListener(this);

        arraryFilterStrings = getResources().getStringArray(
                R.array.filter_category);

        filterTypeMap.put(arraryFilterStrings[0], FileCategoryHelper.FileCategory.All);
        filterTypeMap.put(arraryFilterStrings[1], FileCategoryHelper.FileCategory.MUSIC);
        filterTypeMap.put(arraryFilterStrings[2], FileCategoryHelper.FileCategory.VIDEO);
        filterTypeMap.put(arraryFilterStrings[3], FileCategoryHelper.FileCategory.PICTURE);
        filterTypeMap.put(arraryFilterStrings[4], FileCategoryHelper.FileCategory.APK);
        mCurrentFilter = arraryFilterStrings[0];

        tvSearchCategory.setText(mCurrentFilter);
    }

    private void showEmptyView(boolean show) {
        if (null != mEmptyView) {
            mEmptyView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFilePathGridView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showProgressBar(boolean able) {
        if (null != mRefreshProgress) {
            if (able) {
                mRefreshProgress.setVisibility(View.VISIBLE);
            } else {
                mRefreshProgress.setVisibility(View.INVISIBLE);
//                showEmptyView(mFileNameList.size() == 0);
            }
        }

    }

    private void showNormalMenuDialog() {
        Logger.getLogger().d("****showNormalMenuDialog****");
        FragmentTransaction mFragTransaction = getFragmentManager().beginTransaction();
        mSearchMenuDialog = (SearchMenuDialogFragment) getFragmentManager().findFragmentByTag(SearchMenuDialogFragment.class.getSimpleName());
        if (mSearchMenuDialog != null) {
            mFragTransaction.remove(mSearchMenuDialog);
            mSearchMenuDialog = null;
        }
        mSearchMenuDialog = new SearchMenuDialogFragment(this);
        mSearchMenuDialog.show(getFragmentManager(), SearchMenuDialogFragment.class.getSimpleName());
    }

    private void onStartSearchFile(String filename) {

        mSearchFileName = filename;
        if (TextUtils.isEmpty(mSearchFileName)) {
            mAdapter.changeCursor(null);
            return;
        }

        if (mSearchFileTask != null
                && mSearchFileTask.getStatus() != AsyncTask.Status.FINISHED) {
            mSearchFileTask.cancel(true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        mSearchFileTask = new SearchFileTask();
        mSearchFileTask.execute(filterTypeMap.get(mCurrentFilter));
    }

    private void onEndSearchFile(Cursor c) {
        showProgressBar(false);
        mAdapter.changeCursor(c);
        showEmptyView(c == null || c.getCount() == 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mSearchMenuDialog != null && mSearchMenuDialog.getShowsDialog()) {
                mSearchMenuDialog.dismiss();
                return true;
            } else {
                finish();
                return true;

            }
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
                showNormalMenuDialog();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void menuItemSelected(int id) {
        switch (id) {
            case R.id.menu_filter_all:
                mCurrentFilter = arraryFilterStrings[0];
                break;
            case R.id.menu_filter_music:
                mCurrentFilter = arraryFilterStrings[1];
                break;
            case R.id.menu_filter_video:
                mCurrentFilter = arraryFilterStrings[2];
                break;
            case R.id.menu_filter_picture:
                mCurrentFilter = arraryFilterStrings[3];
                break;
            case R.id.menu_filter_apk:
                mCurrentFilter = arraryFilterStrings[4];
                break;
            default:
                break;
        }

    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Logger.getLogger().d("mFilter = " + mCurrentFilter);
        if (!TextUtils.isEmpty(mSearchFileName)) {
            onStartSearchFile(mSearchFileName);
        }
        tvSearchCategory.setText(mCurrentFilter);
        FragmentTransaction mFragTransaction = getFragmentManager().beginTransaction();
        mSearchMenuDialog = (SearchMenuDialogFragment) getFragmentManager().findFragmentByTag(SearchMenuDialogFragment.class.getSimpleName());
        if (mSearchMenuDialog != null) {
            mFragTransaction.remove(mSearchMenuDialog);
            mSearchMenuDialog = null;
        }
    }

    @Override
    public View getViewById(int id) {
        return null;
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public FileInfo getItem(int pos) {
        return mAdapter.getFileItem(pos);
    }

    @Override
    public void onPick(FileInfo f) {

    }

    @Override
    public void onDataChanged() {

    }

    @Override
    public void sortCurrentList(FileSortHelper sort) {

    }

    @Override
    public boolean onRefreshFileList(String path, FileSortHelper sort) {
        return false;
    }

    @Override
    public Collection<FileInfo> getAllFiles() {
        return null;
    }

    @Override
    public void addSingleFile(FileInfo file) {

    }

    @Override
    public void ShowMovingOperationBar(boolean isShow) {

    }

    @Override
    public void updateMediaData() {

    }

    @Override
    public void updateMenuItem(int resId, boolean isShow) {

    }

    @Override
    public FileCategoryHelper.FileCategory getCurrentCategory() {
        return null;
    }

    @Override
    public void notifyUpdateListUI() {
    }

    class SearchTextWatcher implements TextWatcher {

        private Context mContext;

        public SearchTextWatcher(SearchActivity searchActivity) {
            mContext = searchActivity;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub

            mSearchEditText.setSelection(s.toString().length());

            if (s.toString().equals("")) {
                tvSearchHint.setVisibility(View.VISIBLE);
                tvSearchInputHint.setText(R.string.search_info);
                showEmptyView(true);
//                mbtnClean.setVisibility(View.GONE);
//                mvoiceLayout.setVisibility(View.VISIBLE);
//                mbtnVoice.setVisibility(View.VISIBLE);

            } else {
                tvSearchHint.setVisibility(View.GONE);
                showEmptyView(false);
                tvSearchInputHint.setText(R.string.no_file);
//                mbtnClean.setVisibility(View.VISIBLE);
//                mvoiceLayout.setVisibility(View.GONE);
//                mbtnVoice.setVisibility(View.GONE);

            }

            //
            if (!s.equals(mSearchFileName)) {
                onStartSearchFile(s.toString());
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

    }

    class SearchFocusChange implements View.OnFocusChangeListener {

        public SearchFocusChange(SearchActivity searchActivity) {
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            if (hasFocus) {
                mInputLayout
                        .setBackgroundResource(R.drawable.textfield_search_selected);

            } else {
                mInputLayout
                        .setBackgroundResource(R.drawable.textfield_search_default);
            }
        }

    }

    class SearchAction implements Runnable {

        public SearchAction(SearchActivity searchActivity) {
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            InputMethodManager inputMethodManager = (InputMethodManager) mSearchEditText
                    .getContext().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(mSearchEditText, 0);
            mSearchEditText.setFocusableInTouchMode(true);

        }

    }

    private class SearchFileTask extends
            AsyncTask<FileCategoryHelper.FileCategory, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            showProgressBar(true);
            showEmptyView(false);
        }


        @Override
        protected Cursor doInBackground(FileCategoryHelper.FileCategory... params) {
            Cursor cursor = null;
            if (!isCancelled())
                cursor = mFileCagetoryHelper.query(params[0], mSearchFileName, FileSortHelper.SortMethod.NAME);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            // TODO Auto-generated method stub
            if (!isCancelled())
                onEndSearchFile(result);
        }

    }
}
