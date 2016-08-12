package com.zx.zx2000onlinevideo;

import android.app.Application;
import android.content.Context;

import com.example.aaron.library.MLog;
import com.youku.player.base.YoukuPlayerInit;


/**
 * User: ShaudXiao
 * Date: 2016-08-02
 * Time: 17:04
 * Company: zx
 * Description:
 * FIXME
 */

public class OnlineVideoApp extends Application {


    public static Context context;

    private static OnlineVideoApp onlineVideoApp;

    public static OnlineVideoApp getInstance() {
        return onlineVideoApp;
    }

    public static Context getContext() {
        return onlineVideoApp;
    }

    @SuppressWarnings("static-access")
    @Override
    public void onCreate() {
        super.onCreate();

        onlineVideoApp = this;

        context = getApplicationContext();
        YoukuPlayerInit.init(context);

        if(BuildConfig.LOG_DEBUG == true) {
            MLog.init(true);
        } else {
            MLog.init(false);
        }
    }


}
