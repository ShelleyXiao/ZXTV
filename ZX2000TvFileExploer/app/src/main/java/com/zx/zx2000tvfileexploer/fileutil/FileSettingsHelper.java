/* ---------------------------------------------------------------------------------------------
 *
 *            Capital Alliance Software Confidential Proprietary
 *            (c) Copyright CAS 201{x}, All Rights Reserved
 *                          www.pekall.com
 *
 * ----------------------------------------------------------------------------------------------
 */
package com.zx.zx2000tvfileexploer.fileutil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.HashMap;

/**
 * @author ShaudXiao
 * 
 */
public class FileSettingsHelper {
	public static final String KEY_SHOW_HIDEFILE = "showhidefile";
	public static final String KEY_SHOW_VIEW_COUNT = "showViewCount";
	public static final String KEY_ONLY_SHOW_FILENAME = "onlyshowname";
	public static final String KEY_LARGE_FILES = "largerfiles";
	public static final String KEY_SORT_DESC = "sortdesc";
	public static final String KEY_SORT_TYPE = "sorttype";

	public static HashMap<Integer, FileSortHelper.SortMethod> getSortTypeByType = new HashMap<Integer, FileSortHelper.SortMethod>();
	public static HashMap<FileSortHelper.SortMethod, Integer> getTypeBySortType = new HashMap<FileSortHelper.SortMethod, Integer>();

	static {
		getSortTypeByType.put(1, FileSortHelper.SortMethod.NAME);
		getSortTypeByType.put(2, FileSortHelper.SortMethod.SIZE);
		getSortTypeByType.put(3, FileSortHelper.SortMethod.DATE);
		getSortTypeByType.put(4, FileSortHelper.SortMethod.TYPE);
		getTypeBySortType.put(FileSortHelper.SortMethod.NAME, 1);
		getTypeBySortType.put(FileSortHelper.SortMethod.SIZE, 2);
		getTypeBySortType.put(FileSortHelper.SortMethod.DATE, 3);
		getTypeBySortType.put(FileSortHelper.SortMethod.TYPE, 4);
	}

	public FileSortHelper.SortMethod getSortType() {
		return getSortTypeByType.get(getInt(KEY_SORT_TYPE, 1));
	}

	public void putSortType(FileSortHelper.SortMethod sortType) {
		putInt(KEY_SORT_TYPE, getTypeBySortType.get(sortType));
	}

	private static FileSettingsHelper mInstance;
	private SharedPreferences mSettings;
	private Editor mSettingsEditor;
	private boolean mPreShowHideSettings;
	private boolean mPreShowFileNameSettings;
	private boolean mPreSortDescSettings;

	private FileSettingsHelper(Context context) {
		mSettings = PreferenceManager.getDefaultSharedPreferences(context);
		mSettingsEditor = mSettings.edit();
		updatePreSettings();
	}

	public static FileSettingsHelper getInstance(Context context) {
		if (mInstance == null)
			mInstance = new FileSettingsHelper(context);
		return mInstance;
	}

	public boolean getBoolean(String key, boolean defValue) {
		return mSettings.getBoolean(key, defValue);
	}

	public int getInt(String key, int defValue) {
		return mSettings.getInt(key, defValue);
	}

	public void putBoolean(String key, boolean value) {
		mSettingsEditor.putBoolean(key, value).commit();
	}

	public String getString(String key,String defaultValue){
	    return mSettings.getString(key, defaultValue);
	}
	
	public void putInt(String key, int value) {
		mSettingsEditor.putInt(key, value).commit();
	}

	private void updatePreSettings() {
		mPreShowHideSettings = getBoolean(KEY_SHOW_HIDEFILE, false);
		mPreShowFileNameSettings = getBoolean(KEY_ONLY_SHOW_FILENAME, false);
		mPreSortDescSettings = getBoolean(KEY_SORT_DESC, false);
	}

	/**
	 * @return
	 */
	public boolean isSettingsChange() {
		boolean isChange = (mPreShowHideSettings != getBoolean(KEY_SHOW_HIDEFILE, false))
				|| (mPreShowFileNameSettings != getBoolean(KEY_ONLY_SHOW_FILENAME, false))
				|| (mPreSortDescSettings != getBoolean(KEY_SORT_DESC, false));
		if (isChange) {
			updatePreSettings();
		}
		return isChange;
	}
}
