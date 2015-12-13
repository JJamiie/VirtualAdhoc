package com.example.jjamie.virtualadhoc;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

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


    public void broadcast(){
        try {
            System.out.println("BroadcastAddress: " + getBroadcastAddress());
            if(data == null){
                Log.d("Broadcast","No picture to broadcast");
                return;
            }
            System.out.println("Data: " + data.length);

            DatagramChannel channel = DatagramChannel.open();
            DatagramSocket socket = channel.socket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(PORT));
            socket.setBroadcast(true);

            DatagramPacket packet = new DatagramPacket(data, data.length, getBroadcastAddress(), PORT);
            socket.send(packet);
            socket.close();
        }catch(IOException ex){
            Log.d("Broadcast",ex.toString());
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    public void setDataToSent(InputStream iStream) {
        try{
            data = getBytes(iStream);
        }catch (IOException ex){

        }
    }

}
