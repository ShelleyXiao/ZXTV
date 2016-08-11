package com.zx.zx2000tvfileexploer.mode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.FileIconHelper;
import com.zx.zx2000tvfileexploer.utils.OtherUtil;

public class FileListItem {

    private static final String TAG = "FileListItem";

    @SuppressLint("NewApi")
//	public static void setupFileListItemInfo(Context context, View view,
//											 FileInfo fileInfo, FileIconHelper fileIcon,
//											 FileViewInteractionHub fileViewInteractionHub) {
//
//	        // if in moving mode, show selected file always
////	        if (fileViewInteractionHub.isMoveState()) {
////	            fileInfo.Selected = fileViewInteractionHub.isFileSelected(fileInfo.filePath);
////	        }
//
////	        ImageView checkbox = (ImageView) view.findViewById(R.id.file_checkbox);
////            checkbox.setVisibility(fileViewInteractionHub.canShowCheckBox() ? View.VISIBLE : View.GONE);
////            checkbox.setImageResource(fileInfo.Selected ? R.drawable.btn_check_on_holo_light
////                    : R.drawable.btn_check_off_holo_light);
////            checkbox.setTag(fileInfo);
////            view.setSelected(fileInfo.Selected);
//
//
//	        View fileThumbView = view.findViewById(R.id.thumb);
//	        Point point = OtherUtil.getScreenWidth(context);
////	        fileThumbView.setPadding(point.x / 2 - point.x / 9, 0, 0, 0);
//
//	        TextView fileName = (TextView)view.findViewById(R.id.file_name);
////	        fileName.setWidth(point.x / 3);
//
//	        OtherUtil.setText(view, R.id.file_name, fileInfo.fileName);
////	        OtherUtil.setText(view, R.id.file_count, fileInfo.IsDir ? "(" + fileInfo.Count + ")" : "");
////	        OtherUtil.setText(view, R.id.modified_time, OtherUtil.formatDateString(context, fileInfo.ModifiedDate));
////	        OtherUtil.setText(view, R.id.file_size, (fileInfo.IsDir ? "" :  OtherUtil.convertStorage(fileInfo.fileSize) + "|"));
//	        ImageView lFileImage = (ImageView) view.findViewById(R.id.file_image);
//	        ImageView lFileImageFrame = (ImageView) view.findViewById(R.id.file_image_frame);
//
//	        if (fileInfo.IsDir) {
//	            lFileImageFrame.setVisibility(View.GONE);
//	            lFileImage.setImageResource(R.drawable.folder);
//	        } else {
//	            fileIcon.setIcon(fileInfo, lFileImage, lFileImageFrame);
//	        }
//	    }

    public static void setupFileListItemInfo(Context context, View view,
                                             FileInfo fileInfo, FileIconHelper fileIcon) {

        TextView tvName = (TextView) view.findViewById(R.id.file_name);
        tvName.setText(fileInfo.fileName);
        ImageView fileiconImageView = (ImageView) view
                .findViewById(R.id.file_image);
        ImageView fileiconframeImageView = (ImageView) view
                .findViewById(R.id.file_image_frame);
        if (fileInfo.IsDir) {
            fileiconframeImageView.setVisibility(View.GONE);
            fileiconImageView.setImageResource(R.drawable.folder);
        } else {
            fileIcon.setIcon(fileInfo, fileiconImageView,
                    fileiconframeImageView);
        }

    }

    public static void setupFileListItemInfo(Context context, View view,
                                             FileInfo fileInfo, FileIconHelper fileIcon,
                                             FileViewInteractionHub fileInteractionHub) {
        ImageView checkboxImageView = (ImageView) view
                .findViewById(R.id.file_checkbox);
        if (fileInteractionHub == null
                || fileInteractionHub.getMode() == FileViewInteractionHub.Mode.View) {
            checkboxImageView.setVisibility(View.GONE);
            view.setBackgroundResource(R.drawable.grid_item_bg);
        } else {
            view.setBackgroundResource(fileInteractionHub.canShowSelectBg() ? R.drawable.grid_select_bg : R.drawable.grid_item_bg);
            checkboxImageView.setVisibility(View.VISIBLE);
            checkboxImageView
                    .setImageResource(fileInfo.Selected ? R.drawable.checked
                            : R.drawable.check);


            checkboxImageView.setTag(fileInfo);
            view.setSelected(fileInfo.Selected);
        }

        TextView fileName = (TextView)view.findViewById(R.id.file_name);
        OtherUtil.setText(view, R.id.file_name, fileInfo.fileName);

        ImageView fileiconImageView = (ImageView) view
                .findViewById(R.id.file_image);
        ImageView fileiconframeImageView = (ImageView) view
                .findViewById(R.id.file_image_frame);
        if (fileInfo.IsDir) {
            fileiconframeImageView.setVisibility(View.GONE);
            fileiconImageView.setImageResource(R.drawable.folder);
        } else {
            fileIcon.setIcon(fileInfo, fileiconImageView,
                    fileiconframeImageView);
        }

    }

    public static class FileItemOnClickListener implements OnClickListener {
        private FileViewInteractionHub mFileViewInteractionHub;

        public FileItemOnClickListener(Context context,
                                       FileViewInteractionHub fileViewInteractionHub) {
            mFileViewInteractionHub = fileViewInteractionHub;
        }

        @Override
        public void onClick(View v) {
            ImageView img = (ImageView) v.findViewById(R.id.file_checkbox);
            assert (img != null && img.getTag() != null);

            FileInfo tag = (FileInfo) img.getTag();
            tag.Selected = !tag.Selected;

//	            mFileViewInteractionHub.showOrHideOptionMenu(true);
            if (mFileViewInteractionHub.onCheckItem(tag, v)) {
                img.setImageResource(tag.Selected ? R.drawable.checked
                        : R.drawable.check);
            } else {
                tag.Selected = !tag.Selected;
            }

            if (mFileViewInteractionHub.getSelectedFileList().size() == 0) {
//	            	mFileViewInteractionHub.showOrHideOptionMenu(false);
            }
        }
    }
}
