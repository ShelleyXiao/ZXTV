package com.zx.zx2000onlinevideo.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 类说明： 	SD卡工具类
 *
 * @version 1.0
 * @date 2012-2-7
 */
public class SDCardUtils {

    private static final String TAG = "SDCardUtils";

    private static final String TYPE_CACHE = "cache";

    private static final String TYPE_FILES = "files";

    /**
     * SD卡是否挂载
     *
     * @return
     */
    public static boolean isMounted() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED) ? true : false;
    }

    public static boolean isMounted(String dir) {
        if(dir == null) {
            return false;
        }
        File file = new File(dir);
        if(file.exists() && file.canRead()) {
            return true;
        }


        return false;
    }

    /**
     * 获取扩展存储file的绝对路径
     * <p/>
     * 返回：sdcard/Android/data/{package_name}/files/
     *
     * @param context
     * @return
     */
    public static String getExternalFileDir(Context context) {
        return getExternalDir(context, TYPE_FILES);
    }

    /**
     * 获取扩展存储cache的绝对路径
     * <p/>
     * 返回：sdcard/Android/data/{package_name}/cache/
     *
     * @param context
     * @return
     */
    public static String getExternalCacheDir(Context context) {

        return getExternalDir(context, TYPE_CACHE);
    }

    private static String getExternalDir(Context context, String type) {

        StringBuilder sb = new StringBuilder();

        if (context == null)
            return null;

        if (!isMounted()) {

            if (type.equals(TYPE_CACHE)) {
                sb.append(context.getCacheDir()).append(File.separator);
            } else {
                sb.append(context.getFilesDir()).append(File.separator);
            }
        }

        File file = null;

        if (type.equals(TYPE_CACHE)) {
            file = context.getExternalCacheDir();
        } else {
            file = context.getExternalFilesDir(null);
        }

        // In some case, even the sd card is mounted,
        // getExternalCacheDir will return null
        // may be it is nearly full.

        if (file != null) {
            sb.append(file.getAbsolutePath()).append(File.separator);
        } else {
            sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/").append(context.getPackageName())
                    .append("/").append(type).append("/").append(File.separator).toString();
        }

        return sb.toString();
    }

    /**
     * 获取内部存储路径
     * <p/>
     * 返回：/mnt/external_sd
     *
     * @return String
     */

    public static String getInternalStorageDirectoryPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }


    /**
     * 获取所有存储路径
     * <p/>
     * 返回：/mnt/external_sd
     * @return String
     */
    public static ArrayList<String> getExternalStorageDirectory() {
        String dir = new String();
        ArrayList<String> list = new ArrayList<String>();

        try {
            Runtime rt = Runtime.getRuntime();
            Process ps = rt.exec("mount");
            InputStream is = ps.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("secure")) continue;
                if (line.contains("asec")) continue;
//				Logger.getLogger().d("  " + line);
                if (line.contains("fat")) {
                    String[] column = line.split(" ");
                    if (column != null && column.length > 1) {
//						dir = dir.concat(column[1]);
                        dir = column[1];
                        if (dir != null && !list.contains(dir)) {
                            list.add(dir);
                            Logger.getLogger().d("1   " + dir);
                        }
                    }
                } else if (line.contains("fues")) {
                    String[] column = line.split(" ");
                    if (column != null && column.length > 1) {
//						dir = dir.concat(column[1] );
                        dir = column[1];
                        if (dir != null && !list.contains(dir)) {
                            list.add(dir);
                            Logger.getLogger().d("2   " + dir);
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }


    /**
     * 获取TF或者USB存储路径
     * <p/>
     * 返回：/mnt/external_sd
     * @return String
     */

    public static String getExternalStorageDirectoryPath(boolean getTF) {
        String path = null;
        ArrayList<String> list = getExternalStorageDirectory();
        if (list.size() <= 1) {
            return null;
        }
        String internalStoragePath = getInternalStorageDirectoryPath();
        int index = list.indexOf(internalStoragePath);
        Logger.getLogger().d("index: " + index + internalStoragePath);
        if (getTF) {
            path = list.get(index + 1);
        } else {
            if (list.size() > 2) {
                path = list.get(index + 2);
            }
        }
        Logger.getLogger().d("       " + path);
        return path;
    }

    public static String getFlashDirectory() {
        return SDCardUtils.getInternalStorageDirectoryPath();
    }

    public static String getSdcardDirectory() {
        return SDCardUtils.getExternalStorageDirectoryPath(true);
    }

    public static String getOTADirectory() {
        return SDCardUtils.getExternalStorageDirectoryPath(false);
    }

    // storage, G M K B
    public static String convertStorage(long size) {
        Logger.getLogger().d("szie: " + size);
        if(size <= 0) {
            return 0 + " ";
        }
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    public static class SDCardInfo {
        public long total;

        public long free;
    }

    public static SDCardInfo getSDCardInfo() {
        String sDcString = Environment.getExternalStorageState();

        if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
            File pathFile = Environment.getExternalStorageDirectory();

            try {
                android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());

                long nTotalBlocks = statfs.getBlockCount();

                long nBlocSize = statfs.getBlockSize();

                long nAvailaBlock = statfs.getAvailableBlocks();

                long nFreeBlock = statfs.getFreeBlocks();

                SDCardInfo info = new SDCardInfo();
                info.total = nTotalBlocks * nBlocSize;
                info.free = nAvailaBlock * nBlocSize;
                return info;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, e.toString());
            }
        }

        return null;
    }

    public static SDCardInfo getDiskInfo(String path) {
        String status = Environment.getExternalStorageState();
        if(status.equals(Environment.MEDIA_MOUNTED)) {
            File lFile = new File(path);
            if(lFile.exists()) {
                try {
                    android.os.StatFs statfs = new android.os.StatFs(lFile.getPath());
                    long nTotalBlocks = statfs.getBlockCount();
                    long nBlocSize = statfs.getBlockSize();
                    long nAvailaBlock = statfs.getAvailableBlocks();
                    long nFreeBlock = statfs.getFreeBlocks();
                    SDCardInfo info = new SDCardInfo();
                    info.total = nTotalBlocks * nBlocSize;
                    info.free = nAvailaBlock * nBlocSize;
                    return info;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, e.toString());
                }
            }

        }

        return null;
    }


    public static boolean isDstHaveFreeSpace(String src, String dst) {
        File srcFile = new File(src);
        long srcSize = 0;
        if(srcFile.isDirectory()) {
            srcSize = FileUtils.getFolderSize(srcFile);
        } else {
            srcSize = srcFile.length();
        }

        long destFreeSize = getDiskInfo(dst).free;
        if(destFreeSize > srcSize) {
            return true;
        }

        return false;
    }

}