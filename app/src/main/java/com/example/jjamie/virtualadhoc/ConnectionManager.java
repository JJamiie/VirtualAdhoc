package com.example.jjamie.virtualadhoc;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    static Map<String,Integer> apHistory;
    static boolean scannerStatus =true;
    static File[] imgFile;
    static Context contexts ;
    public ConnectionManager(Context context){
        contexts = context;
    }
    public void run(){
        availableAP = new List<String>() {
            @Override
            public void add(int location, String object) {

            }

            @Override
            public boolean add(String object) {
                return false;
            }

            @Override
            public boolean addAll(int location, Collection<? extends String> collection) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends String> collection) {
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
            public String get(int location) {
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
            public Iterator<String> iterator() {
                return null;
            }

            @Override
            public int lastIndexOf(Object object) {
                return 0;
            }

            @Override
            public ListIterator<String> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<String> listIterator(int location) {
                return null;
            }

            @Override
            public String remove(int location) {
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
            public String set(int location, String object) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @NonNull
            @Override
            public List<String> subList(int start, int end) {
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
        while(true) {
          listAP(contexts);
            String r = availableAP.size()+"";
           // Log.d("ConnectionManager",r);
            if (availableAP.size() <= 0) {
                System.out.println("eieieiei");
                //Noone around here use this App so turn on AP.
                ApManager.configApState(contexts, true);
                try {
                    Thread.sleep(180000);
                    ApManager.configApState(contexts, false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                //connect AP
                //apHistory.put(availableAP.get(0),3);////////////////////////////////////////////////////////////////////////////
                while(availableAP.size()<=0){
                    connectAP(TabActivity.getAppContext());
                    imgFile=ManageImage.getFile();

                    for (int i=0;i<imgFile.length;i++){
                        try {
                            byte[] img = new byte[(int) imgFile[i].length()];
                            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(imgFile[i]));
                            buf.read(img, 0, img.length);
                            buf.close();
                            Image image = new Image(TabActivity.senderName,1,img);
                            Broadcaster.broadcast(image,TabActivity.getActivity());
                        } catch (SenderNameIncorrectLengthException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    try {
                        Thread.sleep(15000);// change ? Dynamic?
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
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
        wifiManager.setWifiEnabled(true);
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
        //////////////////////////////////////////////////delete ap from history
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
    public static List getAplist(){
        return availableAP;

    }
    public static boolean isBusy(){
        if (scannerStatus){
            return false;
        }
        return true;
    }

}

