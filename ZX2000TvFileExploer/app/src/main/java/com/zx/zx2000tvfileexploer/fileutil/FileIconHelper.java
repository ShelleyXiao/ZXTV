package com.zx.zx2000tvfileexploer.fileutil;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.interfaces.IconLoadFinishListener;
import com.zx.zx2000tvfileexploer.utils.FileUtils;

import java.util.HashMap;

/**
 * Created by ShaudXiao on 2016/7/15.
 */
public class FileIconHelper implements IconLoadFinishListener {

    private static HashMap<ImageView, ImageView> imageFrames = new HashMap<ImageView, ImageView>();
    private static HashMap<String, Integer> fileExtToIcons = new HashMap<>();

    private FileIconLoader mFileIconLoader;

    static {
        addItem(new String[] { "txt" ,"log", "xml", "ini", "lrc"}, R.drawable.file_icon_txt);
        addItem(new String[] { "mp4", "wmv", "mpeg", "m4v", "3gp", "3gpp", "3g2", "3gpp2", "asf","rmvb","avi" }, R.drawable.file_icon_video);
        addItem(new String[] { "jpg", "jpeg", "gif", "png", "bmp", "wbmp" }, R.drawable.file_icon_picture);
        addItem(new String[] { "wav" }, R.drawable.file_icon_wav);
        addItem(new String[] { "mp3"}, R.drawable.file_icon_mp3);
        addItem(new String[] { "wma",}, R.drawable.file_icon_wma);
    }

    private static void addItem(String[] exts, int resID) {
        if(exts != null && exts.length > 0) {
            for(String ext : exts) {
                fileExtToIcons.put(ext.toLowerCase(), resID);
            }
        }
    }

    public FileIconHelper(Context context) {
        this.mFileIconLoader = new FileIconLoader(context, this);
    }

    public static int getFileIcon(String ext) {
        Integer i = fileExtToIcons.get(ext.toLowerCase());
        if(i != null) {
            return i.intValue();
        } else {
            return R.drawable.file_icon_default;
        }
    }

    public void setIcon(FileInfo fileInfo, ImageView fileImage, ImageView fileImageFrame) {
        String filePath = fileInfo.filePath;
        long fileId = fileInfo.dbId;
        String extFromFileName = FileUtils.getFileExtension(filePath);
        FileCategoryHelper.FileCategory fc = FileCategoryHelper.getCategoryFromPath(filePath);
        fileImageFrame.setVisibility(View.GONE);
        boolean set = false;
        int id = getFileIcon(extFromFileName);
        fileImage.setImageResource(id);

        mFileIconLoader.cancelRequest(fileImage);
        switch (fc) {
            case APK:
                set = mFileIconLoader.loadIcon(fileImage, filePath, fileId, fc);
                break;
            case PICTURE:
            case VIDEO:
                set = mFileIconLoader.loadIcon(fileImage, filePath, fileId, fc);
                if(set) {
                    fileImageFrame.setVisibility(View.VISIBLE);
                } else {
                    fileImage.setImageResource(fc == FileCategoryHelper.FileCategory.VIDEO ? R.drawable.file_icon_video : R.drawable.file_icon_picture);
                    imageFrames.put(fileImage, fileImageFrame);
                    set = true;
                }
                break;
            default:
                set = true;
                break;
        }

        if(!set) {
            fileImage.setImageResource(R.drawable.file_icon_default);
        }
    }


    @Override
    public void onIconLoadFinished(ImageView view) {
        ImageView frame = imageFrames.get(view);
        if(frame != null) {
            frame.setVisibility(View.VISIBLE);
            imageFrames.remove(view);
        }
    }
}
