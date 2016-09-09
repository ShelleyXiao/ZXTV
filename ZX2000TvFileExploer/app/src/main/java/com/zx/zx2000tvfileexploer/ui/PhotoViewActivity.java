package com.zx.zx2000tvfileexploer.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.view.MainUpView;
import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.adapter.PhotoViewByCursorAdapter;
import com.zx.zx2000tvfileexploer.adapter.PhotoViewByPathPresenter;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.FileCategoryHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileExtFilter;
import com.zx.zx2000tvfileexploer.fileutil.FileSettingsHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileSortHelper;
import com.zx.zx2000tvfileexploer.ui.base.BaseActivity;
import com.zx.zx2000tvfileexploer.utils.FileUtils;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.io.File;
import java.util.ArrayList;

/**
 * User: ShaudXiao
 * Date: 2016-09-08
 * Time: 14:28
 * Company: zx
 * Description:
 * FIXME
 */

public class PhotoViewActivity extends BaseActivity implements RecyclerViewTV.OnItemListener {

    private Context mContext;
    private RecyclerViewTV mThumbRecyclerView;
    private MainUpView mainUpView;
    private RecyclerViewBridge mRecyclerViewBridge;
    private View oldView;

    private ImageView mPhotoView;

    private ArrayList<FileInfo> mFileNameList = new ArrayList<FileInfo>();

    private FileCategoryHelper mFileCagetoryHelper;
    private FileSortHelper mFileSortHelper;

    private FileCategoryHelper.FileCategory mCurrentCategory;

    private FileExtFilter mFileExtFilter;

    private PhotoViewByCursorAdapter mPhotoViewByCursorAdapter;

    private LruCache<String, Bitmap> mBitmapLruCache;

    private String mCurrentPath;
    private int mType;
    private GeneralAdapter mPhotoAdapter;
    private RefreshFileAsyncTask mRefreshFileAsyncTask;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_photoview_layout;
    }

    @Override
    public void init() {
        mContext = PhotoViewActivity.this;
        mFileCagetoryHelper = new FileCategoryHelper(this);
        mFileSortHelper = FileSortHelper.getInstance(FileSettingsHelper.getInstance(mContext));
        mFileExtFilter = new FileExtFilter(new String[]{"jpg", "jpeg", "gif", "png", "bmp", "wbmp"});
        initView();

        initData();

        buildCache();
    }

    @Override
    public void updateDiskInfo() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBitmapLruCache != null) {
            mBitmapLruCache.evictAll();
        }
    }

    private void initView() {
        mThumbRecyclerView = (RecyclerViewTV) findViewById(R.id.photo_thumb_rc);
        mainUpView = (MainUpView) findViewById(R.id.mainUpView);
        mPhotoView = (ImageView) findViewById(R.id.photo_show);

        mThumbRecyclerView.setOnItemListener(this);

        mainUpView.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        mRecyclerViewBridge = (RecyclerViewBridge) mainUpView.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.focus);
//        RectF receF = new RectF(getDimension(R.dimen.px45), getDimension(R.dimen.px40),
//                getDimension(R.dimen.px45), getDimension(R.dimen.px40));
//        mRecyclerViewBridge.setDrawUpRectPadding(receF);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mThumbRecyclerView.setLayoutManager(layoutManager);
        mThumbRecyclerView.setFocusable(false);

    }

    private void initData() {
        Intent intent = getIntent();
        if (null != intent) {
            mType = intent.getIntExtra("category", -1);
            if (mType == GlobalConsts.INTENT_EXTRA_PICTURE_VLAUE) {
                mPhotoViewByCursorAdapter = new PhotoViewByCursorAdapter(this);
                mThumbRecyclerView.setAdapter(mPhotoViewByCursorAdapter);

                Cursor c = mFileCagetoryHelper.query(FileCategoryHelper.FileCategory.PICTURE,
                        mFileSortHelper.getSortMethod());

                setFileNums(String.valueOf(c.getCount()));
                Logger.getLogger().d("cursor nums " + String.valueOf(c.getCount()));

                mPhotoViewByCursorAdapter.swapCursor(c);

            } else if (mType == GlobalConsts.INTENT_EXTRA_ALL_VLAUE) {
                String path = intent.getStringExtra("path");
                if (!TextUtils.isEmpty(path)) {
                    mCurrentPath = new File(path).getParentFile().getAbsolutePath();
                    mPhotoAdapter = new GeneralAdapter(new PhotoViewByPathPresenter(this, mFileNameList));
                    mThumbRecyclerView.setAdapter(mPhotoAdapter);
                    mRefreshFileAsyncTask = new RefreshFileAsyncTask();

                    mRefreshFileAsyncTask.execute(new File(mCurrentPath));

                }

            }
        }
    }

    private void buildCache() {
        int memSize = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = Math.max(1024 * 1024 * 8, 1024 * 1024 * memSize / 6);
        mBitmapLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {

                return bitmap.getByteCount();
            }
        };
    }

    public float getDimension(int id) {
        return getResources().getDimension(id);
    }

    @Override
    public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
        mRecyclerViewBridge.setUnFocusView(oldView);
    }

    @Override
    public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
        mRecyclerViewBridge.setFocusView(itemView, 1.2f);
        oldView = itemView;

        FileInfo fileInfo = null;
        if(mType == GlobalConsts.INTENT_EXTRA_PICTURE_VLAUE) {
            fileInfo = mPhotoViewByCursorAdapter.getFileItem(position);
        } else {
            fileInfo = mFileNameList.get(position);
        }

        Bitmap bitmap = null;

        if (null != fileInfo) {

            try {
                bitmap = readBitmap(fileInfo.getFilePath());
            } catch (OutOfMemoryError ignored) {
                mBitmapLruCache.evictAll();
                try {
                    bitmap = readBitmap(fileInfo.getFilePath());
                } catch (OutOfMemoryError ignoredToo) {

                }
            }

            if (null != bitmap) {
                mPhotoView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
        mRecyclerViewBridge.setFocusView(itemView, 1.2f);
        oldView = itemView;

    }

    private void addBitmapToCache(String key, Bitmap bitmap) {
        Logger.getLogger().d("key = " + key );
        if(key != null && null != bitmap) {
            mBitmapLruCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromCache(String key) {
        return mBitmapLruCache.get(key);
    }

    private Bitmap readBitmap(String path) {
        Bitmap bitmap = getBitmapFromCache(path);
        if (null == bitmap) {
            new BitmapWorkerTask().execute(path);

        }

        return bitmap;
    }


    public class RefreshFileAsyncTask extends AsyncTask<File, Void, Integer> {

        @Override
        protected Integer doInBackground(File... files) {

            ArrayList<FileInfo> fileList = mFileNameList;
            fileList.clear();
            Logger.getLogger().d("path: " + files[0]);

            File[] listFiles = files[0].listFiles(mFileExtFilter);

            if (listFiles == null) {
                return Integer.valueOf(0);
            }

            for (File child : listFiles) {
                String absolutePath = child.getAbsolutePath();
                if (FileUtils.isNormalFile(absolutePath)) {
                    FileInfo lFileInfo = FileUtils.getFileInfo(child,/*
                            mFileCagetoryHelper.getFilter(), */FileSettingsHelper.getInstance(getContext()).getBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, false));
                    if (lFileInfo != null) {
                        fileList.add(lFileInfo);
                    }
                }
            }
            return Integer.valueOf(fileList.size());
        }

        @Override
        protected void onPostExecute(Integer result) {
            setFileNums(String.valueOf(result.intValue()));
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    private class BitmapWorkerTask extends AsyncTask<String , Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            if(null != strings[0]) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(strings[0]);
                addBitmapToCache(strings[0], bitmap);
                Logger.getLogger().d("BitmapWorkerTask +  " + strings[0] + " " + bitmap);
                return bitmap ;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(null != bitmap) {
                mPhotoView.setImageBitmap(bitmap);
            }
        }
    }
}
