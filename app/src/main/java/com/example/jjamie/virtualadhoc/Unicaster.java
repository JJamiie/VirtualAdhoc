package com.example.jjamie.virtualadhoc;


import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Unicaster {


    private int port;
    private Socket socket = null;

    public Unicaster(int port) {
        this.port = port;
    }

    public void unicast(byte[] bytes, String dstAddress) {
        try {
            long startTime = System.currentTimeMillis();
            socket = new Socket(dstAddress, port);

            Log.d("Unicast", "Connecting...");
            long estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println("1: " + estimatedTime);

            //Send file
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            Log.d("Unicast", "Sending...");

            long estimatedTime2 = System.currentTimeMillis() - startTime;
            System.out.println("2: " + estimatedTime2);

            objectOutputStream.writeObject(bytes);
            objectOutputStream.flush();
            long estimatedTime3 = System.currentTimeMillis() - startTime;
            System.out.println("3: " + estimatedTime3);

            socket.close();

        } catch (IOException ex) {
            Log.d("Unicast", "IOException: " + ex);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("Unicast", "Finished...");

    }


}
