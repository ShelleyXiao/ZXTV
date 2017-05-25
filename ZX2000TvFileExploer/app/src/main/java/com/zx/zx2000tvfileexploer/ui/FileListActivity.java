package com.zx.zx2000tvfileexploer.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.zx.zx2000tvfileexploer.FileManagerApplication;
import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.adapter.FileListAdapter;
import com.zx.zx2000tvfileexploer.adapter.FileListCursorAdapter;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.entity.OpenMode;
import com.zx.zx2000tvfileexploer.entity.Operation;
import com.zx.zx2000tvfileexploer.fileutil.CopyHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileCategoryHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileIconHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileOperationHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileSettingsHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileSortHelper;
import com.zx.zx2000tvfileexploer.fileutil.RootHelper;
import com.zx.zx2000tvfileexploer.fileutil.ServiceWatcherUtil;
import com.zx.zx2000tvfileexploer.fileutil.service.CopyService;
import com.zx.zx2000tvfileexploer.fileutil.service.DeleteTask;
import com.zx.zx2000tvfileexploer.fileutil.service.MoveFiles;
import com.zx.zx2000tvfileexploer.interfaces.IFileInteractionListener;
import com.zx.zx2000tvfileexploer.interfaces.IMenuItemSelectListener;
import com.zx.zx2000tvfileexploer.presenter.FileViewInteractionHub;
import com.zx.zx2000tvfileexploer.ui.base.BaseFileOperationActivity;
import com.zx.zx2000tvfileexploer.ui.dialog.EditMenuDialogFragment;
import com.zx.zx2000tvfileexploer.ui.dialog.NormalMenuDialogFragment;
import com.zx.zx2000tvfileexploer.ui.dialog.ProgressUpdateDialog;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.zx.zx2000tvfileexploer.GlobalConsts.TAG_INTENT_FILTER_FAILED_OPS;

public class FileListActivity extends BaseFileOperationActivity implements IFileInteractionListener, IMenuItemSelectListener, DialogInterface.OnDismissListener {

    private GridView mFilePathGridView;
    private View mEmptyView;
    private ProgressBar mRefreshProgress;
    private TextView tvSelectInfo;

    private FileViewInteractionHub mFileViewInteractionHub;
    private FileIconHelper mFileIconHelper;
    private FileCategoryHelper mFileCagetoryHelper;
    private FileSortHelper mFileSortHelper;

    private FileListCursorAdapter mFileListCursorAdapter;
    private FileListAdapter mFileListNormalAdapter;

    private FileCategoryHelper.FileCategory mCurrentCategory;

    private ArrayList<FileInfo> mFileNameList = new ArrayList<FileInfo>();

    private refreshFileAsyncTask mrefreshFileAsyncTask;

    private NormalMenuDialogFragment mNormalMenuDialog;
    private EditMenuDialogFragment mEditMenuDialog;

    public static OpenMode mOpenMode = OpenMode.UNKNOWN;

    private LocalBroadcastManager mLocalBroadcastManager;

    private BroadcastReceiver receiver1 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.getLogger().w("receive: lexa");
            Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent1, 3);
        }
    };


    private BroadcastReceiver receiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.getLogger().w("receive: loadlist");

            clearCopyOpeartion();
            onRefresh();
        }
    };

    private BroadcastReceiver receiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            if (i.getStringArrayListExtra(TAG_INTENT_FILTER_FAILED_OPS) != null) {
                ArrayList<FileInfo> failedOps = i.getParcelableArrayListExtra(TAG_INTENT_FILTER_FAILED_OPS);
                if (failedOps != null) {
                    showFailedOperationDialog(failedOps, i.getBooleanExtra("move", false), FileListActivity.this);
                }
                clearCopyOpeartion();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        mLocalBroadcastManager.registerReceiver(receiver1, new IntentFilter(GlobalConsts.LAUNCHER_LEXA));
        mLocalBroadcastManager.registerReceiver(receiver2, new IntentFilter(GlobalConsts.LOAD_LIST));
        mLocalBroadcastManager.registerReceiver(receiver3, new IntentFilter(GlobalConsts.TAG_INTENT_FILTER_GENERAL));

    }

    @Override
    public void onPause() {
        super.onPause();
        mLocalBroadcastManager.unregisterReceiver(receiver1);
        mLocalBroadcastManager.unregisterReceiver(receiver2);
        mLocalBroadcastManager.unregisterReceiver(receiver3);

//        mFileIconHelper.stop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 3) {
            Uri treeUri;
            if (resultCode == Activity.RESULT_OK) {
                // Get Uri from Storage Access Framework.
                treeUri = intent.getData();
                Logger.getLogger().d("Uri: " + treeUri.toString());
                // Persist URI - this is required for verification of writability.
                if (treeUri != null)
                    PreferenceManager.getDefaultSharedPreferences(this).edit()
                            .putString("URI", treeUri.toString()).commit();
            } else {
                // If not confirmed SAF, or if still not writable, then revert settings.
                /* DialogUtil.displayError(getActivity(), R.string.message_dialog_cannot_write_to_folder_saf, false, currentFolder);
                        ||!FileUtil.isWritableNormalOrSaf(currentFolder)*/
                return;
            }

            // After confirmation, update stored value of folder.
            // Persist access permissions.
            final int takeFlags = intent.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
            Operation operation = FileManagerApplication.getInstance().getOperation();
            switch (operation) {
                case delete://deletion
                    new DeleteTask(null, this).execute(FileManagerApplication.getInstance().getOppatheList());
                    break;
                case Copy://copying
                    //legacy compatibility
                    CopyHelper helper = FileManagerApplication.getInstance().getCopyHelper();
                    if (helper.oparrayList != null && helper.oparrayList.size() != 0) {
                        helper.oparrayListList = new ArrayList<>();
                        helper.oparrayListList.add(helper.oparrayList);
                        helper.oparrayList = null;
                        helper.oppatheList = new ArrayList<>();
                        helper.oppatheList.add(helper.oppathe);
                        helper.oppathe = "";
                    }
                    for (int i = 0; i < helper.oparrayListList.size(); i++) {
                        Intent intent1 = new Intent(getContext(), CopyService.class);
                        intent1.putExtra(CopyService.TAG_COPY_SOURCES, helper.oparrayList.get(i));
                        intent1.putExtra(CopyService.TAG_COPY_TARGET, helper.oppatheList.get(i));
                        ServiceWatcherUtil.runService(this, intent1);
                    }
                    break;
                case Cut://moving
                    //legacy compatibility
                    CopyHelper helper1 = FileManagerApplication.getInstance().getCopyHelper();
                    if (helper1.oparrayList != null && helper1.oparrayList.size() != 0) {
                        helper1.oparrayListList = new ArrayList<>();
                        helper1.oparrayListList.add(helper1.oparrayList);
                        helper1.oparrayList = null;
                        helper1.oppatheList = new ArrayList<>();
                        helper1.oppatheList.add(helper1.oppathe);
                        helper1.oppathe = "";
                    }
                    Logger.getLogger().i("************** " +
                            helper1.oparrayListList.get(0).get(0));

                    new MoveFiles(helper1.oparrayListList, getContext(),
                            OpenMode.FILE, mFileViewInteractionHub.getCurrentPath(), null)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, helper1.oppatheList);
                    break;
                case mkdir://mkdir
                    FileOperationHelper.mkDir(RootHelper.generateBaseFile(new File(FileManagerApplication.getInstance().getOppathe()), true), this);
                    break;
                case rename:
                    FileOperationHelper.rename(mOpenMode,
                            FileManagerApplication.getInstance().getOppathe(),
                            FileManagerApplication.getInstance().getOppathe1(),
                            this,
                            false);
                    break;

            }
            FileManagerApplication.getInstance().setOperation(Operation.Unkonw);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_file_list;
    }

    @Override
    protected void setupViews() {
        super.setupViews();

    }

    @Override
    protected void initialized() {
        super.initialized();

        initDataCategory();
        initView();

        initData();

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
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
        mFileSortHelper = FileSortHelper.getInstance(FileSettingsHelper.getInstance(this));
        mFileViewInteractionHub = new FileViewInteractionHub(this);
        mFileViewInteractionHub.setCurrentPath(mCurPath);
        mFileViewInteractionHub.setMode(FileViewInteractionHub.Mode.View);

        mFileCagetoryHelper = new FileCategoryHelper(this);
        mFileIconHelper = new FileIconHelper(this);
        if (mCurrentCategory != FileCategoryHelper.FileCategory.All) {
            mFileListCursorAdapter = new FileListCursorAdapter(this, null, mFileViewInteractionHub, mFileIconHelper, OpenMode.CUSTOM);
            mFilePathGridView.setAdapter(mFileListCursorAdapter);
        } else {
            mFileListNormalAdapter = new FileListAdapter(this, R.layout.list_grid_item_layout, mFileNameList, mFileViewInteractionHub, mFileIconHelper);
            mFilePathGridView.setAdapter(mFileListNormalAdapter);
        }


        onCategorySelected();
        mFileViewInteractionHub.setRootPath(mCurPath);

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

//        mFileViewInteractionHub.refreshFileList();
        onRefresh();
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
    public FileCategoryHelper.FileCategory getCurrentCategory() {
        return mCurrentCategory;
    }

    @Override
    public FileInfo getItem(int pos) {
        if (mCurrentCategory == FileCategoryHelper.FileCategory.All) {
            if (pos < 0 || pos > mFileNameList.size() - 1) {
                return null;
            }
            return mFileNameList.get(pos);
        } else {
//            Object[] datas = getAllFiles().toArray();
            List<FileInfo> datas = getAllFiles();
            if (pos < 0 || pos > datas.size() - 1) {
                return null;
            }

            return datas.get(pos);
        }

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
        Collections.sort(mFileNameList, sort.getComparator(FileSettingsHelper.getInstance(getContext()).getSortType()));
        onDataChanged();
    }

    @Override
    public boolean onRefreshFileList(String path, FileSortHelper sort) {
        Logger.getLogger().d(" onRefreshFileList**************path " + path + " mCurrentCategory "
                + mCurrentCategory);
        mFileSortHelper = sort;

//        mFileIconHelper.stop();

        if (mCurrentCategory != FileCategoryHelper.FileCategory.All) {

            FileCategoryHelper.FileCategory curCategoryType = mFileCagetoryHelper
                    .getCurFileCategory();


            Cursor c = mFileCagetoryHelper.query(curCategoryType,
                    sort.getSortMethod());

            setFileNums(String.valueOf(c.getCount()));
            showEmptyView(c == null || c.getCount() == 0);
            mRefreshProgress.setVisibility(View.GONE);

            mFileListCursorAdapter.changeCursor(c);
            mOpenMode = OpenMode.CUSTOM;

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
    public List<FileInfo> getAllFiles() {
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
    public void notifyUpdateListUI() {
        if (mCurrentCategory != FileCategoryHelper.FileCategory.All) {
            mFileListCursorAdapter.notifyDataSetChanged();
        } else {
            mFileListNormalAdapter.notifyDataSetChanged();
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
                notifyUpdateListUI();

                break;
            case R.id.menu_rename:
                mFileViewInteractionHub.onOperationRename();
                FileManagerApplication.getInstance().setOperation(Operation.rename);
                break;
            case R.id.menu_new_folder:
                mFileViewInteractionHub.onOperationCreateFloder();
                FileManagerApplication.getInstance().setOperation(Operation.mkdir);
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
//                    mFileViewInteractionHub.onOperationCopy();
                }

                mFileViewInteractionHub.onOperationCopy();
                FileManagerApplication.getInstance().setOperation(Operation.Copy);
                break;
            case R.id.menu_copy_cancel:
                mEditMenuDialog.setCopying(false);
                ((FileManagerApplication) getApplication()).getCopyHelper().clear();
                mFileViewInteractionHub.clearSelection();
                break;
            case R.id.normal_menu_copy_cancel:
                ((FileManagerApplication) getApplication()).getCopyHelper().clear();
                mFileViewInteractionHub.clearSelection();
                break;
            case R.id.menu_paste:
                if (mFileViewInteractionHub.getMode() == FileViewInteractionHub.Mode.Pick) {
                    mFileViewInteractionHub.onOperationPaste();
                }
//                ((FileManagerApplication)getApplication()).getCopyHelper().clear();
//                mFileViewInteractionHub.clearSelection();
                break;
            case R.id.normal_menu_paste:
                if (mFileViewInteractionHub.getMode() == FileViewInteractionHub.Mode.Pick) {
//                    mFileViewInteractionHub.onOperationPaste();
                }

                mFileViewInteractionHub.onOperationPaste();

                break;
            case R.id.menu_move:
                mFileViewInteractionHub.onOperationMove();
                FileManagerApplication.getInstance().setOperation(Operation.Cut);
                break;
            case R.id.menu_move_cancel:
                mEditMenuDialog.setMoveing(false);
                ((FileManagerApplication) getApplication()).getCopyHelper().clear();
                mFileViewInteractionHub.clearSelection();
                break;
            case R.id.menu_delete:
                mFileViewInteractionHub.onOperationDelete();
                FileManagerApplication.getInstance().setOperation(Operation.delete);
                break;

        }
    }

    public void onRefresh() {
//        mFileViewInteractionHub.refreshFileList();
        onRefreshFileList(mCurPath, mFileSortHelper);
    }

    public void updateSelectInfo(int selectedNums) {
        tvSelectInfo.setText(getString(R.string.multi_select_title, selectedNums));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.getLogger().i("keyCode " + keyCode);
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
                if (exitActivity) {
                    finish();
                } else {
                    return true;
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_INFO) {
            Logger.getLogger().i("******************** KEYCODE_MENU");
            if (mFileViewInteractionHub.getMode() == FileViewInteractionHub.Mode.Pick) {
                mFileViewInteractionHub.setMode(FileViewInteractionHub.Mode.Pick);
                showEditMenuDialog();
            } else {
                showNormalMenuDialog();
            }

            return true;
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
//        Logger.getLogger().d("****showNormalMenuDialog****");
        FragmentTransaction mFragTransaction = getFragmentManager().beginTransaction();
        mNormalMenuDialog = (NormalMenuDialogFragment) getFragmentManager().findFragmentByTag(NormalMenuDialogFragment.class.getSimpleName());
        if (mNormalMenuDialog != null) {
            mFragTransaction.remove(mNormalMenuDialog);
            mNormalMenuDialog = null;
        }
        mNormalMenuDialog = new NormalMenuDialogFragment(this);
        if (mCurrentCategory != FileCategoryHelper.FileCategory.All) {
            mNormalMenuDialog.setNormal(false);
        } else {
            mNormalMenuDialog.setNormal(true);
        }
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
        if (((FileManagerApplication) this.getApplication()).getCopyHelper().isCoping()) {
            if (((FileManagerApplication) getApplication()).getCopyHelper().getOperationType() == Operation.Copy) {
                mEditMenuDialog.setCopying(true);
                Logger.getLogger().d(" showEditMenuDialog copy");
            } else if (((FileManagerApplication) getApplication()).getCopyHelper().getOperationType() == Operation.Cut) {
                mEditMenuDialog.setMoveing(true);
            }

        } else {
            mEditMenuDialog.setCopying(false);
            mEditMenuDialog.setMoveing(false);
        }

        if (mCurrentCategory != FileCategoryHelper.FileCategory.All) {
            mEditMenuDialog.setNormal(false);
        } else {
            mEditMenuDialog.setNormal(true);
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

    public void showFailedOperationDialog(ArrayList<FileInfo> failedOps, boolean move,
                                          Context contextc) {
        FragmentTransaction mFragTransaction = getFragmentManager().beginTransaction();
        ProgressUpdateDialog dialog = (ProgressUpdateDialog) getFragmentManager().findFragmentByTag(ProgressUpdateDialog.class.getName());
        if (null != dialog) {
            dialog.dismiss();
            mFragTransaction.remove(dialog);
        }

        MaterialDialog.Builder mat = new MaterialDialog.Builder(contextc);
        mat.title(contextc.getString(R.string.operationunsuccesful));
        mat.positiveColor(getResources().getColor(R.color.primary_pink));
        mat.positiveText(R.string.cancle);
        String content = contextc.getResources().getString(R.string.operation_fail_following);
        int k = 1;
        for (FileInfo s : failedOps) {
            content = content + "\n" + (k) + ". " + s.getName();
            k++;
        }
        mat.content(content);
        mat.build().show();
    }

    //防止出错后影响下步操作
    private void clearCopyOpeartion() {
        CopyHelper helper = FileManagerApplication.getInstance().getCopyHelper();
        if (helper.isCoping()) {
            helper.operation = Operation.Unkonw;
            helper.MOVE_PATH = null;
            helper.COPY_PATH = null;
        }

        helper.oparrayList = null;
        helper.oparrayListList = null;
    }

    public class refreshFileAsyncTask extends AsyncTask<File, Void, Integer> {

        @Override
        protected Integer doInBackground(File... files) {

//            ArrayList<FileInfo> fileList = mFileNameList;
//            fileList.clear();
//            File[] listFiles = files[0].listFiles(FileSettingsHelper.getInstance(getContext()).getBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, false) ? null : hideFileFilter);
//            if (listFiles == null)
//                return Integer.valueOf(0);
//
//            for (File child : listFiles) {
//                String absolutePath = child.getAbsolutePath();
//                if (FileUtils.isNormalFile(absolutePath)) {
//                    FileInfo lFileInfo = FileUtils.getFileInfo(child,/*
//                            mFileCagetoryHelper.getFilter(), */FileSettingsHelper.getInstance(getContext()).getBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, false));
//                    if (lFileInfo != null) {
//                        fileList.add(lFileInfo);
//                    }
//                }
//            }

            ArrayList<FileInfo> arrayList1;
            try {
                arrayList1 = RootHelper.getFilesList(files[0].getAbsolutePath(), false, FileSettingsHelper.getInstance(getContext()).getBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, false),
                        new RootHelper.GetModeCallBack() {

                            @Override
                            public void getMode(OpenMode mode) {
                                mOpenMode = mode;

                            }
                        });

            } catch (Exception e) {
                return null;
            }
//            Logger.getLogger().i("mOpenMode: " + mOpenMode.toString() +  "arrayList1 " + arrayList1.size() +
//            "name; " + arrayList1.get(0).getFileName());
            mFileNameList.clear();
            mFileNameList.addAll(arrayList1);

            return Integer.valueOf(arrayList1.size());
        }

        @Override
        protected void onPostExecute(Integer result) {
            setFileNums(String.valueOf(result.intValue()));
            showProgressBar(false);
        }
    }


}
