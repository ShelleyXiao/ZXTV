package com.zx.zx2000tvfileexploer.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zx.zx2000tvfileexploer.FileManagerApplication;
import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.entity.Operation;
import com.zx.zx2000tvfileexploer.fileutil.FileOperationHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileUtil;
import com.zx.zx2000tvfileexploer.ui.FileListActivity;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.io.File;

public class CreateDirectoryDialog extends DialogFragment implements OverwriteFileDialog.Overwritable {
    private File mIn;
    private String filePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePath = getArguments().getString(GlobalConsts.EXTRA_DIR_PATH);
        mIn = new File(filePath);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout view = (LinearLayout) inflater.inflate(
                R.layout.dialog_text_input, null);
        final EditText v = (EditText) view.findViewById(R.id.foldername);
        v.setHint(R.string.folder_name);

        v.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView text, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO)
//                    createFolder(text.getText(), getActivity());
                FileOperationHelper.mkDir(new FileInfo(FileListActivity.mOpenMode, filePath + "/" + v.getText()), getActivity());
                return true;
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_new_folder)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
//                                createFolder(v.getText(), getActivity());
                                FileOperationHelper.mkDir(new FileInfo(FileListActivity.mOpenMode, filePath + "/" + v.getText()), getActivity());
                            }
                        }).setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private void createFolder(final CharSequence text, Activity activity) {
        if (text.length() != 0) {
//			tbcreated = new File(mIn + File.separator + text.toString());
//			Logger.getLogger().d(mIn.getAbsolutePath());
//			if (tbcreated.exists()) {
//				this.text = text;
//				this.c = c;
//				OverwriteFileDialog dialog = new OverwriteFileDialog();
//				dialog.setTargetFragment(this, 0);
//				dialog.show(getFragmentManager(), "OverwriteFileDialog");
//			} else {
//				if (tbcreated.mkdirs())
//					Toast.makeText(c, R.string.create_dir_success, Toast.LENGTH_SHORT).show();
//				else
//					Toast.makeText(c, R.string.create_dir_failure, Toast.LENGTH_SHORT).show();
//
//				((FileListActivity)this.getActivity())).onRefresh();
//				dismiss();
//			}
            final Toast toast = Toast.makeText(getActivity(), getString(R.string.create_new_folder),
                    Toast.LENGTH_SHORT);
            toast.show();

            FileUtil.mkdir(new FileInfo(FileListActivity.mOpenMode, filePath + File.separator + text.toString()), getActivity(), false, new FileUtil.ErrorCallBack() {
                @Override
                public void exists(final FileInfo file) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (toast != null) toast.cancel();
                            Toast.makeText(getActivity(), getString(R.string.file_exists),
                                    Toast.LENGTH_SHORT).show();
                            if (getActivity() != null) {
                                // retry with dialog prompted again

                            }
                        }
                    });
                }

                @Override
                public void launchSAF(FileInfo file) {
                    if (toast != null) toast.cancel();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            getActivity().oppathe = path.getPath();
                            Logger.getLogger().d("oppathe>>>> " + filePath + File.separator + text.toString());
                            FileManagerApplication.getInstance().setOppathe(filePath + File.separator + text.toString());
                            FileManagerApplication.getInstance().setOperation(Operation.mkdir);
                            FileUtil.guideDialogForLEXA(getActivity(), filePath);
                        }
                    });

                }

                @Override
                public void launchSAF(FileInfo file, FileInfo file1) {

                }

                @Override
                public void done(FileInfo hFile, final boolean b) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (b) {
                                ((FileListActivity)getActivity()).onRefresh();
                            } else
                                Toast.makeText(getActivity(), getString(R.string.operationunsuccesful),
                                        Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void invalidName(final FileInfo file) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (toast != null) toast.cancel();
                            Toast.makeText(getActivity(), getString(R.string.invalid_name)
                                    + ": " + file.getName(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }

    private File tbcreated;
    private CharSequence text;
    private Context c;

    @Override
    public void overwrite() {
        tbcreated.delete();
//        createFolder(text, c);
    }
}