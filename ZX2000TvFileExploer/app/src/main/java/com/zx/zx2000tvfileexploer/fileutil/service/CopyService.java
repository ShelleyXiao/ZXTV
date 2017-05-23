package com.zx.zx2000tvfileexploer.fileutil.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Formatter;

import com.zx.zx2000tvfileexploer.GlobalConsts;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.DataPackage;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.entity.OpenMode;
import com.zx.zx2000tvfileexploer.fileutil.FileUtil;
import com.zx.zx2000tvfileexploer.fileutil.GenericCopyUtil;
import com.zx.zx2000tvfileexploer.fileutil.ProgressHandler;
import com.zx.zx2000tvfileexploer.fileutil.ServiceWatcherUtil;
import com.zx.zx2000tvfileexploer.ui.FileListActivity;
import com.zx.zx2000tvfileexploer.ui.MainActivity;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;

public class CopyService extends Service {

    public static final String TAG_COPY_TARGET = "COPY_DIRECTORY";
    public static final String TAG_COPY_SOURCES = "FILE_PATHS";
    public static final String TAG_COPY_OPEN_MODE = "MODE"; // target open mode
    public static final String TAG_COPY_MOVE = "move";
    private static final String TAG_COPY_START_ID = "id";

    public static final String TAG_BROADCAST_COPY_CANCEL = "copycancel";

    // list of data packages, to initiate chart in process viewer fragment
    private ArrayList<DataPackage> dataPackages = new ArrayList<>();
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private Context c;

    private ProgressListener progressListener;
    private final IBinder mBinder = new LocalBinder();
    private ProgressHandler progressHandler;
    private ServiceWatcherUtil watcherUtil;

    private long totalSize = 0L;
    private int totalSourceFiles = 0;
    private int sourceProgress = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        c = getApplicationContext();
        registerReceiver(receiver3, new IntentFilter(TAG_BROADCAST_COPY_CANCEL));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Bundle b = new Bundle();
        ArrayList<FileInfo> files = intent.getParcelableArrayListExtra(TAG_COPY_SOURCES);
        String targetPath = intent.getStringExtra(TAG_COPY_TARGET);
        int mode = intent.getIntExtra(TAG_COPY_OPEN_MODE, OpenMode.UNKNOWN.ordinal());
        final boolean move = intent.getBooleanExtra(TAG_COPY_MOVE, false);

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        b.putInt(TAG_COPY_START_ID, startId);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra(GlobalConsts.KEY_INTENT_PROCESS_VIEWER, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mBuilder = new NotificationCompat.Builder(c);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setContentTitle(getResources().getString(R.string.copying))
                .setSmallIcon(R.drawable.ic_content_copy_white_36dp);

        startForeground(Integer.parseInt("456" + startId), mBuilder.build());

        b.putBoolean(TAG_COPY_MOVE, move);
        b.putString(TAG_COPY_TARGET, targetPath);
        b.putInt(TAG_COPY_OPEN_MODE, mode);
        b.putParcelableArrayList(TAG_COPY_SOURCES, files);

        //going async
        new DoInBackground().execute(b);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    /**
     * Helper method to calculate source files size
     *
     * @param files
     * @param context
     * @return
     */
    long getTotalBytes(ArrayList<FileInfo> files, Context context) {
        long totalBytes = 0;
        for (FileInfo file : files) {
            if (file.isDir()) totalBytes += file.folderSize(context);
            else totalBytes += file.length(context);
        }
        return totalBytes;
    }

    public void onDestroy() {
        this.unregisterReceiver(receiver3);
    }

    private class DoInBackground extends AsyncTask<Bundle, Void, Integer> {
        ArrayList<FileInfo> sourceFiles;
        boolean move;
        Copy copy;
        private String targetPath;
        private OpenMode openMode;

        protected Integer doInBackground(Bundle... p1) {

            sourceFiles = p1[0].getParcelableArrayList(TAG_COPY_SOURCES);
            final int id = p1[0].getInt(TAG_COPY_START_ID);

            // setting up service watchers and initial data packages
            // finding total size on background thread (this is necessary condition for SMB!)
            totalSize = getTotalBytes(sourceFiles, c);
            totalSourceFiles = sourceFiles.size();
            progressHandler = new ProgressHandler(totalSourceFiles, totalSize);

            progressHandler.setProgressListener(new ProgressHandler.ProgressListener() {

                @Override
                public void onProgressed(String fileName, int sourceFiles, int sourceProgress,
                                         long totalSize, long writtenSize, int speed) {
                    publishResults(id, fileName, sourceFiles, sourceProgress, totalSize,
                            writtenSize, speed, false, move);
                }
            });

            watcherUtil = new ServiceWatcherUtil(progressHandler, totalSize);

            DataPackage intent1 = new DataPackage();
            intent1.setName(sourceFiles.get(0).getFileName());
            intent1.setSourceFiles(sourceFiles.size());
            intent1.setSourceProgress(0);
            intent1.setTotal(totalSize);
            intent1.setByteProgress(0);
            intent1.setSpeedRaw(0);
            intent1.setMove(move);
            intent1.setCompleted(false);
            putDataPackage(intent1);

            targetPath = p1[0].getString(TAG_COPY_TARGET);
            move = p1[0].getBoolean(TAG_COPY_MOVE);
            openMode = OpenMode.getOpenMode(p1[0].getInt(TAG_COPY_OPEN_MODE));
            copy = new Copy();
            copy.execute(sourceFiles, targetPath, move, openMode);

            if (copy.failedFOps.size() == 0) {

                // adding/updating new encrypted db entry if any encrypted file was copied/moved
                for (FileInfo sourceFile : sourceFiles) {
//                    findAndReplaceEncryptedEntry(sourceFile);
                }
            }
            return id;
        }

        @Override
        public void onPostExecute(Integer b) {

            super.onPostExecute(b);
            watcherUtil.stopWatch();
            Logger.getLogger().e("copy.failedFOps " + copy.failedFOps.size());
            generateNotification(copy.failedFOps, move);

            Intent intent = new Intent("loadlist");
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
            stopSelf();
        }

        class Copy {

            ArrayList<FileInfo> failedFOps;
            ArrayList<FileInfo> toDelete;

            Copy() {
                failedFOps = new ArrayList<>();
                toDelete = new ArrayList<>();
            }

            /**
             * Method iterate through files to be copied
             *
             * @param sourceFiles
             * @param targetPath
             * @param move
             * @param mode        target file open mode (current path's open mode)
             */
            public void execute(final ArrayList<FileInfo> sourceFiles, final String targetPath,
                                final boolean move, OpenMode mode) {

                // initial start of copy, initiate the watcher
                watcherUtil.watch();
                Logger.getLogger().i("taretPath start coping>>>>>>>>>>>>>>>>>>>>> checkfolder =  " + FileUtil.checkFolder((targetPath), c) );
                if (FileUtil.checkFolder((targetPath), c) == 1) {
                    Logger.getLogger().i("taretPath start coping>>>>>>>>>>>>>>>>>>>>> " + mode );

                    for (int i = 0; i < sourceFiles.size(); i++) {
                        sourceProgress = i;
                        FileInfo f1 = (sourceFiles.get(i));
                        Logger.getLogger().i( "basefile\t" + f1.getFilePath());

                        try {

                            FileInfo hFile;
                            if (targetPath.contains(getExternalCacheDir().getPath())) {
                                // the target open mode is not the one we're currently in!
                                // we're processing the file for cache
                                hFile = new FileInfo(OpenMode.FILE, targetPath, sourceFiles.get(i).getFileName(),
                                        f1.isDir());
                            } else {

                                // the target open mode is where we're currently at
                                hFile = new FileInfo(mode, targetPath, sourceFiles.get(i).getFileName(),
                                        f1.isDir());
                            }

                            if (!progressHandler.getCancelled()) {

//                                if ((f1.getMode() == OpenMode.ROOT || mode == OpenMode.ROOT)
//                                        && BaseActivity.rootMode) {
//                                    // either source or target are in root
//                                    progressHandler.setSourceFilesProcessed(++sourceProgress);
//                                    copyRoot(f1, hFile, move);
//                                    continue;
//                                }
                                progressHandler.setSourceFilesProcessed(++sourceProgress);
                                copyFiles((f1), hFile, progressHandler);
                            } else {
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.getLogger().e(">>>>>>>>>>>>>>>>Got exception checkout");

                            failedFOps.add(sourceFiles.get(i));
                            for (int j = i + 1; j < sourceFiles.size(); j++)
                                failedFOps.add(sourceFiles.get(j));
                            break;
                        }
                    }

                } else {
                    for (FileInfo f : sourceFiles) failedFOps.add(f);
                    return;
                }

                // making sure to delete files after copy operation is done
                // and not if the copy was cancelled
                if (move && !progressHandler.getCancelled()) {
                    ArrayList<FileInfo> toDelete = new ArrayList<>();
                    for (FileInfo a : sourceFiles) {
                        if (!failedFOps.contains(a))
                            toDelete.add(a);
                    }
                    new DeleteTask(getContentResolver(), c).execute((toDelete));
                }
            }

            private void copyFiles(final FileInfo sourceFile, final FileInfo targetFile,
                                   ProgressHandler progressHandler) throws IOException {

                if (sourceFile.isDir()) {
                    if (progressHandler.getCancelled()) return;

                    if (!targetFile.exists()) {
                        targetFile.mkdir(c);
                    }

                    // various checks
                    // 1. source file and target file doesn't end up in loop
                    // 2. source file has a valid name or not
                    if (!FileUtil.isFileNameValid(sourceFile.getName())
                            || FileUtil.isCopyLoopPossible(sourceFile, targetFile)) {
                        failedFOps.add(sourceFile);
                        return;
                    }
                    targetFile.setModifiedDate(sourceFile.getModifiedDate());

                    if (progressHandler.getCancelled()) return;
                    ArrayList<FileInfo> filePaths = sourceFile.listFiles(c, false);
                    for (FileInfo file : filePaths) {
                        FileInfo destFile = new FileInfo(targetFile.getMode(), targetFile.getFilePath(),
                                file.getName(), file.isDir());
                        copyFiles(file, destFile, progressHandler);
                    }
                } else {
                    if (progressHandler.getCancelled()) return;
                    if (!FileUtil.isFileNameValid(sourceFile.getName())) {
                        failedFOps.add(sourceFile);
                        return;
                    }

                    GenericCopyUtil copyUtil = new GenericCopyUtil(c);

                    progressHandler.setFileName(sourceFile.getName());
                    copyUtil.copy(sourceFile, targetFile);
                }
            }
        }
    }

    /**
     * Displays a notification, sends intent and cancels progress if there were some failures
     * in copy progress
     *
     * @param failedOps
     * @param move
     */
    void generateNotification(ArrayList<FileInfo> failedOps, boolean move) {

        mNotifyManager.cancelAll();

        if (failedOps.size() == 0) return;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c);
        mBuilder.setContentTitle(c.getString(R.string.operationunsuccesful));
        mBuilder.setContentText(c.getString(R.string.copy_error).replace("%s",
                move ? c.getString(R.string.moved) : c.getString(R.string.copied)));
        mBuilder.setAutoCancel(true);

        progressHandler.setCancelled(true);

        Intent intent = new Intent(this, FileListActivity.class);
        intent.putExtra(GlobalConsts.TAG_INTENT_FILTER_FAILED_OPS, failedOps);
        intent.putExtra("move", move);

        PendingIntent pIntent = PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pIntent);
        mBuilder.setSmallIcon(R.drawable.ic_content_copy_white_36dp);

        mNotifyManager.notify(741, mBuilder.build());

        intent = new Intent(GlobalConsts.TAG_INTENT_FILTER_GENERAL);
        intent.putExtra(GlobalConsts.TAG_INTENT_FILTER_FAILED_OPS, failedOps);
        intent.putExtra(TAG_COPY_MOVE, move);

        sendBroadcast(intent);
    }

    /**
     * Publish the results of the progress to notification and {@link DataPackage}
     *
     * @param id             id of current service
     * @param fileName       file name of current file being copied
     * @param sourceFiles    total number of files selected by user for copy
     * @param sourceProgress files been copied out of them
     * @param totalSize      total size of selected items to copy
     * @param writtenSize    bytes successfully copied
     * @param speed          number of bytes being copied per sec
     * @param isComplete     whether operation completed or ongoing (not supported at the moment)
     * @param move           if the files are to be moved
     */
    private void publishResults(int id, String fileName, int sourceFiles, int sourceProgress,
                                long totalSize, long writtenSize, int speed, boolean isComplete,
                                boolean move) {
        if (!progressHandler.getCancelled()) {

            //notification
            float progressPercent = ((float) writtenSize / totalSize) * 100;
            Logger.getLogger().i("progressPercent = " + progressPercent);
            mBuilder.setProgress(100, Math.round(progressPercent), false);
            mBuilder.setOngoing(true);
            int title = R.string.copying;
            if (move) title = R.string.moving;
            mBuilder.setContentTitle(c.getResources().getString(title));
            mBuilder.setContentText(fileName + " " + Formatter.formatFileSize(c, writtenSize) + "/" +
                    Formatter.formatFileSize(c, totalSize));
            int id1 = Integer.parseInt("456" + id);
            mNotifyManager.notify(id1, mBuilder.build());
            if (writtenSize == totalSize || totalSize == 0) {
                if (move) {

                    //mBuilder.setContentTitle(getString(R.string.move_complete));
                    // set progress to indeterminate as deletion might still be going on from source
                    mBuilder.setProgress(0, 0, true);
                } else {

                    mBuilder.setContentTitle(getString(R.string.copy_complete));
                    mBuilder.setProgress(0, 0, false);
                }
                mBuilder.setContentText("");
                mBuilder.setOngoing(false);
                mBuilder.setAutoCancel(true);
                mNotifyManager.notify(id1, mBuilder.build());
                publishCompletedResult(id1);
            }

            //for processviewer
            DataPackage intent = new DataPackage();
            intent.setName(fileName);
            intent.setSourceFiles(sourceFiles);
            intent.setSourceProgress(sourceProgress);
            intent.setTotal(totalSize);
            intent.setByteProgress(writtenSize);
            intent.setSpeedRaw(speed);
            intent.setMove(move);
            intent.setCompleted(isComplete);
            putDataPackage(intent);
            if (progressListener != null) {
                progressListener.onUpdate(intent);
//                Logger.getLogger().i("CopyService DataPackage: " + intent.toString());
                if (isComplete || writtenSize == totalSize || totalSize == 0) {
                    progressListener.refresh();
                }
            }
        } else {
            publishCompletedResult(Integer.parseInt("456" + id));
        }
    }

    public void publishCompletedResult(int id1) {
        try {
            mNotifyManager.cancel(id1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver receiver3 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //cancel operation
            progressHandler.setCancelled(true);
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public CopyService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CopyService.this;
        }
    }

    public interface ProgressListener {
        void onUpdate(DataPackage dataPackage);

        void refresh();
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * Returns the {@link #dataPackages} list which contains
     * Method call is synchronized so as to avoid modifying the list
     * by {@link ServiceWatcherUtil#handlerThread} while {@link MainActivity#runOnUiThread(Runnable)}
     *
     * @return
     */
    public synchronized DataPackage getDataPackage(int index) {
        return this.dataPackages.get(index);
    }

    public synchronized int getDataPackageSize() {
        return this.dataPackages.size();
    }

    /**
     * Puts a {@link DataPackage} into a list
     * Method call is synchronized so as to avoid modifying the list
     * by {@link ServiceWatcherUtil#handlerThread} while {@link MainActivity#runOnUiThread(Runnable)}
     *
     * @param dataPackage
     */
    private synchronized void putDataPackage(DataPackage dataPackage) {
        this.dataPackages.add(dataPackage);
    }

}
