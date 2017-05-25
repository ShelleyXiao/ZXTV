package com.zx.zx2000tvfileexploer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.entity.OpenMode;
import com.zx.zx2000tvfileexploer.fileutil.FileCategoryHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileIconHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileSettingsHelper;
import com.zx.zx2000tvfileexploer.fileutil.RootHelper;
import com.zx.zx2000tvfileexploer.utils.FileUtils;
import com.zx.zx2000tvfileexploer.utils.Logger;
import com.zx.zx2000tvfileexploer.view.RecyclerViewCursorAdapter;
import com.zx.zx2000tvfileexploer.view.RecyclerViewCursorViewHolder;

import java.util.Collection;
import java.util.HashMap;

/**
 * User: ShaudXiao
 * Date: 2016-09-08
 * Time: 15:20
 * Company: zx
 * Description:
 * FIXME
 */

public class PhotoViewByCursorAdapter extends RecyclerViewCursorAdapter<PhotoViewByCursorAdapter.PicViewHolder> {

    private HashMap<Integer, FileInfo> mFileNameList = new HashMap<>();

    FileIconHelper mFileIconHelper;

    public PhotoViewByCursorAdapter(Context context) {

        super(context);

        setupCursorAdapter(null, 0, R.layout.item_photo_view, false);

        mFileIconHelper = new FileIconHelper(context);
    }

    @Override
    public PicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent));
    }

    @Override
    public void onBindViewHolder(PicViewHolder holder, int position) {
        // Move cursor to this position
        mCursorAdapter.getCursor().moveToPosition(position);

        // Set the ViewHolder
        setViewHolder(holder);

        // Bind this view
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());
    }


    public Collection<FileInfo> getAllFiles() {
        if(mFileNameList.size() == mCursorAdapter.getCount()) {
            return mFileNameList.values();
        }

        Cursor cursor = mCursorAdapter.getCursor();
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

        Cursor cursor = (Cursor) mCursorAdapter.getItem(pos);
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
                RootHelper.getFileInfo(cursor.getString(FileCategoryHelper.COLUMN_PATH), OpenMode.CUSTOM, FileSettingsHelper.getInstance(mContext).getBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, false));
    }


    public class PicViewHolder extends RecyclerViewCursorViewHolder {
        public ImageView iv;
        public ImageView ivFrame;

        public PicViewHolder(View view) {
            super(view);

            iv = (ImageView) itemView.findViewById(R.id.thumb);
            ivFrame = (ImageView) itemView.findViewById(R.id.file_image_frame);
        }

        @Override
        public void bindCursor(Cursor cursor) {
            FileInfo fileInfo = getFileItem(cursor.getPosition());
            if(null == fileInfo) {
                fileInfo = new FileInfo();
                fileInfo.dbId = cursor.getLong(FileCategoryHelper.COLUMN_ID);
                fileInfo.filePath = cursor.getString(FileCategoryHelper.COLUMN_PATH);
                fileInfo.fileName = FileUtils.getFileName(fileInfo.filePath);
                fileInfo.fileSize = cursor.getLong(FileCategoryHelper.COLUMN_SIZE);
                fileInfo.ModifiedDate = cursor.getLong(FileCategoryHelper.COLUMN_DATE);
            }

            mFileIconHelper.setIcon(fileInfo, iv, ivFrame);
            Logger.getLogger().d("filename = " + fileInfo.fileName);
        }
    }
}





