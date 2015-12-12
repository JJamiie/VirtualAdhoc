package com.example.jjamie.virtualadhoc;


import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Broadcaster {

    private static final String TAG = "Broadcast";
    private WifiManager mWifi;
    private static int PORT = 3333;

    Broadcaster(WifiManager mWifi){
        this.mWifi = mWifi;
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


    public void broadcast(byte[] data) throws IOException{
        System.out.println("------------Broadcast picture------------"+data.toString());
        DatagramSocket socket = new DatagramSocket(PORT);
        socket.setBroadcast(true);
        DatagramPacket packet = new DatagramPacket(data, data.length,getBroadcastAddress(), PORT);
        socket.send(packet);
    }



}
