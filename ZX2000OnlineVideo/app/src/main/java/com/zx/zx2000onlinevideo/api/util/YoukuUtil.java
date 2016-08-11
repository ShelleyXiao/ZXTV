package com.zx.zx2000onlinevideo.api.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.zx.zx2000onlinevideo.bean.youku.YoukuOpenBean;

/**
 * Created by Shelley on 2016/8/7.
 */
public class YoukuUtil {

    public static void saveToken(Context c, YoukuOpenBean data) {
        SharedPreferences sp = c.getSharedPreferences("youku", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("access_token", data.getAccess_token());
        editor.putString("expires_in", data.getExpires_in());
        editor.putString("refresh_token", data.getRefresh_token());
        editor.putString("token_type", data.getToken_type());

        editor.apply();
    }

    public static YoukuOpenBean getToken(Context c) {
        SharedPreferences sp = c.getSharedPreferences("youku", Context.MODE_PRIVATE);
        YoukuOpenBean data = new YoukuOpenBean();
        data.setAccess_token(sp.getString("access_token", ""));
        data.setExpires_in(sp.getString("expires_in", ""));
        data.setRefresh_token(sp.getString("refresh_token", ""));
        data.setToken_type(sp.getString("token_type", ""));

        return data;
    }
}
