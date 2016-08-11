package com.zx.zx2000tvfileexploer;

import android.app.Application;

import com.zx.zx2000tvfileexploer.fileutil.CopyHelper;

/**
 * Created by Shelley on 2016/7/24.
 */
public class FileManagerApplication extends Application {

    private CopyHelper mCopyHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        mCopyHelper = new CopyHelper(this);
    }

    public CopyHelper getCopyHelper(){
        return mCopyHelper;
    }
}
