package com.zx.zx2000tvfileexploer.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.FileOperationHelper;
import com.zx.zx2000tvfileexploer.ui.FileListActivity;

import java.util.ArrayList;

public class MultiDeleteDialog extends DialogFragment {
    private ArrayList<FileInfo> mFileHolders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFileHolders = getArguments().getParcelableArrayList(GlobalConsts.EXTRA_DIALOG_FILE_HOLDER);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.really_delete_multiselect, mFileHolders.size()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.deleting), Toast.LENGTH_SHORT).show();

                        FileOperationHelper.deleteFiles(getActivity(), mFileHolders);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    public void refresh() {
        ((FileListActivity) this.getActivity()).onRefresh();
    }
}