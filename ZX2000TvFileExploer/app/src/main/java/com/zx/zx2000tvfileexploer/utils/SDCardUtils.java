package com.zx.zx2000tvfileexploer.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.zx.zx2000tvfileexploer.utils.SDCardUtils.StorageInfo.TYPE_SD;
import static com.zx.zx2000tvfileexploer.utils.SDCardUtils.StorageInfo.TYPE_USB;
import static com.zx.zx2000tvfileexploer.utils.SDCardUtils.StorageInfo.TYPE_ext;

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
        if (dir == null) {
            return false;
        }
        File file = new File(dir);
        if (file.exists() && file.canRead()) {
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
     *
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

    public static class StorageInfo {

        public static final String TYPE_SD = "sd";
        public static final String TYPE_USB = "usb";
        public static final String TYPE_ext = "ext";

        public String path;
        public String state;
        public boolean isRemoveable;

        public boolean isOnlyReader;

        public String type;

        public String label;

        public long total;

        public long free;

        public StorageInfo() {
        }

        public StorageInfo(StorageInfo info) {
            this.path = info.path;
            this.state = info.state;
            this.isRemoveable = info.isRemoveable;
        }

        public StorageInfo(String path) {
            this.path = path;
        }

        public boolean isMounted() {
            return "mounted".equals(state);
        }
    }

    /**
     * 获取所有存储路径
     * <p/>
     * 返回：/mnt/external_sd
     *
     * @return String
     */

    public static List<StorageInfo> listAvaliableStorage(Context context) {
        ArrayList<StorageInfo> storagges = new ArrayList();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
            getVolumeList.setAccessible(true);
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);

            Class<?> storageVolumeClazz = null;

            if (invokes != null) {
                StorageInfo info = null;

                storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
                Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");

                int usbIndex = 1;

                for (int i = 0; i < invokes.length; i++) {
                    Object obj = invokes[i];

                    Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                    String path = (String) getPath.invoke(obj, new Object[0]);

                    Method getLabel = obj.getClass().getMethod("getUserLabel", new Class[0]);
                    String label = (String) getUserLabel.invoke(obj);
                    Logger.getLogger().i("label " + label);

                    info = new StorageInfo(path);

                    File file = new File(info.path);
                    Logger.getLogger().i("path " + path + " canWrite: ");
                    if ((file.exists()) && (file.isDirectory()) && (file.canWrite())) {
                        Method isRemovable = obj.getClass().getMethod("isRemovable", new Class[0]);
                        String state = null;
                        try {
                            Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);
                            state = (String) getVolumeState.invoke(storageManager, info.path);
                            info.state = state;
                            getDiskType(context, info);
                            if (TYPE_USB.equals(info.type)) {
                                info.label += String.valueOf(usbIndex);
                                usbIndex++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (info.isMounted()) {
                            getDiskInfo(info);
                            info.isRemoveable = ((Boolean) isRemovable.invoke(obj, new Object[0])).booleanValue();
                            storagges.add(info);
                        }
                    }
                }
            }
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {

        }
        storagges.trimToSize();

        return storagges;
    }

    /**
     * 6.0获取外置sdcard和U盘路径，并区分
     *
     * @param mContext
     * @param keyword  SD = "内部存储"; EXT = "SD卡"; USB = "U盘"
     * @return
     */
    public static String getStoragePath(Context mContext, String keyword) {
        String targetpath = "";
        StorageManager mStorageManager = (StorageManager) mContext
                .getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");

            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");

            Method getPath = storageVolumeClazz.getMethod("getPath");

            Object result = getVolumeList.invoke(mStorageManager);

            final int length = Array.getLength(result);

            Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");


            for (int i = 0; i < length; i++) {

                Object storageVolumeElement = Array.get(result, i);

                String userLabel = (String) getUserLabel.invoke(storageVolumeElement);

                String path = (String) getPath.invoke(storageVolumeElement);

                if (userLabel.contains(keyword)) {
                    targetpath = path;
                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return targetpath;
    }

    private static StorageInfo getDiskType(Context mContext, StorageInfo info) {
        if (info.path.contains(TYPE_SD) || info.path.contains("emulated")) {
            info.type = TYPE_SD;
            info.label = mContext.getResources().getString(R.string.label_sd);
        }

        if (info.path.contains(TYPE_USB)) {
            info.type = TYPE_USB;
            info.label = mContext.getResources().getString(R.string.label_usb);
        }

        if (info.path.contains(TYPE_ext)) {
            info.type = TYPE_ext;
            info.label = mContext.getResources().getString(R.string.label_ext);
        }

        return info;
    }

    /**
     * 获取TF或者USB存储路径
     * <p/>
     * 返回：/mnt/external_sd
     *
     * @return String
     */

    public static String getExternalStorageDirectoryPath(boolean getTF) {
        String path = null;
        ArrayList<String> list = getExternalStorageDirectory();
        if (list.size() <= 1) {
            return null;
        }
//        String internalStoragePath = getInternalStorageDirectoryPath();
//        int index = list.indexOf(internalStoragePath);
//        Logger.getLogger().d("index: " + index + internalStoragePath);
//        if (getTF) {
//            path = list.get(index + 1);
//        } else {
//            if (list.size() > 2) {
//                path = list.get(index + 2);
//            }
//        }

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
        if (size <= 0) {
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


    public static StorageInfo getSDCardInfo() {
        String sDcString = Environment.getExternalStorageState();

        if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
            File pathFile = Environment.getExternalStorageDirectory();

            try {
                android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());

                long nTotalBlocks = statfs.getBlockCount();

                long nBlocSize = statfs.getBlockSize();

                long nAvailaBlock = statfs.getAvailableBlocks();

                long nFreeBlock = statfs.getFreeBlocks();

                StorageInfo info = new StorageInfo();
                info.total = nTotalBlocks * nBlocSize;
                info.free = nAvailaBlock * nBlocSize;
                return info;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, e.toString());
            }
        }

        return null;
    }

    public static void getDiskInfo(StorageInfo info) {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            File lFile = new File(info.path);
            if (lFile.exists()) {
                try {
                    android.os.StatFs statfs = new android.os.StatFs(lFile.getPath());
                    long nTotalBlocks = statfs.getBlockCount();
                    long nBlocSize = statfs.getBlockSize();
                    long nAvailaBlock = statfs.getAvailableBlocks();
                    long nFreeBlock = statfs.getFreeBlocks();
                    info.total = nTotalBlocks * nBlocSize;
                    info.free = nAvailaBlock * nBlocSize;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, e.toString());
                }
            }

        }
    }

    public static StorageInfo getDiskInfo(String path) {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            File lFile = new File(path);
            if (lFile.exists()) {
                try {
                    android.os.StatFs statfs = new android.os.StatFs(lFile.getPath());
                    long nTotalBlocks = statfs.getBlockCount();
                    long nBlocSize = statfs.getBlockSize();
                    long nAvailaBlock = statfs.getAvailableBlocks();
                    long nFreeBlock = statfs.getFreeBlocks();
                    StorageInfo info = new StorageInfo();
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
        if (srcFile.isDirectory()) {
            srcSize = FileUtils.getFolderSize(srcFile);
        } else {
            srcSize = srcFile.length();
        }

        long destFreeSize = getDiskInfo(dst).free;
        if (destFreeSize > srcSize) {
            return true;
        }

        return false;
    }

    public static boolean isDstHaveFreeSpace(List<FileInfo> listFile, String dst) {
        long srcSizeTotal = 0;
        Log.i(TAG, "  **************** " + listFile.size());
        for (FileInfo fileInfo : listFile) {
            File file = new File(fileInfo.filePath);
            Log.i(TAG, "*************** " + fileInfo.filePath);
            if (file.isDirectory()) {
                srcSizeTotal += FileUtils.getFolderSize(file.getAbsoluteFile());
            } else {
                srcSizeTotal += file.length();
            }
        }

        long dstFreeSpace = getDiskInfo(dst).free;
        Log.i(TAG, "isDstHaveFreeSpace() .dstFreeSpace: " + dstFreeSpace + " srcSizeTotal: " + srcSizeTotal);
        if (dstFreeSpace > srcSizeTotal) {
            return true;
        }

        return false;
    }
}