package com.zx.zx2000onlinevideo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.zx.zx2000onlinevideo.R;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.ui.view.widget.ConfirmDialog;
import com.zx.zx2000onlinevideo.utils.ImageLoader;
import com.zx.zx2000onlinevideo.utils.Logger;
import com.zx.zx2000onlinevideo.utils.NetWorkUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by WXT on 2016/7/8.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = BaseActivity.class.getSimpleName();
    private boolean isNetWork = true;
    public Context context;

    protected Unbinder mUnbinder;

    protected String category , genre;

    protected YoukuConfig.TYPE type = YoukuConfig.TYPE.other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag,flag);
        context = this.getApplicationContext();
        int layoutId = getLayoutId();
        if (layoutId != 0) {
            setContentView(layoutId);
            // 删除窗口背景
            getWindow().setBackgroundDrawable(null);

            mUnbinder = ButterKnife.bind(this);
        }

        Bundle bundle = getIntent().getExtras();
        if(null != bundle) {
            category = bundle.getString(YoukuConfig.INTENT_CATEGROY_KEY);
            genre = bundle.getString(YoukuConfig.INTENT_GENRE_KEY);
            Logger.getLogger().d(category + " " + genre);

            if(YoukuConfig.MOVIE.equals(category)) {
                type = YoukuConfig.TYPE.Moive;
            } else if(YoukuConfig.SERIALS.equals(category)) {
                type = YoukuConfig.TYPE.Serails;
            } else {
                type = YoukuConfig.TYPE.other;
            }
        }

        //向用户展示信息前的准备工作在这个方法里处理
        preliminary();
    }

    public void onResume() {
        super.onResume();

        if(!NetWorkUtil.isNetWorkAvailable(this)) {
            netWorkNo();
        }
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    /**
     * 向用户展示信息前的准备工作在这个方法里处理
     */
    protected void preliminary() {
        // 初始化组件
        setupViews();

        // 初始化数据
        initialized();
    }

    /**
     * 获取全局的Context
     *
     * @return {@link #context = this.getApplicationContext();}
     */
    public Context getContext() {
        return context;
    }

    /**
     * 布局文件ID
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化组件
     */
    protected abstract void setupViews();

    /**
     * 初始化数据
     */
    protected abstract void initialized();

    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    /**
     * 通过Action跳转界面
     **/
    public void startActivity(String action) {
        startActivity(action, null);
    }

    /**
     * 含有Bundle通过Action跳转界面
     **/
    public void startActivity(String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 含有Bundle通过Class打开编辑界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void showToastLong(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public void showToastShort(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 监听广播
     */
    class MyConnectionChanngeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                Logger.getLogger().i( "网络不可以用");
                showToastShort("网络断开连接");
                netWorkNo();
                isNetWork = false;
            } else {
                Logger.getLogger().i( "网络连接成功");
                netWorkYew();
            }
        }
    }

    /**
     * 带有右进右出动画的退出
     */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 默认退出
     */
    public void defaultFinish() {
        super.finish();
    }

    public void netWorkNo() {
        ConfirmDialog.Builder dialog = new ConfirmDialog.Builder(this);
        dialog.setMessage(R.string.no_network_promat);
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoNetWorkSetting();
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    public void netWorkYew() {
        if (!isNetWork) {
            showToastShort("网络连接成功");
            isNetWork = true;
        }
    }

    public void gotoNetWorkSetting() {
        Intent intent = null;
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent("android.settings.WIFI_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        BaseActivity.this.startActivity(intent);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //低内存运行
        Logger.getLogger().e( "clear cache");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Logger.getLogger().e( "内存级别：" + level);
        switch(level){
            //UI组件不可见时
            case TRIM_MEMORY_UI_HIDDEN:
                Logger.getLogger().e("clear cache");
                ImageLoader.clearImageMemoryCache();
                break;
        }

    }


}
