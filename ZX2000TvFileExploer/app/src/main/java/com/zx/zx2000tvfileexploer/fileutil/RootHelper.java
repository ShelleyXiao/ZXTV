package com.zx.zx2000tvfileexploer.fileutil;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.entity.OpenMode;

import java.io.File;
import java.util.ArrayList;

/**
 * User: ShaudXiao
 * Date: 2017-05-18
 * Time: 11:10
 * Company: zx
 * Description:
 * FIXME
 */


public class RootHelper {

    public static String parseFilePermission(File f) {
        String per = "";
        if (f.canRead()) {
            per = per + "r";
        }
        if (f.canWrite()) {
            per = per + "w";
        }
        if (f.canExecute()) {
            per = per + "x";
        }
        return per;
    }

    public static String parseDocumentFilePermission(DocumentFile file) {
        String per = "";
        if (file.canRead()) {
            per = per + "r";
        }
        if (file.canWrite()) {
            per = per + "w";
        }
        if (file.canWrite()) {
            per = per + "x";
        }
        return per;
    }

    /**
     * Callback to setting type of file to handle, while loading list of files
     */
    public interface GetModeCallBack {
        void getMode(OpenMode mode);
    }

    private static final String UNIX_ESCAPE_EXPRESSION = "(\\(|\\)|\\[|\\]|\\s|\'|\"|`|\\{|\\}|&|\\\\|\\?)";

    public static String getCommandLineString(String input) {
        return input.replaceAll(UNIX_ESCAPE_EXPRESSION, "\\\\$1");
    }

    /**
     * Loads files in a path using basic filesystem callbacks
     *
     * @param path       the path
     * @param showHidden
     * @return
     */
    public static ArrayList<FileInfo> getFilesList(String path, boolean showHidden) {
        File f = new File(path);
        ArrayList<FileInfo> files = new ArrayList<>();
        try {
            if (f.exists() && f.isDirectory()) {
                for (File x : f.listFiles()) {
                    long size = 0;
                    if (!x.isDirectory()) size = x.length();
                    FileInfo baseFile = new FileInfo(x.getPath(), parseFilePermission(x),
                            x.lastModified(), size, x.isDirectory());
                    baseFile.setFileName(x.getName());
                    baseFile.setMode(OpenMode.FILE);
                    if (showHidden) {
                        files.add(baseFile);
                    } else {
                        if (!x.isHidden()) {
                            files.add(baseFile);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        return files;
    }


    /**
     * Get a list of files using shell, supposing the path is not a SMB/OTG/Custom (*.apk/images)
     *
     * @param path
     * @param root            whether root is available or not
     * @param showHidden      to show hidden files
     * @param getModeCallBack callback to set the type of file
     * @return TODO: Avoid parsing ls
     */
    public static ArrayList<FileInfo> getFilesList(String path, boolean root, boolean showHidden,
                                                   GetModeCallBack getModeCallBack) {
        OpenMode mode = OpenMode.FILE;
        ArrayList<FileInfo> files = new ArrayList<>();
        ArrayList<String> ls;
        if (root) {
            // we're rooted and we're trying to load file with superuser
            if (!path.startsWith("/storage") && !path.startsWith("/sdcard")) {
                // we're at the root directories, superuser is required!
//                String cpath = getCommandLineString(path);
//                //ls = Shell.SU.run("ls -l " + cpath);
//                ls = runShellCommand("ls -l " + (showHidden ? "-a " : "") + "\"" + cpath + "\"");
//                if (ls != null) {
//                    for (int i = 0; i < ls.size(); i++) {
//                        String file = ls.get(i);
//                        if (!file.contains("Permission denied"))
//                            try {
//                                BaseFile array = Futils.parseName(file);
//                                array.setMode(OpenMode.ROOT);
//                                if (array != null) {
//                                    array.setName(array.getPath());
//                                    array.setPath(path + "/" + array.getPath());
//                                    if (array.getLink().trim().length() > 0) {
//                                        boolean isdirectory = isDirectory(array.getLink(), root, 0);
//                                        array.setDirectory(isdirectory);
//                                    } else array.setDirectory(isDirectory(array));
//                                    files.add(array);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                    }
//                    mode = OpenMode.ROOT;
//                }
            } else if (FileUtil.canListFiles(new File(path))) {
                // we might as well not require root to load files
                files = getFilesList(path, showHidden);
                mode = OpenMode.FILE;
            } else {
                // couldn't load files using native java filesystem callbacks
                // maybe the access is not allowed due to android system restrictions, we'll see later
                mode = OpenMode.FILE;
                files = new ArrayList<>();
            }
        } else if (FileUtil.canListFiles(new File(path))) {
            // we don't have root, so we're taking a chance to load files using basic java filesystem
            files = getFilesList(path, showHidden);
            mode = OpenMode.FILE;
        } else {
            // couldn't load files using native java filesystem callbacks
            // maybe the access is not allowed due to android system restrictions, we'll see later
            mode = OpenMode.FILE;
            files = new ArrayList<>();
        }
        if (getModeCallBack != null) getModeCallBack.getMode(mode);
        return files;
    }

    /**
     * Traverse to a specified path in OTG
     *
     * @param path
     * @param context
     * @param createRecursive flag used to determine whether to create new file while traversing to path,
     *                        in case path is not present. Notably useful in opening an output stream.
     * @return
     */
    public static DocumentFile getDocumentFile(String path, Context context, boolean createRecursive) {
        SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(context);
        String rootUriString = manager.getString(GlobalConsts.KEY_PREF_OTG, null);

        // start with root of SD card and then parse through document tree.
        DocumentFile rootUri = DocumentFile.fromTreeUri(context, Uri.parse(rootUriString));

        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {

            if (path.equals("otg:/")) break;
            if (parts[i].equals("otg:") || parts[i].equals("")) continue;
            Log.d(context.getClass().getSimpleName(), "Currently at: " + parts[i]);
            // iterating through the required path to find the end point

            DocumentFile nextDocument = rootUri.findFile(parts[i]);
            if (createRecursive) {
                if (nextDocument == null || !nextDocument.exists()) {
                    nextDocument = rootUri.createFile(parts[i].substring(parts[i].lastIndexOf(".")), parts[i]);
                    Log.d(context.getClass().getSimpleName(), "NOT FOUND! File created: " + parts[i]);
                }
            }
            rootUri = nextDocument;
        }
        return rootUri;
    }

}
