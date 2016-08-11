package com.zx.zx2000onlinevideo.ui.activity;

import android.os.Bundle;
import android.util.Log;

import com.ykcloud.sdk.opentools.player.VODPlayerStatListener;
import com.youku.player.base.YoukuBasePlayerActivity;
import com.youku.player.base.YoukuPlayer;
import com.youku.player.base.YoukuPlayerView;
import com.youku.player.plugin.MediaPlayerDelegate;
import com.youku.player.plugin.fullscreen.PluginFullScreenPlay;
import com.youku.player.plugin.small.PluginSmall;
import com.zx.zx2000onlinevideo.R;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.utils.Logger;

public class PlayYoukuActivity extends YoukuBasePlayerActivity implements VODPlayerStatListener {

    private String TAG = "PlayYoukuActivity";
    //视频跳过时长
    private int point = 0;// 播放进度
    private String playId;

    private YoukuPlayer mYoukuPlayer;
    private YoukuPlayerView mYoukuPlayerView;
    private MediaPlayerDelegate mediaPlayerDelegate;
    private PluginFullScreenPlay mFullScreenPlay;// 全屏插件
    private PluginSmall pluginSmall;// 小屏插件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parseExtra();
        setContentView(R.layout.activity_play_youku);

        init();
    }

    private void parseExtra() {
        playId = getIntent().getStringExtra(YoukuConfig.CONSTANCE_VID);

        Logger.getLogger().e("playId =  " + playId);
    }

    private void init() {
        mYoukuPlayerView = (YoukuPlayerView) findViewById(R.id.player_view);
        //播放器初始化 初始化成功后回调onInitializationSuccess方法
        mYoukuPlayerView.initialize(this);
    }

    //处理状态回调
    @Override
    public void onPlayerStat(int stat, int ext) {
//        ToastUtils.show("onPlayerStat : " + stat, 2000);
        Log.d(TAG, "onPlayerStat stat : " + stat);
    }

    /**
     * 播放器初始化成功回调
     */
    @Override
    public void onInitializationSuccess(YoukuPlayer player) {
        super.onInitializationSuccess(player);
        Log.d(TAG, "onInitializationSuccess 初始化成功");
        this.mYoukuPlayer = player;
        mediaPlayerDelegate = getMediaPlayerDelegate();
        //设置状态回调函数
        mediaPlayerDelegate.setVodPlayerStatListener(this);
        // must!!!
        setPlayerController(mYoukuPlayer.getPlayerUiControl());

        //全屏插件
        mFullScreenPlay = new PluginFullScreenPlay(this, mediaPlayerDelegate);
        //半屏插件
        pluginSmall = new PluginSmall(this, mediaPlayerDelegate);
        setmPluginSmallScreenPlay(pluginSmall);
        setmPluginFullScreenPlay(mFullScreenPlay);
        addPlugins();

        play();
    }

    private void play() {
        //播放视频
        mYoukuPlayer.playVideo(playId, false, point);
        //全屏播放（横屏）
        mediaPlayerDelegate.goFullScreen();
        //半屏播放（竖屏）
        //mediaPlayerDelegate.goSmall();
    }

    @Override
    public void setPadHorizontalLayout() {

    }

    @Override
    public void onFullscreenListener() {

    }

    @Override
    public void onSmallscreenListener() {

    }
}
