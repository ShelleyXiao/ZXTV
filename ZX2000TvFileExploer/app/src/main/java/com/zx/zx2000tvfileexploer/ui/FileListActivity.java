package com.zx.zx2000tvfileexploer.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zx.zx2000tvfileexploer.FileManagerApplication;
import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.adapter.FileListAdapter;
import com.zx.zx2000tvfileexploer.adapter.FileListCursorAdapter;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.CopyHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileCategoryHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileIconHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileSettingsHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileSortHelper;
import com.zx.zx2000tvfileexploer.interfaces.IFileInteractionListener;
import com.zx.zx2000tvfileexploer.interfaces.IMenuItemSelectListener;
import com.zx.zx2000tvfileexploer.mode.FileViewInteractionHub;
import com.zx.zx2000tvfileexploer.ui.base.BaseActivity;
import com.zx.zx2000tvfileexploer.ui.dialog.EditMenuDialogFragment;
import com.zx.zx2000tvfileexploer.ui.dialog.NormalMenuDialogFragment;
import com.zx.zx2000tvfileexploer.utils.FileUtils;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class FileListActivity extends BaseActivity implements IFileInteractionListener, IMenuItemSelectListener, DialogInterface.OnDismissListener {

    private GridView mFilePathGridView;
    private View mEmptyView;
    private ProgressBar mRefreshProgress;
    private TextView tvSelectInfo;

    private FileViewInteractionHub mFileViewInteractionHub;
    private FileIconHelper mFileIconHelper;
    private FileCategoryHelper mFileCagetoryHelper;
    private FileSortHelper mFileSortHelper;
    private FileSettingsHelper mFileSettingsHelper;

    private FileListCursorAdapter mFileListCursorAdapter;
    private FileListAdapter mFileListNormalAdapter;

    private FileCategoryHelper.FileCategory mCurrentCategory;

    private ArrayList<FileInfo> mFileNameList = new ArrayList<FileInfo>();


    private refreshFileAsyncTask mrefreshFileAsyncTask;

    private NormalMenuDialogFragment mNormalMenuDialog;
    private EditMenuDialogFragment mEditMenuDialog;


    private HashMap<String, FileCategoryHelper.FileCategory> filterTypeMap = new HashMap<String, FileCategoryHelper.FileCategory>();

    private FilenameFilter hideFileFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return !filename.startsWith(".");
        }
    };

    @Override
    public int getLayoutResId() {
        return R.layout.activity_file_list;
    }

    @Override
    public void init() {
        initDataCategory();
        initView();

        initData();
    }

    @Override
    public void updateDiskInfo() {

    }

    private void initView() {
        mFilePathGridView = (GridView) findViewById(R.id.file_path_list);
        mEmptyView = findViewById(R.id.empty_view_layout);
        mRefreshProgress = (ProgressBar) findViewById(R.id.refresh_progress);
        tvSelectInfo = (TextView) findViewById(R.id.select_promat_msg);
        updateSelectInfo(0);
    }

    private void initData() {
        mFileViewInteractionHub = new FileViewInteractionHub(this);
        mFileViewInteractionHub.setCurrentPath(mCurPath);
        mFileViewInteractionHub.setMode(FileViewInteractionHub.Mode.View);

//        mSettingHelper = SettingHelper.getInstance(this);

        mFileCagetoryHelper = new FileCategoryHelper(this);
        mFileIconHelper = new FileIconHelper(this);
        if (mCurrentCategory != FileCategoryHelper.FileCategory.All) {
            mFileListCursorAdapter = new FileListCursorAdapter(this, null, mFileViewInteractionHub, mFileIconHelper);
            mFilePathGridView.setAdapter(mFileListCursorAdapter);
        } else {
            mFileListNormalAdapter = new FileListAdapter(this, R.layout.list_grid_item_layout, mFileNameList, mFileViewInteractionHub, mFileIconHelper);
            mFilePathGridView.setAdapter(mFileListNormalAdapter);
        }


        onCategorySelected();
        mFileViewInteractionHub.setRootPath(mCurPath);

        mFileSettingsHelper = FileSettingsHelper.getInstance(this);
    }

    private void initDataCategory() {
        Intent intent = getIntent();
        int category = intent.getIntExtra("category", GlobalConsts.INTENT_EXTRA_ALL_VLAUE);
        switch (category) {
            case GlobalConsts.INTENT_EXTRA_APK_VLAUE:
                mCurrentCategory = FileCategoryHelper.FileCategory.APK;

                break;
            case GlobalConsts.INTENT_EXTRA_MUSIC_VLAUE:
                mCurrentCategory = FileCategoryHelper.FileCategory.MUSIC;
                break;
            case GlobalConsts.INTENT_EXTRA_PICTURE_VLAUE:
                mCurrentCategory = FileCategoryHelper.FileCategory.PICTURE;
                break;
            case GlobalConsts.INTENT_EXTRA_VIDEO_VLAUE:
                mCurrentCategory = FileCategoryHelper.FileCategory.VIDEO;
                break;
            default:
                mCurrentCategory = FileCategoryHelper.FileCategory.All;
                break;
        }

        String path = intent.getStringExtra("path");
        if (path != null) {
            mCurPath = path;
            Logger.getLogger().d("path: " + path);
        }
    }

    private void onCategorySelected() {
        mFileCagetoryHelper.setCurFileCategory(mCurrentCategory);
        if (mCurrentCategory != FileCategoryHelper.FileCategory.All) {
            mFileViewInteractionHub.setCurrentPath(mFileViewInteractionHub
                    .getRootPath()
                    + getString(mFileCagetoryHelper.getCurCategoryNameResId()));
            setCurPath(getString(mFileCagetoryHelper.getCurCategoryNameResId()));
        } else {
            mFileViewInteractionHub.setCurrentPath(mCurPath);
//            setCurPath(mCurPath);
        }

        mFileViewInteractionHub.refreshFileList();
    }

    @Override
    public View getViewById(int id) {
        return findViewById(id);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public FileInfo getItem(int pos) {
        if (mCurrentCategory == FileCategoryHelper.FileCategory.All) {
            if (pos < 0 || pos > mFileNameList.size() - 1) {
                return null;
            }
            return mFileNameList.get(pos);
        }
        return null;
    }

    @Override
    public void onPick(FileInfo f) {
        try {
            Intent intent = Intent.parseUri(Uri.fromFile(new File(f.filePath)).toString(), 0);
            this.setResult(Activity.RESULT_OK, intent);
            this.finish();
            return;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataChanged() {
        Logger.getLogger().d("*****onDataChanged*******");
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mCurrentCategory != FileCategoryHelper.FileCategory.All) {
                    mFileListCursorAdapter.notifyDataSetChanged();
                } else {
                    mFileListNormalAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void sortCurrentList(FileSortHelper sort) {
        Collections.sort(mFileNameList, sort.getComparator(mFileSettingsHelper.getSortType()));
        onDataChanged();
    }

    @Override
    public boolean onRefreshFileList(String path, FileSortHelper sort) {
        Logger.getLogger().d(" onRefreshFileList**************path " + path);
        mFileSortHelper = sort;

        if (mCurrentCategory != FileCategoryHelper.FileCategory.All) {

            FileCategoryHelper.FileCategory curCategoryType = mFileCagetoryHelper
                    .getCurFileCategory();


            Cursor c = mFileCagetoryHelper.query(curCategoryType,
                    sort.getSortMethod());

            setFileNums(String.valueOf(c.getCount()));
            showEmptyView(c == null || c.getCount() == 0);
//            showProgressBar(false);
            mRefreshProgress.setVisibility(View.GONE);

            mFileListCursorAdapter.changeCursor(c);
        } else {
            File file = new File(path);
            if (!file.exists() || !file.isDirectory()) {
                return false;
            }

            mFileSortHelper = sort;

            mrefreshFileAsyncTask = new refreshFileAsyncTask();
            mrefreshFileAsyncTask.execute(file);
            mCurPath = path;
            Logger.getLogger().d("ddddddddddddddddddddd");
            showProgressBar(true);


            return true;
        }

        return true;
    }

    @Override
    public Collection<FileInfo> getAllFiles() {
        if (mCurrentCategory != FileCategoryHelper.FileCategory.All) {
            return mFileListCursorAdapter.getAllFiles();
        } else {
            return mFileNameList;
        }
    }

    @Override
    public void addSingleFile(FileInfo file) {
        mFileNameList.add(file);
        onDataChanged();
    }

    @Override
    public void ShowMovingOperationBar(boolean isShow) {

    }

    @Override
    public void updateMediaData() {

    }

    @Override
    public void updateMenuItem(int resId, boolean isShow) {

        if (mFileViewInteractionHub.getMode() == FileViewInteractionHub.Mode.Pick) {
            if (mEditMenuDialog == null) {
                mEditMenuDialog = new EditMenuDialogFragment(this);
            }
            mEditMenuDialog.show(getFragmentManager(), "Menu");
        } else if (mFileViewInteractionHub.getMode() == FileViewInteractionHub.Mode.View) {
            if (mNormalMenuDialog == null) {
                mNormalMenuDialog = new NormalMenuDialogFragment(this);
            }
            mNormalMenuDialog.show(getFragmentManager(), "Menu");

        }
    }

    @Override
    public void menuItemSelected(int id) {
        switch (id) {
            case R.id.menu_refresh:
                mFileViewInteractionHub.refreshFileList();
                break;
            case R.id.menu_select:
                mFileViewInteractionHub.setMode(FileViewInteractionHub.Mode.Pick);
                mFileViewInteractionHub.refreshFileList();
                break;
            case R.id.menu_rename:
                mFileViewInteractionHub.onOperationRename();
                break;
            case R.id.menu_new_folder:
                mFileViewInteractionHub.createFloder();
                break;
            case R.id.menu_select_all:
                if (mFileViewInteractionHub.getMode() == FileViewInteractionHub.Mode.Pick) {
                    mFileViewInteractionHub.onOperationSelectAll();
                }
                break;
            case R.id.menu_select_all_cancle:
                if (mFileViewInteractionHub.getMode() == FileViewInteractionHub.Mode.Pick) {
                    if (mFileViewInteractionHub.isAllSelection()) {
                        mFileViewInteractionHub.clearSelection();
                    } else {
                        mFileViewInteractionHub.norSlection();
                    }
                }
                break;
            case R.id.menu_copy:
                if (mFileViewInteractionHub.getMode() == FileViewInteractionHub.Mode.Pick) {
                    mFileViewInteractionHub.onOperationCopy();
                }

                break;
            case R.id.menu_copy_cancel:
                mEditMenuDialog.setCopying(false);
                ((FileManagerApplication)getApplication()).getCopyHelper().clear();
                mFileViewInteractionHub.clearSelection();
                break;
            case R.id.menu_paste:
                if (mFileViewInteractionHub.getMode() == FileViewInteractionHub.Mode.Pick) {
                    mFileViewInteractionHub.onOperationPaste();
                }
//                ((FileManagerApplication)getApplication()).getCopyHelper().clear();
//                mFileViewInteractionHub.clearSelection();
                break;
            case R.id.menu_move:
                mFileViewInteractionHub.onOperationMove();
                break;
            case R.id.menu_move_cancel:
                mEditMenuDialog.setMoveing(false);
                ((FileManagerApplication)getApplication()).getCopyHelper().clear();
                mFileViewInteractionHub.clearSelection();
                break;
            case R.id.menu_delete:
                mFileViewInteractionHub.onOperationDelete();
                break;

        }
    }

    public void onRefresh() {
        mFileViewInteractionHub.refreshFileList();
    }

    public void updateSelectInfo(int selectedNums) {
        tvSelectInfo.setText(getString(R.string.multi_select_title, selectedNums));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mFileViewInteractionHub == null) {
                return false;
            }
            if (mEditMenuDialog != null && mEditMenuDialog.getShowsDialog()) {
                mEditMenuDialog.dismiss();
                Logger.getLogger().d("******onKeyDown****mEditMenuDialog***");
                return true;
            } else if (mNormalMenuDialog != null && mNormalMenuDialog.getShowsDialog()) {
                mNormalMenuDialog.dismiss();
                Logger.getLogger().d("******onKeyDown****mNormalMenuDialog***");
                return true;
            } else {
                Logger.getLogger().d("******onKeyDown********");
                boolean exitActivity = !mFileViewInteractionHub.onBackPressed();
                if(exitActivity) {
                    finish();
                } else {
                    return true;
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mFileViewInteractionHub.getMode() == FileViewInteractionHub.Mode.Pick) {
                mFileViewInteractionHub.setMode(FileViewInteractionHub.Mode.Pick);
//                mFileViewInteractionHub.refreshFileList();
                showEditMenuDialog();
            } else {
                showNormalMenuDialog();
            }
        }

        return super.onKeyDown(keyCode, event);
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
                mFilePathGridView.setVisibility(View.INVISIBLE);
            } else {
                mRefreshProgress.setVisibility(View.INVISIBLE);
                mFilePathGridView.setVisibility(View.VISIBLE);
                if (mCurrentCategory == FileCategoryHelper.FileCategory.All) {
                    sortCurrentList(mFileSortHelper);
                }
                showEmptyView(mFileNameList.size() == 0);
            }
        }

    }

    private void showNormalMenuDialog() {
        Logger.getLogger().d("****showNormalMenuDialog****");
        FragmentTransaction mFragTransaction = getFragmentManager().beginTransaction();
        mNormalMenuDialog = (NormalMenuDialogFragment) getFragmentManager().findFragmentByTag(NormalMenuDialogFragment.class.getSimpleName());
        if (mNormalMenuDialog != null) {
            mFragTransaction.remove(mNormalMenuDialog);
            mNormalMenuDialog = null;
        }
        mNormalMenuDialog = new NormalMenuDialogFragment(this);
        mNormalMenuDialog.show(getFragmentManager(), NormalMenuDialogFragment.class.getSimpleName());
    }

    private void showEditMenuDialog() {
        FragmentTransaction mFragTransaction = getFragmentManager().beginTransaction();
        mEditMenuDialog = (EditMenuDialogFragment) getFragmentManager().findFragmentByTag(EditMenuDialogFragment.class.getSimpleName());
        if (mEditMenuDialog != null) {
            mFragTransaction.remove(mEditMenuDialog);
            mEditMenuDialog = null;
        }
        mEditMenuDialog = new EditMenuDialogFragment(this);
        if(((FileManagerApplication)this.getApplication()).getCopyHelper().isCoping()) {
            if(((FileManagerApplication)getApplication()).getCopyHelper().getOperationType() == CopyHelper.Operation.Copy) {
                mEditMenuDialog.setCopying(true);
                Logger.getLogger().d(" showEditMenuDialog copy");
            }  else if(((FileManagerApplication)getApplication()).getCopyHelper().getOperationType() == CopyHelper.Operation.Cut) {
                mEditMenuDialog.setMoveing(true);
            }

        } else {
            mEditMenuDialog.setCopying(false);
            mEditMenuDialog.setMoveing(false);
        }
        mEditMenuDialog.show(getFragmentManager(), EditMenuDialogFragment.class.getSimpleName());

    }

    private void hideMenuDialog(DialogFragment dialogFragment) {
        FragmentTransaction mFragTransaction = getFragmentManager().beginTransaction();
        if (dialogFragment != null) {
            mFragTransaction.remove(dialogFragment);
            dialogFragment = null;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Logger.getLogger().d("onDismiss");
        FragmentTransaction mFragTransaction = getFragmentManager().beginTransaction();
        mEditMenuDialog = (EditMenuDialogFragment) getFragmentManager().findFragmentByTag(EditMenuDialogFragment.class.getSimpleName());
        if (mEditMenuDialog != null) {
            mFragTransaction.remove(mEditMenuDialog);
            mEditMenuDialog = null;
        }
        mNormalMenuDialog = (NormalMenuDialogFragment) getFragmentManager().findFragmentByTag(NormalMenuDialogFragment.class.getSimpleName());
        if (mNormalMenuDialog != null) {
            mFragTransaction.remove(mNormalMenuDialog);
            mNormalMenuDialog = null;
        }
    }

    public class refreshFileAsyncTask extends AsyncTask<File, Void, Integer> {

        @Override
        protected Integer doInBackground(File... files) {

            ArrayList<FileInfo> fileList = mFileNameList;
            fileList.clear();

            File[] listFiles = files[0].listFiles(mFileSettingsHelper.getBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, false) ? null : hideFileFilter);
            Logger.getLogger().e("refreshFileAsyncTask " + mFileCagetoryHelper
                    .getFilter());
            if (listFiles == null)
                return Integer.valueOf(0);

            for (File child : listFiles) {
                String absolutePath = child.getAbsolutePath();
                if (FileUtils.isNormalFile(absolutePath)) {
                    FileInfo lFileInfo = FileUtils.getFileInfo(child,/*
                            mFileCagetoryHelper.getFilter(), */mFileSettingsHelper.getBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, false));
                    if (lFileInfo != null) {
                        fileList.add(lFileInfo);
                    }
                }
            }
            return Integer.valueOf(fileList.size());
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            setFileNums(String.valueOf(result.intValue()));
            showProgressBar(false);
        }
    }

}
