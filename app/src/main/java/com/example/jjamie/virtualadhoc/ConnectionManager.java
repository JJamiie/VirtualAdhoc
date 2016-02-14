package com.example.jjamie.virtualadhoc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    public static int size = 0;
    public static List<String> availableAP;
    static ArrayList<String> allAP;
    public static Map<String, Integer> apHistory;
    public static boolean scannerStatus = true;
    public static Context contexts;
    private SQLiteDatabase sqLiteDatabase;
    private MyDatabase myDatabase;
    private Cursor mCursor;

    // Active for start and stop thread
    private boolean active = false;


    public ConnectionManager(Context context) {
        contexts = context;
        myDatabase = new MyDatabase(context);
        sqLiteDatabase = myDatabase.getWritableDatabase();
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

        while (true) {

            enableWifi(contexts);
            System.out.println("Stage: Sleep0");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Stage: Awake0");
            listAP(contexts);
            if (availableAP.size() <= 0) {
                listAP(contexts);
            }
            if (availableAP.size() <= 0) {
                listAP(contexts);
            }
            String r = availableAP.size() + "";
            Log.d("ConnectionManager", r);
            if (availableAP.size() <= 0) {
                System.out.println("No AP around ");
                //Noone around here use this App so turn on AP.
                //
                ApManager.configApState(contexts, true);
                System.out.println("Stage: Sleep1");
                try {
                    Thread.sleep(60000);
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

                    sendData();
                    try {
                        System.out.println("Going to sleep");
                        Thread.sleep(15000);// change ? Dynamic?
                        System.out.println("wake");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

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

    public static boolean joinAp(String SSID, Context context) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + SSID + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + SSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }

        return true;
    }

    public static void clientJoinAp(String SSID, Context context){
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + SSID + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + SSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }

    }


    public static boolean enableWifi(Context context) {
        ApManager.configApState(contexts, false);
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        return true;

    }

    public static boolean disconnectWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifiManager.disconnect();
        System.out.println("Disconnected");
        return true;

    }

    public static boolean listAP(Context context) {
        String[] tokens = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        ApManager.configApState(contexts, false);
        wifiManager.setWifiEnabled(true);
        wifiManager.startScan();
        results = wifiManager.getScanResults();

        size = results.size();
        int tsize = size - 1;
        System.out.println("tsize" + tsize);
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
            System.out.println("SSIDNAME=" + SSID);
            joinAp(SSID, context);
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
        notify();
    }

    public void sleep() {
        active = false;
    }


    public void sendData() {
        // Query data from TABLE_NAME_PICTURE
        mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MyDatabase.TABLE_NAME_PICTURE + " ORDER BY _id DESC", null);
        mCursor.moveToFirst();

        for (int position = 0; position < mCursor.getColumnCount(); ++position) {
            //Set sendername
            int columnIndex = mCursor.getColumnIndex(MyDatabase.COL_SENDER_NAME);
            String senderName = mCursor.getString(columnIndex);

            //Set message
            columnIndex = mCursor.getColumnIndex(MyDatabase.COL_MESSAGE);
            String message = mCursor.getString(columnIndex);

            //Set location
            columnIndex = mCursor.getColumnIndex(MyDatabase.COL_LOCATION);
            String location = mCursor.getString(columnIndex);

            //Set filename
            columnIndex = mCursor.getColumnIndex(MyDatabase.COL_FILE_NAME);
            String filename = mCursor.getString(columnIndex);
            File fileImage = ManageImage.isExist(filename);
            try {
                if (fileImage != null) {
                    byte[] img = new byte[(int) fileImage.length()];
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(fileImage));
                    buf.read(img, 0, img.length);
                    buf.close();
                    Image image = new Image( senderName, filename, message, location, img);
                    Broadcaster.broadcast(image.getBytes(),ListenerPacket.PORT_PACKET);
                } else {
                    Image image = new Image( senderName, filename, message, location, null);
                    Broadcaster.broadcast(image.getBytes(),ListenerPacket.PORT_PACKET);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (LengthIncorrectLengthException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<String> listNeighbourAp(Context context) {
        String tokens[] = null;
        //Todo add if this node is hotspot
        allAP = new ArrayList<>();
        //below is normal case
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        ApManager.configApState(context, false);
        wifiManager.setWifiEnabled(true);
        wifiManager.startScan();
        List<ScanResult> results = wifiManager.getScanResults();
        int size = results.size();
        int tsize = size - 1;
        for (int i = 0; i <= tsize; i++) {
            Log.d("ConnectionManager", results.get(i).SSID);
            tokens = results.get(i).SSID.split(":");
            if (tokens[0].equals("ViR")) {
                allAP.add(results.get(i).SSID);
            }
        }
        System.out.println("scan result: " + allAP.size());

        return allAP;
    }
    public String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "Unknown";
        }
    }
}
