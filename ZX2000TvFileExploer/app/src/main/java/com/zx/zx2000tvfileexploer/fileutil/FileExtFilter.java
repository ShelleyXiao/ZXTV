package com.zx.zx2000tvfileexploer.fileutil;


import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by ShaudXiao on 2016/7/14.
 */
public class FileExtFilter implements FilenameFilter {

    private HashSet<String> contains = new HashSet<>();

    public FileExtFilter(String[] ext) {
        if (ext != null) {
            contains.addAll(Arrays.asList(ext));
        }
    }

    public boolean isContains(String ext) {
        return contains.contains(ext.toLowerCase());
    }


    @Override
    public boolean accept(File dir, String fileName) {
        File file = new File(dir + File.separator + fileName);
        if (file.isDirectory()) {
            return true;
        }

        int dot = fileName.lastIndexOf(".");
        if (dot != -1) {
            String ext = (String) fileName.subSequence(dot + 1, fileName.length());
            return contains.contains(ext.toLowerCase());
        }

        return false;
    }

}
