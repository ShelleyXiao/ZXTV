package com.zx.zx2000tvfileexploer.fileutil;

import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.utils.FileUtils;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by ShaudXiao on 2016/7/14.
 */
public class FileSortHelper {
    private boolean isFileFirst = false;

    public enum SortMethod {
        NAME, SIZE, DATE, TYPE
    }

    private SortMethod mSortMethod;

    private HashMap<SortMethod, Comparator<FileInfo>> mSortMethodComparatorHashMap = new HashMap<>();

    public FileSortHelper() {
        this.mSortMethod = SortMethod.DATE;
        mSortMethodComparatorHashMap.put(SortMethod.NAME, cmpName);
        mSortMethodComparatorHashMap.put(SortMethod.SIZE, cmpSize);
        mSortMethodComparatorHashMap.put(SortMethod.DATE, cmpDate);
        mSortMethodComparatorHashMap.put(SortMethod.TYPE, cmpType);
    }

    public boolean isFileFirst() {
        return isFileFirst;
    }

    public void setFileFirst(boolean fileFirst) {
        isFileFirst = fileFirst;
    }

    public void setSortMethod(SortMethod s) {
        mSortMethod = s;
    }

    public SortMethod getSortMethod() {
        return mSortMethod;
    }

    private abstract class FileComparator implements Comparator<FileInfo> {

        @Override
        public int compare(FileInfo lhs, FileInfo rhs) {
            if (lhs.IsDir && rhs.IsDir) {
                return doCompare(lhs, rhs);
            } else if(!lhs.isDir() && !rhs.IsDir) {
                return doCompare(lhs, rhs);
            }

            if (isFileFirst) {
                return lhs.IsDir ? 1 : -1;
            } else {
                return lhs.IsDir ? -1 : 1;
            }

        }

        abstract int doCompare(FileInfo lhs, FileInfo rhs);
    }

    public Comparator getComparator() {
        return mSortMethodComparatorHashMap.get(mSortMethod);
    }

    private Comparator<FileInfo> cmpName = new FileComparator() {
        @Override
        int doCompare(FileInfo lhs, FileInfo rhs) {
//            Logger.getLogger().d(lhs.filePath  + " $rhs$ " + rhs.filePath);

            //jdk1.7 会报 Comparison method violates its general contract!异常
//            return lhs.fileName.compareToIgnoreCase(rhs.fileName);

//            int retVal = lhs.fileName.compareToIgnoreCase(rhs.fileName);
//            if (retVal > 0) {
//                return 1;
//            } else if (retVal < 0) {
//                return -1;
//
//            } else {
//                return 0;
//            }

            return compareToIgnoreCase(lhs.fileName, rhs.fileName);
        }
    };

    private Comparator<FileInfo> cmpSize = new FileComparator() {
        @Override
        int doCompare(FileInfo lhs, FileInfo rhs) {
            return longToCompareInt(lhs.fileSize - rhs.fileSize);
        }
    };

    private Comparator<FileInfo> cmpDate = new FileComparator() {
        @Override
        int doCompare(FileInfo lhs, FileInfo rhs) {
            return longToCompareInt(lhs.ModifiedDate - rhs.ModifiedDate);
        }
    };

    private Comparator<FileInfo> cmpType = new FileComparator() {
        @Override
        int doCompare(FileInfo lhs, FileInfo rhs) {
            int result = FileUtils.getFileExtension(lhs.fileName).compareToIgnoreCase(
                    FileUtils.getFileExtension(rhs.fileName)
            );
            if (result != 0) {
                return result;
            }

            return FileUtils.getFileExtension(lhs.fileName).compareToIgnoreCase(
                    FileUtils.getFileExtension(rhs.fileName)
            );
        }
    };

    private int longToCompareInt(long result) {
        return result > 0 ? 1 : (result == 0) ? 0 : -1;
    }

    private  int compareToIgnoreCase(CharSequence me, CharSequence another) {
        // Code adapted from String#compareTo
        int myLen = me.length(), anotherLen = another.length();
        int myPos = 0, anotherPos = 0, result;
        int end = (myLen < anotherLen) ? myLen : anotherLen;
        while (myPos < end) {
            if ((result = Character.toLowerCase(me.charAt(myPos++))
                    - Character.toLowerCase(another.charAt(anotherPos++))) != 0) {
                return result;
            }
        }
        return myLen - anotherLen;
    }

}
