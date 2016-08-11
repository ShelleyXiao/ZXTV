package com.zx.zx2000tvfileexploer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.FileCategoryHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileIconHelper;
import com.zx.zx2000tvfileexploer.mode.FileListItem;
import com.zx.zx2000tvfileexploer.mode.FileViewInteractionHub;
import com.zx.zx2000tvfileexploer.utils.FileUtils;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by ShaudXiao on 2016/7/15.
 */
public class FileListCursorAdapter extends CursorAdapter {

    private final LayoutInflater  mLayoutInflater;

    private FileIconHelper mFileIconHelper;

    private HashMap<Integer, FileInfo> mFileNameList = new HashMap<>();

    private Context mContext;

    private FileViewInteractionHub mFileViewInteractionHub;

    public FileListCursorAdapter(Context context, Cursor cursor, FileViewInteractionHub fHub, FileIconHelper fileIconHelper) {
        super(context, cursor, false /* auto-requery */);
        this.mContext = context;
        this.mFileIconHelper = fileIconHelper;
        this.mFileViewInteractionHub = fHub;
        this.mLayoutInflater = LayoutInflater.from(context);
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
        Logger.getLogger().d("FileInfo: " + fileInfo.fileName);
        FileListItem.setupFileListItemInfo(mContext, view, fileInfo, mFileIconHelper,
                mFileViewInteractionHub);
    }

    @Override
    public void changeCursor(Cursor cursor) {
        mFileNameList.clear();
        super.changeCursor(cursor);
    }

    public Collection<FileInfo> getAllFiles() {
        if(mFileNameList.size() == getCount()) {
            return mFileNameList.values();
        }

        Cursor cursor = getCursor();
        if(cursor.moveToNext()) {
            do {
                Integer position = Integer.valueOf(cursor.getPosition());
                if(mFileNameList.containsKey(position)) {
                    continue;
                }
                FileInfo fileInfo = getFileInfo(cursor);
                if(null != fileInfo) {
                    mFileNameList.put(position, fileInfo);
                }
            } while(cursor.moveToNext());
        }

        return mFileNameList.values();
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
                FileUtils.getFileInfo(cursor.getString(FileCategoryHelper.COLUMN_PATH));
    }
}
