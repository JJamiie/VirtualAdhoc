package com.example.jjamie.virtualadhoc;
import android.content.*;
import android.net.wifi.*;
import java.lang.reflect.*;
/**
 * Created by Administrator on 16/12/2558.
 */
// this class is used to toggle hotspot
public class ApManager {
    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    // toggle wifi hotspot on or off
    public static boolean configApState(Context context,boolean tog) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            // if WiFi is on, turn it off
            /*if(isApOn(context)) {
                wifimanager.setWifiEnabled(false);
            }*/
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            if(tog){
                setHotspotName(context);
                wifimanager.setWifiEnabled(!tog);
                method.invoke(wifimanager, wificonfiguration, true);
            }
            else {
                method.invoke(wifimanager, wificonfiguration, false);
                wifimanager.setWifiEnabled(!tog);
            }

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean setHotspotName(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

            WifiInfo wInfo = wifiManager.getConnectionInfo();
            String macAddress = wInfo.getMacAddress();

            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

            String SSID = "ViR:"+macAddress;

            wifiConfig.SSID = SSID;

            Method setConfigMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfigMethod.invoke(wifiManager, wifiConfig);

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

