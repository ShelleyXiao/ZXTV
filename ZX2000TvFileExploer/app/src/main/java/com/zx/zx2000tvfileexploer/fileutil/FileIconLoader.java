package com.zx.zx2000tvfileexploer.fileutil;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.zx.zx2000tvfileexploer.interfaces.IconLoadFinishListener;
import com.zx.zx2000tvfileexploer.utils.Logger;
import com.zx.zx2000tvfileexploer.utils.OtherUtil;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ShaudXiao on 2016/7/15.
 */
public class FileIconLoader implements Handler.Callback {

    private static final int MESSAGE_REQUEST_LOADING = 1;
    private static final int MESSAGE_REQUEST_LOADED = 2;

    private static final int MICRO_KIND = 3;

    private final static ConcurrentHashMap<String, ImageHolder> mImageCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ImageView, FileId> mPendingRequests = new ConcurrentHashMap<>();

    private Handler mMainThreadHandler = new Handler(this);

    private Context mContext;

    private boolean mPaused;
    private boolean mLoadingRequested;

    private LoaderThread mLoaderThread;

    private IconLoadFinishListener mIconLoadFinishListener;

    public FileIconLoader(Context context, IconLoadFinishListener l) {
        this.mContext = context;
        this.mIconLoadFinishListener = l;
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case MESSAGE_REQUEST_LOADED:
                if(!mPaused) {
                    processLoadedIcons();
                }

                return true;
            case MESSAGE_REQUEST_LOADING:
                mLoadingRequested = false;
                if(!mPaused) {
                    if(null == mLoaderThread) {
                        mLoaderThread = new LoaderThread();
                        mLoaderThread.start();
                    }
                    mLoaderThread.requestLoaing();
                }
                return true;
        }
        return false;
    }

    public boolean loadIcon(ImageView view, String path, long id, FileCategoryHelper.FileCategory cate) {
        boolean loaded = loadCacheIcon(view, path, cate);
        Logger.getLogger().e("loaded " + loaded + " mPaused " + mPaused);
        if(loaded) {
            mPendingRequests.remove(view);
        } else {
            FileId fd = new FileId(path, id, cate);
            mPendingRequests.put(view, fd);
            if(!mPaused) {
                requestLoading();
            }
        }

        return loaded;
    }

    public void cancelRequest(ImageView view) {
        mPendingRequests.remove(view);
    }

    public void stop() {
        pause();

        if(mLoaderThread != null) {
            mLoaderThread.quit();
            mLoaderThread = null;
        }

        clear();
    }

    public void clear() {
        mPendingRequests.clear();
        mImageCache.clear();
    }

    public void pause() {
        mPaused = true;
    }

    public void resume() {
        mPaused = false;
        if(!mPendingRequests.isEmpty()) {
            requestLoading();
        }
    }

    private boolean loadCacheIcon(ImageView view, String path,FileCategoryHelper.FileCategory cate) {
        ImageHolder holder = mImageCache.get(path);
        if(holder == null) {
            holder = ImageHolder.create(cate);
            if(holder == null) {
                return false;
            }
            mImageCache.put(path, holder);
        } else if(holder.state == ImageHolder.LOADED) {
            if(holder.isNULL()) {
                return true;
            }

            if(holder.setImageView(view)) {
                return true;
            }
        }

        holder.state = ImageHolder.NEEDED;
        return  false;
    }

    private void requestLoading() {
        if(!mLoadingRequested) {
            mLoadingRequested = true;
            mMainThreadHandler.sendEmptyMessage(MESSAGE_REQUEST_LOADING);
        }
    }

    public long getDbId(String path, boolean video) {
        String volumeName = "external";
        Uri uri = video ?
                MediaStore.Video.Media.getContentUri(path) : MediaStore.Images.Media.getContentUri(path);
        String selection = MediaStore.Files.FileColumns.DATA + "=?";
        String[] selectionArgs = new String[] {
            path
        };

        String[] colums = new String[] {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA
        };

        Cursor cursor = mContext.getContentResolver().query(uri, colums, selection, selectionArgs, null);
        if(null == cursor) {
            return 0;
        }

        long id = 0;
        if(cursor.moveToNext()) {
            id = cursor.getLong(0);
        }
        cursor.close();

        return id;
    }

    private void processLoadedIcons() {
        Iterator<ImageView> iterator = mPendingRequests.keySet().iterator();
        while(iterator.hasNext()) {
            ImageView view = iterator.next();
            FileId fd = mPendingRequests.get(view);
            boolean loaded = loadCacheIcon(view, fd.mPath, fd.mCategory);
            if(loaded) {
                iterator.remove();
                mIconLoadFinishListener.onIconLoadFinished(view);
            }
        }

        if(!mPendingRequests.isEmpty()) {
            requestLoading();
        }
    }

    private Bitmap getImageThumbnail(long id) {
        return MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), id, MICRO_KIND, null);
    }

    private Bitmap getVideoThumbnail(long id) {
        return MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), id, MICRO_KIND, null);
    }

    private static abstract class ImageHolder {
        public static final int NEEDED = 0;
        public static final int LOADING = 1;
        public static final int LOADED = 2;

        int state;

        public static ImageHolder create(FileCategoryHelper.FileCategory cate) {
            switch (cate) {
                case APK:
                    return new DrawableHolder();
                case PICTURE:
                case VIDEO:
                    return  new BitmapHolder();
            }
            return null;
        }

        public abstract boolean setImageView(ImageView v);

        public abstract boolean isNULL();

        public abstract  void setImage(Object image);
    }

    private static class BitmapHolder extends ImageHolder {

        private SoftReference<Bitmap> bitmapRef;

        @Override
        public boolean setImageView(ImageView v) {
            if(bitmapRef.get() == null) {
                return false;
            }
            v.setImageBitmap(bitmapRef.get());
            return true;
        }

        @Override
        public boolean isNULL() {
            return bitmapRef == null;
        }

        @Override
        public void setImage(Object image) {
            bitmapRef = image == null ? null : new SoftReference<Bitmap>((Bitmap)image);
        }
    }

    private static class DrawableHolder extends ImageHolder {
        private SoftReference<Drawable> bitmapRef;

        @Override
        public boolean setImageView(ImageView v) {
            if(bitmapRef.get() == null) {
                return false;
            }
            v.setImageDrawable(bitmapRef.get());
            return true;
        }

        @Override
        public boolean isNULL() {
            return bitmapRef == null;
        }

        @Override
        public void setImage(Object image) {
            bitmapRef = image == null ? null : new SoftReference<Drawable>((Drawable)image);
        }
    }

    public static class FileId {
        public String mPath;

        public long mId; // database id

        public FileCategoryHelper.FileCategory mCategory;

        public FileId(String path, long id, FileCategoryHelper.FileCategory cate) {
            mPath = path;
            mId = id;
            mCategory = cate;
        }
    }

    private class LoaderThread extends HandlerThread implements Handler.Callback {

        private Handler mLoaderThreadHandler;

        public LoaderThread() {
            super("FileIconLoader");
        }

        public void requestLoaing() {
         if(null == mLoaderThreadHandler) {
                mLoaderThreadHandler = new Handler(getLooper(), this);
            }
            mLoaderThreadHandler.sendEmptyMessage(0);
        }

        @Override
        public boolean handleMessage(Message message) {
            Iterator<FileId> iterator = mPendingRequests.values().iterator();
            while(iterator.hasNext()) {
                FileId id = iterator.next();
                ImageHolder holder = mImageCache.get(id.mPath);
                if(null != holder && holder.state == ImageHolder.NEEDED) {
                    holder.state = ImageHolder.LOADING;
                    Logger.getLogger().i(id.mPath + " catge " + id.mCategory) ;
                    switch (id.mCategory) {
                        case APK:
                            Drawable icon = OtherUtil.getApkIcon(mContext, id.mPath);
                            holder.setImage(icon);
                            break;
                        case PICTURE:
                        case VIDEO:
                            boolean isVideo = id.mCategory == FileCategoryHelper.FileCategory.VIDEO;
                            if(id.mId == 0) {
                                id.mId = getDbId(id.mPath, isVideo);
                            }

                            if(id.mId == 0) {

                            }
                            holder.setImage(isVideo ? getVideoThumbnail(id.mId) : getImageThumbnail(id.mId) );
                            break;
                    }
                    holder.state = ImageHolder.LOADED;
                    mImageCache.put(id.mPath, holder);
                }
            }

            mMainThreadHandler.sendEmptyMessage(MESSAGE_REQUEST_LOADED);
            return false;
        }
    }

}
