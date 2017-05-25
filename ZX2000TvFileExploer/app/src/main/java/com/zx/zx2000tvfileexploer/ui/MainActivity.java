package com.zx.zx2000tvfileexploer.ui;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.open.androidtvwidget.bridge.EffectNoDrawBridge;
import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.view.MainUpView;
import com.open.androidtvwidget.view.RelativeMainLayout;
import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.fileutil.FileCategoryHelper;
import com.zx.zx2000tvfileexploer.fileutil.MediaStoreHack;
import com.zx.zx2000tvfileexploer.ui.base.BaseFileOperationActivity;
import com.zx.zx2000tvfileexploer.utils.Logger;
import com.zx.zx2000tvfileexploer.utils.SDCardUtils;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseFileOperationActivity implements View.OnClickListener {

    private final int APK_UPDATE_SIZE_EVENT = 0X01;
    private final int MUSIC_UPDATE_SIZE_EVENT = 0X02;
    private final int VIDEO_UPDATE_SIZE_EVENT = 0X04;
    private final int PIC_UPDATE_SIZE_EVENT = 0X08;

    private MainUpView mAnimationFrame;
    private OpenEffectBridge mOpenEffectBridge;
    private View mOldFocus;
    private LayoutInflater mLayoutInflater;
    private TextView tvAppCount;
    private TextView tvMusicCount;
    private TextView tvPictureCount;
    private TextView tvVideoCount;

    private LinearLayout mDeviceContainer;

    private QueryHandler mApkSizeQueryHandler;
    private QueryHandler mVideoSizeQueryHandler;
    private QueryHandler mPicSizeQueryHandler;
    private QueryHandler mMusicSizeQueryHandler;

    private FileCategoryHelper mFileCategoryHelper;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Logger.getLogger().e("cont " + msg.arg1 + " " + msg.what);
            int count = msg.arg1;
            switch (msg.what) {
                case APK_UPDATE_SIZE_EVENT:
                    tvAppCount.setText(count + getString(R.string.main_apk_nums));
                    break;
                case MUSIC_UPDATE_SIZE_EVENT:
                    tvMusicCount.setText(count + getString(R.string.main_music_nums));
                    break;
                case VIDEO_UPDATE_SIZE_EVENT:
                    tvVideoCount.setText(count + getString(R.string.main_vidio_nums));
                    break;
                case PIC_UPDATE_SIZE_EVENT:
                    tvPictureCount.setText(count + getString(R.string.main_image_nums));
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setupViews() {
        initView();
    }

    @Override
    protected void initialized() {

        initData();
        initMainMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDiskInfo();
        initQuery();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void updateDiskInfo() {
        if (mDeviceContainer.getChildCount() > 0) {
            mDeviceContainer.removeAllViews();
        }

        List<SDCardUtils.StorageInfo> storageInfos = SDCardUtils.listAvaliableStorage(this);

        for (int i = 0; i < storageInfos.size(); i++) {
            final SDCardUtils.StorageInfo info = storageInfos.get(i);
//            Logger.getLogger().d("path " + info.path + " " + info.isMounted());
            if (info.isMounted()) {
                View view = mLayoutInflater.inflate(R.layout.main_device_info_layout, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 10, 15, 0);

                mDeviceContainer.addView(view, params);

                updateItemView(view, info);
            }

        }
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, FileListActivity.class);
        Intent intentAll = new Intent(MainActivity.this, DiskSelectActivity2.class);
        Intent intentSerach = new Intent(MainActivity.this, SearchActivity.class);
        Intent intentSetting = new Intent(MainActivity.this, SettingActivivty.class);


        switch (view.getId()) {
            case R.id.main_apk_lay:
                intent.putExtra("category", GlobalConsts.INTENT_EXTRA_APK_VLAUE);
                break;
            case R.id.main_video_lay:
                intent.putExtra("category", GlobalConsts.INTENT_EXTRA_VIDEO_VLAUE);
                break;
            case R.id.main_image_lay:
                intent.putExtra("category", GlobalConsts.INTENT_EXTRA_PICTURE_VLAUE);
                break;
            case R.id.main_music_lay:
                intent.putExtra("category", GlobalConsts.INTENT_EXTRA_MUSIC_VLAUE);
                break;
        }

//        ActivityOptionsCompat  compat = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_bottom_in, R.anim.slide_bottom_out);
//        ActivityCompat.startActivity(MainActivity.this, intent, compat.toBundle());
        if (view.getId() == R.id.main_allfile_lay) {
            startActivity(intentAll);
        } else if (view.getId() == R.id.main_search_lay) {
            startActivity(intentSerach);
        } else if (view.getId() == R.id.main_setting_lay) {
            startActivity(intentSetting);
        } else {
            startActivity(intent);
        }
    }

    private void initData() {
        mFileCategoryHelper = new FileCategoryHelper(this);
        mFileCategoryHelper.refreshCategoryInfo();
    }

    private void initMainMenu() {
        HashMap<FileCategoryHelper.FileCategory, FileCategoryHelper.CategoryInfo> mCategoryMap = mFileCategoryHelper.getCategoryInfo();

        FileCategoryHelper.CategoryInfo musicCatgory = mCategoryMap.get(FileCategoryHelper.FileCategory.MUSIC);
        tvMusicCount.setText(musicCatgory.count + getString(R.string.main_music_nums));

        FileCategoryHelper.CategoryInfo apkCategory = mCategoryMap.get(FileCategoryHelper.FileCategory.MUSIC);
        tvAppCount.setText(apkCategory.count + getString(R.string.main_apk_nums));

        FileCategoryHelper.CategoryInfo pictureCategory = mCategoryMap.get(FileCategoryHelper.FileCategory.PICTURE);
        tvPictureCount.setText(pictureCategory.count + getString(R.string.main_image_nums));

        FileCategoryHelper.CategoryInfo videoCategory = mCategoryMap.get(FileCategoryHelper.FileCategory.VIDEO);
        tvVideoCount.setText(videoCategory.count + getString(R.string.main_vidio_nums));
    }

    private void updateItemView(View view, SDCardUtils.StorageInfo info) {
        TextView name = (TextView) view.findViewById(R.id.device_name);
        TextView size = (TextView) view.findViewById(R.id.device_size);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.device_size_progress);

        setProgress(progressBar, info);
        size.setText(Formatter.formatFileSize(this, info.total));
        name.setText(info.label);
    }

    private void initView() {
        mLayoutInflater = LayoutInflater.from(this);

        mAnimationFrame = (MainUpView) findViewById(R.id.main_up_view);

        mAnimationFrame.setEffectBridge(new EffectNoDrawBridge());
        mAnimationFrame.setDrawUpRectPadding(new Rect(11, 11, 11, 11));
        mAnimationFrame.setUpRectResource(R.drawable.health_focus_border);
        mAnimationFrame.setShadowResource(R.drawable.item_shadow);

        RelativeMainLayout mainLayout = (RelativeMainLayout) findViewById(R.id.main_layout);
        mainLayout.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(final View oldFocus, final View newFocus) {

                if (newFocus != null) {
                    newFocus.bringToFront();
                }
                float scale = 1.10f;
                mAnimationFrame.setFocusView(newFocus, mOldFocus, scale);
                mOldFocus = newFocus;
            }
        });

        findViewById(R.id.main_allfile_lay).setOnClickListener(this);
        findViewById(R.id.main_apk_lay).setOnClickListener(this);
        findViewById(R.id.main_image_lay).setOnClickListener(this);
        findViewById(R.id.main_music_lay).setOnClickListener(this);
        findViewById(R.id.main_video_lay).setOnClickListener(this);
        findViewById(R.id.main_search_lay).setOnClickListener(this);
        findViewById(R.id.main_setting_lay).setOnClickListener(this);

        tvAppCount = (TextView) findViewById(R.id.apk_count);
        tvMusicCount = (TextView) findViewById(R.id.music_count);
        tvPictureCount = (TextView) findViewById(R.id.picture_count);
        tvVideoCount = (TextView) findViewById(R.id.video_count);

        mDeviceContainer = (LinearLayout) findViewById(R.id.device_container);

    }

    private void initQuery() {
        mApkSizeQueryHandler = new QueryHandler(getContentResolver(), FileCategoryHelper.FileCategory.APK);
        mMusicSizeQueryHandler = new QueryHandler(getContentResolver(), FileCategoryHelper.FileCategory.MUSIC);
        mPicSizeQueryHandler = new QueryHandler(getContentResolver(), FileCategoryHelper.FileCategory.PICTURE);
        mVideoSizeQueryHandler = new QueryHandler(getContentResolver(), FileCategoryHelper.FileCategory.VIDEO);
    }

    private void setProgress(ProgressBar bar, SDCardUtils.StorageInfo info) {
        if (null == bar) {
            return;
        }
        if (info.total == 0) {
            bar.setMax(100);
            bar.setProgress(0);
            return;
        }

        float percent = ((float) (info.total - info.free) / info.total) * 100;
        bar.setProgress((int)percent);
    }

    private final class QueryHandler extends AsyncQueryHandler {

        FileCategoryHelper.FileCategory mFileCategory;


        String[] columns = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.SIZE,
        };


        public QueryHandler(ContentResolver cr, FileCategoryHelper.FileCategory category) {
            super(cr);

            mFileCategory = category;
            String selection = null;
            if (category == FileCategoryHelper.FileCategory.APK) {
                selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.apk'";
            }

            startQuery(0, null, MediaStoreHack.getContentUriByCategory(category), columns,
                    selection, null, null);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            int count = cursor.getCount();
            Message message = new Message();
            message.arg1 = count;
            if (mFileCategory == FileCategoryHelper.FileCategory.APK) {
                message.what = APK_UPDATE_SIZE_EVENT;
                mHandler.sendMessage(message);
            } else if (mFileCategory == FileCategoryHelper.FileCategory.MUSIC) {
                message.what = MUSIC_UPDATE_SIZE_EVENT;
                mHandler.sendMessage(message);
            } else if (mFileCategory == FileCategoryHelper.FileCategory.VIDEO) {
                message.what = VIDEO_UPDATE_SIZE_EVENT;
                mHandler.sendMessage(message);
            } else if (mFileCategory == FileCategoryHelper.FileCategory.PICTURE) {
                message.what = PIC_UPDATE_SIZE_EVENT;
                mHandler.sendMessage(message);
            }

        }

    }

}
