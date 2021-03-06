package com.zx.zx2000tvfileexploer.fileutil;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import java.io.File;
import java.io.OutputStream;

/**
 * User: ShaudXiao
 * Date: 2017-05-15
 * Time: 15:16
 * Company: zx
 * Description:
 * FIXME
 */


public class MediaStoreHack {

    public static Uri getContentUriByCategory(FileCategoryHelper.FileCategory fc) {
        Uri uri;
        String volumeName = "external";
        switch (fc) {
            case All:
            case APK:
                uri = MediaStore.Files.getContentUri(volumeName);
                break;
            case MUSIC:
                uri = MediaStore.Audio.Media.getContentUri(volumeName);
                break;
            case PICTURE:
                uri = MediaStore.Images.Media.getContentUri(volumeName);
                break;
            case VIDEO:
                uri = MediaStore.Video.Media.getContentUri(volumeName);
                break;
            default:
                uri = null;
        }

        return uri;
    }

    public static Uri getUriFromFile(final String path, Context context) {
        ContentResolver resolver = context.getContentResolver();

        Cursor filecursor = resolver.query(MediaStore.Files.getContentUri("external"),
                new String[]{BaseColumns._ID}, MediaStore.MediaColumns.DATA + " = ?",
                new String[]{path}, MediaStore.MediaColumns.DATE_ADDED + " desc");
        filecursor.moveToFirst();

        if (filecursor.isAfterLast()) {
            filecursor.close();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, path);
            return resolver.insert(MediaStore.Files.getContentUri("external"), values);
        } else {
            int imageId = filecursor.getInt(filecursor.getColumnIndex(BaseColumns._ID));
            Uri uri = MediaStore.Files.getContentUri("external").buildUpon().appendPath(
                    Integer.toString(imageId)).build();
            filecursor.close();
            return uri;
        }
    }

    public static OutputStream getOutputStream(Context context, String str) {
        OutputStream outputStream = null;
        Uri fileUri = getUriFromFile(str, context);
        if (fileUri != null) {
            try {
                outputStream = context.getContentResolver().openOutputStream(fileUri);
            } catch (Throwable th) {
            }
        }
        return outputStream;
    }

    public static boolean delete(final Context context, final File file) {
        final String where = MediaStore.MediaColumns.DATA + "=?";
        final String[] selectionArgs = new String[]{
                file.getAbsolutePath()
        };
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri filesUri = MediaStore.Files.getContentUri("external");
        // Delete the entry from the media database. This will actually delete media files.
        contentResolver.delete(filesUri, where, selectionArgs);
        // If the file is not a media file, create a new entry.
        if (file.exists()) {
            final ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            // Delete the created entry, such that content provider will delete the file.
            contentResolver.delete(filesUri, where, selectionArgs);
        }
        return !file.exists();
    }

}
