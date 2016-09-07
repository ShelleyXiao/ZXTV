package com.zx.zxtvsettings.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zx.zxtvsettings.R;
import com.zx.zxtvsettings.activity.EthernetActvity;
import com.zx.zxtvsettings.ethernet.NetData;
import com.zx.zxtvsettings.ethernet.NetState;

public class EthernetFragment extends Fragment implements View.OnClickListener {
	private static final String TAG = "EthernetFragment";
	private RadioGroup mRadioGroup = null;
	private RadioButton mRadioAuto = null;
	private RadioButton mRadioMan = null;
	private RadioButton mRadioPppoe = null;
	private Button mBtn_ConfigStart = null;
	private EthernetActvity mEthernetActvity = null;
	private View mView = null;
	private Boolean mAutochecked = true;
	private Boolean mManchecked = false;
	private Boolean mPppoechecked = false;
	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_ethernet, container, false);
		mEthernetActvity = (EthernetActvity) getActivity();
		mRadioGroup = (RadioGroup) mView.findViewById(R.id.radiogroup_id);
		mRadioAuto = (RadioButton) mView.findViewById(R.id.auto_radio_id);
		mRadioMan = (RadioButton) mView.findViewById(R.id.man_radio_id);
		mRadioPppoe = (RadioButton) mView.findViewById(R.id.pppoe_radio_id);
		mBtn_ConfigStart = (Button) mView.findViewById(R.id.ethernet_configstart_id);
		mBtn_ConfigStart.setOnClickListener(this);
		mPreferences = mEthernetActvity.getSharedPreferences("networkactivity", EthernetActvity.MODE_PRIVATE);
		mEditor = mPreferences.edit();
		initView();
		getFocus();
		restoremode();
		return mView;
	}

	private void restoremode() {
		String mode;
		if (mPreferences == null)
			return;
		mode = mPreferences.getString("MODE", null);
		Log.i(TAG, "restore mode = " + mode);
		if (mode == null)
			return;

		if (mode.equals("auto")) {
			Log.i(TAG, "restore mode auto");
			mAutochecked = true;
			mManchecked = false;
			mPppoechecked = false;
			mRadioAuto.setChecked(true);
			mRadioMan.setChecked(false);
			mRadioPppoe.setChecked(false);
		} else if (mode.equals("man")) {
			Log.i(TAG, "restore mode man");
			mAutochecked = false;
			mManchecked = true;
			mPppoechecked = false;
			mRadioAuto.setChecked(false);
			mRadioMan.setChecked(true);
			mRadioPppoe.setChecked(false);
		} else if (mode.equals("pppoe")) {
			Log.i(TAG, "restore mode pppoe");
			mAutochecked = false;
			mManchecked = false;
			mPppoechecked = true;
			mRadioAuto.setChecked(false);
			mRadioMan.setChecked(false);
			mRadioPppoe.setChecked(true);
		} else {
			Log.e(TAG, "restore mode error! please check!");
		}

	}

	private void storemode() {
		if (mAutochecked) {
			mEditor.putString("MODE", "auto");
		} else if (mManchecked) {
			mEditor.putString("MODE", "man");
		} else if (mPppoechecked) {
			mEditor.putString("MODE", "pppoe");
		}
		mEditor.commit();
	}

	public void initView() {
		mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.auto_radio_id:
					mAutochecked = true;
					mManchecked = false;
					mPppoechecked = false;
					Toast.makeText(getActivity(), "Network Auto Connect ", Toast.LENGTH_LONG).show();
					break;
				case R.id.man_radio_id:
					mAutochecked = false;
					mManchecked = true;
					mPppoechecked = false;
					Toast.makeText(getActivity(), "Network Manul Connect", Toast.LENGTH_LONG).show();
					break;
				case R.id.pppoe_radio_id:
					mAutochecked = false;
					mManchecked = false;
					mPppoechecked = true;
					Toast.makeText(getActivity(), "pppoe config", Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}

			}
		});
	}

	private void getFocus() {
		FragmentManager fm = getFragmentManager();
		if (fm.findFragmentByTag(NetData.Ethernet_Tag) != null) {
			Log.i(TAG, "  getArguments()   " + getArguments());
			if (getArguments() != null) {
				if (getArguments().getBoolean(NetData.Ethernet_KeyId)) {
					mBtn_ConfigStart.setFocusable(true);
					mBtn_ConfigStart.requestFocus();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ethernet_configstart_id:
			Log.i(TAG, "enter onClick ethernet_configstart_id ");
			storemode();
			detect_start();
			break;
		default:
			break;
		}
	}

	private void detect_start() {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (mAutochecked) {
			mEthernetActvity.getNetState().setEthernetMode(NetState.EthernetMode.MODE_AUTO);
			Fragment fragment = mEthernetActvity.getHashMap().get(NetData.EthernetAutoDetect_KeyId);
			Bundle arguments = new Bundle();
			arguments.putBoolean(NetData.EthernetAutoDetect_KeyId, true);
			if(fragment.isAdded()) {

			} else {

			}
			fragment.setArguments(arguments);
			transaction.replace(R.id.container, fragment, fragment.getClass().getName()).commit();

		}
		if (mManchecked) {
			mEthernetActvity.getNetState().setEthernetMode(NetState.EthernetMode.MODE_MAN);
			Fragment fragment = mEthernetActvity.getHashMap().get(NetData.EthernetManConfig_KeyId);
			Bundle arguments = new Bundle();
			arguments.putBoolean(NetData.EthernetManConfig_KeyId, true);
			fragment.setArguments(arguments);
			transaction.replace(R.id.container, fragment, fragment.getClass().getName()).commit();

		}

		if (mPppoechecked) {
			mEthernetActvity.getNetState().setEthernetMode(NetState.EthernetMode.MODE_PPPOE);
			Fragment fragment = mEthernetActvity.getHashMap().get(NetData.Pppoe_KeyId);
			Bundle arguments = new Bundle();
			arguments.putBoolean(NetData.Pppoe_KeyId, true);
			fragment.setArguments(arguments);
			transaction.replace(R.id.container, fragment, NetData.Pppoe_Tag).commit();
		}
	}

}
