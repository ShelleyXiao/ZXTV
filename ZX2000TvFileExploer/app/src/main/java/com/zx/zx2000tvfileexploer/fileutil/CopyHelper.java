package com.zx.zx2000tvfileexploer.fileutil;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.interfaces.IOperationProgressListener;
import com.zx.zx2000tvfileexploer.utils.FileUtils;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.zx.zx2000tvfileexploer.fileutil.CopyHelper.Operation.Copy;
import static com.zx.zx2000tvfileexploer.fileutil.CopyHelper.Operation.Cut;
import static com.zx.zx2000tvfileexploer.fileutil.CopyHelper.Operation.Unkonw;


public class CopyHelper {
    private static final int COPY_BUFFER_SIZE = 32 * 1024;

    private Context mContext;
    private List<FileInfo> mClipboard;
    private IOperationProgressListener mListener;

    public Operation operation = Unkonw;
    public ArrayList<FileInfo> oparrayList;
    public ArrayList<ArrayList<FileInfo>> oparrayListList;

    public ArrayList<FileInfo> COPY_PATH = null, MOVE_PATH = null;
    public String CURRENT_PATH = null;

    // oppathe - the path at which certain operation needs to be performed
    // oppathe1 - the new path which user wants to create/modify
    // oppathList - the paths at which certain operation needs to be performed (pairs with oparrayList)
    public String oppathe, oppathe1;
    public ArrayList<String> oppatheList;


    public enum Operation {
        Copy, Cut, Unkonw
    }

    public CopyHelper(Context c) {
        mContext = c;
    }

    public boolean isCopying() {
        return ((COPY_PATH != null && COPY_PATH.size() > 0) || (MOVE_PATH != null && MOVE_PATH.size() > 0))
                && (operation == Copy || operation == Cut);
    }

    public void copy(List<FileInfo> tbc) {
        operation = Operation.Copy;
        mClipboard = tbc;
//        mClipboard = new ArrayList<>(tbc);
//        Logger.getLogger().d("mClipboard " + mClipboard.size());
    }

    public void copy(FileInfo tbc) {
        ArrayList<FileInfo> tbcl = new ArrayList<>();
        tbcl.add(tbc);
        copy(tbcl);
    }

    public void cut(List<FileInfo> tbc) {
        operation = Operation.Cut;

        mClipboard = tbc;
    }

    public void clear() {
        operation = Unkonw;
        COPY_PATH = null;
    }

    /**
     * Call this to check whether there are file references on the clipboard.
     */
    public boolean canPaste() {
        return mClipboard != null && !mClipboard.isEmpty();
    }

    public Operation getOperationType() {
        return operation;
    }

    public boolean isCoping() {
        return operation == Operation.Copy || operation == Operation.Cut;
    }

    /**
     * Call this to actually copy.
     *
     * @param dest The path to copy the clipboard into.
     * @return false if ANY error has occurred. This may mean that some files have been successfully copied, but not all.
     */
    private boolean performCopy(File dest) {
        boolean res = true;
        Logger.getLogger().d("dest :" + dest.getAbsolutePath() + mClipboard.size());
        for (FileInfo fh : mClipboard) {
            if (fh.getFile().isFile())
                res &= copyFile(fh.getFile(), FileUtils.createUniqueCopyName(mContext, dest, fh.getFileName()));
            else
                res &= copyFolder(fh.getFile(), FileUtils.createUniqueCopyName(mContext, dest, fh.getFileName()));
        }

        return res;
    }

    /**
     * Copy a file.
     *
     * @param oldFile File to copy.
     * @param newFile The file to be created.
     * @return Was copy successful?
     */
    private boolean copyFile(File oldFile, File newFile) {
        Logger.getLogger().d("copyFile " + oldFile.getAbsolutePath());
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            input = new FileInputStream(oldFile);
            output = new FileOutputStream(newFile);

            byte[] buffer = new byte[COPY_BUFFER_SIZE];

            while (true) {
                int bytes = input.read(buffer);

                if (bytes <= 0) {
                    break;
                }

                output.write(buffer, 0, bytes);
            }

            // Request media scan
            MediaScannerUtils.informFileAdded(mContext, newFile);

        } catch (Exception e) {
            return false;
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                // ignore
            }
            try {
                output.close();
            } catch (IOException e) {
                // ignore
            }
        }
        return true;
    }

    /**
     * Recursively copy a folder.
     *
     * @param oldFile Folder to copy.
     * @param newFile The dir to be created.
     * @return Was copy successful?
     */
    private boolean copyFolder(File oldFile, File newFile) {
        boolean res = true;
        Logger.getLogger().d("copyFolder " + oldFile.getAbsolutePath());
        if (oldFile.isDirectory()) {
            // if directory not exists, create it
            if (!newFile.exists()) {
                newFile.mkdir();
                // System.out.println("Directory copied from " + src + "  to " + dest);
            }

            // list all the directory contents
            String[] files = oldFile.list();

            for (String file : files) {
                // construct the src and dest file structure
                File srcFile = new File(oldFile, file);
                File destFile = new File(newFile, file);
                // recursive copy
                res &= copyFolder(srcFile, destFile);
            }
        } else {
            res &= copyFile(oldFile, newFile);
        }

        return res;
    }

    /**
     * Call this to actually move.
     *
     * @param dest The path to move the clipboard into.
     * @return false if ANY error has occurred. This may mean that some files have been successfully moved, but not all.
     */
    private boolean performCut(File dest) {
        boolean res = true;
        boolean deleteOk;

        File from;
        for (FileInfo fh : mClipboard) {
            from = fh.getFile().getAbsoluteFile();

            deleteOk = fh.getFile().renameTo(FileUtils.getFile(dest, fh.getFileName()));

            // Inform media scanner
            if (deleteOk) {
                MediaScannerUtils.informFileDeleted(mContext, from);
                MediaScannerUtils.informFileAdded(mContext, FileUtils.getFile(dest, fh.getFileName()));
            }

            res &= deleteOk;
        }

        return res;
    }

    /**
     * Paste the copied/cut items.
     *
     * @param copyTo   Path to paste to.
     * @param listener Listener that will be informed on operation finish. CAN'T BE NULL.
     */
    public void paste(File copyTo, IOperationProgressListener listener) {
        mListener = listener;

        // Quick check just to make sure. Normally this should never be the case as the path we get is not user-generated.
        if (!copyTo.isDirectory())
            return;

        switch (operation) {
            case Copy:
                new CopyAsync().execute(copyTo);
                break;
            case Cut:
                new MoveAsync().execute(copyTo);
                break;
            default:
                return;
        }
    }

    private class CopyAsync extends AsyncTask<File, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(mContext, R.string.copying, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(File... params) {
            return performCopy(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Toast.makeText(mContext, result ? R.string.copied : R.string.copy_error, Toast.LENGTH_SHORT).show();

            // Clear as the references have been invalidated.
            mClipboard.clear();

            mListener.onOperationFinish(result);

            // Invalidate listener.
            mListener = null;
        }
    }

    private class MoveAsync extends AsyncTask<File, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(mContext, R.string.moving, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(File... params) {
            return performCut(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Toast.makeText(mContext, result ? R.string.moved : R.string.move_error, Toast.LENGTH_SHORT).show();

            // Clear as the references have been invalidated.
            mClipboard.clear();

            mListener.onOperationFinish(result);

            // Invalidate listener.
            mListener = null;
        }
    }

}