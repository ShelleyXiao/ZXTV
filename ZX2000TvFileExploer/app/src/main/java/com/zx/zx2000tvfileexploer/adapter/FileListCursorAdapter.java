package com.zx.zx2000tvfileexploer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.entity.OpenMode;
import com.zx.zx2000tvfileexploer.fileutil.FileCategoryHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileIconHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileSettingsHelper;
import com.zx.zx2000tvfileexploer.fileutil.RootHelper;
import com.zx.zx2000tvfileexploer.presenter.FileListItem;
import com.zx.zx2000tvfileexploer.presenter.FileViewInteractionHub;
import com.zx.zx2000tvfileexploer.utils.FileUtils;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ShaudXiao on 2016/7/15.
 */
public class FileListCursorAdapter extends CursorAdapter {

    private final LayoutInflater  mLayoutInflater;

    private FileIconHelper mFileIconHelper;

    private HashMap<Integer, FileInfo> mFileNameList = new HashMap<>();

    private Context mContext;

    private FileViewInteractionHub mFileViewInteractionHub;

    private OpenMode mOpenMode;


    public FileListCursorAdapter(Context context, Cursor cursor, FileViewInteractionHub fHub, FileIconHelper fileIconHelper, OpenMode mode) {
        super(context, cursor, false /* auto-requery */);
        this.mContext = context;
        this.mFileIconHelper = fileIconHelper;
        this.mFileViewInteractionHub = fHub;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mOpenMode = mode;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mLayoutInflater.inflate(R.layout.list_grid_item_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        FileInfo fileInfo = getFileItem(cursor.getPosition());
        if(null == fileInfo) {
            fileInfo = new FileInfo();
            fileInfo.dbId = cursor.getLong(FileCategoryHelper.COLUMN_ID);
            fileInfo.filePath = cursor.getString(FileCategoryHelper.COLUMN_PATH);
            fileInfo.fileName = FileUtils.getFileName(fileInfo.filePath);
            fileInfo.fileSize = cursor.getLong(FileCategoryHelper.COLUMN_SIZE);
            fileInfo.ModifiedDate = cursor.getLong(FileCategoryHelper.COLUMN_DATE);
        }
//        Logger.getLogger().d("FileInfo: " + fileInfo.fileName);
        FileListItem.setupFileListItemInfo(mContext, view, fileInfo, mFileIconHelper,
                mFileViewInteractionHub);
    }

    @Override
    public void changeCursor(Cursor cursor) {
        mFileNameList.clear();
        super.changeCursor(cursor);
    }

    public List<FileInfo> getAllFiles() {
//        if(mFileNameList.size() == getCount()) {
//            return mFileNameList.values();
//        }
        Logger.getLogger().i("mFileNameList = " + mFileNameList.size() + " " + getCount());
        mFileNameList.clear();
        ArrayList<FileInfo> dataList = new ArrayList<>();
        Cursor cursor = getCursor();

        if(cursor.moveToFirst()) {
            do {
                Integer position = Integer.valueOf(cursor.getPosition());
//                if(mFileNameList.containsKey(position)) {
//                    continue;
//                }
                FileInfo fileInfo = getFileInfo(cursor);
                if(null != fileInfo) {
                    mFileNameList.put(position, fileInfo);
                    Logger.getLogger().i("mFileNameList = " + fileInfo.getFilePath()
                    + "position = " + cursor.getPosition());
                    dataList.add(fileInfo);
                }

            } while(cursor.moveToNext());
        }
        Logger.getLogger().i("mFileNameList = " + mFileNameList.size());
//        return mFileNameList.values();
        return dataList;
    }

    public FileInfo getFileItem(int pos) {
        Integer position = Integer.valueOf(pos);
        if(mFileNameList.containsKey(position)) {
            return mFileNameList.get(position);
        }

        Cursor cursor = (Cursor) getItem(pos);
        FileInfo fileInfo = getFileInfo(cursor);
        if(null == fileInfo) {
            return null;
        }

        fileInfo.dbId = cursor.getLong(FileCategoryHelper.COLUMN_ID);
        mFileNameList.put(position, fileInfo);

        return fileInfo;
    }

    private FileInfo getFileInfo(Cursor cursor) {
        return (cursor == null || cursor.getCount() == 0) ? null :
                RootHelper.getFileInfo(cursor.getString(FileCategoryHelper.COLUMN_PATH), mOpenMode, FileSettingsHelper.getInstance(mContext).getBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, false));
    }
}
