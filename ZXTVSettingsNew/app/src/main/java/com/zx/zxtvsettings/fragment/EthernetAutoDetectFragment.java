package com.zx.zxtvsettings.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zx.zxtvsettings.R;
import com.zx.zxtvsettings.Utils.Logger;
import com.zx.zxtvsettings.activity.EthernetActvity;
import com.zx.zxtvsettings.ethernet.NetData;

public class EthernetAutoDetectFragment extends Fragment {
    private static final String TAG = "EthernetAutoDetectFragment";
    private View mView = null;
    private TextView mTextView = null;
    private EthernetActvity mNetworkActivity = null;
    private Callbacks mCallbacks;
    private Integer times = 0;
    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (times >= 3) {
                if (mCallbacks.ethernet_autodetect_ethernet()) {
                    Logger.getLogger().d( "ethernet_autodetect  success!");
                    detect_result();

                } else {
                    Logger.getLogger().d( "ethernet_autodetect detect again");
                    times++;
                    if (times < 30) {
                        mHandler.postDelayed(mRunnable, 1000);
                    } else {
                        Logger.getLogger().d( "detect fail!");
                        detect_fail();
                    }
                }
            } else {
                times++;
                mHandler.postDelayed(mRunnable, 1000);
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_ethernetautodetect, container, false);
        mNetworkActivity = (EthernetActvity) getActivity();
        mTextView = (TextView) mView.findViewById(R.id.audodetect_text_id);
        FragmentManager fm = getFragmentManager();
        times = 0;
        mCallbacks.autodetect_ethernet();
        if (fm.findFragmentByTag(NetData.EthernetAutoDetect_Tag) != null) {
            Logger.getLogger().d(  "  getArguments()   " + getArguments());
            if (getArguments() != null) {
                if (getArguments().getBoolean(NetData.EthernetAutoDetect_KeyId)) {
                    mTextView.setFocusable(true);
                    mTextView.requestFocus();
                }
            }

        }
        mHandler.post(mRunnable);
        return mView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "NetActivity must realize Callbacks!");
        }
        mCallbacks = (Callbacks) activity;

    }

    public interface Callbacks {
         boolean ethernet_autodetect_ethernet();

         void autodetect_ethernet();
    }

    private void detect_result() {
        mHandler.removeCallbacks(mRunnable);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = mNetworkActivity.getHashMap().get(NetData.EthernetDetectResult_KeyId);
        Bundle arguments = new Bundle();
        arguments.putBoolean(NetData.EthernetDetectResult_KeyId, true);
        fragment.setArguments(arguments);
        Logger.getLogger().d(  "fragment = " + fragment);
        Logger.getLogger().d(  "transaction = " + transaction);
        transaction.replace(R.id.container, fragment, NetData.EthernetDetectResult_Tag).commit();
    }

    private void detect_fail() {
        mHandler.removeCallbacks(mRunnable);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = mNetworkActivity.getHashMap().get(NetData.EthernetConfigFail_KeyId);
        Bundle arguments = new Bundle();
        arguments.putBoolean(NetData.EthernetConfigFail_KeyId, true);
        fragment.setArguments(arguments);
        Logger.getLogger().d( "fragment = " + fragment);
        Logger.getLogger().d(  "transaction = " + transaction);
        transaction.replace(R.id.container, fragment, NetData.EthernetConfigFail_Tag).commit();
    }
}