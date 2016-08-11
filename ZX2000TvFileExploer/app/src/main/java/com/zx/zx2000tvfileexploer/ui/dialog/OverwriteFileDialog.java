package com.zx.zx2000tvfileexploer.ui.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;

import com.zx.zx2000tvfileexploer.R;

public class OverwriteFileDialog extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.file_exists)
				.setMessage(R.string.overwrite_question)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								((Overwritable) getTargetFragment()).overwrite();
							}
						}).setNegativeButton(android.R.string.cancel, null)
				.create();
	}
	
	public interface Overwritable {
		public void overwrite();
	}
}