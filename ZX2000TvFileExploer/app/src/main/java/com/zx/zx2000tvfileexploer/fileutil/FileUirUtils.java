package com.zx.zx2000tvfileexploer.fileutil;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * User: ShaudXiao
 * Date: 2017-05-15
 * Time: 15:16
 * Company: zx
 * Description:
 * FIXME
 */


public class FileUirUtils {

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

}
