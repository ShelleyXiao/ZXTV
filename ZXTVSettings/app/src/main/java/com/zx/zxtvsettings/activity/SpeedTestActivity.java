package com.zx.zxtvsettings.activity;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zx.zxtvsettings.R;
import com.zx.zxtvsettings.speedTest.NetworkSpeedInfo;
import com.zx.zxtvsettings.speedTest.ReadFileUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * User: ShaudXiao
 * Date: 2016-08-23
 * Time: 14:20
 * Company: zx
 * Description:
 * FIXME
 */

public class SpeedTestActivity extends BaseActivity {

    @BindView(R.id.speedtest_btn_start)
    Button mSpeedtestBtnStart;
    @BindView(R.id.speedtset_didinotlayout)
    LinearLayout mSpeedtsetDidinotlayout;
    @BindView(R.id.speed_test_percent)
    TextView mSpeedTestPercent;
    @BindView(R.id.tester)
    ImageView mTester;
    @BindView(R.id.needle)
    ImageView mNeedle;
    @BindView(R.id.heart)
    ImageView mHeart;
    @BindView(R.id.speedtset_btn_stoptest)
    Button mSpeedtsetBtnStoptest;
    @BindView(R.id.speedtest_instartlayout)
    LinearLayout mSpeedtestInstartlayout;
    @BindView(R.id.speedtest_speed)
    TextView mSpeedtestSpeed;
    @BindView(R.id.speed_movietype)
    TextView mSpeedMovietype;
    @BindView(R.id.speedtest_btn_startagain)
    Button mSpeedtestBtnStartagain;
    @BindView(R.id.speedtest_startagainlayout)
    LinearLayout mSpeedtestStartagainlayout;
    @BindView(R.id.speed_test_fl)
    FrameLayout mSpeedTestFl;
    @BindView(R.id.nandb_tittle)
    TextView mNandbTittle;
    @BindView(R.id.speed_test_promat)
    TextView mSpeedTestPromat;

    private final String URL = "http://gdown.baidu.com/data/wisegame/6546ec811c58770b/labixiaoxindamaoxian_8.apk";
    private final int PROGRESSCHANGE = 0;
    private final int SPEEDUPDATE = 1;
    private final int SPEED_FINISH = 2;

    private int last_degree = 0, cur_degree;

    private long mCurrenSpeed = 0;//当前速度
    private long mAverageSpeed = 0;//平均速度
    private long mSpeedTaital = 0;

    private byte[] mFileData = null;
    private NetworkSpeedInfo networkSpeedInfo = null;
    private List<Long> list = new ArrayList<Long>();

    private int progress;

    private Thread thread;

    private Boolean THREADCANRUN = true;
    private Boolean PROGRESSTHREADCANRUN = true;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESSCHANGE:
                    progress = NetworkSpeedInfo.progress;
                    mSpeedTestPercent.setText(progress + "%");
                    break;
                case SPEEDUPDATE:
                    mCurrenSpeed = NetworkSpeedInfo.Speed;
                    list.add(mCurrenSpeed);
                    for (long speed : list) {
                        mSpeedTaital += speed;
                    }
                    mAverageSpeed = mSpeedTaital / list.size();
                    mSpeedTaital = 0;

                    startAnimation(mCurrenSpeed);
                    break;
                case SPEED_FINISH:

                    mSpeedtestSpeed.setText(mAverageSpeed + "kb/s");
                    if (mAverageSpeed <= 200) {
                        mSpeedMovietype.setText(getResources().getStringArray(R.array.speed_level)[0]);
                    } else if (mAverageSpeed <= 400) {
                        mSpeedMovietype.setText(getResources().getStringArray(R.array.speed_level)[1]);
                    } else if (mAverageSpeed > 400) {
                        mSpeedMovietype.setText(getResources().getStringArray(R.array.speed_level)[2]);
                    }

                    PROGRESSTHREADCANRUN = false;
                    THREADCANRUN = false;
                    NetworkSpeedInfo.FILECANREAD = false;
                    break;
            }
        }

    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_speed_test;
    }

    @Override
    protected void setupViews() {
    }

    @Override
    protected void initialized() {

    }

    @OnClick({R.id.speedtest_btn_start, R.id.speedtset_btn_stoptest, R.id.speedtest_btn_startagain})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.speedtest_btn_start:

                break;
            case R.id.speedtset_btn_stoptest:
                if (mSpeedtsetBtnStoptest.getText().equals(getString(R.string.net_speed_test_btn_start))) {
                    mSpeedtsetBtnStoptest.setText(R.string.net_speed_test_btn_stop);

                    PROGRESSTHREADCANRUN = true;
                    THREADCANRUN = true;
                    NetworkSpeedInfo.FILECANREAD = true;

                    new Thread() {

                        @Override
                        public void run() {
                            mFileData = ReadFileUtil.ReadFileFromURL(URL,
                                    networkSpeedInfo);
                        }
                    }.start();

                    thread = new Thread() {

                        @Override
                        public void run() {
                            while (THREADCANRUN) {
                                try {
                                    sleep(50);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                handler.sendEmptyMessage(SPEEDUPDATE);
                                if (NetworkSpeedInfo.FinishBytes >= NetworkSpeedInfo.totalBytes) {
                                    handler.sendEmptyMessage(SPEED_FINISH);
                                    NetworkSpeedInfo.FinishBytes = 0;
                                }
                            }
                        }
                    };
                    thread.start();

                    new Thread() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            while (PROGRESSTHREADCANRUN) {
                                try {
                                    sleep(500);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                handler.sendEmptyMessage(PROGRESSCHANGE);
                            }
                        }
                    }.start();

                } else if (mSpeedtsetBtnStoptest.getText().equals(getString(R.string.net_speed_test_btn_stop))) {
                    NetworkSpeedInfo.progress = 0;
                    NetworkSpeedInfo.FinishBytes = 0;
                    handler.sendEmptyMessage(SPEED_FINISH);

                    mSpeedtestStartagainlayout.setVisibility(View.VISIBLE);
                    mSpeedtestInstartlayout.setVisibility(View.GONE);

                    mSpeedtestBtnStartagain.requestFocus();
                    mSpeedtestBtnStartagain.requestFocusFromTouch();
                }

                break;
            case R.id.speedtest_btn_startagain:

                mSpeedtestStartagainlayout.setVisibility(View.GONE);
                mSpeedtestInstartlayout.setVisibility(View.VISIBLE);

                mSpeedtsetBtnStoptest.setText(R.string.net_speed_test_btn_start);

                break;
        }
    }

    private void startAnimation(long cur_speed) {
        cur_degree = getDegree(cur_speed);

        RotateAnimation rotateAnimation = new RotateAnimation(last_degree, cur_degree, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(1000);
        last_degree = cur_degree;
        mNeedle.startAnimation(rotateAnimation);
    }

    private int getDegree(double cur_speed) {
        int ret = 0;
        if (cur_speed >= 0 && cur_speed <= 512) {
            ret = (int) (15.0 * cur_speed / 128.0);
        } else if (cur_speed >= 512 && cur_speed <= 1024) {
            ret = (int) (60 + 15.0 * cur_speed / 256.0);
        } else if (cur_speed >= 1024 && cur_speed <= 10 * 1024) {
            ret = (int) (90 + 15.0 * cur_speed / 1024.0);
        } else {
            ret = 180;
        }
        return ret;
    }
}
