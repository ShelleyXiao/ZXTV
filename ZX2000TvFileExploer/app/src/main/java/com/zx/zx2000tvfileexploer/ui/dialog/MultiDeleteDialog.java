package com.zx.zx2000tvfileexploer.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.FileOperationHelper;
import com.zx.zx2000tvfileexploer.fileutil.MediaScannerUtils;
import com.zx.zx2000tvfileexploer.ui.FileListActivity;

import java.io.File;
import java.util.ArrayList;

public class MultiDeleteDialog extends DialogFragment {
    private ArrayList<FileInfo> mFileHolders;
    private Context mContext;
    private FileOperationHelper mFileOperationHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFileHolders = getArguments().getParcelableArrayList(GlobalConsts.EXTRA_DIALOG_FILE_HOLDER);
        mContext = (FileListActivity) this.getActivity();

        mFileOperationHelper = new FileOperationHelper(null, mContext);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.really_delete_multiselect, mFileHolders.size()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        new RecursiveDeleteTask().execute();

                        Toast.makeText(getActivity(), getResources().getString(R.string.deleting), Toast.LENGTH_SHORT).show();

                        mFileOperationHelper.deleteFiles(mFileHolders);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private class RecursiveDeleteTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;

        public RecursiveDeleteTask() {
            // Init before having the fragment in an undefined state because of dialog dismissal
//			mContext = getTargetFragment().getActivity().getApplicationContext();
        }

        /**
         * If 0 some failed, if 1 all succeeded.
         */
        private int mResult = 1;
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        /**
         * Recursively delete a file or directory and all of its children.
         *
         * @returns 0 if successful, error value otherwise.
         */
        private void recursiveDelete(File file) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                // If it's a directory delete all children.
                for (File childFile : files) {
                    if (childFile.isDirectory()) {
                        recursiveDelete(childFile);
                    } else {
                        mResult *= childFile.delete() ? 1 : 0;

                        MediaScannerUtils.informFileDeleted(mContext, childFile);
                    }
                }
            }
            // And then delete parent. -- or just delete the file.
            mResult *= file.delete() ? 1 : 0;
            MediaScannerUtils.informFileDeleted(mContext, file);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getActivity().getString(R.string.deleting));
            dialog.setIndeterminate(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (FileInfo fh : mFileHolders)
                recursiveDelete(fh.getFile());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(dialog.getContext(), mResult == 0 ? R.string.delete_failure : R.string.delete_success, Toast.LENGTH_LONG).show();
            refresh();
            dialog.dismiss();

            mContext = null;
        }
    }

    public void refresh() {
        ((FileListActivity) this.getActivity()).onRefresh();
    }
}