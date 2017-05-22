package com.zx.zx2000tvfileexploer.fileutil.service;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.zx.zx2000tvfileexploer.FileManagerApplication;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.entity.OpenMode;
import com.zx.zx2000tvfileexploer.fileutil.CopyHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileOperationHelper;
import com.zx.zx2000tvfileexploer.fileutil.ServiceWatcherUtil;
import com.zx.zx2000tvfileexploer.interfaces.IOperationProgressListener;
import com.zx.zx2000tvfileexploer.ui.FileListActivity;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by arpitkh996 on 12-01-2016, modified by Emmanuel Messulam<emmanuelbendavid@gmail.com>
 * <p>
 * This AsyncTask works by creating a tree where each folder that can be fusioned together with
 * another in the destination is a node (CopyNode), each node is copied when the conflicts are dealt
 * with (the dialog is shown, and the tree is walked via a BFS).
 * If the process is cancelled (via the button in the dialog) the dialog closes without any more code
 * to be executed, finishCopying() is never executed so no changes are made.
 */
public class CopyFileCheck extends AsyncTask<ArrayList<FileInfo>, String, CopyFileCheck.CopyNode> {

    private enum DO_FOR_ALL_ELEMENTS {
        DO_NOT_REPLACE,
        REPLACE
    }

    private String path;
    private Boolean move;
    private int counter = 0;
    private Context context;
    private boolean rootMode = false;
    private OpenMode openMode = OpenMode.FILE;
    private DO_FOR_ALL_ELEMENTS dialogState = null;

    //causes folder containing filesToCopy to be deleted
    private ArrayList<File> deleteCopiedFolder = null;
    private CopyNode copyFolder;
    private final ArrayList<String> paths = new ArrayList<>();
    private final ArrayList<ArrayList<FileInfo>> filesToCopyPerFolder = new ArrayList<>();
    private ArrayList<FileInfo> filesToCopy;    // a copy of params sent to this
    private IOperationProgressListener mProgressListener;

    public CopyFileCheck(String path, Boolean move, Context con, boolean rootMode, IOperationProgressListener listener) {
        this.move = move;
        context = con;
        openMode = FileListActivity.mOpenMode;
        this.rootMode = rootMode;

        this.path = path;

        mProgressListener = listener;
    }

    @Override
    public void onProgressUpdate(String... message) {
        Toast.makeText(context, message[0], Toast.LENGTH_LONG).show();
    }

    @Override
    protected CopyNode doInBackground(ArrayList<FileInfo>... params) {
        filesToCopy = params[0];
        Logger.getLogger().i("paster start ************* filesToCopy" + filesToCopy.size() + " " + filesToCopy.get(0).getFilePath()
         + " openmode " + openMode
        );
        long totalBytes = 0;

        if (openMode == OpenMode.OTG) {
            // no helper method for OTG to determine storage space
            return null;
        }

        for (int i = 0; i < filesToCopy.size(); i++) {
            FileInfo file = filesToCopy.get(i);

            if (file.isDir()) {
                totalBytes = totalBytes + file.folderSize(context);
            } else {
                totalBytes = totalBytes + file.length(context);
            }
        }

        Logger.getLogger().d("paster start ************* totalBytes" + totalBytes);

        FileInfo destination = new FileInfo(openMode, path);
        if (destination.getUsableSpace() < totalBytes) {
            publishProgress(context.getResources().getString(R.string.in_safe));
            Logger.getLogger().w("paster start ************* no space ");
            return null;
        }

        copyFolder = new CopyNode(path, filesToCopy);

        return copyFolder;
    }

    private ArrayList<FileInfo> checkConflicts(ArrayList<FileInfo> filesToCopy, FileInfo destination) {
        ArrayList<FileInfo> conflictingFiles = new ArrayList<>();

        for (FileInfo k1 : destination.listFiles(context, rootMode)) {
            for (FileInfo j : filesToCopy) {
                if (k1.getName().equals((j).getName())) {
                    conflictingFiles.add(j);
                }
            }
        }

        return conflictingFiles;
    }

    @Override
    protected void onPostExecute(CopyNode copyFolder) {
        super.onPostExecute(copyFolder);

        if (openMode == OpenMode.OTG) {

            startService(filesToCopy, path, openMode);
            mProgressListener.onOperationFinish(true);
        } else {

            onEndDialog(null, null, null);
        }
    }

    private void startService(ArrayList<FileInfo> sourceFiles, String target, OpenMode openmode) {
        Logger.getLogger().i("paster start ************* startService" + target
         + " sourceFiles " + sourceFiles.size());
        Intent intent = new Intent(context, CopyService.class);
        intent.putParcelableArrayListExtra(CopyService.TAG_COPY_SOURCES, sourceFiles);
        intent.putExtra(CopyService.TAG_COPY_TARGET, target);
        intent.putExtra(CopyService.TAG_COPY_OPEN_MODE, openmode.ordinal());
        intent.putExtra(CopyService.TAG_COPY_MOVE, move);
        ServiceWatcherUtil.runService(context, intent);

        mProgressListener.onOperationFinish(true);
    }

    private void showDialog(final String path, final ArrayList<FileInfo> filesToCopy,
                            final ArrayList<FileInfo> conflictingFiles) {
        final MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(context);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.copy_dialog, null);
        dialogBuilder.customView(view, true);
        // textView
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(context.getResources().getString(R.string.file_exists) + "\n" + conflictingFiles.get(counter).getName());

        // checkBox
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        dialogBuilder.title(context.getResources().getString(R.string.paste));
        dialogBuilder.positiveText(R.string.skip);
        dialogBuilder.negativeText(R.string.overwrite);
        dialogBuilder.neutralText(R.string.cancle);
        dialogBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (checkBox.isChecked())
                    dialogState = DO_FOR_ALL_ELEMENTS.DO_NOT_REPLACE;
                doNotReplaceFiles(path, filesToCopy, conflictingFiles);
            }
        });
        dialogBuilder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (checkBox.isChecked())
                    dialogState = DO_FOR_ALL_ELEMENTS.REPLACE;
                replaceFiles(path, filesToCopy, conflictingFiles);
            }
        });

        final MaterialDialog dialog = dialogBuilder.build();
        dialog.show();
        if (filesToCopy.get(0).getParent(context).equals(path)) {
            View negative = dialog.getActionButton(DialogAction.NEGATIVE);
            negative.setEnabled(false);
        }
    }

    private void onEndDialog(String path, ArrayList<FileInfo> filesToCopy,
                             ArrayList<FileInfo> conflictingFiles) {
        if (conflictingFiles != null && counter != conflictingFiles.size() && conflictingFiles.size() > 0) {
            if (dialogState == null)
                showDialog(path, filesToCopy, conflictingFiles);
            else if (dialogState == DO_FOR_ALL_ELEMENTS.DO_NOT_REPLACE)
                doNotReplaceFiles(path, filesToCopy, conflictingFiles);
            else if (dialogState == DO_FOR_ALL_ELEMENTS.REPLACE)
                replaceFiles(path, filesToCopy, conflictingFiles);
        } else {
            CopyNode c = !copyFolder.hasStarted() ? copyFolder.startCopy() : copyFolder.goToNextNode();

            if (c != null) {
                counter = 0;

                paths.add(c.getPath());
                filesToCopyPerFolder.add(c.filesToCopy);

                if (dialogState == null)
                    onEndDialog(c.path, c.filesToCopy, c.conflictingFiles);
                else if (dialogState == DO_FOR_ALL_ELEMENTS.DO_NOT_REPLACE)
                    doNotReplaceFiles(c.path, c.filesToCopy, c.conflictingFiles);
                else if (dialogState == DO_FOR_ALL_ELEMENTS.REPLACE)
                    replaceFiles(c.path, c.filesToCopy, c.conflictingFiles);
            } else {
                finishCopying(paths, filesToCopyPerFolder);
            }
        }
    }

    private void doNotReplaceFiles(String path, ArrayList<FileInfo> filesToCopy, ArrayList<FileInfo> conflictingFiles) {
        if (counter < conflictingFiles.size()) {
            if (dialogState != null) {
                filesToCopy.remove(conflictingFiles.get(counter));
                counter++;
            } else {
                for (int j = counter; j < conflictingFiles.size(); j++) {
                    filesToCopy.remove(conflictingFiles.get(j));
                }
                counter = conflictingFiles.size();
            }
        }

        onEndDialog(path, filesToCopy, conflictingFiles);
    }

    private void replaceFiles(String path, ArrayList<FileInfo> filesToCopy, ArrayList<FileInfo> conflictingFiles) {
        if (counter < conflictingFiles.size()) {
            if (dialogState != null) {
                counter++;
            } else {
                counter = conflictingFiles.size();
            }
        }

        onEndDialog(path, filesToCopy, conflictingFiles);
    }

    private void finishCopying(ArrayList<String> paths, ArrayList<ArrayList<FileInfo>> filesToCopyPerFolder) {
        for (int i = 0; i < filesToCopyPerFolder.size(); i++) {
            if (filesToCopyPerFolder.get(i) == null || filesToCopyPerFolder.get(i).size() == 0) {
                filesToCopyPerFolder.remove(i);
                paths.remove(i);
                i--;
            }
        }
        Logger.getLogger().i(" finishCopying************** move " + move);
        if (filesToCopyPerFolder.size() != 0) {
            int mode = FileOperationHelper.checkFolder(new File(path), context);
            if (mode == FileOperationHelper.CAN_CREATE_FILES && !path.contains("otg:/")) {
                //This is used because in newer devices the user has to accept a permission,
                // see MainActivity.onActivityResult()
                FileManagerApplication.getInstance().getCopyHelper().oparrayListList = filesToCopyPerFolder;
                FileManagerApplication.getInstance().getCopyHelper().oparrayList = null;
                FileManagerApplication.getInstance().getCopyHelper().operation = move ? CopyHelper.Operation.Cut : CopyHelper.Operation.Copy;
                FileManagerApplication.getInstance().getCopyHelper().oppatheList = paths;
            } else {
                if (!move) {
                    for (int i = 0; i < filesToCopyPerFolder.size(); i++) {

                        startService(filesToCopyPerFolder.get(i), paths.get(i), openMode);
                    }
                } else {
                    new MoveFiles(filesToCopyPerFolder, context, openMode, this.path, mProgressListener)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paths);

                }
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.no_file_overwrite), Toast.LENGTH_SHORT).show();
        }
    }

    class CopyNode {
        private String path;
        private ArrayList<FileInfo> filesToCopy, conflictingFiles;
        private ArrayList<CopyNode> nextNodes = new ArrayList<>();

        CopyNode(String p, ArrayList<FileInfo> filesToCopy) {
            path = p;
            this.filesToCopy = filesToCopy;

            FileInfo destination = new FileInfo(openMode, path);
            conflictingFiles = checkConflicts(filesToCopy, destination);

            for (int i = 0; i < conflictingFiles.size(); i++) {
                if (conflictingFiles.get(i).isDir()) {
                    if (deleteCopiedFolder == null)
                        deleteCopiedFolder = new ArrayList<>();

                    deleteCopiedFolder.add(new File(conflictingFiles.get(i).getFilePath()));

                    nextNodes.add(new CopyNode(path + "/"
                            + conflictingFiles.get(i).getName(),
                            conflictingFiles.get(i).listFiles(context, rootMode)));

                    filesToCopy.remove(filesToCopy.indexOf(conflictingFiles.get(i)));
                    conflictingFiles.remove(i);
                    i--;
                }
            }
        }

        /**
         * The next 2 methods are a BFS that runs through one node at a time.
         */
        private LinkedList<CopyNode> queue = null;
        private Set<CopyNode> visited = null;

        CopyNode startCopy() {
            queue = new LinkedList<>();
            visited = new HashSet<>();

            queue.add(this);
            visited.add(this);
            return this;
        }

        /**
         * @return true if there are no more nodes
         */
        CopyNode goToNextNode() {
            if (queue.isEmpty())
                return null;
            else {
                CopyNode node = queue.element();
                CopyNode child;
                if ((child = getUnvisitedChildNode(visited, node)) != null) {
                    visited.add(child);
                    queue.add(child);
                    return child;
                } else {
                    queue.remove();
                    return goToNextNode();
                }
            }
        }

        boolean hasStarted() {
            return queue != null;
        }

        String getPath() {
            return path;
        }

        ArrayList<FileInfo> getFilesToCopy() {
            return filesToCopy;
        }

        ArrayList<FileInfo> getConflictingFiles() {
            return conflictingFiles;
        }

        private CopyNode getUnvisitedChildNode(Set<CopyNode> visited, CopyNode node) {
            for (CopyNode n : node.nextNodes) {
                if (!visited.contains(n)) {
                    return n;
                }
            }

            return null;
        }
    }

}