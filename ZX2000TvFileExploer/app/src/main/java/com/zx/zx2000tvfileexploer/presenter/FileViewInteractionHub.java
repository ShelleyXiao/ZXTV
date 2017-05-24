package com.zx.zx2000tvfileexploer.presenter;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.zx.zx2000tvfileexploer.FileManagerApplication;
import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.entity.Operation;
import com.zx.zx2000tvfileexploer.fileutil.CopyHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileCategoryHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileOperationHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileSettingsHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileSortHelper;
import com.zx.zx2000tvfileexploer.fileutil.IntentBuilder;
import com.zx.zx2000tvfileexploer.fileutil.service.CopyFileCheck;
import com.zx.zx2000tvfileexploer.interfaces.IFileInteractionListener;
import com.zx.zx2000tvfileexploer.interfaces.IOperationProgressListener;
import com.zx.zx2000tvfileexploer.ui.FileListActivity;
import com.zx.zx2000tvfileexploer.ui.dialog.CreateDirectoryDialog;
import com.zx.zx2000tvfileexploer.ui.dialog.MultiDeleteDialog;
import com.zx.zx2000tvfileexploer.ui.dialog.ProgressUpdateDialog;
import com.zx.zx2000tvfileexploer.ui.dialog.RenameDialog;
import com.zx.zx2000tvfileexploer.ui.dialog.SingleDeleteDialog;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ShaudXiao on 2016/7/18.
 */
public class FileViewInteractionHub implements IOperationProgressListener {

    private static final String LOG_TAG = "FileInteractionHub";

    private String createoutputImageName;

    private IFileInteractionListener mFileInteractionListener;

    private ArrayList<FileInfo> mCheckedFileNameList = new ArrayList<FileInfo>();

    private FileOperationHelper mFileOperationHelper;

    private FileSortHelper mFileSortHelper;

    private ProgressDialog progressDialog;

    private Context mContext;

    // File List view setup
    private GridView mFileListView;

    private String mCurrentPath;

    private String mRootPath;

    public enum Mode {
        View, Pick
    }

    ;

    private Mode mCurrentMode;


    public FileViewInteractionHub(IFileInteractionListener fileInteractionListener) {
        assert (fileInteractionListener != null);

        mFileInteractionListener = fileInteractionListener;
        mContext = mFileInteractionListener.getContext();

        mFileSortHelper = FileSortHelper.getInstance(FileSettingsHelper.getInstance(mContext));
        mFileOperationHelper = new FileOperationHelper(this, mContext);
        setup();
    }

    private void setup() {
        setupFileListView();
    }

    private void setupFileListView() {
        mFileListView = (GridView) mFileInteractionListener
                .getViewById(R.id.file_path_list);

        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onListItemClick(parent, view, position, id);
            }
        });

        mFileListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                if (mCurrentMode != Mode.Pick) {
                    setMode(Mode.Pick);
//                    refreshFileList();

                    FileInfo lFileInfo = mFileInteractionListener.getItem(position);

                    if (lFileInfo == null) {
                        Logger.getLogger().e("file does not exist on position:" + position);
                        return true;
                    }

                    if (isInSelection() && !((FileManagerApplication) getActivity().getApplication()).getCopyHelper().isCoping()) {
                        boolean selected = lFileInfo.Selected;
                        ImageView checkBox = (ImageView) view
                                .findViewById(R.id.file_checkbox);
                        Logger.getLogger().i("checkBox >>>>>>>> " + checkBox);
                        if (selected) {
                            mCheckedFileNameList.remove(lFileInfo);
                            checkBox.setImageResource(R.drawable.check);
                        } else {
                            mCheckedFileNameList.add(lFileInfo);
                            checkBox.setImageResource(R.drawable.checked);
                        }
                        Logger.getLogger().e("Select file mode = " + lFileInfo.getMode());
                        lFileInfo.Selected = !selected;
                        getActivity().updateSelectInfo(mCheckedFileNameList.size());
                    }
                }
                mFileInteractionListener.notifyUpdateListUI();

                return true;
            }
        });
    }


    public void onListItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
        FileInfo lFileInfo = mFileInteractionListener.getItem(position);

        if (lFileInfo == null) {
            Logger.getLogger().e("file does not exist on position:" + position);
            return;
        }

        Logger.getLogger().d("onListItemClick "
                + ((FileManagerApplication) getActivity().getApplication()).getCopyHelper().isCoping()
                + " " + isInSelection() + " " + mCurrentMode);

        if (isInSelection() && !((FileManagerApplication) getActivity().getApplication()).getCopyHelper().isCoping()) {
            boolean selected = lFileInfo.Selected;
            ImageView checkBox = (ImageView) view
                    .findViewById(R.id.file_checkbox);
            if (selected) {
                mCheckedFileNameList.remove(lFileInfo);
                checkBox.setImageResource(R.drawable.check);
            } else {
                mCheckedFileNameList.add(lFileInfo);
                checkBox.setImageResource(R.drawable.checked);
            }
            Logger.getLogger().e("Select file mode = " + lFileInfo.getMode());
            lFileInfo.Selected = !selected;
            getActivity().updateSelectInfo(mCheckedFileNameList.size());
            return;
        }

        if (!lFileInfo.IsDir) {
            if (mCurrentMode != Mode.Pick) {
                viewFile(lFileInfo);
            }
            return;
        }

        mCurrentPath = getAbsoluteName(mCurrentPath, lFileInfo.fileName);

//        setCurrentPath(getAbsoluteName(mCurrentPath, lFileInfo.fileName));
        ((FileListActivity) mFileInteractionListener).setCurPath(mCurrentPath);

        refreshFileList();
    }

    public void refreshFileList() {
        mFileInteractionListener.onRefreshFileList(mCurrentPath,
                mFileSortHelper);
    }

    // check or uncheck
    public boolean onCheckItem(FileInfo f, View v) {
        switch (v.getId()) {
            case R.id.file_checkbox_area: {
                if (f.Selected) {
                    mCheckedFileNameList.add(f);
                } else {
                    mCheckedFileNameList.remove(f);
                }
            }
            break;

            default:
                break;
        }

        return true;
    }

    private void viewFile(FileInfo lFileInfo) {
        try {
            FileCategoryHelper.FileCategory category = ((FileListActivity) mFileInteractionListener).getCurrentCategory();

            IntentBuilder.viewFile(mContext, lFileInfo.filePath, category);


        } catch (ActivityNotFoundException e) {
            Logger.getLogger().e("fail to view file: " + e.toString());
        }
    }

    public boolean isAllSelection() {
        return mCheckedFileNameList.size() == mFileInteractionListener
                .getAllFiles().size();
    }

    public boolean isInSelection() {
        return mCurrentMode == Mode.Pick || mCheckedFileNameList.size() > 0;
    }

    private String getAbsoluteName(String path, String name) {
        return path.equals(GlobalConsts.ROOT_PATH) ? path + name : path
                + File.separator + name;
    }

    public ArrayList<FileInfo> getSelectedFileList() {
        return new ArrayList<>(mCheckedFileNameList);
    }

    public void setRootPath(String path) {
        mRootPath = path;
        mCurrentPath = path;
    }

    public String getRootPath() {
        return mRootPath;
    }

    public String getCurrentPath() {
        return mCurrentPath;
    }

    public void setCurrentPath(String path) {
        mCurrentPath = path;
        ((FileListActivity) mFileInteractionListener).setCurPath(mCurrentPath);
    }

    public void setMode(Mode mode) {
        mCurrentMode = mode;
    }

    public Mode getMode() {
        return mCurrentMode;
    }

    public boolean canShowSelectBg() {
        return mCurrentMode == Mode.Pick;
    }

    public FileInfo getItem(int pos) {
        return mFileInteractionListener.getItem(pos);
    }

    @Override
    public void onOperationFinish(boolean sucess) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        mFileInteractionListener.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                clearSelection();
                refreshFileList();
            }
        });
    }

    @Override
    public void onFileChanged(String path) {
        notifyFileSystemChanged(path);
    }

    private void notifyFileSystemChanged(String path) {
        if (path == null)
            return;

        // MediaScannerConnection.scanFile(mContext, new String[] { path },
        // null, new OnScanCompletedListener() {
        //
        // @Override
        // public void onScanCompleted(String path, Uri uri) {
        // // TODO Auto-generated method stub
        // //mFileInteractionListener.updateMediaData();
        // Logger.getLogger.d(LOG_TAG, "notifyFileSystemChanged");
        // }
        // });

        final File f = new File(path);
        final Intent intent;
        if (f.isDirectory()) {
            intent = new Intent(GlobalConsts.FILEUPDATEBROADCAST);

        } else {
            intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(new File(path)));

        }
        mContext.sendBroadcast(intent);
    }

    public boolean onOperationUpLevel() {
        Logger.getLogger().d("mRootPath = " + mRootPath + "mCurrentPath " + mCurrentPath);
        if (!mRootPath.equals(mCurrentPath)
                && !mRootPath.contains(mCurrentPath)) {
            setCurrentPath(new File(mCurrentPath).getParent());
            refreshFileList();
            return true;
        }

        return false;
    }


    public boolean onBackPressed() {
        Logger.getLogger().d("******onBackPressed******");
        if (isInSelection() || getMode() == Mode.Pick
                && !((FileManagerApplication) getActivity().getApplication()).getCopyHelper().isCoping()) {
            setMode(Mode.View);
            clearSelection();
        } else if (!onOperationUpLevel()) {
            return false;
        }
        return true;
    }

    public FileListActivity getActivity() {
        return (FileListActivity) mFileInteractionListener;
    }

    public void norSlection() {
        for (FileInfo f : mFileInteractionListener.getAllFiles()) {
            if (f.Selected == true) {
                f.Selected = true;
                mCheckedFileNameList.add(f);
            } else if (mCheckedFileNameList.indexOf(f) > 0) {
                f.Selected = false;
                mCheckedFileNameList.remove(f);
            }
        }
        getActivity().updateSelectInfo(mCheckedFileNameList.size());
    }

    public void clearSelection() {
        if (mCheckedFileNameList.size() > 0) {
            for (FileInfo f : mCheckedFileNameList) {
                if (f == null) {
                    continue;
                }
                f.Selected = false;
            }
            mCheckedFileNameList.clear();

        }
        getActivity().updateSelectInfo(mCheckedFileNameList.size());
        mFileInteractionListener.notifyUpdateListUI();
    }

    public void onOperationSelectAll() {
        mCheckedFileNameList.clear();
        for (FileInfo f : mFileInteractionListener.getAllFiles()) {
            f.Selected = true;
            mCheckedFileNameList.add(f);
        }

        mFileInteractionListener.onDataChanged();
        getActivity().updateSelectInfo(mCheckedFileNameList.size());
    }

    public void onOperationDelete() {
        doOperationDelete(getSelectedFileList());
    }

    private void doOperationDelete(final ArrayList<FileInfo> selectedFileList) {
        final ArrayList<FileInfo> selectedFiles = new ArrayList<FileInfo>(
                selectedFileList);
        DialogFragment dialog = null;
        Bundle args = new Bundle();
        if(mCurrentMode == Mode.View) {
            long index = mFileListView.getSelectedItemId();
            FileInfo fileInfo = mFileInteractionListener.getItem((int)index);
            selectedFiles.add(fileInfo);
        }

        if (selectedFiles.size() == 0) {
            Toast.makeText(mContext, R.string.nothing_to_delete, Toast.LENGTH_LONG).show();
            return;
        }
        Logger.getLogger().d("selectedFiles size = " + selectedFiles.size()
            + " " + selectedFiles.get(0).getFilePath());
        if (selectedFiles.size() > 1) {

            dialog = new MultiDeleteDialog();

            args.putParcelableArrayList(GlobalConsts.EXTRA_DIALOG_FILE_HOLDER, new ArrayList<Parcelable>(selectedFiles));
            dialog.setArguments(args);
            dialog.show(getActivity().getFragmentManager(), MultiDeleteDialog.class.getName());

        } else if (selectedFiles.size() == 1) {
            dialog = new SingleDeleteDialog();
            args.putParcelable(GlobalConsts.EXTRA_DIALOG_FILE_HOLDER, selectedFiles.get(0));
            dialog.setArguments(args);
            dialog.show(getActivity().getFragmentManager(), SingleDeleteDialog.class.getName());
        }


        setMode(Mode.View);
        clearSelection();
    }

    public void onOperationCopy() {
        CopyHelper helper = ((FileManagerApplication) getActivity().getApplication()).getCopyHelper();
        if(mCurrentMode == Mode.View) {

            long index = mFileListView.getSelectedItemId();
            FileInfo fileInfo = mFileInteractionListener.getItem((int)index);
            ArrayList<FileInfo> selects = new ArrayList<>();
            selects.add(fileInfo);

            helper.MOVE_PATH = null;
            helper.COPY_PATH = selects;
            helper.operation = Operation.Copy;

            Logger.getLogger().i("***********onOperationCopy***********" + fileInfo.getFilePath());

        } else {
            final ArrayList<FileInfo> copies = getSelectedFileList();
            mCheckedFileNameList.clear();

            if (copies.size() > 0) {
                helper.MOVE_PATH = null;
                helper.COPY_PATH = copies;
                helper.operation = Operation.Copy;

                for (int i = 0; i < helper.COPY_PATH.size(); i++) {
                    Logger.getLogger().i("helper.COPY_PATH  " + helper.COPY_PATH.get(i).getFilePath());
                }
                Logger.getLogger().i("helper.COPY_PATH " + helper.COPY_PATH.size());

//            ((FileManagerApplication) getActivity().getApplication()).getCopyHelper().copy(copies);

            } else {
                Toast.makeText(mContext, mContext.getString(R.string.copy_no_selection_msg), Toast.LENGTH_LONG).show();
            }

            setMode(Mode.View);
            clearSelection();
        }

    }

    public void onOperationPaste() {
        CopyHelper helper = ((FileManagerApplication) getActivity().getApplication()).getCopyHelper();

        if (helper.isCopying()) {

            Logger.getLogger().i("paster start *************");
            String path = mCurrentPath;
            ArrayList<FileInfo> arrayList = helper.COPY_PATH != null ? helper.COPY_PATH : helper.MOVE_PATH;
            boolean move = helper.MOVE_PATH != null;

            new CopyFileCheck(path, move, mContext, false, new IOperationProgressListener() {
                @Override
                public void onOperationFinish(boolean success) {
                    if (success) {
                        Logger.getLogger().i("**********show ProgressUpdateDialog****************");
                        ProgressUpdateDialog dialog = new ProgressUpdateDialog();
                        dialog.show(((FileListActivity) mFileInteractionListener).getFragmentManager(), ProgressUpdateDialog.class.getName());
                        CopyHelper helper = ((FileManagerApplication) getActivity().getApplication()).getCopyHelper();
                    }
                }

                @Override
                public void onFileChanged(String path) {

                }

            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arrayList);

            helper.COPY_PATH = null;
            helper.MOVE_PATH = null;
            helper.operation = Operation.Unkonw;
        }
    }

    public void onOperationMove() {

        ArrayList<FileInfo> copies = null;
        CopyHelper helper = ((FileManagerApplication) getActivity().getApplication()).getCopyHelper();
        if(mCurrentMode == Mode.View) {
            copies = new ArrayList<>();
            long index = mFileListView.getSelectedItemId();
            FileInfo fileInfo = mFileInteractionListener.getItem((int)index);
            copies.add(fileInfo);
        } else {
            copies = getSelectedFileList();
        }

        mCheckedFileNameList.clear();
        if (copies.size() > 0) {
            helper.MOVE_PATH = copies;

            helper.COPY_PATH = null;
            helper.operation = Operation.Cut;
            for (int i = 0; i < helper.MOVE_PATH.size(); i++) {
                Logger.getLogger().i("helper.MOVE_PATH  " + helper.MOVE_PATH.get(i).getFilePath());
            }
            Logger.getLogger().i("helper.MOVE_PATH " + helper.MOVE_PATH.size());

//            ((FileManagerApplication) getActivity().getApplication()).getCopyHelper().copy(copies);

        } else {
            Toast.makeText(mContext, mContext.getString(R.string.copy_no_selection_msg), Toast.LENGTH_LONG).show();
        }

        setMode(Mode.View);
        clearSelection();
    }


    public void onOperationRename() {
//        if (getSelectedFileList().size() > 1) {
//            Toast.makeText(getActivity(), getActivity().getString(R.string.wrong_rename_msg), Toast.LENGTH_LONG).show();
//            return;
//        }

        long index = mFileListView.getSelectedItemId();
        FileInfo lFileInfo = mFileInteractionListener.getItem((int)index);
        Logger.getLogger().d("Selected id = " + lFileInfo.getFilePath());
        if(lFileInfo != null) {
            DialogFragment dialog = new RenameDialog();
            Bundle args = new Bundle();
            args.putParcelable(GlobalConsts.EXTRA_DIALOG_FILE_HOLDER, lFileInfo);
            dialog.setArguments(args);
            dialog.show(getActivity().getFragmentManager(), RenameDialog.class.getName());
        }



    }

    public void onOperationNewFile() {

//        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
//        View view = layoutInflater.inflate(R.layout.dialog_list_layout, null);
//        ListView fileListView = (ListView) view.findViewById(R.id.listView);
//
//        List<FileIcon> fileIconList = new ArrayList<FileIcon>();
//        FileIcon fileIcon1 = new FileIcon("�ļ���", R.drawable.ic_folder_new);
//        fileIconList.add(fileIcon1);
//        FileIcon fileIcon2 = new FileIcon("����", R.drawable.ic_takephoto_new);
//        fileIconList.add(fileIcon2);
//
//        ArrayAdapter<FileIcon> fileAdapter = new CreateFileListAdater(mContext,
//                R.layout.create_file_list_item, fileIconList);
//        fileListView.setAdapter(fileAdapter);
//
//        final Dialog dialog = new CustomDialog.Builder(mContext)
//                .setTitle(R.string.create).setContentView(view)
//                .setNegativeButton(R.string.cancel, new OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//                        dialog.dismiss();
//                    }
//                }).create();
//        dialog.show();
//
//        fileListView.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                // TODO Auto-generated method stub
//                dialog.dismiss();
//                if (position == 0) {
//                    // �����ļ���
//                    createFloder();
//                } else if (position == 1) {
//                    // ����
//                    createTakePhoto();
//                }
//            }
//        });

    }

    public void onOperationCreateFloder() {
        CreateDirectoryDialog dialog = new CreateDirectoryDialog();
//        dialog.setTargetFragment(this, 0);
        Bundle args = new Bundle();
        args.putString(GlobalConsts.EXTRA_DIR_PATH, getCurrentPath());
        dialog.setArguments(args);
        dialog.show(((FileListActivity) mFileInteractionListener).getFragmentManager(), CreateDirectoryDialog.class.getName());
    }

}
