package com.example.jjamie.virtualadhoc;

import android.os.Environment;
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

    public void run() {
        byte[] buf = new byte[1024];
        try {
            DatagramSocket socket = null;
            while (true) {
                System.out.println("listenerrrrrrrrrrrrr");
                socket = new DatagramSocket(PORT);

                socket = new DatagramSocket(PORT,InetAddress.getByName("0.0.0.0"));
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                System.out.println("listener"+socket.getInetAddress()+"");
                //write file
                String filename = Environment.getExternalStorageDirectory().getPath() + "/folder/testfile.jpg";
                File file = new File(filename);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(packet.getData());

                socket.close();

                System.out.println("Receive from: " + socket.getInetAddress() + " packet: " + packet);

            }
        } catch (SocketTimeoutException e) {
            Log.d(TAG, "Receive timed out: "+ e);
        } catch (IOException e){
            Log.d(TAG, "IOException: " + e);
        }
    }

}
