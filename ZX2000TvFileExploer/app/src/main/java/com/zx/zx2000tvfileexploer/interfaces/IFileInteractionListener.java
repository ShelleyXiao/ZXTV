package com.zx.zx2000tvfileexploer.interfaces;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.FileCategoryHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileSortHelper;

import java.util.Collection;

public interface IFileInteractionListener {

	public View getViewById(int id);

	public Context getContext();

	public FileInfo getItem(int pos);

	public void onPick(FileInfo f);

	public void onDataChanged();

	public void runOnUiThread(Runnable r);

	public void sortCurrentList(FileSortHelper sort);

	public boolean onRefreshFileList(String path, FileSortHelper sort);

	public Collection<FileInfo> getAllFiles();

	public void startActivity(Intent intent);
	
	public void startActivityForResult(Intent intent, int requestCode);

	public void addSingleFile(FileInfo file);

	public void ShowMovingOperationBar(boolean isShow);

	public void updateMediaData();

	public void updateMenuItem(int resId, boolean isShow);

	public FileCategoryHelper.FileCategory  getCurrentCategory();

}
