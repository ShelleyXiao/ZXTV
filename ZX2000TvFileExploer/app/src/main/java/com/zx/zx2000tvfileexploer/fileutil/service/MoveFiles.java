package com.zx.zx2000tvfileexploer.fileutil.service;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.entity.OpenMode;
import com.zx.zx2000tvfileexploer.fileutil.FileUtil;
import com.zx.zx2000tvfileexploer.fileutil.ServiceWatcherUtil;
import com.zx.zx2000tvfileexploer.interfaces.IOperationProgressListener;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.io.File;
import java.util.ArrayList;


public class MoveFiles extends AsyncTask<ArrayList<String>, Void, Boolean> {
    private ArrayList<ArrayList<FileInfo>> files;
    private ArrayList<String> paths;
    private Context context;
    private OpenMode mode;
    private String currentPath;

    private IOperationProgressListener mProgressListener;

    public MoveFiles(ArrayList<ArrayList<FileInfo>> files, Context context, OpenMode mode, String currentPath,
                     IOperationProgressListener listener) {
        this.context = context;
        this.files = files;
        this.mode = mode;
        this.currentPath = currentPath;
        this.mProgressListener = listener;
    }

    @Override
    protected Boolean doInBackground(ArrayList<String>... strings) {
        paths = strings[0];

        if (files.size() == 0) return true;

        switch (mode) {
            case FILE:
                for (int i = 0; i < paths.size(); i++) {
                    for (FileInfo f : files.get(i)) {
                        File dest = new File(paths.get(i) + "/" + f.getName());
                        File source = new File(f.getFilePath());
                        if (!source.renameTo(dest)) {

                            return false;
                        }
                    }
                }
                break;

            default:
                return false;
        }

        return true;
    }

    @Override
    public void onPostExecute(Boolean movedCorrectly) {
        Logger.getLogger().i("movedCorrectly " + movedCorrectly
                + " paths.get(0) " + paths.get(0));
        if (movedCorrectly) {
            if (this.currentPath != null && this.currentPath.equals(paths.get(0))) {
                Intent intent = new Intent("loadlist");
                context.sendBroadcast(intent);
            }

            for (int i = 0; i < paths.size(); i++) {
                for (FileInfo f : files.get(i)) {
                    FileUtil.scanFile(f.getFilePath(), context);
                    FileUtil.scanFile(paths.get(i) + "/" + f.getName(), context);
                }
            }

        } else {
            for (int i = 0; i < paths.size(); i++) {
                Intent intent = new Intent(context, CopyService.class);
                intent.putExtra(CopyService.TAG_COPY_SOURCES, files.get(i));
                intent.putExtra(CopyService.TAG_COPY_TARGET, paths.get(i));
                intent.putExtra(CopyService.TAG_COPY_MOVE, true);
                intent.putExtra(CopyService.TAG_COPY_OPEN_MODE, mode.ordinal());

                ServiceWatcherUtil.runService(context, intent);
                if (null != mProgressListener) {
                    mProgressListener.onOperationFinish(true);
                }
            }
        }
    }
}
