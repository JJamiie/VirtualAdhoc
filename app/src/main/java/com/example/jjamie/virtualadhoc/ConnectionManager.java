package com.example.jjamie.virtualadhoc;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by Administrator on 16/12/2558.
 */
public class ConnectionManager extends Thread {
    private ApScanner apScanner;
        public void run(){
            while (true){
                apScanner = new ApScanner();
                List a = ApScanner.getAplist();
                //if (apScanner.isbusy)
                /*
                *
                *
                *
                *
                *
                *
                *
                *
                * */
            }
        }

}

