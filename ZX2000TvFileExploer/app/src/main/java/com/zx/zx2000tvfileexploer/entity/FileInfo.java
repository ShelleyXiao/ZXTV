package com.zx.zx2000tvfileexploer.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.provider.DocumentFile;

import com.zx.zx2000tvfileexploer.fileutil.FileUtil;
import com.zx.zx2000tvfileexploer.fileutil.OTGUtil;
import com.zx.zx2000tvfileexploer.fileutil.RootHelper;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ShaudXiao on 2016/7/14.
 */
public class FileInfo implements Parcelable, Comparable<FileInfo> {

    public String fileName;

    public String filePath;

    public long fileSize;

    public boolean IsDir;

    public int Count;

    public long ModifiedDate;

    public boolean Selected;

    public boolean canRead;

    public boolean canWrite;

    public boolean isHidden;

    public OpenMode mode = OpenMode.UNKNOWN;

    public String permission;

    public long dbId; // id in the database, if is from database

    public FileInfo() {
    }

    public FileInfo(OpenMode mode, String path) {
        this.filePath = path;
        this.mode = mode;
    }

    public FileInfo(OpenMode mode, String path, String name, boolean isDirectory) {
        this.mode = mode;
        this.filePath = path + "/" + name;
    }

    public FileInfo(String path, String permission, long date, long size, boolean isDirectory) {
        this.mode = OpenMode.FILE;
        this.filePath = path;
        this.fileSize = size;
        this.IsDir = isDirectory;
        this.ModifiedDate = date;
        this.filePath = path;
        this.permission = permission;
    }

    public FileInfo(Parcel in) {

        fileName = in.readString();
        filePath = in.readString();
        fileSize = in.readLong();
        IsDir = in.readInt() == 1 ? true : false;
        Count = in.readInt();
        Selected = in.readInt() == 1 ? true : false;
        ModifiedDate = in.readLong();

        canRead = in.readInt() == 1 ? true : false;
        canWrite = in.readInt() == 1 ? true : false;
        isHidden = in.readInt() == 1 ? true : false;

        dbId = in.readLong();

        permission = in.readString();

        mode = OpenMode.getOpenMode(in.readInt());
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * @return
     * @deprecated use {@link #getName(Context)}
     */
    public String getName() {
        String name = null;
        switch (mode) {
            case FILE:
                return new File(filePath).getName();
            default:
                StringBuilder builder = new StringBuilder(filePath);
                name = builder.substring(builder.lastIndexOf("/") + 1, builder.length());
        }
        return name;
    }

    public String getName(Context context) {
        String name = null;
        switch (mode) {
            case FILE:
                return new File(filePath).getName();
            case OTG:
                return RootHelper.getDocumentFile(filePath, context, false).getName();
            default:
                StringBuilder builder = new StringBuilder(filePath);
                name = builder.substring(builder.lastIndexOf("/") + 1, builder.length());
        }
        return name;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    /**
     * Helper method to find length
     *
     * @param context
     * @return
     */
    public long length(Context context) {
        long s = 0l;
        if(mode == OpenMode.UNKNOWN) {
            Logger.getLogger().w("mode " + mode.ordinal());
        }

        switch (mode) {
            case FILE:
                s = new File(filePath).length();
                return s;
            case OTG:
                s = RootHelper.getDocumentFile(filePath, context, false).length();
                break;

            default:
                break;
        }
        return s;
    }

    public long getUsableSpace() {
        long size = (new File(filePath).getUsableSpace());
        return size;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isDir() {
        return IsDir;
    }

    public void setDir(boolean dir) {
        IsDir = dir;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public long getModifiedDate() {
        return ModifiedDate;
    }

    public void setModifiedDate(long modifiedDate) {
        ModifiedDate = modifiedDate;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public File getFile() {
        return new File(filePath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(filePath);
        dest.writeLong(fileSize);
        dest.writeInt(IsDir ? 1 : 0);
        dest.writeInt(Count);
        dest.writeInt(Selected ? 1 : 0);
        dest.writeLong(ModifiedDate);
        dest.writeInt(canRead ? 1 : 0);
        dest.writeInt(canWrite ? 1 : 0);
        dest.writeInt(isHidden ? 1 : 0);
        dest.writeLong(dbId);
        dest.writeString(permission);
        dest.writeInt(mode.ordinal());
    }

    public static final Parcelable.Creator<FileInfo> CREATOR = new Parcelable.Creator<FileInfo>() {
        public FileInfo createFromParcel(Parcel in) {
            return new FileInfo(in);
        }

        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };

    @Override
    public int compareTo(FileInfo another) {
        return new File(filePath).compareTo(another.getFile());
    }

    /**
     * Parse the extension from the filename of the mFile member.
     */
    private String parseExtension() {
        String ext = "";
        String name = getFileName();

        int i = name.lastIndexOf('.');

        if (i > 0 && i < name.length() - 1) {
            ext = name.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    @Override
    public String toString() {
        return super.toString() + "-" + getFileName();
    }

    public boolean delete(Context context) {

        FileUtil.deleteFile(new File(filePath), context);

        return !exists();
    }

    public boolean exists() {
        return new File(filePath).exists();
    }

    /**
     * Helper method to check file existence in otg
     *
     * @param context
     * @return
     */
    public boolean exists(Context context) {
        if (isOtgFile()) {
            DocumentFile fileToCheck = RootHelper.getDocumentFile(filePath, context, false);
            return fileToCheck != null;
        } else return (exists());
    }

    /**
     * Helper method to get length of folder in an otg
     *
     * @param context
     * @return
     */
    public long folderSize(Context context) {

        long size = 0l;
        size = FileUtil.folderSize(new File(filePath), null);

        return size;
    }

    public void generateMode(Context context) {
        if (filePath.startsWith(OTGUtil.PREFIX_OTG)) {
            mode = OpenMode.OTG;
        } else if (isCustomPath()) {
            mode = OpenMode.CUSTOM;
        } else {
            if (context == null) {
                mode = OpenMode.FILE;
                return;
            }

            if (FileUtil.isOnExtSdCard(getFile(), context)) mode = OpenMode.FILE;

            if (mode == OpenMode.UNKNOWN) mode = OpenMode.FILE;
        }

    }

    public void setMode(OpenMode mode) {
        this.mode = mode;
    }

    public OpenMode getMode() {
        return mode;
    }

    /**
     * apk . video , music . picture
     */
    public boolean isCustomPath() {
        return filePath.equals("0") ||
                filePath.equals("1") ||
                filePath.equals("2") ||
                filePath.equals("3") ||
                filePath.equals("4") ||
                filePath.equals("5") ||
                filePath.equals("6");
    }


    public boolean isLocal() {
        return mode == OpenMode.FILE;
    }

    public boolean isOtgFile() {
        return mode == OpenMode.OTG;
    }

    /**
     * Helper method to get parent path
     *
     * @param context
     * @return
     */
    public String getParent(Context context) {

        String parentPath = "";
        switch (mode) {
            case FILE:

                parentPath = new File(filePath).getParent();
                break;
            case OTG:
            default:
                StringBuilder builder = new StringBuilder(filePath);
                StringBuilder parentPathBuilder = new StringBuilder(builder.substring(0,
                        builder.length() - (getFileName().length() + 1)));
                return parentPathBuilder.toString();
        }
        return parentPath;
    }

    public void mkdir(Context context) {
        if (isOtgFile()) {
            if (!exists(context)) {
                DocumentFile parentDirectory = RootHelper.getDocumentFile(getParent(context), context, false);
                if (parentDirectory.isDirectory()) {
                    parentDirectory.createDirectory(getName(context));
                }
            }
        } else
            FileUtil.mkdir(new File(filePath), context);
    }

    /**
     * Helper method to list children of this file
     *
     * @param context
     * @return
     */
    public ArrayList<FileInfo> listFiles(Context context, boolean isRoot) {
        ArrayList<FileInfo> arrayList = new ArrayList<>();
        switch (mode) {
            case OTG:
                arrayList = OTGUtil.getDocumentFilesList(filePath, context);
                break;

            default:
                try {
                    arrayList = RootHelper.getFilesList(filePath, isRoot, true, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        return arrayList;
    }


}
