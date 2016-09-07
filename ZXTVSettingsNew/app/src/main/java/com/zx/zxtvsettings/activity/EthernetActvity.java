package com.zx.zxtvsettings.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.IpConfiguration.IpAssignment;
import android.net.LinkAddress;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.StaticIpConfiguration;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.ServiceManager;
import android.os.SystemProperties;

import com.zx.zxtvsettings.R;
import com.zx.zxtvsettings.Utils.Logger;
import com.zx.zxtvsettings.ethernet.NetData;
import com.zx.zxtvsettings.ethernet.NetState;
import com.zx.zxtvsettings.fragment.EthernetAutoDetectFragment;
import com.zx.zxtvsettings.fragment.EthernetConfigFailFragment;
import com.zx.zxtvsettings.fragment.EthernetDetectResultFragment;
import com.zx.zxtvsettings.fragment.EthernetFragment;
import com.zx.zxtvsettings.fragment.EthernetManConfigFragment;
import com.zx.zxtvsettings.fragment.EthernetManDetectFragment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * User: ShaudXiao
 * Date: 2016-08-23
 * Time: 10:35
 * Company: zx
 * Description:
 * FIXME
 */

public class EthernetActvity extends BaseActivity implements EthernetAutoDetectFragment.Callbacks,
        EthernetManDetectFragment.Callbacks{

    private static final String TAG = "NetworkActivity";
    private NetState mNetState = null;
    private Context mContext;
    NetworkInfo.State mEthernetState;
    NetworkInfo.State mWifiState;
    private boolean mFastSetFlag = false;
    private SharedPreferences mPreferences;
    ConnectivityManager cm;
    private static final String ETH0_MAC_ADDR = "/sys/class/net/eth0/address";
    private static final String WLAN0_MAC_ADDR = "/sys/class/net/wlan0/address";
    private HashMap<String, Fragment> mFragmentMap = new HashMap<String, Fragment>(30);

    // Ethernet ===============================================================

    Inet4Address inetAddr = null;
    InetAddress gatewayAddr = null;
    InetAddress dnsAddr1 = null;
    InetAddress dnsAddr2 = null;
    int networkPrefixLength = -1;
    private static final String IP_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
    private static final String SLASH_FORMAT = IP_ADDRESS + "/(\\d{1,3})";
    private static final Pattern addressPattern = Pattern.compile(IP_ADDRESS);
    private static final Pattern cidrPattern = Pattern.compile(SLASH_FORMAT);

    IpConfiguration staticIpConfig = new IpConfiguration();
    private IpConfiguration mIpConfiguration;
    private EthernetManager mEthernetManager;
    // private final EthernetConfigStore mEthernetConfigStore;
    /** To set link state and configure IP addresses. */
    private INetworkManagementService mNMService;

    // update setting values on IP address, Gateway, Netmask, DNS
    StaticIpConfiguration staticIpConfiguration = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ethernet;
    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void initialized() {

        mNetState = new NetState();
        mContext = this;
        IBinder b = ServiceManager.getService(mContext.NETWORKMANAGEMENT_SERVICE);
        mNMService = INetworkManagementService.Stub.asInterface(b);

        restoredata();
        updateNetstate();

        registerfragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = getHashMap().get(NetData.Ethernet_KeyId);
        Logger.getLogger().i( "fragment = " + fragment);
        if(!fragment.isAdded()) {
            transaction.add(R.id.container, fragment, fragment.getClass().getName());
        }
        transaction.show(fragment);
        transaction.commit();
    }

    private void restoredata() {
        String mode = null;
        String ipaddress = null;
        String maskaddress = null;
        String gateaddress = null;
        String dns1address = null;
        String dns2address = null;
        mPreferences = getSharedPreferences("networkactivity", MODE_PRIVATE);
        mode = mPreferences.getString("MODE", null);
        if (mode != null) {
            if (mode.equals("auto")) {
                getNetState().setEthernetMode(NetState.EthernetMode.MODE_AUTO);
            } else if (mode.equals("man")) {
                getNetState().setEthernetMode(NetState.EthernetMode.MODE_MAN);
            } else if (mode.equals("pppoe")) {
                getNetState().setEthernetMode(NetState.EthernetMode.MODE_PPPOE);
            }
        }
        if (mode != null && mode.equals("man")) {
            ipaddress = mPreferences.getString("man_ip", null);
            maskaddress = mPreferences.getString("man_mask", null);
            gateaddress = mPreferences.getString("man_gate", null);
            dns1address = mPreferences.getString("man_dns1", null);
            dns2address = mPreferences.getString("man_dns2", null);
            getNetState().setNetStateString(NetState.Data_Id.sETHERNET_IP_ID, ipaddress);
            getNetState().setNetStateString(NetState.Data_Id.sETHERNET_MASK_ID, maskaddress);
            getNetState().setNetStateString(NetState.Data_Id.sETHERNET_GATE_ID, gateaddress);
            getNetState().setNetStateString(NetState.Data_Id.sETHERNET_DNS1_ID, dns1address);
            getNetState().setNetStateString(NetState.Data_Id.sETHERNET_DNS2_ID, dns2address);
        }

    }

    public NetState getNetState() {
        return mNetState;
    }

    public HashMap<String, Fragment> getHashMap() {
        return mFragmentMap;
    }

    public void register_ethernetcontainer_fragments(String key, Fragment frag) {
        Logger.getLogger().i("enter on register_ethernetcontainer_fragments key = " + key);
        if (mFragmentMap.get(key) == null)
            mFragmentMap.put(key, frag);

    }

    public void registerfragment() {
        register_ethernetcontainer_fragments(NetData.Ethernet_KeyId,
                new EthernetFragment());
        register_ethernetcontainer_fragments(NetData.EthernetAutoDetect_KeyId,
                new EthernetAutoDetectFragment());
        register_ethernetcontainer_fragments(NetData.EthernetManDetect_KeyId,
                new EthernetManDetectFragment());
        register_ethernetcontainer_fragments(NetData.EthernetManConfig_KeyId,
                new EthernetManConfigFragment());
        register_ethernetcontainer_fragments(NetData.EthernetDetectResult_KeyId,
                new EthernetDetectResultFragment());

        register_ethernetcontainer_fragments(NetData.EthernetConfigFail_KeyId,
                new EthernetConfigFailFragment());

//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        Fragment fragment = getHashMap().get(NetData.Ethernet_KeyId);
//        if (!fragment.isAdded()) {
//            transaction.add(R.id.container, fragment, fragment.getClass().getName());
//        }
//        transaction.hide(fragment);
//
//        fragment = getHashMap().get(NetData.EthernetAutoDetect_KeyId);
//        if (!fragment.isAdded()) {
//            transaction.add(R.id.container, fragment, fragment.getClass().getName());
//        }
//        transaction.hide(fragment);
//
//        fragment = getHashMap().get(NetData.EthernetManDetect_KeyId);
//        if (!fragment.isAdded()) {
//            transaction.add(R.id.container, fragment, fragment.getClass().getName());
//        }
//        transaction.hide(fragment);
//
//        fragment = getHashMap().get(NetData.EthernetManConfig_KeyId);
//        if (!fragment.isAdded()) {
//            transaction.add(R.id.container, fragment, fragment.getClass().getName());
//        }
//        transaction.hide(fragment);
//
//        fragment = getHashMap().get(NetData.EthernetDetectResult_KeyId);
//        if (!fragment.isAdded()) {
//            transaction.add(R.id.container, fragment, fragment.getClass().getName());
//        }
//        transaction.hide(fragment);

//        fragment = getHashMap().get(NetData.EthernetConfigFail_KeyId);
//        if (!fragment.isAdded()) {
//            transaction.add(R.id.container, fragment, fragment.getClass().getName());
//        }
//        transaction.hide(fragment);
//
//        transaction.commit();

    }

    @Override
    public boolean ethernet_autodetect_ethernet() {
        mFastSetFlag = false;
        updateNetstate();
        return isEthernetConnected();
    }

    private void autodetect() {
        mIpConfiguration = new IpConfiguration();
        mEthernetManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        try {
            mNMService.setInterfaceDown("eth0");
        } catch (Exception e) {
            Logger.getLogger().i("Failed to setInterfaceDown");
        }
        // clear address
        try {
            mNMService.clearInterfaceAddresses("eth0");
        } catch (Exception e) {
            Logger.getLogger().i( "Failed to clear addresses or disable ipv6" + e);
        }
        mIpConfiguration.setStaticIpConfiguration(null);
        mIpConfiguration.setIpAssignment(IpAssignment.DHCP);
        mEthernetManager.setConfiguration(mIpConfiguration);
        try {
            mNMService.setInterfaceUp("eth0");
        } catch (Exception e) {
            Logger.getLogger().i( "Failed to setInterfaceUp");
        }
    }


    @Override
    public boolean ethernet_mandetect_ethernet() {
        if (isEthernetConnected()) {
            getNetState().setNetType(NetState.NetType.TYPE_ETHERNET);
            getNetState().setEthernetConnectState(true);
            Logger.getLogger().i( "get Man Ethernet info success!");
        } else {
            getNetState().setNetType(NetState.NetType.TYPE_NONE);
            getNetState().setEthernetConnectState(false);
            Logger.getLogger().i( "Man ethernet disconnect!");
        }
        return isEthernetConnected();
    }

    @Override
    public void mandetect_ethernet() {
        mIpConfiguration = new IpConfiguration();
        mEthernetManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        staticIpConfiguration = new StaticIpConfiguration();
        try {
            mNMService.setInterfaceDown("eth0");
        } catch (Exception e) {
            Logger.getLogger().i( "Failed to setInterfaceDown");
        }
        // clear address
        try {
            mNMService.clearInterfaceAddresses("eth0");
        } catch (Exception e) {
            Logger.getLogger().i( "Failed to clear addresses or disable ipv6" + e);
        }
        try {
            inetAddr = getIPv4Address(getNetState().getNetStateString(NetState.Data_Id.sETHERNET_IP_ID));
            gatewayAddr = getIPv4Address(getNetState().getNetStateString(NetState.Data_Id.sETHERNET_GATE_ID));
            networkPrefixLength = (int) toInteger(getNetState().getNetStateString(NetState.Data_Id.sETHERNET_MASK_ID));
            dnsAddr1 = getIPv4Address(getNetState().getNetStateString(NetState.Data_Id.sETHERNET_DNS1_ID));
            dnsAddr2 = getIPv4Address(getNetState().getNetStateString(NetState.Data_Id.sETHERNET_DNS2_ID));

        } catch (Exception e) {
            Logger.getLogger().i( "mIpAddress   = " + getNetState().getNetStateString(NetState.Data_Id.sETHERNET_IP_ID));
            Logger.getLogger().i( "mGatewayAddress   = " + getNetState().getNetStateString(NetState.Data_Id.sETHERNET_GATE_ID));
            Logger.getLogger().i( "mMaskAddress   = " + getNetState().getNetStateString(NetState.Data_Id.sETHERNET_MASK_ID));
            Logger.getLogger().i( "mDns1Address   = " + getNetState().getNetStateString(NetState.Data_Id.sETHERNET_DNS1_ID));
            Logger.getLogger().i( "mDns2Address   = " + getNetState().getNetStateString(NetState.Data_Id.sETHERNET_DNS2_ID));
            Logger.getLogger().i( "mandetect_ethernet  STATIC FAIL!!!!!!!!!!!");
            return;
        }
        if (inetAddr != null && networkPrefixLength != -1) {
            Logger.getLogger().i( "ipAddress set ok");
            staticIpConfiguration.ipAddress = new LinkAddress(inetAddr, networkPrefixLength);
        } else {
            try {
                mNMService.setInterfaceDown("eth0");
            } catch (Exception e) {
                Logger.getLogger().i( "Failed to setInterfaceDown");
            }
        }
        if (gatewayAddr != null) {
            Logger.getLogger().i( "gateway set ok");
            staticIpConfiguration.gateway = gatewayAddr;

        }

        if (dnsAddr1 != null) {
            Logger.getLogger().i( "dns1 set ok");
            staticIpConfiguration.dnsServers.add(dnsAddr1);
        }

        if (dnsAddr2 != null) {
            Logger.getLogger().i( "dns2 set ok");
            staticIpConfiguration.dnsServers.add(dnsAddr2);
        }
        mIpConfiguration.setStaticIpConfiguration(staticIpConfiguration);
        mIpConfiguration.setIpAssignment(IpAssignment.STATIC);
        mEthernetManager.setConfiguration(mIpConfiguration);
        try {
            mNMService.setInterfaceUp("eth0");
        } catch (Exception e) {
            Logger.getLogger().i( "Failed to setInterfaceUp");
        }

    }


    @Override
    public void autodetect_ethernet() {
        autodetect();
    }

    private void updateNetstate() {
        String ipaddress;
        String netmask;
        String gateway;
        String dns1;
        String dns2;
        String mac;
        NetState.EthernetMode ethernetmode = getNetState().getEthernetMode();
        Logger.getLogger().i( "ethernetmode = " + ethernetmode + "mFastSetFlag = " + mFastSetFlag);
        if (isEthernetConnected()) {
            mac = geteth0MacAddr();
            getNetState().setNetStateString(NetState.Data_Id.sETHERNET_MAC_ID, mac);
            if (ethernetmode == NetState.EthernetMode.MODE_AUTO || mFastSetFlag) {
                ipaddress = SystemProperties.get("dhcp.eth0.ipaddress", "error");
                netmask = SystemProperties.get("dhcp.eth0.mask", "error");
                gateway = SystemProperties.get("dhcp.eth0.gateway", "error");
                dns1 = SystemProperties.get("dhcp.eth0.dns1", "error");
                dns2 = SystemProperties.get("dhcp.eth0.dns2", "0.0.0.0");
                if (ipaddress.equals("error") || netmask.equals("error") || gateway.equals("error")
                        || dns1.equals("error")) {
                    Logger.getLogger().i( "get Ethernet info Error!");
                    Logger.getLogger().i( "dhcp.eth0.ipaddress = " + ipaddress);
                    Logger.getLogger().i( "dhcp.eth0.mask= " + netmask);
                    Logger.getLogger().i( "dhcp.eth0.gateway = " + gateway);
                    Logger.getLogger().i( "dhcp.eth0.dns1 = " + dns1);
                    return;
                }
                getNetState().setNetStateString(NetState.Data_Id.sETHERNET_IP_ID, ipaddress);
                getNetState().setNetStateString(NetState.Data_Id.sETHERNET_MASK_ID, netmask);
                getNetState().setNetStateString(NetState.Data_Id.sETHERNET_GATE_ID, gateway);
                getNetState().setNetStateString(NetState.Data_Id.sETHERNET_DNS1_ID, dns1);
                getNetState().setNetStateString(NetState.Data_Id.sETHERNET_DNS2_ID, dns2);
                // getNetState().setNetType(NetType.TYPE_ETHERNET);
            }
            getNetState().setEthernetConnectState(true);
        } else {
            getNetState().setEthernetConnectState(false);
        }

        if (isWifiConnected()) {
            ipaddress = SystemProperties.get("dhcp.wlan0.ipaddress", "error");
            netmask = SystemProperties.get("dhcp.wlan0.mask", "error");
            gateway = SystemProperties.get("dhcp.wlan0.gateway", "error");
            dns1 = SystemProperties.get("dhcp.wlan0.dns1", "error");
            dns2 = SystemProperties.get("dhcp.wlan0.dns2", "0.0.0.0");
            mac = getwlan0MacAddr();
            if (ipaddress.equals("error") || netmask.equals("error") || gateway.equals("error")
                    || dns1.equals("error")) {
                Logger.getLogger().i( "get Wifi info Error!");
                Logger.getLogger().i( "dhcp.wlan0.ipaddress = " + ipaddress);
                Logger.getLogger().i( "dhcp.wlan0.mask= " + netmask);
                Logger.getLogger().i( "dhcp.wlan0.gateway = " + gateway);
                Logger.getLogger().i( "dhcp.wlan0.dns1 = " + dns1);
                return;
            }
            getNetState().setNetStateString(NetState.Data_Id.sWIFI_IP_ID, ipaddress);
            getNetState().setNetStateString(NetState.Data_Id.sWIFI_MASK_ID, netmask);
            getNetState().setNetStateString(NetState.Data_Id.sWIFI_GATE_ID, gateway);
            getNetState().setNetStateString(NetState.Data_Id.sWIFI_DNS1_ID, dns1);
            getNetState().setNetStateString(NetState.Data_Id.sWIFI_DNS2_ID, dns2);
            getNetState().setNetStateString(NetState.Data_Id.sWIFI_MAC_ID, mac);
            // getNetState().setNetType(NetType.TYPE_WIFI);
            getNetState().setWifiConnectState(true);
        } else {
            getNetState().setWifiConnectState(false);
        }

        if (isPppoeConnected()) {
            ipaddress = SystemProperties.get("net.ppp0.local-ip", "error");
            netmask = SystemProperties.get("net.ppp0.mask", "255.255.255.0");
            gateway = SystemProperties.get("net.ppp0.gw", "error");
            dns1 = SystemProperties.get("net.ppp0.dns1", "0.0.0.0");
            dns2 = SystemProperties.get("net.ppp0.dns2", "0.0.0.0");
            mac = geteth0MacAddr();
            if (ipaddress.equals("error") || netmask.equals("error") || gateway.equals("error")) {
                Logger.getLogger().i( "get Pppoe info Error!");
                Logger.getLogger().i( "net.ppp0.local-ip = " + ipaddress);
                Logger.getLogger().i( "net.ppp0.mask = " + netmask);
                Logger.getLogger().i( "net.ppp0.gw = " + gateway);
                Logger.getLogger().i( "net.ppp0.dns1 = " + dns1);
                return;
            }
            getNetState().setNetStateString(NetState.Data_Id.sPPPOE_IP_ID, ipaddress);
            getNetState().setNetStateString(NetState.Data_Id.sPPPOE_MASK_ID, netmask);
            getNetState().setNetStateString(NetState.Data_Id.sPPPOE_GATE_ID, gateway);
            getNetState().setNetStateString(NetState.Data_Id.sPPPOE_DNS1_ID, dns1);
            getNetState().setNetStateString(NetState.Data_Id.sPPPOE_DNS2_ID, dns2);
            getNetState().setNetStateString(NetState.Data_Id.sPPPOE_MAC_ID, mac);
            // getNetState().setNetType(NetType.TYPE_WIFI);
            getNetState().setPppoeConnectState(true);
        } else {
            getNetState().setPppoeConnectState(false);
        }

		/* sort network priority */
        if (isEthernetConnected() && mFastSetFlag) {
            Logger.getLogger().i( "enter case 1");
            getNetState().setNetType(NetState.NetType.TYPE_ETHERNET);
            return;
        } else if (isEthernetConnected() && (ethernetmode != NetState.EthernetMode.MODE_PPPOE)) {
            Logger.getLogger().i( "enter case 2");
            getNetState().setNetType(NetState.NetType.TYPE_ETHERNET);
            return;
        } else if (isPppoeConnected()) {
            Logger.getLogger().i( "enter case 3");
            getNetState().setNetType(NetState.NetType.TYPE_PPPOE);
            return;
        } else if (isWifiConnected()) {
            Logger.getLogger().i( "enter case 4");
            getNetState().setNetType(NetState.NetType.TYPE_WIFI);
            return;
        } else {
            Logger.getLogger().i( "enter case 5");
            getNetState().setNetType(NetState.NetType.TYPE_NONE);
        }
    }

    private String geteth0MacAddr() {
        try {
            return readLine(ETH0_MAC_ADDR);
        } catch (IOException e) {
            Logger.getLogger().i( "IO Exception when getting eth0 mac address" + e);
            e.printStackTrace();
            return "unavailable";
        }
    }

    private String getwlan0MacAddr() {
        try {
            return readLine(WLAN0_MAC_ADDR);
        } catch (IOException e) {
            Logger.getLogger().i( "IO Exception when getting eth0 mac address" +  e);
            e.printStackTrace();
            return "unavailable";
        }
    }

    private boolean isEthernetConnected() {
        boolean result = false;

        cm = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        mEthernetState = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).getState();
        if (mEthernetState == NetworkInfo.State.CONNECTED)
            result = true;
        Logger.getLogger().i( "Ethernet Connect state = " + result);
        return result;

    }

    private boolean isWifiConnected() {
        boolean result = false;
        cm = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        mWifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (mWifiState == NetworkInfo.State.CONNECTED)
            result = true;
        Logger.getLogger().i( "Wifi Connect state = " + result);
        return result;

    }

    private boolean isPppoeConnected() {
        String state;
        boolean ret = false;
        state = SystemProperties.get("net.pppoe.status", "error");
        Logger.getLogger().i( "PppoE connect state = " + state);
        if (state.equals("connected")) {
            ret = true;
        } else if (state.equals("disconnected")) {
            ret = false;
        }
        return ret;
    }

    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    /*
     * Convert a dotted decimal format address to a packed integer format
     */
    private int toInteger(String address) {
        Matcher matcher = addressPattern.matcher(address);
        if (matcher.matches()) {
            return matchAddress(matcher);
        } else
            throw new IllegalArgumentException("Could not parse [" + address + "]");
    }

    /*
     * Convenience method to extract the components of a dotted decimal address
     * and pack into an integer using a regex match
     */
    private int matchAddress(Matcher matcher) {
        int addr = 0;
        for (int i = 1; i <= 4; ++i) {
            int n = (rangeCheck(Integer.parseInt(matcher.group(i)), 0, 255));
            while ((n & 128) == 128) {
                addr = addr + 1;
                n = n << 1;
            }
        }
        return addr;
    }

    /*
     * Convenience function to check integer boundaries
     */
    private int rangeCheck(int value, int begin, int end) {
        if (value >= begin && value <= end)
            return value;
        throw new IllegalArgumentException("Value out of range: [" + value + "]");
    }

    private Inet4Address getIPv4Address(String text) {
        try {
            return (Inet4Address) NetworkUtils.numericToInetAddress(text);
        } catch (IllegalArgumentException | ClassCastException e) {
            return null;
        }
    }
}
