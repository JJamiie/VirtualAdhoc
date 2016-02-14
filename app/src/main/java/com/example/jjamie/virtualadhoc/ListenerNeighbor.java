package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ListenerNeighbor extends Thread {
    public static final int PORT_NEIGHBOR = 4444;
    public static final String TAG = "ListenerNeighbor";
    private ServerSocket serverSocket;
    private Activity activity;
    private WifiManager mWifi;
    private PeopleNearByAdapter peopleNearByAdapter;

    public static final int TYPE_LENGTH = 4;
    public static final int REPORT_NEIGHBOR_TYPE = 2;
    public static final int CLIENT_REPORT_TYPE = 3;


    public ListenerNeighbor(Activity activity, PeopleNearByAdapter peopleNearByAdapter) {
        this.activity = activity;
        this.peopleNearByAdapter = peopleNearByAdapter;
        this.mWifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
    }

    public void run() {
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(); // <-- create an unbound socket first
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(PORT_NEIGHBOR));
            while (true) {
                Log.d(TAG, "Waiting...");
                socket = serverSocket.accept();

                //Receive file
                Log.d(TAG, "Receive from " + socket.getInetAddress());

                //Recieve from IP
                InetAddress receivedIP = socket.getInetAddress();

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                byte[] byte_packet = (byte[]) objectInputStream.readObject();
                socket.close();

                // if type is REPORT_NEIGHBOR_TYPE, packet is sent from hotspot.
                // if type is CLIENT_REPORT_TYPE,packet is sent from client.
                byte[] type_packet_byte = new byte[TYPE_LENGTH];
                System.arraycopy(byte_packet, 0, type_packet_byte, 0, TYPE_LENGTH);
                int type_packet = Image.bytesToInt(type_packet_byte);

                switch (type_packet) {
                    case REPORT_NEIGHBOR_TYPE:
                        ArrayList<Neighbor> neighbors = ReportNeighbor.clientRecieveUpdateClient(byte_packet);
                        //Remove myself
                        for (int i = 0; i < neighbors.size(); i++) {
                            if (neighbors.get(i).senderName.equals(TabActivity.senderName)) {
                                neighbors.remove(i);
                                break;
                            }
                        }
                        peopleNearByAdapter.setNeighbors(neighbors);
                        break;
                    case CLIENT_REPORT_TYPE:
                        String senderName = ReportNeighbor.hotspotRecievedSenderNameFromClient(byte_packet);
                        ArrayList<Neighbor> _neighbors = new ArrayList<Neighbor>();
                        _neighbors.addAll(peopleNearByAdapter.addNeighbors(new Neighbor(senderName, receivedIP)));
                        //add hotspot sendername and IP
                        InetAddress hotspotIP = InetAddress.getByName(getIPAddressItSelf());
                        _neighbors.add(new Neighbor(TabActivity.senderName, hotspotIP));
                        ReportNeighbor.hotspotBroadcastUpdateClient(_neighbors);

                        break;
                    default:
                        break;
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getIPAddressItSelf() { //if it is hotspot, it will return 0.0.0.0
        return Formatter.formatIpAddress(mWifi.getConnectionInfo().getIpAddress());
    }


}
