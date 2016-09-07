package com.zx.zxtvsettings.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zx.zxtvsettings.R;
import com.zx.zxtvsettings.Utils.Logger;
import com.zx.zxtvsettings.activity.EthernetActvity;
import com.zx.zxtvsettings.ethernet.NetData;

public class EthernetConfigFailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "EthernetConfigFailFragment";
    private View mView = null;
    private EthernetActvity mEthernetActvity = null;
    private TextView mTextView = null;
    private Button mBtn_finish = null;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_ethernetconfigfail, container, false);
        mEthernetActvity = (EthernetActvity) getActivity();
        mTextView = (TextView) mView.findViewById(R.id.ethernetconfigfail_text_id);
        mBtn_finish = (Button) mView.findViewById(R.id.ethernetconfigfail_button_id);
        mBtn_finish.setOnClickListener(this);
        getFocus();
        return mView;
    }

    @Override
    public void onDestroy() {
        //mHandler.removeCallbacks(mRunnalbe);
        Logger.getLogger().i("enter EthernetConfigFailFragment Destory!");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ethernetconfigfail_button_id:
                detect_finish();
                break;
            default:
                break;
        }
    }

    private void getFocus() {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag(NetData.EthernetConfigFail_Tag) != null) {
            Logger.getLogger().i("  getArguments()   " + getArguments());
            if (getArguments() != null) {
                if (getArguments().getBoolean(NetData.EthernetConfigFail_KeyId)) {
                    mBtn_finish.setFocusable(true);
                    mBtn_finish.requestFocus();
                }
            }
        }
    }

    private void detect_finish() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = (EthernetFragment) mEthernetActvity.getHashMap().get(NetData.Ethernet_KeyId);
        Bundle arguments = new Bundle();
        arguments.putBoolean(NetData.Ethernet_KeyId, true);
        fragment.setArguments(arguments);
        Logger.getLogger().i( "fragment = " + fragment);
        transaction.replace(R.id.container, fragment, NetData.Ethernet_Tag).commit();

    }
}