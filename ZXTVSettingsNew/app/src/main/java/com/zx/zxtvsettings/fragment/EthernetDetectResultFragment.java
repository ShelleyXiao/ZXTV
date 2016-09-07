package com.zx.zxtvsettings.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zx.zxtvsettings.R;
import com.zx.zxtvsettings.Utils.Logger;
import com.zx.zxtvsettings.activity.EthernetActvity;
import com.zx.zxtvsettings.ethernet.NetData;
import com.zx.zxtvsettings.ethernet.NetState;

import java.util.HashMap;

public class EthernetDetectResultFragment extends Fragment implements View.OnClickListener, View.OnKeyListener {
    private static final String TAG = "EthernetDetectResultFragment";
    private View mView = null;
    private EthernetActvity mEthernetActvity = null;
    private Button mBtn_LastStep = null;
    private Button mBtn_finish = null;
    private TextView mTextView = null;

    enum TextViewID {
        ethernetresult_ip_1_id, ethernetresult_ip_2_id, ethernetresult_ip_3_id, ethernetresult_ip_4_id, ethernetresult_mask_1_id, ethernetresult_mask_2_id, ethernetresult_mask_3_id, ethernetresult_mask_4_id, ethernetresult_gate_1_id, ethernetresult_gate_2_id, ethernetresult_gate_3_id, ethernetresult_gate_4_id, ethernetresult_dns1_1_id, ethernetresult_dns1_2_id, ethernetresult_dns1_3_id, ethernetresult_dns1_4_id, ethernetresult_dns2_1_id, ethernetresult_dns2_2_id, ethernetresult_dns2_3_id, ethernetresult_dns2_4_id,

    };

    private HashMap<TextViewID, TextView> mHashMap = new HashMap<TextViewID, TextView>(20);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_ethernetdetectresult, container, false);
        mEthernetActvity = (EthernetActvity) getActivity();
        mBtn_LastStep = (Button) mView.findViewById(R.id.ethernetdetectresult_back_id);
        mBtn_LastStep.setOnClickListener(this);
        mBtn_LastStep.setOnKeyListener(this);
        mBtn_finish = (Button) mView.findViewById(R.id.ethernetdetectresult_finish_id);
        mBtn_finish.setOnClickListener(this);
        mBtn_finish.setOnKeyListener(this);
        initHashMap();
        updateui();
        getFocus();
        return mView;
    }

    private void getFocus() {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag(NetData.EthernetDetectResult_Tag) != null) {
            Logger.getLogger().i("  getArguments()   " + getArguments());
            if (getArguments() != null) {
                if (getArguments().getBoolean(NetData.EthernetDetectResult_KeyId)) {
                    mBtn_finish.setFocusable(true);
                    mBtn_finish.requestFocus();
                }
            }
        }
    }

    private void initHashMap() {
        mHashMap.clear();
        if (mHashMap.isEmpty()) {
            mHashMap.put(TextViewID.ethernetresult_ip_1_id, (TextView) mView.findViewById(R.id.ethernetresult_ip_1_id));
            mHashMap.put(TextViewID.ethernetresult_ip_2_id, (TextView) mView.findViewById(R.id.ethernetresult_ip_2_id));
            mHashMap.put(TextViewID.ethernetresult_ip_3_id, (TextView) mView.findViewById(R.id.ethernetresult_ip_3_id));
            mHashMap.put(TextViewID.ethernetresult_ip_4_id, (TextView) mView.findViewById(R.id.ethernetresult_ip_4_id));
            mHashMap.put(TextViewID.ethernetresult_mask_1_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_mask_1_id));
            mHashMap.put(TextViewID.ethernetresult_mask_2_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_mask_2_id));
            mHashMap.put(TextViewID.ethernetresult_mask_3_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_mask_3_id));
            mHashMap.put(TextViewID.ethernetresult_mask_4_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_mask_4_id));
            mHashMap.put(TextViewID.ethernetresult_gate_1_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_gate_1_id));
            mHashMap.put(TextViewID.ethernetresult_gate_2_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_gate_2_id));
            mHashMap.put(TextViewID.ethernetresult_gate_3_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_gate_3_id));
            mHashMap.put(TextViewID.ethernetresult_gate_4_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_gate_4_id));
            mHashMap.put(TextViewID.ethernetresult_dns1_1_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_dns1_1_id));
            mHashMap.put(TextViewID.ethernetresult_dns1_2_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_dns1_2_id));
            mHashMap.put(TextViewID.ethernetresult_dns1_3_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_dns1_3_id));
            mHashMap.put(TextViewID.ethernetresult_dns1_4_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_dns1_4_id));
            mHashMap.put(TextViewID.ethernetresult_dns2_1_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_dns2_1_id));
            mHashMap.put(TextViewID.ethernetresult_dns2_2_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_dns2_2_id));
            mHashMap.put(TextViewID.ethernetresult_dns2_3_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_dns2_3_id));
            mHashMap.put(TextViewID.ethernetresult_dns2_4_id,
                    (TextView) mView.findViewById(R.id.ethernetresult_dns2_4_id));
        }
    }

    private void updateui() {
        if (mEthernetActvity.getNetState().isConnected()) {
            if (mEthernetActvity.getNetState().getNetType() == NetState.NetType.TYPE_ETHERNET) {
                String[] iparray = mEthernetActvity.getNetState().getNetStateString(NetState.Data_Id.sETHERNET_IP_ID)
                        .split("\\.");
                String[] maskarray = mEthernetActvity.getNetState().getNetStateString(NetState.Data_Id.sETHERNET_MASK_ID)
                        .split("\\.");
                String[] gatewayarray = mEthernetActvity.getNetState().getNetStateString(NetState.Data_Id.sETHERNET_GATE_ID)
                        .split("\\.");
                String[] dns1array = mEthernetActvity.getNetState().getNetStateString(NetState.Data_Id.sETHERNET_DNS1_ID)
                        .split("\\.");
                String[] dns2array = mEthernetActvity.getNetState().getNetStateString(NetState.Data_Id.sETHERNET_DNS2_ID)
                        .split("\\.");
                String[] macarray = mEthernetActvity.getNetState().getNetStateString(NetState.Data_Id.sETHERNET_MAC_ID)
                        .split(":");
                mHashMap.get(TextViewID.ethernetresult_ip_1_id).setText(iparray[0]);
                mHashMap.get(TextViewID.ethernetresult_ip_2_id).setText(iparray[1]);
                mHashMap.get(TextViewID.ethernetresult_ip_3_id).setText(iparray[2]);
                mHashMap.get(TextViewID.ethernetresult_ip_4_id).setText(iparray[3]);
                mHashMap.get(TextViewID.ethernetresult_mask_1_id).setText(maskarray[0]);
                mHashMap.get(TextViewID.ethernetresult_mask_2_id).setText(maskarray[1]);
                mHashMap.get(TextViewID.ethernetresult_mask_3_id).setText(maskarray[2]);
                mHashMap.get(TextViewID.ethernetresult_mask_4_id).setText(maskarray[3]);
                mHashMap.get(TextViewID.ethernetresult_gate_1_id).setText(gatewayarray[0]);
                mHashMap.get(TextViewID.ethernetresult_gate_2_id).setText(gatewayarray[1]);
                mHashMap.get(TextViewID.ethernetresult_gate_3_id).setText(gatewayarray[2]);
                mHashMap.get(TextViewID.ethernetresult_gate_4_id).setText(gatewayarray[3]);
                mHashMap.get(TextViewID.ethernetresult_dns1_1_id).setText(dns1array[0]);
                mHashMap.get(TextViewID.ethernetresult_dns1_2_id).setText(dns1array[1]);
                mHashMap.get(TextViewID.ethernetresult_dns1_3_id).setText(dns1array[2]);
                mHashMap.get(TextViewID.ethernetresult_dns1_4_id).setText(dns1array[3]);
                mHashMap.get(TextViewID.ethernetresult_dns2_1_id).setText(dns2array[0]);
                mHashMap.get(TextViewID.ethernetresult_dns2_2_id).setText(dns2array[1]);
                mHashMap.get(TextViewID.ethernetresult_dns2_3_id).setText(dns2array[2]);
                mHashMap.get(TextViewID.ethernetresult_dns2_4_id).setText(dns2array[3]);
                return;
            } else if (mEthernetActvity.getNetState().getNetType() == NetState.NetType.TYPE_WIFI) {
                Logger.getLogger().i(  "add wifi info");
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ethernetdetectresult_back_id:
            case R.id.ethernetdetectresult_finish_id:
                Logger.getLogger().i( "enter onClick ethernet_configstart_id ");
                detect_start();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (v == null) {
            return false;
        }
        Logger.getLogger().i( "keyCode =  " + keyCode);
        boolean handled = false;
        String str;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    detect_start();
                    handled = true;
                }
                break;
            default:
                break;

        }
        return handled;
    }

    private void detect_start() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = mEthernetActvity.getHashMap().get(NetData.Ethernet_KeyId);
        Bundle arguments = new Bundle();
        arguments.putBoolean(NetData.Ethernet_KeyId, true);
        fragment.setArguments(arguments);
        Logger.getLogger().i(  "fragment = " + fragment);
        Logger.getLogger().i(  "transaction = " + transaction);
        transaction.replace(R.id.container, fragment, NetData.Ethernet_Tag).commit();
    }
}