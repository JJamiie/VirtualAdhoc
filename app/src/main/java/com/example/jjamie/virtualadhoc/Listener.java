package com.example.jjamie.virtualadhoc;

import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Listener extends Thread{

    private static final String TAG = "Listener";
    private static final int PORT = 3333;
    DatagramSocket socket = null;
    public void run() {
        byte[] buf = new byte[65000];
        try {
            socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                System.out.println("---------Listener run---------");
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                System.out.println("Receive from: " + socket.getInetAddress() + " packet: " + packet);

                //write file
                File file = ManageImage.setUpPhotoFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(packet.getData());
                ManageImage.galleryAddPic(file.getAbsolutePath());
            }

        } catch (SocketTimeoutException e) {
            Log.d(TAG, "Receive timed out: "+ e);
        } catch (IOException e){
            Log.d(TAG, "IOException: " + e);
        }
    }

}
