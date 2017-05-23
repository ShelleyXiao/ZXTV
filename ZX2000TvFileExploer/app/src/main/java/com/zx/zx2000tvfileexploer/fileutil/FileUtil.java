package com.zx.zx2000tvfileexploer.fileutil;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.interfaces.OnProgressUpdate;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2017-05-17
 * Time: 14:58
 * Company: zx
 * Description:
 * FIXME
 */


public class FileUtil {

    // reserved characters by OS, shall not be allowed in file names
    private static final String FOREWARD_SLASH = "/";
    private static final String BACKWARD_SLASH = "\\";
    private static final String COLON = ":";
    private static final String ASTERISK = "*";
    private static final String QUESTION_MARK = "?";
    private static final String QUOTE = "\"";
    private static final String GREATER_THAN = ">";
    private static final String LESS_THAN = "<";

    private static final String FAT = "FAT";

    public interface ErrorCallBack {

        /**
         * Callback fired when file being created in process already exists
         *
         * @param file
         */
        void exists(FileInfo file);

        /**
         * Callback fired when creating new file/directory and required storage access framework permission
         * to access SD Card is not available
         *
         * @param file
         */
        void launchSAF(FileInfo file);

        /**
         * Callback fired when renaming file and required storage access framework permission to access
         * SD Card is not available
         *
         * @param file
         * @param file1
         */
        void launchSAF(FileInfo file, FileInfo file1);

        /**
         * Callback fired when we're done processing the operation
         *
         * @param hFile
         * @param b     defines whether operation was successful
         */
        void done(FileInfo hFile, boolean b);

        /**
         * Callback fired when an invalid file name is found.
         *
         * @param file
         */
        void invalidName(FileInfo file);
    }

    /**
     * Copy a file. The target file may even be on external SD card for Kitkat.
     *
     * @param source The source file
     * @param target The target file
     * @return true if the copying was successful.
     */
    @SuppressWarnings("null")
    private static boolean copyFile(final File source, final File target, Context context) {
        FileInputStream inStream = null;
        OutputStream outStream = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inStream = new FileInputStream(source);

            // First try the normal way
            if (isWritable(target)) {
                // standard way
                outStream = new FileOutputStream(target);
                inChannel = inStream.getChannel();
                outChannel = ((FileOutputStream) outStream).getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Storage Access Framework
                    DocumentFile targetDocument = getDocumentFile(target, false, context);
                    outStream =
                            context.getContentResolver().openOutputStream(targetDocument.getUri());
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    // Workaround for Kitkat ext SD card
                    Uri uri = MediaStoreHack.getUriFromFile(target.getAbsolutePath(), context);
                    outStream = context.getContentResolver().openOutputStream(uri);
                } else {
                    return false;
                }

                if (outStream != null) {
                    // Both for SAF and for Kitkat, write to output stream.
                    byte[] buffer = new byte[16384]; // MAGIC_NUMBER
                    int bytesRead;
                    while ((bytesRead = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }

            }
        } catch (Exception e) {
            Log.e("AmazeFileUtils",
                    "Error when copying file from " + source.getAbsolutePath() + " to " + target.getAbsolutePath(), e);
            return false;
        } finally {
            try {
                inStream.close();
            } catch (Exception e) {
                // ignore exception
            }

            try {
                outStream.close();
            } catch (Exception e) {
                // ignore exception
            }

            try {
                inChannel.close();
            } catch (Exception e) {
                // ignore exception
            }

            try {
                outChannel.close();
            } catch (Exception e) {
                // ignore exception
            }
        }
        return true;
    }

    public static OutputStream getOutputStream(final File target, Context context) throws Exception {
        return getOutputStream(target, context, 0);
    }

    public static OutputStream getOutputStream(final File target, Context context, long s) throws Exception {
        OutputStream outStream = null;
        try {
            // First try the normal way
            if (isWritable(target)) {
                // standard way
                outStream = new FileOutputStream(target);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Storage Access Framework
                    DocumentFile targetDocument = getDocumentFile(target, false, context);
                    outStream = context.getContentResolver().openOutputStream(targetDocument.getUri());
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    // Workaround for Kitkat ext SD card
                    return MediaStoreHack.getOutputStream(context, target.getPath());
                }
            }
        } catch (Exception e) {
            Log.e("AmazeFileUtils",
                    "Error when copying file from " + target.getAbsolutePath(), e);
            throw new Exception();
        }
        return outStream;
    }

    public static boolean deleteFile(@NonNull final File file, Context context) {
        // First try the normal deletion.
        if (file == null) return true;
        boolean fileDelete = deleteFilesInFolder(file, context);
        if (file.delete() || fileDelete)
            return true;

        // Try with Storage Access Framework.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isOnExtSdCard(file, context)) {

            DocumentFile document = getDocumentFile(file, false, context);
            return document.delete();
        }

        // Try the Kitkat workaround.
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            ContentResolver resolver = context.getContentResolver();

            try {
                Uri uri = MediaStoreHack.getUriFromFile(file.getAbsolutePath(), context);
                resolver.delete(uri, null, null);
                return !file.exists();
            } catch (Exception e) {
                Log.e("AmazeFileUtils", "Error when deleting file " + file.getAbsolutePath(), e);
                return false;
            }
        }

        return !file.exists();
    }


    /**
     * Delete all files in a folder.
     *
     * @param folder the folder
     * @return true if successful.
     */
    private static final boolean deleteFilesInFolder(final File folder, Context context) {
        boolean totalSuccess = true;
        if (folder == null)
            return false;
        if (folder.isDirectory()) {
            for (File child : folder.listFiles()) {
                deleteFilesInFolder(child, context);
            }

            if (!folder.delete())
                totalSuccess = false;
        } else {

            if (!folder.delete())
                totalSuccess = false;
        }
        return totalSuccess;
    }

    /**
     * Determine if a file is on external sd card. (Kitkat or higher.)
     *
     * @param file The file.
     * @return true if on external sd card.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isOnExtSdCard(final File file, Context c) {
        return getExtSdCardFolder(file, c) != null;
    }

    /**
     * Determine the main folder of the external SD card containing the given file.
     *
     * @param file the file.
     * @return The main folder of the external SD card containing this file, if the file is on an SD card. Otherwise,
     * null is returned.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getExtSdCardFolder(final File file, Context context) {
        String[] extSdPaths = getExtSdCardPaths(context);
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().startsWith(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Get a list of external SD card paths. (Kitkat or higher.)
     *
     * @return A list of external SD card paths.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String[] getExtSdCardPaths(Context context) {
        List<String> paths = new ArrayList<>();
        for (File file : context.getExternalFilesDirs("external")) {
            if (file != null && !file.equals(context.getExternalFilesDir("external"))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Logger.getLogger().w("Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        if (paths.isEmpty()) paths.add("/storage/sdcard1");
        return paths.toArray(new String[0]);
    }

    /**
     * Get a DocumentFile corresponding to the given file (for writing on ExtSdCard on Android 5). If the file is not
     * existing, it is created.
     *
     * @param file        The file.
     * @param isDirectory flag indicating if the file should be a directory.
     * @return The DocumentFile
     */
    public static DocumentFile getDocumentFile(final File file, final boolean isDirectory, Context context) {
        String baseFolder = getExtSdCardFolder(file, context);
        Logger.getLogger().d("getDocumentFile********baseFolder " + baseFolder);
        boolean originalDirectory = false;
        if (baseFolder == null) {
            return null;
        }

        String relativePath = null;
        try {
            String fullPath = file.getCanonicalPath();
            if (!baseFolder.equals(fullPath))
                relativePath = fullPath.substring(baseFolder.length() + 1);
            else originalDirectory = true;
        } catch (IOException e) {
            return null;
        } catch (Exception f) {
            originalDirectory = true;
            //continue
        }
        String as = PreferenceManager.getDefaultSharedPreferences(context).getString("URI", null);

        Uri treeUri = null;
        if (as != null) treeUri = Uri.parse(as);
        if (treeUri == null) {
            return null;
        }

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(context, treeUri);
        if (originalDirectory) return document;
        String[] parts = relativePath.split("\\/");
        for (int i = 0; i < parts.length; i++) {
            DocumentFile nextDocument = document.findFile(parts[i]);

            if (nextDocument == null) {
                if ((i < parts.length - 1) || isDirectory) {
                    nextDocument = document.createDirectory(parts[i]);
                } else {
                    nextDocument = document.createFile("image", parts[i]);
                }
            }
            document = nextDocument;
        }

        return document;
    }

    /**
     * Check if a file is writable. Detects write issues on external SD card.
     *
     * @param file The file
     * @return true if the file is writable.
     */
    public static boolean isWritable(final File file) {
        if (file == null)
            return false;
        boolean isExisting = file.exists();

        try {
            FileOutputStream output = new FileOutputStream(file, true);
            try {
                output.close();
            } catch (IOException e) {
                // do nothing.
            }
        } catch (FileNotFoundException e) {
            return false;
        }
        boolean result = file.canWrite();

        // Ensure that file is not created during this process.
        if (!isExisting) {
            file.delete();
        }

        return result;
    }


    // Utility methods for Android 5

    /**
     * Check for a directory if it is possible to create files within this directory, either via normal writing or via
     * Storage Access Framework.
     *
     * @param folder The directory
     * @return true if it is possible to write in this directory.
     */
    public static boolean isWritableNormalOrSaf(final File folder, Context c) {
        // Verify that this is a directory.
        if (folder == null)
            return false;
        if (!folder.exists() || !folder.isDirectory()) {
            return false;
        }

        // Find a non-existing file in this directory.
        int i = 0;
        File file;
        do {
            String fileName = "AugendiagnoseDummyFile" + (++i);
            file = new File(folder, fileName);
        } while (file.exists());

        // First check regular writability
        if (isWritable(file)) {
            return true;
        }

        // Next check SAF writability.
        DocumentFile document = getDocumentFile(file, false, c);

        if (document == null) {
            return false;
        }

        // This should have created the file - otherwise something is wrong with access URL.
        boolean result = document.canWrite() && file.exists();

        // Ensure that the dummy file is not remaining.
        deleteFile(file, c);
        return result;
    }

    public static long folderSize(File directory, OnProgressUpdate<Long> updateState) {
        long length = 0;
        try {
            for (File file : directory.listFiles()) {
                if (file.isFile())
                    length += file.length();
                else
                    length += folderSize(file, updateState);

                if (updateState != null)
                    updateState.onUpdate(length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }

    public static boolean canListFiles(File f) {
        try {
            return f.canRead() && f.isDirectory();
        } catch (Exception e) {
            return false;
        }
    }


    private static int checkFolder(final File folder, Context context) {
        Logger.getLogger().d("folder name " + folder.getAbsolutePath());
        boolean lol = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        if (lol) {

            boolean ext = FileUtil.isOnExtSdCard(folder, context);
            if (ext) {


                if (!folder.exists() || !folder.isDirectory()) {
                    Logger.getLogger().i("**************d* 0");
                    return 0;
                }

                // On Android 5, trigger storage access framework.
                if (!FileUtil.isWritableNormalOrSaf(folder, context)) {
                    return 2;
                }


                return 1;
            }
        } else if (Build.VERSION.SDK_INT == 19) {
            // Assume that Kitkat workaround works
            if (FileUtil.isOnExtSdCard(folder, context)) return 1;

        }

        // file not on external sd card
        if (FileUtil.isWritable(new File(folder, "DummyFile"))) {
            return 1;
        } else {
            Logger.getLogger().i("*************** 0");
            return 0;
        }
    }

    /**
     * Checks whether the target path exists or is writable
     *
     * @param f       the target path
     * @param context
     * @return 1 if exists or writable, 0 if not writable
     */
    public static int checkFolder(final String f, Context context) {
        if (f == null) return 0;
        if (f.startsWith(OTGUtil.PREFIX_OTG))
            return 1;

        File folder = new File(f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && FileUtil.isOnExtSdCard(folder, context)) {

            if (FileUtil.isWritableNormalOrSaf(folder, context)) {
                return 2;
            }

            if (!folder.exists() || !folder.isDirectory()) {
                return 0;
            }

            // On Android 5, trigger storage access framework.

        } else if (Build.VERSION.SDK_INT == 19 && FileUtil.isOnExtSdCard(folder, context)) {
            // Assume that Kitkat workaround works
            return 1;
        } else if (folder.canWrite()) {
            return 1;
        } else {
            return 0;
        }
        return 0;
    }

    /**
     * Well, we wouldn't want to copy when the target is inside the source
     * otherwise it'll end into a loop
     *
     * @param sourceFile
     * @param targetFile
     * @return true when copy loop is possible
     */
    public static boolean isCopyLoopPossible(FileInfo sourceFile, FileInfo targetFile) {
        return targetFile.getFilePath().contains(sourceFile.getFilePath());
    }

    /**
     * Create a folder. The folder may even be on external SD card for Kitkat.
     *
     * @param file The folder to be created.
     * @return True if creation was successful.
     * @deprecated use {@link #mkdirs(Context, FileInfo)}
     */
    public static boolean mkdir(final File file, Context context) {
        if (file == null)
            return false;
        if (file.exists()) {
            // nothing to create.
            return file.isDirectory();
        }

        // Try the normal way
        if (file.mkdirs()) {
            return true;
        }

        // Try with Storage Access Framework.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && FileUtil.isOnExtSdCard(file, context)) {
            DocumentFile document = getDocumentFile(file, true, context);
            // getDocumentFile implicitly creates the directory.
            return document.exists();
        }

        return false;
    }

    public static boolean mkdirs(Context context, FileInfo file) {
        boolean isSuccessful = true;
        switch (file.mode) {
            case OTG:
                DocumentFile documentFile = RootHelper.getDocumentFile(file.getFilePath(), context, true);
                isSuccessful = documentFile != null;
                break;
            case FILE:
                isSuccessful = mkdir(new File(file.getFilePath()), context);
                break;
            default:
                isSuccessful = true;
                break;
        }

        return isSuccessful;
    }

    public static void rename(final FileInfo oldFile, final FileInfo newFile, final boolean rootMode,
                              final Context context, final ErrorCallBack errorCallBack) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // check whether file names for new file are valid or recursion occurs
                if (isNewDirectoryRecursive(newFile) ||
                        !isFileNameValid(newFile.getName(context))) {
                    errorCallBack.invalidName(newFile);
                    return null;
                }

                if (newFile.exists()) {
                    errorCallBack.exists(newFile);
                    return null;
                }

                if (oldFile.isOtgFile()) {
                    DocumentFile oldDocumentFile = RootHelper.getDocumentFile(oldFile.getFilePath(), context, false);
                    DocumentFile newDocumentFile = RootHelper.getDocumentFile(newFile.getFilePath(), context, false);
                    if (newDocumentFile != null) {
                        errorCallBack.exists(newFile);
                        return null;
                    }
                    errorCallBack.done(newFile, oldDocumentFile.renameTo(newFile.getName(context)));
                    return null;
                } else {

                    File file = new File(oldFile.getFilePath());
                    File file1 = new File(newFile.getFilePath());
                    switch (oldFile.getMode()) {
                        case FILE:
                            int mode = checkFolder(file.getParentFile(), context);
                            Logger.getLogger().d(">>>>>>>>>>>> mode " + mode);
                            if (mode == 2) {
                                errorCallBack.launchSAF(oldFile, newFile);
                            } else if (mode == 1 || mode == 0) {
                                try {
                                    FileUtil.renameFolder(file, file1, context);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                boolean a = !file.exists() && file1.exists();
                                errorCallBack.done(newFile, a);
                                return null;
                            }
                            break;


                    }
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private static boolean rename(File f, String name, boolean root){
        String newPath = f.getParent() + "/" + name;
        if (f.getParentFile().canWrite()) {
            return f.renameTo(new File(newPath));
        }

        return false;
    }

    static boolean renameFolder(@NonNull final File source, @NonNull final File target,
                                Context context) {
        // First try the normal rename.
        if (rename(source, target.getName(), false)) {
            return true;
        }
        if (target.exists()) {
            return false;
        }

        // Try the Storage Access Framework if it is just a rename within the same parent folder.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && source.getParent().equals(target.getParent()) && FileUtil.isOnExtSdCard(source, context)) {
            DocumentFile document = getDocumentFile(source, true, context);
            if (document.renameTo(target.getName())) {
                return true;
            }
        }

        // Try the manual way, moving files individually.
        if (!mkdir(target, context)) {
            return false;
        }

        File[] sourceFiles = source.listFiles();

        if (sourceFiles == null) {
            return true;
        }

        for (File sourceFile : sourceFiles) {
            String fileName = sourceFile.getName();
            File targetFile = new File(target, fileName);
            if (!copyFile(sourceFile, targetFile, context)) {
                // stop on first error
                return false;
            }
        }
        // Only after successfully copying all files, delete files on source folder.
        for (File sourceFile : sourceFiles) {
            if (!deleteFile(sourceFile, context)) {
                // stop on first error
                return false;
            }
        }
        return true;
    }

    /**
     * Validates file name
     * special reserved characters shall not be allowed in the file names on FAT filesystems
     *
     * @param fileName the filename, not the full path!
     * @return boolean if the file name is valid or invalid
     */
    public static boolean isFileNameValid(String fileName) {

        // TODO: check file name validation only for FAT filesystems
        return !(fileName.contains(ASTERISK) || fileName.contains(BACKWARD_SLASH) ||
                fileName.contains(COLON) || fileName.contains(FOREWARD_SLASH) ||
                fileName.contains(GREATER_THAN) || fileName.contains(LESS_THAN) ||
                fileName.contains(QUESTION_MARK) || fileName.contains(QUOTE));
    }

    /**
     * Check if a file is readable.
     *
     * @param file The file
     * @return true if the file is reabable.
     */
    public static boolean isReadable(final File file) {
        if (file == null)
            return false;
        if (!file.exists()) return false;

        boolean result;
        try {
            result = file.canRead();
        } catch (SecurityException e) {
            return false;
        }

        return result;
    }

    public static void scanFile(String path, Context c) {
        System.out.println(path + " " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 19) {
            MediaScannerConnection.scanFile(c, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {

                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } else {
            Uri contentUri = Uri.fromFile(new File(path));
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
            c.sendBroadcast(mediaScanIntent);
        }
    }

    public static float readableFileSizeFloat(long size) {
        if (size <= 0)
            return 0;
        float digitGroups = (float) (size / (1024 * 1024));
        return digitGroups;
    }

    public static boolean isNewDirectoryRecursive(FileInfo file) {
        return file.getName().equals(file.getParentName());
    }

    public static void mkdir(@NonNull final FileInfo file, final Context context, final boolean rootMode,
                             @NonNull final ErrorCallBack errorCallBack) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // checking whether filename is valid or a recursive call possible
                if (isNewDirectoryRecursive(file) ||
                        !isFileNameValid(file.getName(context))) {
                    errorCallBack.invalidName(file);
                    return null;
                }

                if (file.exists()) {
                    errorCallBack.exists(file);
                    return null;
                }
                if (file.isOtgFile()) {

                    // first check whether new directory already exists
                    DocumentFile directoryToCreate = RootHelper.getDocumentFile(file.getFilePath(), context, false);
                    if (directoryToCreate != null) errorCallBack.exists(file);

                    DocumentFile parentDirectory = RootHelper.getDocumentFile(file.getParent(context), context, false);
                    if (parentDirectory.isDirectory()) {
                        parentDirectory.createDirectory(file.getName(context));
                        errorCallBack.done(file, true);
                    } else errorCallBack.done(file, false);
                    return null;
                } else {
                    if (file.isLocal()) {
                        int mode = checkFolder(new File(file.getParent(context)), context);
//                        int mode = checkFolder(file.getParentName(), context);
                        Logger.getLogger().e("mode = " + mode);
                        if (mode == 2) {
                            errorCallBack.launchSAF(file);
                            return null;
                        }
                        if (mode == 1 || mode == 0)
                            FileUtil.mkdirs(context, file);

                        errorCallBack.done(file, file.exists());
                        return null;
                    }

                    errorCallBack.done(file, file.exists());
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public static void guideDialogForLEXA(final Context context, String path) {
        final MaterialDialog.Builder x = new MaterialDialog.Builder(context);
        x.title(R.string.needsaccess);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.lexadrawer, null);
        x.customView(view, true);
        // textView
        TextView textView = (TextView) view.findViewById(R.id.description);
        textView.setText(context.getResources().getString(R.string.needsaccesssummary) + path + context.getResources().getString(R.string.needsaccesssummary1));
        ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.sd_operate_step);
        x.positiveText(R.string.open);
        x.negativeText(R.string.cancle);
        x.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog materialDialog) {
                Intent intent = new Intent(GlobalConsts.LAUNCHER_LEXA);
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
                manager.sendBroadcast(intent);
            }

            @Override
            public void onNegative(MaterialDialog materialDialog) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
        final MaterialDialog y = x.build();
        y.show();
    }
}
