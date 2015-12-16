package com.example.jjamie.virtualadhoc;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Broadcaster{

    private static final String TAG = "Broadcast";
    private WifiManager mWifi;
    private static int PORT = 3333;
    private byte[] data;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    Broadcaster(WifiManager mWifi) {
        this.mWifi = mWifi;
        data = null;
    }

    private InetAddress getBroadcastAddress() throws IOException {
        DhcpInfo dhcp = mWifi.getDhcpInfo();
        if (dhcp == null) {
            Log.d(TAG, "Could not get dhcp info");
            return null;
        }
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }


    public void broadcast(byte[] data){
        try {
            System.out.println("BroadcastAddress: " + getBroadcastAddress());
            if(data == null){
                Log.d("Broadcast","No picture to broadcast");
                return;
            }
            System.out.println("Data: " + data.length);

            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            DatagramPacket packet = new DatagramPacket(data, data.length, getBroadcastAddress(), PORT);
            socket.send(packet);

        }catch(IOException ex){
            Log.d("Broadcast",ex.toString());
        }
    }



}
