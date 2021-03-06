package com.zx.zx2000onlinevideo.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ykcloud.sdk.openapi.YKAPIFactory;
import com.ykcloud.sdk.opentools.player.VODPlayer;
import com.ykcloud.sdk.opentools.player.VODPlayerStatListener;
import com.ykcloud.sdk.opentools.player.auth.VideoAuth;
import com.ykcloud.sdk.opentools.player.auth.VideoAuthCallback;
import com.ykcloud.sdk.opentools.player.entity.VideoLists;
import com.zx.zx2000onlinevideo.R;
import com.zx.zx2000onlinevideo.config.YoukuConfig;

public class PlayVodActivity extends Activity {

    private String TAG = "PlayVodActivity";

    private String vid;
    private String client_id;
    private String client_secret;

    RelativeLayout layout_player;
    VODPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_vod);
        parseExtra();
        initView();
    }

    private void parseExtra(){
        vid = getIntent().getStringExtra(YoukuConfig.CONSTANCE_VID);
        client_id = YoukuConfig.CLIENT_ID;
        client_secret = YoukuConfig.CLIENT_SECRET;
        initSdk();
    }

    private void initSdk(){
        //初始化播放sdk
        YKAPIFactory.initSDK(this, client_id, client_secret);
    }

    private void initView(){
        player = new VODPlayer(this, true);

        //横竖屏切换(横屏全屏播放)
        player.changeOrientation(VODPlayer.Orientation_land);
        //横竖屏切换(竖屏半屏播放)
//        player.changeOrientation(VODPlayer.Orientation_portrait);
        //状态回调函数
        player.setmStatListener(new VODPlayerStatListener() {
            @Override
            public void onPlayerStat(int stat, int ext) {
//                ToastUtils.show("onPlayerStat : " + stat + "", 2000);
                Log.i(TAG, "player stat " + stat);
            }
        });

        layout_player = (RelativeLayout) findViewById(R.id.layout_player);
        layout_player.addView(player.getPlayRootLayout());

        //竖屏播放 点击返回按钮退出播放界面
        player.getPlayRootLayout().findViewById(R.id.half_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //自行获取视频信息 填充 M3u8Video对象,调用play播放
        VideoAuth.requestM3u8(vid, this, new VideoAuthCallback() {
            @Override
            public void onAuthComplete(Object obj) {
                if (obj instanceof VideoLists) {
                    ((VideoLists) obj).VideoName = "视频标题";
                    player.playVideo((VideoLists) obj);
                }
            }

            @Override
            public void onError(String error) {
                com.ykcloud.sdk.platformtools.Log.e(TAG, " error : " + error);
                if (player != null) {
                    player.destroyVideo();
                    player.showNotice(200, "获取视频失败,关闭播放器:" + error, true);
                }
                Toast.makeText(PlayVodActivity.this, "获取视频失败,关闭播放器:" + error, Toast.LENGTH_SHORT).show();
                layout_player.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //finish();
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.destroyVideo();
            player = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            if(player != null){
                //获得vodPlayer横屏还是竖屏
                int mScreenState = player.getScreenState();
                if(mScreenState == VODPlayer.Orientation_portrait){
                    //如果是竖屏 则退出播放器
                    finish();
                }else{
                    //如果是横屏 则变为横屏
                    player.changeOrientation(VODPlayer.Orientation_portrait);
                }
            }
        }
        return false;
    }
}
