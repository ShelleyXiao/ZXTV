package com.zx.zx2000tvfileexploer.fileutil;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.zx.zx2000tvfileexploer.FileManagerApplication;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.entity.OpenMode;
import com.zx.zx2000tvfileexploer.entity.Operation;
import com.zx.zx2000tvfileexploer.fileutil.service.DeleteTask;
import com.zx.zx2000tvfileexploer.interfaces.IOperationProgressListener;
import com.zx.zx2000tvfileexploer.ui.FileListActivity;

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

    public static void deleteFiles(Context context, ArrayList<FileInfo> files) {
        if (files == null) return;

        int mode = checkFolder(new File(files.get(0).getFilePath()).getParentFile(), context);
        if (mode == 2) {
//            mainActivity.oparrayList = (files);
//            mainActivity.operation = DataUtils.DELETE;
            FileManagerApplication.getInstance().setOppatheList(files);
        } else if (mode == 1 || mode == 0)
            new DeleteTask(null, context).execute((files));
        else {
            Toast.makeText(context, R.string.not_allowed, Toast.LENGTH_SHORT).show();
        }
    }

    public static void rename(OpenMode mode, final String oldPath, final String newPath,
                       final Activity context, boolean rootmode) {
        final Toast toast = Toast.makeText(context, context.getString(R.string.rename),
                Toast.LENGTH_SHORT);
        toast.show();

        FileUtil.rename(new FileInfo(mode, oldPath), new FileInfo(mode, newPath), rootmode, context, new FileUtil.ErrorCallBack() {
            @Override
            public void exists(FileInfo file) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toast != null) toast.cancel();
                        Toast.makeText(context, context.getString(R.string.file_exists),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void launchSAF(FileInfo file) {

            }

            @Override
            public void launchSAF(final FileInfo file, final FileInfo file1) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toast != null) toast.cancel();

                        FileManagerApplication.getInstance().setOppathe(file.getFilePath());
                        FileManagerApplication.getInstance().setOppathe1(file1.getFilePath());
                        FileManagerApplication.getInstance().setOperation(Operation.rename);
                        FileUtil.guideDialogForLEXA(context, file1.getFilePath());
                    }
                });
            }

            @Override
            public void done(FileInfo hFile, final boolean b) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (b) {
                            ((FileListActivity) context).onRefresh();
                        } else {
                            Toast.makeText(context, context.getString(R.string.operationunsuccesful),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void invalidName(final FileInfo file) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (toast != null) toast.cancel();
                        Toast.makeText(context, context.getString(R.string.invalid_name) + ": "
                                + file.getName(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public static void mkDir(final FileInfo path, final Activity activity) {
        final Toast toast = Toast.makeText(activity, activity.getString(R.string.create_new_folder),
                Toast.LENGTH_SHORT);
        toast.show();
        FileUtil.mkdir(path, activity, false, new FileUtil.ErrorCallBack() {
            @Override
            public void exists(final FileInfo file) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toast != null) toast.cancel();
                        Toast.makeText(activity, activity.getString(R.string.file_exists),
                                Toast.LENGTH_SHORT).show();
                        if (activity != null) {
                            // retry with dialog prompted again
//                            mkdir(file.getMode(), file.getParent(), ma);
                        }
                    }
                });
            }

            @Override
            public void launchSAF(FileInfo file) {
                if (toast != null) toast.cancel();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FileManagerApplication.getInstance().setOppathe(path.getFilePath());
                        FileManagerApplication.getInstance().setOperation(Operation.mkdir);
                        FileUtil.guideDialogForLEXA(activity, path.getFilePath());
                    }
                });

            }

            @Override
            public void launchSAF(FileInfo file, FileInfo file1) {

            }

            @Override
            public void done(FileInfo hFile, final boolean b) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (b) {
                            ((FileListActivity) activity).onRefresh();
                        } else
                            Toast.makeText(activity, activity.getString(R.string.operationunsuccesful),
                                    Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void invalidName(final FileInfo file) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (toast != null) toast.cancel();
                        Toast.makeText(activity, activity.getString(R.string.invalid_name)
                                + ": " + file.getName(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

//    private boolean MoveFile(FileInfo f, String dest) {
//
//        if (f == null || dest == null) {
//            return false;
//        }
//
//        File file = new File(f.filePath);
//        String newPath = FileUtils.makePath(dest, f.fileName);
//        try {
//
//            File destFile = new File(newPath);
//
//            if (file.renameTo(destFile)) {
//                MoveFileToDB(destFile, file, destFile);
//                return true;
//            }
//
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    private void MoveFileToDB(File destFile, File srcFile, File rootFile) {
//
//        if (destFile.isDirectory()) {
//            for (File child : destFile.listFiles(mFilter)) {
//                if (!child.isHidden()
//                        && FileUtils.isNormalFile(child.getAbsolutePath())) {
//                    MoveFileToDB(destFile, srcFile, rootFile);
//                }
//            }
//        } else {
//            int pos = -1;
//            String destFilePath = destFile.getAbsolutePath();
//            String srcFilePath = srcFile.getAbsolutePath();
//            String rootFilePath = rootFile.getAbsolutePath();
//            if (srcFile.isDirectory()
//                    && (pos = destFilePath.indexOf(rootFilePath)) != -1) {
//                srcFilePath = srcFilePath
//                        + destFilePath.substring(rootFilePath.length(),
//                        destFilePath.length());
//            }
//
//            FileInfo desFileInfo = RootHelper.getFileInfo(destFilePath, FileSettingsHelper.getInstance(mContext).getBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, false));
//            ops.add(ContentProviderOperation
//                    .newUpdate(
//                            FileUtils.getMediaUriFromFilename(desFileInfo.fileName))
//                    .withSelection("_data = ?",
//                            new String[]{srcFilePath})
//                    .withValue("_data", destFilePath).build());
//        }
//
//    }
//
//    private void copyFileList(ArrayList<FileInfo> files) {
//        synchronized (mCurFileNameList) {
//            mCurFileNameList.clear();
//            for (FileInfo f : files) {
//                mCurFileNameList.add(f);
//            }
//        }
//    }


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
                    FileUtil.guideDialogForLEXA(context, folder.getPath());
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


}
