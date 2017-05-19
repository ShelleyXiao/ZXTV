package com.zx.zx2000tvfileexploer.fileutil;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.service.DeleteTask;
import com.zx.zx2000tvfileexploer.interfaces.IOperationProgressListener;
import com.zx.zx2000tvfileexploer.utils.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class FileOperationHelper {

    private static final String LOG_TAG = "FileOperation";

    private ArrayList<FileInfo> mCurFileNameList = new ArrayList<FileInfo>();

    private IOperationProgressListener moperationListener;

    private ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

    private FilenameFilter mFilter = null;

    private Context mContext;

    private boolean mMoving;



    public FileOperationHelper(IOperationProgressListener l, Context context) {
        moperationListener = l;
        mContext = context;
    }

    public void setFilenameFilter(FilenameFilter f) {
        mFilter = f;
    }

    public void Copy(ArrayList<FileInfo> files) {
        copyFileList(files);
    }

    public void clear() {
        synchronized (mCurFileNameList) {
            mCurFileNameList.clear();
        }
    }


    private void asnycExecute(Runnable r) {
        final Runnable _r = r;
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                synchronized (mCurFileNameList) {
                    _r.run();
                }

                try {
                    mContext.getContentResolver().applyBatch(
                            MediaStore.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

                if (moperationListener != null) {
                    moperationListener.onOperationFinish(true);
                }
                ops.clear();
                return null;
            }
        }.execute();
    }


    public  boolean createFolder(String path, String name) {
        File f = new File(FileUtils.makePath(path, name));
        if (f.exists())
            return false;

        return f.mkdir();
    }

    public void deleteFiles(ArrayList<FileInfo> files) {
        if (files == null) return;

        int mode = checkFolder(new File(files.get(0).getFilePath()).getParentFile(), mContext);
        if (mode == 2) {
//            mainActivity.oparrayList = (files);
//            mainActivity.operation = DataUtils.DELETE;
        } else if (mode == 1 || mode == 0)
            new DeleteTask(null, mContext).execute((files));
        else {
            Toast.makeText(mContext, R.string.not_allowed, Toast.LENGTH_SHORT).show();
        }
    }


    public boolean Rename(FileInfo f, String newName) {
        if (f == null || newName == null) {
            return false;
        }

        File file = new File(f.filePath);
        String newPath = FileUtils.makePath(
                FileUtils.getPathFromFilepath(f.filePath), newName);
        final boolean needScan = file.isFile();
        try {
            boolean ret = file.renameTo(new File(newPath));
            if (ret) {
                if (needScan) {
                    moperationListener.onFileChanged(f.filePath);
                }
                moperationListener.onFileChanged(newPath);
            }
            return ret;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void StartMove(ArrayList<FileInfo> files) {
        if (mMoving)
            return;

        mMoving = true;
        copyFileList(files);
    }

    public boolean isMoveState() {
        return mMoving;
    }


    public boolean EndMove(String path) {
        if (!mMoving)
            return false;
        mMoving = false;

        if (TextUtils.isEmpty(path))
            return false;

        final String _path = path;
        asnycExecute(new Runnable() {
            @Override
            public void run() {
                for (FileInfo f : mCurFileNameList) {
                    MoveFile(f, _path);
                }

//                moperationListener.onFileChanged(FileUtil.getSdDirectory());

                clear();
            }
        });

        return true;
    }


    public boolean Paste(String path) {
        if (mCurFileNameList.size() == 0)
            return false;

        final String _path = path;
        asnycExecute(new Runnable() {
            @Override
            public void run() {
                for (FileInfo f : mCurFileNameList) {
                    CopyFile(f, _path);
                }

//                moperationListener.onFileChanged(FileUtil.getSdDirectory());

                clear();
            }
        });

        return true;
    }

    public boolean canPaste() {
        return mCurFileNameList.size() != 0;
    }

    private void CopyFile(FileInfo f, String dest) {
        if (f == null || dest == null) {
            return;
        }

        File file = new File(f.filePath);
        if (file.isDirectory()) {

//            // directory exists in destination, rename it
//            String destPath = FileUtil.makePath(dest, f.fileName);
//            File destFile = new File(destPath);
//            int i = 1;
//            while (destFile.exists()) {
//                destPath = FileUtil.makePath(dest, f.fileName + " " + i++);
//                destFile = new File(destPath);
//            }
//
//            for (File child : file.listFiles(mFilter)) {
//                if (!child.isHidden()
//                        && FileUtil.isNormalFile(child.getAbsolutePath())) {
//                    CopyFile(FileUtil.GetFileInfo(child, mFilter, false),
//                            destPath);
//                }
//            }
        } else {

//            String destFile = FileUtil.copyFile(f.filePath, dest);
//
//            FileInfo destFileInfo = FileUtil.GetFileInfo(destFile);
//            ops.add(ContentProviderOperation
//                    .newInsert(
//                            FileUtil.getMediaUriFromFilename(destFileInfo.fileName))
//                    .withValue(FileColumns.TITLE, destFileInfo.fileName)
//                    .withValue(FileColumns.DATA, destFileInfo.filePath)
//                    .withValue(
//                            FileColumns.MIME_TYPE,
//                            FileUtil.getMimetypeFromFilename(destFileInfo.fileName))
//                    .withValue(FileColumns.DATE_MODIFIED,
//                            destFileInfo.ModifiedDate)
//                    .withValue(FileColumns.SIZE, destFileInfo.fileSize).build());
        }

    }

    private boolean MoveFile(FileInfo f, String dest) {

        if (f == null || dest == null) {
            return false;
        }

        File file = new File(f.filePath);
        String newPath = FileUtils.makePath(dest, f.fileName);
        try {

            File destFile = new File(newPath);

            if (file.renameTo(destFile)) {
                MoveFileToDB(destFile, file, destFile);
                return true;
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void MoveFileToDB(File destFile, File srcFile, File rootFile) {

        if (destFile.isDirectory()) {
            for (File child : destFile.listFiles(mFilter)) {
                if (!child.isHidden()
                        && FileUtils.isNormalFile(child.getAbsolutePath())) {
                    MoveFileToDB(destFile, srcFile, rootFile);
                }
            }
        } else {
            int pos = -1;
            String destFilePath = destFile.getAbsolutePath();
            String srcFilePath = srcFile.getAbsolutePath();
            String rootFilePath = rootFile.getAbsolutePath();
            if (srcFile.isDirectory()
                    && (pos = destFilePath.indexOf(rootFilePath)) != -1) {
                srcFilePath = srcFilePath
                        + destFilePath.substring(rootFilePath.length(),
                        destFilePath.length());
            }

            FileInfo desFileInfo = FileUtils.getFileInfo(destFilePath, FileSettingsHelper.getInstance(mContext).getBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, false));
            ops.add(ContentProviderOperation
                    .newUpdate(
                            FileUtils.getMediaUriFromFilename(desFileInfo.fileName))
                    .withSelection("_data = ?",
                            new String[]{srcFilePath})
                    .withValue("_data", destFilePath).build());
        }

    }

    private void copyFileList(ArrayList<FileInfo> files) {
        synchronized (mCurFileNameList) {
            mCurFileNameList.clear();
            for (FileInfo f : files) {
                mCurFileNameList.add(f);
            }
        }
    }


    public static final int DOESNT_EXIST = 0; // 不存在
    public static final int WRITABLE_OR_ON_SDCARD = 1; // 在SD卡上讀寫
    //For Android 5
    public static final int CAN_CREATE_FILES = 2;

    public static int checkFolder(final File folder, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (FileUtil.isOnExtSdCard(folder, context)) {
                if (!folder.exists() || !folder.isDirectory()) {
                    return DOESNT_EXIST;
                }

                // On Android 5, trigger storage access framework.
                if (!FileUtil.isWritableNormalOrSaf(folder, context)) {
                    guideDialogForLEXA(context, folder.getPath());
                    return CAN_CREATE_FILES;
                }

                return WRITABLE_OR_ON_SDCARD;
            } else if (FileUtil.isWritable(new File(folder, "DummyFile"))) {
                return WRITABLE_OR_ON_SDCARD;
            } else return DOESNT_EXIST;
        } else if (Build.VERSION.SDK_INT == 19) {
            if (FileUtil.isOnExtSdCard(folder, context)) {
                // Assume that Kitkat workaround works
                return WRITABLE_OR_ON_SDCARD;
            } else if (FileUtil.isWritable(new File(folder, "DummyFile"))) {
                return WRITABLE_OR_ON_SDCARD;
            } else return DOESNT_EXIST;
        } else if (FileUtil.isWritable(new File(folder, "DummyFile"))) {
            return WRITABLE_OR_ON_SDCARD;
        } else {
            return DOESNT_EXIST;
        }
    }

    public static void guideDialogForLEXA(final Context context, String path) {
        final MaterialDialog.Builder x = new MaterialDialog.Builder(context);
        x.title(R.string.needsaccess);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.lexadrawer, null);
        x.customView(view, true);
        // textView
        TextView textView = (TextView) view.findViewById(R.id.description);
        textView.setText(context.getResources().getString(R.string.needsaccesssummary) + path + context.getResources().getString(R.string.needsaccesssummary1));
        ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.sd_operate_step);
        x.positiveText(R.string.open);
        x.negativeText(R.string.cancle);
        x.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog materialDialog) {
//                triggerStorageAccessFramework();
            }

            @Override
            public void onNegative(MaterialDialog materialDialog) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
        final MaterialDialog y = x.build();
        y.show();
    }

}
