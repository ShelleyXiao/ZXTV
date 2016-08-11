package com.zx.zx2000tvfileexploer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.FileIconHelper;
import com.zx.zx2000tvfileexploer.mode.FileListItem;
import com.zx.zx2000tvfileexploer.mode.FileViewInteractionHub;

import java.util.List;

/**
 * Created by ShaudXiao on 2016/7/20.
 */
public class FileListAdapter extends ArrayAdapter<FileInfo> {

    private LayoutInflater mInflater;

    private FileViewInteractionHub mFileViewInteractionHub;

    private FileIconHelper mFileIcon;

    private Context mContext;

    public FileListAdapter(Context context, int resource,
                           List<FileInfo> objects, FileViewInteractionHub f,
                           FileIconHelper fileIcon) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
        mFileViewInteractionHub = f;
        mFileIcon = fileIcon;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(R.layout.list_grid_item_layout, parent, false);
        }

        FileInfo lFileInfo = mFileViewInteractionHub.getItem(position);
        FileListItem.setupFileListItemInfo(mContext, view, lFileInfo,
                mFileIcon, mFileViewInteractionHub);
        view.findViewById(R.id.file_checkbox_area).setOnClickListener(
                new FileListItem.FileItemOnClickListener(mContext,
                        mFileViewInteractionHub));
        return view;
    }
}
