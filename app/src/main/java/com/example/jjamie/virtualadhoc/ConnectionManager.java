package com.example.jjamie.virtualadhoc;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by Administrator on 16/12/2558.
 */
public class ConnectionManager {
        static List<ScanResult> results;
        static int size = 0;
        static List<String> availableAP;

        public static void forThread(Context context){
            listAP(context);
            if(results.size()<=0){
                //Noone around here use this App so turn on AP.
                ApManager.configApState(context,true);
            }
            else {
                //connect AP
                connectAP(context);
            }

        }
        public static boolean joinAp(String SSID){
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + SSID + "\"";
           // conf.wepKeys[0] = "\"" + networkPass + "\"";    In case of network has password
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            return true;
        }

        public static boolean listAP(Context context){
            String SSID= null;
            String[] tokens = null;
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            wifiManager.startScan();
            results = wifiManager.getScanResults();
            size =results.size();
            int tsize = size-1;
            availableAP.clear();
            for (int i=0; i<=tsize;i++){
                tokens = results.get(i).SSID.split(":");
                if(tokens[0].equals("ViR")){
                    availableAP.add(results.get(i).SSID);
                }
            }
            return true;
        }
    public static boolean connectAP(Context context){ // Change algo later ex. Ap which this device joined
        String SSID= null;
        if (!availableAP.isEmpty()){ //check and select the strongest signal
            SSID = availableAP.get(0);
            joinAp(SSID);
            availableAP.remove(0);

        }
        return true;
    }

}

