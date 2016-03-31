package com.example.jjamie.virtualadhoc;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Shimanosk on 3/7/2016.
 */
public class ScoreListener extends Thread {
    private ServerSocket serverSocket;
    private static final String TAG = "scoreListener";
    public static final int PORT_SCORE = 15243;
    public static final int TYPE_LENGTH = 4;
    public ConnectionManager connectionManager;
    public ScoreListener(ConnectionManager connectionManager){
        this.connectionManager = connectionManager;
    }
    public void run() {
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(); // <-- create an unbound socket first
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(ScoreListener.PORT_SCORE));
            while (true) {
                Log.d(TAG, "Waiting...");
                socket = serverSocket.accept();

                //Receive file
                Log.d(TAG, "Receives from " + socket.getInetAddress());
                LogFragment.print("Receive score from " + socket.getInetAddress());

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                byte[] byte_packet = (byte[]) objectInputStream.readObject();
                socket.close();
                byte[] ans_byte = new byte[byte_packet.length];
                System.arraycopy(byte_packet, 0, ans_byte, 0, byte_packet.length);

                //add decryptbytetostring  set set set and compare
                //add hotspot send data
                String ans = new String(ans_byte);
                String[] parts = ans.split(":");
                if (connectionManager.getTempState()==2){
                    connectionManager.sendData();
                    connectionManager.sendScore();
                    connectionManager.updateCount();
                }
                if (parts.length==4){
                    connectionManager.setCompareMode(Integer.parseInt(parts[1]));
                    connectionManager.setCompareCount(Integer.parseInt(parts[0]));
                    connectionManager.setCompareTime(Integer.parseInt(parts[2]));
                    connectionManager.checkAccTime(Integer.parseInt(parts[1]));
                    connectionManager.updateTime();
                    connectionManager.updateMode();
                }

            }
        } catch (IOException e) {
            LogFragment.print("IOException: "+e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LogFragment.print(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
