package com.zx.zx2000tvfileexploer.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.FileOperationHelper;
import com.zx.zx2000tvfileexploer.ui.FileListActivity;

public class RenameDialog extends DialogFragment {
	private FileInfo mFileHolder;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mFileHolder = getArguments().getParcelable(GlobalConsts.EXTRA_DIALOG_FILE_HOLDER);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_text_input, null);
		final EditText v = (EditText) view.findViewById(R.id.foldername);
		v.setText(mFileHolder.getFileName());

		v.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView text, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO)
					renameTo(text.getText().toString());
				dismiss();
				return true;
			}
		});
		
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.operation_rename)
//				.setIcon(mFileHolder.getIcon())
				.setView(view)
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								renameTo(v.getText().toString());

							}
						}).create();
	}
	
	private void renameTo(String to){
//		boolean res = false;
//
//		if(to.length() > 0){
//			File from = mFileHolder.getFile();
//
//			File dest = new File(mFileHolder.getFile().getParent() + File.separator + to);
//			if(!dest.exists()){
//				res = mFileHolder.getFile().renameTo(dest);
//				((FileListActivity)this.getActivity()).onRefresh();
//
//				// Inform media scanner
//				MediaScannerUtils.informFileDeleted(getActivity().getApplicationContext(), from);
//				MediaScannerUtils.informFileAdded(getActivity().getApplicationContext(), dest);
//			}
//		}
		FileOperationHelper.rename(FileListActivity.mOpenMode, mFileHolder.getFilePath(),
				mFileHolder.getParent(getActivity()) + "/" + to, getActivity(), false);

	}
}