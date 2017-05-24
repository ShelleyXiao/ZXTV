package com.zx.zx2000tvfileexploer.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.text.Spanned;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.DataPackage;
import com.zx.zx2000tvfileexploer.fileutil.service.CopyService;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.util.concurrent.TimeUnit;

/**
 * User: ShaudXiao
 * Date: 2017-05-19
 * Time: 15:10
 * Company: zx
 * Description:
 * FIXME
 */


public class ProgressUpdateDialog extends DialogFragment {

    private Button mCancelButton;

    private View rootView;
    private TextView mProgressTypeText, mProgressFileNameText,
            mProgressBytesText, mProgressFileText, mProgressSpeedText, mProgressTimer;
    private ProgressBar mProgressBar;

    private int accentColor;
    private String written;
    private String processingFile;
    private String currentSpeed;
    private String outOf;
    private String of;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        written = getActivity().getResources().getString(R.string.written);
        processingFile=  getActivity().getResources().getString(R.string.processing_file);
        currentSpeed = getActivity().getResources().getString(R.string.current_speed);
        outOf = getResources().getString(R.string.out_of);
        of = getResources().getString(R.string.of);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Dialog dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = (int) getResources().getDimension(R.dimen.px400);

        window.setAttributes(lp);

        accentColor = getResources().getColor(R.color.primary_pink);

        rootView = inflater.inflate(R.layout.dialog_progress, null);
        mCancelButton = (Button) rootView.findViewById(R.id.delete_button);
        mProgressTypeText = (TextView) rootView.findViewById(R.id.text_view_progress_type);
        mProgressFileNameText = (TextView) rootView.findViewById(R.id.text_view_progress_file_name);
        mProgressBytesText = (TextView) rootView.findViewById(R.id.text_view_progress_bytes);
        mProgressFileText = (TextView) rootView.findViewById(R.id.text_view_progress_file);
        mProgressSpeedText = (TextView) rootView.findViewById(R.id.text_view_progress_speed);
        mProgressTimer = (TextView) rootView.findViewById(R.id.text_view_progress_timer);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        mProgressBar.setMax(100);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelBroadcast(new Intent(CopyService.TAG_BROADCAST_COPY_CANCEL));
            }
        });


        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        getDialog().getWindow().setLayout((int) getResources().getDimension(R.dimen.px800),
                (int) getResources().getDimension(R.dimen.px400));

        Intent intent = new Intent(getActivity(), CopyService.class);
        getActivity().bindService(intent, mConnection, 0);

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(mConnection);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance

            CopyService.LocalBinder localBinder = (CopyService.LocalBinder) service;
            CopyService copyService = localBinder.getService();

            for (int i = 0; i < copyService.getDataPackageSize(); i++) {
                processResults(copyService.getDataPackage(i), ServiceType.COPY);
            }


            copyService.setProgressListener(new CopyService.ProgressListener() {
                @Override
                public void onUpdate(final DataPackage dataPackage) {
                    if (getActivity() == null || getActivity().getFragmentManager().
                            findFragmentByTag(ProgressUpdateDialog.class.getName()) == null) {
                        Logger.getLogger().w("********callback called when we're not inside the app*********** ");
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processResults(dataPackage, ServiceType.COPY);
                        }
                    });
                }

                @Override
                public void refresh() {
                    Logger.getLogger().i("********refresh*********** ");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    });
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    enum ServiceType {

        COPY, EXTRACT, COMPRESS, ENCRYPT, DECRYPT
    }

    public void processResults(final DataPackage dataPackage, ServiceType serviceType) {
        if (dataPackage != null) {
            String name = dataPackage.getName();
            long total = dataPackage.getTotal();
            long doneBytes = dataPackage.getByteProgress();
            boolean move = dataPackage.isMove();

//            Logger.getLogger().i("processResults dataPackage: " + dataPackage);

            mProgressFileNameText.setText(name);

            Spanned bytesText = Html.fromHtml(written
                    + " <font color='" + accentColor + "'><i>" + Formatter.formatFileSize(getActivity(), doneBytes)
                    + " </font></i>" +  outOf + " <i>"
                    + Formatter.formatFileSize(getActivity(), total) + "</i>");
            mProgressBytesText.setText(bytesText);

            Spanned fileProcessedSpan = Html.fromHtml(processingFile
                    + " <font color='" + accentColor + "'><i>" + (dataPackage.getSourceProgress())
                    + " </font></i>" +  of + " <i>"
                    + dataPackage.getSourceFiles() + "</i>");
            mProgressFileText.setText(fileProcessedSpan);

            Spanned speedSpan = Html.fromHtml(currentSpeed
                    + ": <font color='" + accentColor + "'><i>"
                    + Formatter.formatFileSize(getActivity(), dataPackage.getSpeedRaw())
                    + "/s</font></i>");

            mProgressSpeedText.setText(speedSpan);

            float percent = ((float) doneBytes / total) * 100;
            mProgressBar.setProgress((int) percent);
        }
    }

    /**
     * Formats input to plain mm:ss format
     *
     * @param timer
     * @return
     */
    private String formatTimer(long timer) {
        final long min = TimeUnit.SECONDS.toMinutes(timer);
        final long sec = TimeUnit.SECONDS.toSeconds(timer - TimeUnit.MINUTES.toMillis(min));
        return String.format("%02d:%02d", min, sec);
    }

    /**
     * Setup click listener to cancel button click for various intent types
     *
     * @param intent
     */
    private void cancelBroadcast(final Intent intent) {

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.operation_copy_cancel), Toast.LENGTH_LONG).show();
                getActivity().sendBroadcast(intent);
                mProgressTypeText.setText(getResources().getString(R.string.cancelled));
                mProgressSpeedText.setText("");
                mProgressFileText.setText("");
                mProgressBytesText.setText("");
                mProgressFileNameText.setText("");

            }
        });
    }

}
