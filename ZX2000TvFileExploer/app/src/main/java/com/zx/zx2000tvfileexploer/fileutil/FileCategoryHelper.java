package com.zx.zx2000tvfileexploer.fileutil;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.MediaFile;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.io.FilenameFilter;
import java.util.HashMap;

/**
 * Created by ShaudXiao on 2016/7/14.
 */
public class FileCategoryHelper {

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_PATH = 1;
    public static final int COLUMN_SIZE = 2;
    public static final int COLUMN_DATE = 3;

    private static final String APK_EXT = "apk";

    private FileCategory mFileCategory;

    private Context mContext;

    public enum FileCategory {
        All, APK, MUSIC, PICTURE, VIDEO, FAVORITE, OTHRE
    }

    public static HashMap<FileCategory, FileExtFilter> filters = new HashMap<>();
    public static HashMap<FileCategory, Integer> categoryNames = new HashMap<>();
    public static HashMap<String, FileCategory> fileExtCategoryType = new HashMap<>();

    private HashMap<FileCategory, CategoryInfo> mCategoryInfo = new HashMap<>();

    static {
        categoryNames.put(FileCategory.All, R.string.category_all);
        categoryNames.put(FileCategory.APK, R.string.category_apk);
        categoryNames.put(FileCategory.MUSIC, R.string.category_music);
        categoryNames.put(FileCategory.PICTURE, R.string.category_picture);
        categoryNames.put(FileCategory.VIDEO, R.string.category_video);
        categoryNames.put(FileCategory.FAVORITE, R.string.category_favorite);
        categoryNames.put(FileCategory.OTHRE, R.string.category_other);
    }

    static {
        addItem(new String[]{"mp4", "wmv", "mpeg", "m4v", "3gp", "3gpp",
                "3g2", "3gpp2", "asf", "rmvb", "avi", "mkv", "mpg"}, FileCategory.VIDEO);
        addItem(new String[] { "jpg", "jpeg", "gif", "png", "bmp", "wbmp" },
                FileCategory.PICTURE);
        addItem(new String[] { "mp3", "wma", "wav", "ogg" },
                FileCategory.MUSIC);
    }

    public static FileCategory[] sCategories = new FileCategory[] {
            FileCategory.APK, FileCategory.OTHRE, FileCategory.MUSIC, FileCategory.All,
    FileCategory.FAVORITE, FileCategory.PICTURE,FileCategory.VIDEO};

    public FileCategoryHelper(Context context) {
        this.mContext = context;

        mFileCategory = FileCategory.All;
    }

    public FileCategory getCurFileCategory() {
        return mFileCategory;
    }

    public void setCurFileCategory(FileCategory fileCategory) {
        mFileCategory = fileCategory;
    }

    public int getCurCategoryNameResId() {
        return categoryNames.get(mFileCategory);
    }

    public static FileCategory getCategoryFromPath(String path) {
        MediaFile.MediaFileType fileType = MediaFile.getFileType(path);
        if(fileType != null) {
            if(MediaFile.isAudioFileType(fileType.fileType)) {
                return FileCategory.MUSIC;
            }
            if(MediaFile.isImageFileType(fileType.fileType)) {
                return FileCategory.PICTURE;
            }
            if(MediaFile.isVideoFileType(fileType.fileType)) {
                return FileCategory.VIDEO;
            }

        }

        int dotPosition = path.lastIndexOf(".");
        String ext = path.substring(dotPosition + 1);
        if(ext.equalsIgnoreCase(APK_EXT)) {
            return FileCategory.APK;
        }

        if(dotPosition < 0) {
            return FileCategory.OTHRE;
        }

        return FileCategory.OTHRE;
    }

    public FilenameFilter getFilter() {
        return filters.get(mFileCategory);
    }

    public HashMap<FileCategory, CategoryInfo> getCategoryInfo() {
        return mCategoryInfo;
    }

    public CategoryInfo getCategoryInfo(FileCategory category) {
        if(mCategoryInfo.containsKey(category)) {
            return mCategoryInfo.get(category);
        } else {
            CategoryInfo info = new CategoryInfo();
            mCategoryInfo.put(category, info);
            return info;
        }
    }

    private void setCategoryInfo(FileCategory fc, long count, long size) {
        CategoryInfo info = mCategoryInfo.get(fc);
        if(info == null) {
            info = new CategoryInfo();
            mCategoryInfo.put(fc, info);
        }
        info.count = count;
        info.size = size;
    }

    public Cursor query(FileCategory fc, String fileName, FileSortHelper.SortMethod sort) {
        if(TextUtils.isEmpty(fileName)) {
            return null;
        }

        Uri uri = FileUirUtils.getContentUriByCategory(fc);
//        Logger.getLogger().d("uri: " + uri.toString());
        String selection = buildSelectionByCategory(fc);
        if(TextUtils.isEmpty(selection)) {
            selection = MediaStore.Files.FileColumns.TITLE + " LIKE '%" + fileName + "%'";
        } else {
            selection = selection + " AND " + MediaStore.Files.FileColumns.TITLE + " LIKE '%" + fileName + "%'";
        }

        if(uri == null) {
            Logger.getLogger().e("invalid uri, category: " + fc.name());
            return null;
        }
        String sortOrder = buildSortOrder(sort);

        String[] columns = new String[] {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED
        };

        return mContext.getContentResolver().query(uri, columns, selection, null, sortOrder);
    }

    public Cursor query(FileCategory fc, FileSortHelper.SortMethod sort) {
        Uri uri = FileUirUtils.getContentUriByCategory(fc);
        String selection = buildSelectionByCategory(fc);
        String sortOrder = buildSortOrder(sort);

        if(uri == null) {
            Logger.getLogger().e("invalid uri, category: " + fc.name());
            return null;
        }

        String[] columns = new String[] {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED
        };

        return mContext.getContentResolver().query(uri, columns, selection, null, sortOrder);
    }

    public void refreshCategoryInfo() {
        for (FileCategory fc : sCategories) {
            setCategoryInfo(fc, 0, 0);
        }

        String volumeName = "external";

        Uri uri = MediaStore.Audio.Media.getContentUri(volumeName);
        refreshMediaCategory(FileCategory.MUSIC, uri);

        uri = MediaStore.Video.Media.getContentUri(volumeName);
        refreshMediaCategory(FileCategory.VIDEO, uri);

        uri = MediaStore.Images.Media.getContentUri(volumeName);
        refreshMediaCategory(FileCategory.PICTURE, uri);

        uri = MediaStore.Files.getContentUri(volumeName);
        refreshMediaCategory(FileCategory.APK, uri);
    }

    private boolean refreshMediaCategory(FileCategory fc, Uri uri) {
        String[] columns = new String[] {"COUNT(*)", "SUM(_size)"};
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                columns,
                buildSelectionByCategory(fc),
                null,
                null
        );
        if(null == cursor) {
            Logger.getLogger().d("fail to query uri: " + uri);
            return false;
        }
        Logger.getLogger().d("********refreshMediaCategory*********");
        if(cursor.moveToNext()) {
            setCategoryInfo(fc, cursor.getLong(0), cursor.getLong(1));
            Logger.getLogger().d( "Retrieved " + fc.name() + " info >>> count:"
                    + cursor.getLong(0) + " size:" + cursor.getLong(1));
            cursor.close();
            return true;
        }

        return false;
    }


    private String buildSortOrder(FileSortHelper.SortMethod sort) {
        String sortOrder = null;
        switch (sort) {
            case NAME:
                sortOrder = MediaStore.Files.FileColumns.TITLE + " asc";
                break;
            case SIZE:
                sortOrder = MediaStore.Files.FileColumns.SIZE + " asc";
                break;
            case DATE:
                sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " desc";
                break;
            case TYPE:
                sortOrder = MediaStore.Files.FileColumns.MIME_TYPE + " asc," + MediaStore.Files.FileColumns.TITLE + " asc";
        }
        return sortOrder;
    }

    private String buildSelectionByCategory(FileCategory fc) {
        String selection = null;
        switch (fc) {
            case APK:
                selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.apk'";
                break;

            default:
                selection = null;
                break;
        }

        return selection;
    }

    private static void addItem(String[] exts, FileCategory category) {
        if (exts != null) {
            for (String ext : exts) {
                fileExtCategoryType.put(ext.toLowerCase(), category);
            }
        }
    }

    public class CategoryInfo {
        public long count;

        public long size;
    }
}
