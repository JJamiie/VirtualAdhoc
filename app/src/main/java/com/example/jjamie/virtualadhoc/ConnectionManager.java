package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by Administrator on 16/12/2558.
 */
public class ConnectionManager extends Thread {
    private static List<ScanResult> results;
    static int size = 0;
    static List<String> availableAP;
    static Map<String, Integer> apHistory;
    static boolean scannerStatus = true;
    static File[] imgFile;
    static File[] imgMetadata;
    static Context contexts;
    Activity activity;
    private boolean active = false;

    public ConnectionManager(Context context) {
        contexts = context;
    }

    public void run() {
        enableWifi(contexts);
        results = new List<ScanResult>() {
            @Override
            public void add(int location, ScanResult object) {

            }

            @Override
            public boolean add(ScanResult object) {
                return false;
            }

            @Override
            public boolean addAll(int location, Collection<? extends ScanResult> collection) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends ScanResult> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public boolean contains(Object object) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return false;
            }

            @Override
            public ScanResult get(int location) {
                return null;
            }

            @Override
            public int indexOf(Object object) {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @NonNull
            @Override
            public Iterator<ScanResult> iterator() {
                return null;
            }

            @Override
            public int lastIndexOf(Object object) {
                return 0;
            }

            @Override
            public ListIterator<ScanResult> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<ScanResult> listIterator(int location) {
                return null;
            }

            @Override
            public ScanResult remove(int location) {
                return null;
            }

            @Override
            public boolean remove(Object object) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return false;
            }

            @Override
            public ScanResult set(int location, ScanResult object) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @NonNull
            @Override
            public List<ScanResult> subList(int start, int end) {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(T[] array) {
                return null;
            }
        };
        availableAP = new ArrayList<>();

        while(true) {
            while(!active){
                try {
                    synchronized (this){
                        wait();
                    }
                } catch (InterruptedException e) {
                    System.out.println("Connection manager"+e.getMessage());
                }
            }

            enableWifi(contexts);
            System.out.println("Stage: Sleep0");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Stage: Awake0");
            listAP(contexts);

            String r = availableAP.size()+"";
             Log.d("ConnectionManager",r);
            if (availableAP.size() <= 0) {
                System.out.println("No AP around ");
                //Noone around here use this App so turn on AP.
                //
                 ApManager.configApState(contexts, true);
                System.out.println("Stage: Sleep1");
                try {
                    Thread.sleep(120000);
                    ApManager.configApState(contexts, false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Stage: Awake1");

            } else {
                System.out.println("Stage: else condition");
                //connect AP
                //apHistory.put(availableAP.get(0),3);////////////////////////////////////////////////////////////////////////////
                while (availableAP.size() > 0) {
                    System.out.println("Stage: AvailableApSize>0");
                    connectAP(contexts);

                    imgFile = ManageImage.getFile();
                    imgMetadata = ManageImage.getFileMetadata();

                    if(imgFile!=null){
                        System.out.println("File lenght" + imgFile.length);
                        for (int i = 0; i < imgFile.length; i++) {
                            String data = ManageImage.readFromFileText(imgMetadata[i]);

                            Image image = ManageImage.changeFileToImage(data,imgFile[i]);
                            Broadcaster.broadcast(image);
                        }
                    }
                    else System.out.println("No photo history");

                    /*
                    try {
                        System.out.println("Going to sleep");
                        Thread.sleep(30000);// change ? Dynamic?
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    */
                }
            }
        }
    }

    public static boolean joinAp(String SSID,Context context){
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + SSID + "\"";
        // conf.wepKeys[0] = "\"" + networkPass + "\"";    In case of network has password
        /*
        conf.wepTxKeyIndex = 0;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        */
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + SSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }

        try {
            System.out.println("Going to sleep");
            Thread.sleep(30000);// change ? Dynamic?
            System.out.println("wake");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean enableWifi(Context context) {
        ApManager.configApState(contexts, false);
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        return true;

    }

    public static boolean disconnectWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifiManager.disconnect();
        System.out.println("Disconnected");
        return true;

    }

    public static boolean listAP(Context context) {

        String SSID = null;
        String[] tokens = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        ApManager.configApState(contexts, false);
        wifiManager.setWifiEnabled(true);
        wifiManager.startScan();
        results = wifiManager.getScanResults();

        size =results.size();
        int tsize = size-1;
        System.out.println("tsize"+tsize);
        availableAP.clear();
        for (int i = 0; i <= tsize; i++) {
            Log.d("ConnectionManager", results.get(i).SSID);
            tokens = results.get(i).SSID.split(":");
            if (tokens[0].equals("ViR")) {

                availableAP.add(results.get(i).SSID);

                System.out.println("AvailableAP" + availableAP.size());
            }
        }
        //////////////////////////////////////////////////delete ap from history
        return true;
    }

    public static boolean connectAP(Context context) { // Change algo later ex. Ap which this device joined
        String SSID = null;
        if (!availableAP.isEmpty()) { //check and select the strongest signal
            SSID = availableAP.get(0);
            System.out.println("SSIDNAME="+SSID);
            joinAp(SSID,context);
            availableAP.remove(0);

        }
        return true;
    }

    public static List getAplist() {
        return availableAP;

    }

    public static boolean isBusy() {
        if (scannerStatus) {
            return false;
        }
        return true;
    }

    public synchronized void wake() {
        active = true;
        notifyAll();
    }

    public void sleep() {
        active = false;
    }
}