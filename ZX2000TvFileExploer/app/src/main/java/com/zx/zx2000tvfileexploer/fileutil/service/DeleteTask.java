package com.zx.zx2000tvfileexploer.fileutil.service;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.provider.DocumentFile;
import android.widget.Toast;

import com.zx.zx2000tvfileexploer.FileManagerApplication;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.RootHelper;

import java.util.ArrayList;

public class DeleteTask extends AsyncTask<ArrayList<FileInfo>, String, Boolean> {
    private ArrayList<FileInfo> files;
    private Context cd;
    private boolean rootMode;


    public DeleteTask(ContentResolver c, Context cd) {
        this.cd = cd;
    }


    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(cd, values[0], Toast.LENGTH_SHORT).show();
    }

    protected Boolean doInBackground(ArrayList<FileInfo>... p1) {
        files = p1[0];
        boolean b = true;
        if(files.size()==0)return true;

        if (files.get(0).isOtgFile()) {
            for (FileInfo a : files) {
                DocumentFile documentFile = RootHelper.getDocumentFile(a.getFilePath(), cd, false);
                b = documentFile.delete();
            }
        }  else {
            for(FileInfo a : files) {
                try {
                    (a).delete(cd);
                } catch (Exception e) {
                    e.printStackTrace();
                    b = false;
                }
            }
        }



        return b;
    }

    @Override
    public void onPostExecute(Boolean b) {
        Intent intent = new Intent("loadlist");
        LocalBroadcastManager.getInstance(cd).sendBroadcast(intent);

        if (!b) {
            Toast.makeText(cd, cd.getResources().getString(R.string.delete_failure), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(cd, cd.getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
            FileManagerApplication.getInstance().setOppatheList(null);
        }
    }

    private void delete(final Context context, final String file) {
        final String where = MediaStore.MediaColumns.DATA + "=?";
        final String[] selectionArgs = new String[] {
                file
        };
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri filesUri = MediaStore.Files.getContentUri("external");
        // Delete the entry from the media database. This will actually delete media files.
        contentResolver.delete(filesUri, where, selectionArgs);

    }
}



