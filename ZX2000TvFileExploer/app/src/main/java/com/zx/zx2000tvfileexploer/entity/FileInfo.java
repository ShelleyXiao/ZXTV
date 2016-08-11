package com.zx.zx2000tvfileexploer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by ShaudXiao on 2016/7/14.
 */
public class FileInfo implements Parcelable, Comparable<FileInfo>  {

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

    public long dbId; // id in the database, if is from database

    public FileInfo() {}

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
    }

    public String getFileName() {
        return fileName;
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

        if (i > 0 &&  i < name.length() - 1) {
            ext = name.substring(i+1).toLowerCase();
        }
        return ext;
    }

    @Override
    public String toString() {
        return super.toString() + "-" + getFileName();
    }
}
