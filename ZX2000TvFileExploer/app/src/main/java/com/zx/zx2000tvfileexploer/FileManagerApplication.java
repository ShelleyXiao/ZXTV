package com.zx.zx2000tvfileexploer;

import android.app.Application;

import com.zx.zx2000tvfileexploer.fileutil.CopyHelper;
import com.zx.zx2000tvfileexploer.utils.SDCardUtils;

import java.util.List;

/**
 * Created by Shelley on 2016/7/24.
 */
public class FileManagerApplication extends Application {

    private CopyHelper mCopyHelper;

    private SDCardUtils.StorageInfo flash;
    private SDCardUtils.StorageInfo usb;
    private SDCardUtils.StorageInfo tf;

    private  static FileManagerApplication instance;

    public static FileManagerApplication getInstance() {
        return instance;

    }


    @Override
    public void onCreate() {
        super.onCreate();

        mCopyHelper = new CopyHelper(this);

        instance = this;

        setStorageInfo();
    }

    public CopyHelper getCopyHelper(){
        return mCopyHelper;
    }

    public void setStorageInfo() {
        List<SDCardUtils.StorageInfo> storageInfos = SDCardUtils.listAvaliableStorage(this);
        for(SDCardUtils.StorageInfo storageInfo : storageInfos) {
            if(storageInfo.path.contains("usb")) {
                usb = new SDCardUtils.StorageInfo(storageInfo);
            } else if(storageInfo.path.contains("extsdcard")){
                tf = new SDCardUtils.StorageInfo(storageInfo);
            } else {
                flash = new SDCardUtils.StorageInfo(storageInfo);
            }

        }
    }

    public SDCardUtils.StorageInfo getFlash() {

        if(flash != null)
            return flash;
        else return null;

    }

    public String getFlashAbsolutePath() {
        if(null == flash) {
            return null;
        }

        return flash.path;
    }

    public SDCardUtils.StorageInfo getUSB() {

        if(usb != null)
            return usb;
        else return null;
    }

    public String getUSBAbsolutePath() {
        if(null == usb) {
            return null;
        }

        return usb.path;
    }

    public SDCardUtils.StorageInfo getTF() {
        if(tf != null)
        return tf;
        else return null;
    }

    public String getTFAbsolutePath() {
        if(null == tf) {
            return null;
        }

        return tf.path;
    }
}
