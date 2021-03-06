package com.example.jjamie.virtualadhoc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 16/12/2558.
 */
public class ConnectionManager extends Thread {
    private static List<ScanResult> results;
    public static int size = 0;
    public static List<String> availableAP;
    static ArrayList<String> allAP;
    public static boolean scannerStatus = true;
    public static Context contexts;
    private SQLiteDatabase sqLiteDatabase;
    private Cursor mCursor;
    private int score = 0;
    private static int accTime = 0;
    private static final int timeout = 500;
    private static int mode = 1; //1=i 2=h 3=c
    private static int compareMode = 0;
    private static int count = 0;
    private static int compareCount = 0;
    private static int timeState = 0;
    private static int compareTime = 0;
    private static int tempState = 1;
    private static String currSSID="";
    // Active for start and stop thread
    private boolean active = false;
    private Random r;


    public ConnectionManager(Context context, SQLiteDatabase sqLiteDatabase) {
        contexts = context;
        r = new Random();
        this.sqLiteDatabase = sqLiteDatabase;
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

    }

    public void run() {

        enableWifi(contexts);
        results.clear();
        availableAP.clear();
        accTime=0;
        while (true) {

            while (mode == 1) {

                accTime = 0;
                LogFragment.print("--------------------------------Mode 1--------------------------------");
                System.out.println("mode 1");
                timeState = 0;
                while (!isWifiOn(contexts)) {
                    System.out.println("Wait for wifi");
                    enableWifi(contexts);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while(timeState<=25) {
                    tempState = 1;
                    for (int i = 0; i < 5; i++) {
                        listAP(contexts);
                        if (availableAP.size() > 0) {
                            break;
                        }
                        try {
                            Thread.sleep(2000);
                            timeState = timeState + 2;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (availableAP.size() > 0) {
                        System.out.println("Stage: AvailableApSize>0");
                        connectAP(contexts);
                        try {
                            Thread.sleep(5000);
                            timeState = timeState + 5;
                            WifiManager wifiManager = (WifiManager) contexts.getSystemService(contexts.WIFI_SERVICE);
                            WifiInfo info = wifiManager.getConnectionInfo ();
                            String checkSSID=info.getSSID();
                            currSSID="\""+currSSID+"\"";
                            for (int i=0;i<5;i++){
                                LogFragment.print("currSSID="+currSSID+"checkSSID="+checkSSID);
                                if(currSSID.equals(checkSSID)){
                                    break;
                                }
                                else {
                                    Thread.sleep(1000);
                                    timeState=timeState+1;
                                }
                            }
                            LogFragment.print(checkSSID);
                            sendScore();
                            sendData();
                            count++;
                            LogFragment.print("Mode 1 Timestate: " + timeState);
                            System.out.println("Mode1 Timestate" + timeState);
                            Thread.sleep(4000);
                            timeState = timeState + 4;
                            System.out.println("wake");
                            currSSID="";

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
                int timeRandom = r.nextInt(60 - 45 + 1) + 45;
                while (timeState > 25 && timeState <= timeRandom) {
                    ApManager.configApState(contexts, true);
                    tempState = 2;
                    System.out.println("Stage: Sleep1");
                    for (int i = 0; i < 3; i++) {
                        try {
                            Thread.sleep(11000);
                            //add send data
                            timeState = timeState + 11;
                            LogFragment.print("Mode 1 Timestate: " + timeState);
                            System.out.println("Mode1 Timestate" + timeState);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    ApManager.configApState(contexts, false);
                    try {
                        Thread.sleep(2000);
                        //add send data

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeState = timeState + 2;
                }
            }
            while (mode == 2) {
                while (accTime <= timeout) {
                    timeState = 0;
                    LogFragment.print("--------------------------------Mode 2--------------------------------");
                    System.out.println("mode 2");
                    while (timeState < 100) {
                        ApManager.configApState(contexts, true);
                        tempState = 2;
                        System.out.println("Stage: Sleep1");
                        for (int i = 0; i < 16; i++) {
                            try {
                                //System.out.println("abc");

                                Thread.sleep(6000);
                                timeState = timeState + 6;
                                accTime = accTime + 6;
                                if (timeState >= 100) break;

                                LogFragment.print("Mode 2 Timestate: " + timeState);

                                System.out.println("mode 2" + timeState);
                                //System.out.println("xyz");
                                //add send data

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        ApManager.configApState(contexts, false);
                        try {
                            Thread.sleep(4000);
                            //add send data

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        accTime = accTime + 4;
                        timeState = timeState + 4;
                        LogFragment.print("Mode 2 Timestate: " + timeState);

                        System.out.println("mode 2" + timeState);
                    }
                    while (!isWifiOn(contexts)) {
                        System.out.println("Wait for wifi");
                        enableWifi(contexts);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    int timeRandom = r.nextInt(140 - 130 + 1) + 130;

                    while (timeState >= 100 && timeState <= timeRandom) {
                        for (int i = 0; i < 5; i++) {
                            listAP(contexts);
                            tempState = 1;
                            if (availableAP.size() > 0) {
                                break;
                            }
                            try {
                                Thread.sleep(2000);
                                timeState = timeState + 2;
                                System.out.println("mode 2" + timeState);
                                accTime = accTime + 2;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        while (availableAP.size() > 0) {
                            System.out.println("Stage: AvailableApSize>0");
                            connectAP(contexts);
                            try {
                                Thread.sleep(5000);
                                timeState = timeState + 5;
                                WifiManager wifiManager = (WifiManager) contexts.getSystemService(contexts.WIFI_SERVICE);
                                WifiInfo info = wifiManager.getConnectionInfo ();
                                String checkSSID=info.getSSID();
                                currSSID="\""+currSSID+"\"";
                                for (int i=0;i<5;i++){
                                    LogFragment.print("currSSID="+currSSID+"checkSSID="+checkSSID);
                                    if(currSSID.equals(checkSSID)){
                                        break;
                                    }
                                    else {
                                        Thread.sleep(1000);
                                        timeState=timeState+1;
                                    }
                                }
                                LogFragment.print(checkSSID);
                                System.out.println("mode 2" + timeState);
                                LogFragment.print("Mode 2 Timestate: " + timeState);

                                System.out.println("Mode2:Timestate" + timeState);
                                accTime = accTime + 10;
                                sendScore();
                                sendData();
                                count++;
                                Thread.sleep(4000);
                                timeState = timeState + 4;
                                if (timeState > timeRandom) break;

                                LogFragment.print("Mode 2 Timestate: " + timeState);

                                System.out.println("mode 2" + timeState);
                                //Thread.sleep(2000);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                }
                mode = 1;
            }
            while (mode == 3) {
                while (accTime <= timeout) {
                    timeState = 0;
                    LogFragment.print("-------------------------------Mode 3--------------------------------");
                    System.out.println("mode 3");
                    while (!isWifiOn(contexts)) {
                        System.out.println("Wait for wifi");
                        enableWifi(contexts);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (timeState < 100) {
                        for (int i = 0; i < 5; i++) {
                            tempState = 1;
                            listAP(contexts);
                            if (availableAP.size() > 0) {
                                break;
                            }
                            try {
                                Thread.sleep(2000);
                                timeState = timeState + 2;
                                accTime = accTime + 2;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        while (availableAP.size() > 0) {

                            System.out.println("Stage: AvailableApSize>0");
                            connectAP(contexts);
                            try {
                                Thread.sleep(5000);
                                accTime = accTime + 5;
                                timeState = timeState + 10;
                                WifiManager wifiManager = (WifiManager) contexts.getSystemService(contexts.WIFI_SERVICE);
                                WifiInfo info = wifiManager.getConnectionInfo ();
                                String checkSSID=info.getSSID();
                                currSSID="\""+currSSID+"\"";
                                for (int i=0;i<5;i++){
                                    LogFragment.print("currSSID="+currSSID+"checkSSID="+checkSSID);
                                    if(currSSID.equals(checkSSID)){
                                        break;
                                    }
                                    else {
                                        Thread.sleep(1000);
                                        timeState=timeState+1;
                                    }
                                }
                                LogFragment.print(checkSSID);
                                sendScore();
                                sendData();
                                count++;
                                Thread.sleep(4000);
                                accTime = accTime + 4;
                                if (timeState >= 100) break;

                                System.out.println("Going to sleep");
                                //Thread.sleep(2000);
                                LogFragment.print("Mode 3 Timestate: " + timeState);

                                System.out.println("Mode3:timestate" + timeState);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    int timeRandom = r.nextInt(140 - 130 + 1) + 130;
                    while (timeState >= 100 && timeState < timeRandom) {
                        ApManager.configApState(contexts, true);
                        System.out.println("Stage: Sleep1");
                        tempState = 2;
                        for (int i = 0; i < 6; i++) {
                            try {
                                LogFragment.print("Mode 3 Timestate: " + timeState);

                                System.out.println("Mode3 TimeState=" + timeState);
                                Thread.sleep(6000);
                                timeState = timeState + 6;
                                accTime = accTime + 6;
                                //add send data
                                if (timeState > timeRandom) break;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        ApManager.configApState(contexts, false);
                        try {
                            Thread.sleep(3000);
                            //add send data

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        accTime = accTime + 3;
                        timeState = timeState + 3;
                    }

                }
                mode = 1;
            }
        }
    }

    public static boolean joinAp(String SSID, Context context) {
        WifiConfiguration conf = new WifiConfiguration();

        conf.SSID = "\"" + SSID + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        WifiInfo info = wifiManager.getConnectionInfo ();
        String checkSSID=info.getSSID();
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + SSID + "\"")&&!i.SSID.equals("\"" + checkSSID  + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }

        }

        return true;
    }

    public static void clientJoinAp(String SSID, Context context) {
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

    public boolean isWifiOn(Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
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
        if (results == null) {
            size = 0;
        } else {
            size = results.size();

        }
        int tsize = size - 1;
        System.out.println("tsize" + tsize);
        availableAP.clear();
        for (int i = 0; i <= tsize; i++) {
            Log.d("ConnectionManager", results.get(i).SSID);
            tokens = results.get(i).SSID.split(":");
            if (tokens[0].equals("ViR")) {
                LogFragment.print("Found " + tokens[1]);

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
            LogFragment.print("Connect: " + SSID);

            System.out.println("SSIDNAME=" + SSID);
            joinAp(SSID, context);
            currSSID=SSID;
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

    public void wake() {
        active = true;
        notifyAll();
    }

    public void addScore(String sender, int mscore) {

    }

    public void sleep() {
        active = false;
    }

    public void sendScore() {
        String ans = count + ":" + mode + ":" + timeState + ":end";
        LogFragment.print("Send score");
        byte[] b = ans.getBytes();
        Broadcaster.broadcast(b, ScoreListener.PORT_SCORE);
    }

    public void sendData() {
        // Query data from TABLE_NAME_PICTURE
        Long time = getCurrentTimeStamp();
        mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MyDatabase.TABLE_NAME_PICTURE + " ORDER BY _id DESC", null);
        mCursor.moveToFirst();
        if (mCursor.getCount() > 0) {
            System.out.println("TestSendData: " + System.currentTimeMillis());


//            LogFragment.print("Time Send: " + System.currentTimeMillis());
            LogFragment.print("Time Send: " + time);

        }
        for (int position = 0; position < mCursor.getCount(); position++) {
            mCursor.moveToPosition(position);
            System.out.println("getcount: " + position);

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
                    Image image = new Image(senderName, filename, message, location, img);
                    Broadcaster.broadcast(image.getBytes(), ListenerPacket.PORT_PACKET);
                } else {
                    Image image = new Image(senderName, filename, message, location, null);
                    Broadcaster.broadcast(image.getBytes(), ListenerPacket.PORT_PACKET);
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
        int size;
        //Todo add if this node is hotspot
        allAP = new ArrayList<>();
        //below is normal case
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        ApManager.configApState(context, false);
        wifiManager.setWifiEnabled(true);
        wifiManager.startScan();
        List<ScanResult> results = wifiManager.getScanResults();
        if (results == null) {
            size = 0;
        } else {
            size = results.size();
        }

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

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) contexts.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }

    public void setCompareMode(int modes) {
        compareMode = modes;
    }

    public void setCompareTime(int times) {
        compareTime = times;
    }

    public void setCompareCount(int counts) {
        compareCount = counts;
    }

    public void checkAccTime(int mmode) {//call by listener when it has incoming message

        if (mode == 2 && mmode == 3) {
            accTime = 0;
        }
        if (mode == 3 && mmode == 2) {
            accTime = 0;
        }
    }

    public static void updateMode() {
        if (compareMode == 1 && mode == 1) {
            if (tempState == 1) {
                mode = 3;
                LogFragment.print("------------------------Update to mode 3 ------------------------");

                System.out.println("Update mode 3");
            } else {
                mode = 2;
                LogFragment.print("------------------------Update to mode 2 ------------------------");
                System.out.println("Update mode 2");
            }
        } else if (compareMode == 2 && mode == 1) {
            mode = 3;
            LogFragment.print("------------------------Update to mode 3 ------------------------");
            System.out.println("Update mode 3");
        } else if (compareMode == 3 && mode == 1) {
            mode = 2;
            LogFragment.print("------------------------Update to mode 2 ------------------------");
            System.out.println("Update mode 2");
        }

    }

    public static int getTempState() {
        return tempState;
    }

    public static void updateCount() {
        count = count + 1;
    }

    public static void updateTime() {
        if (compareCount == count && compareMode != mode && tempState == 1) {
            timeState = compareTime;
            LogFragment.print("UpdateTime: " + compareTime);
            Log.d("ConnectionManager", "updateTime: " + compareTime);
        }
        if (compareCount > count && compareMode != mode) {
            timeState = compareTime;
            count = compareCount;
            LogFragment.print("UpdateTime: " + compareTime);
            Log.d("ConnectionManager", "updateTime: " + compareTime);
        }
    }


    public static void sendMessageToInternet() {
        OkHttpClient client = new OkHttpClient();
        //Read image file
        String imageDataString = "";
        File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/Pigeon/Nightscape.jpg");
        try {
            // Reading a Image file from file system
            FileInputStream imageInFile = new FileInputStream(file);
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);
            // Converting Image byte array into Base64 String
            imageDataString = encodeImage(imageData);
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }
        RequestBody formBody = new FormBody.Builder()
                .add("Time", "xx:xx")
                .add("Name", "name")
                .add("Message", "message")
                .add("image", imageDataString)
                .add("GPS", "GPS")
                .build();
        Request request = new Request.Builder()
                .url("http://n2p.in.th/mith/toony.php")
                .post(formBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }


    public static Long getCurrentTimeStamp(){
        Calendar rightNow = Calendar.getInstance();

        long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                rightNow.get(Calendar.DST_OFFSET);

        long time = (rightNow.getTimeInMillis() + offset) %
                (24 * 60 * 60 * 1000);
        return time;
    }

}
