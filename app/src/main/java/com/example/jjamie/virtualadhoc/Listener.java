package com.example.jjamie.virtualadhoc;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class Listener {

    private static final String TAG = "Listener";

    private void listenForResponses(DatagramSocket socket) throws IOException {
        byte[] buf = new byte[1024];
        try {
            while (true) {
                
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String filename = Environment.getExternalStorageDirectory().getPath() + "/folder/testfile.jpg";
                Uri imageUri = Uri.fromFile(new File(filename));


                String s = new String(packet.getData(), 0, packet.getLength());
                Log.d(TAG, "Received response " + s);
            }
        } catch (SocketTimeoutException e) {
            Log.d(TAG, "Receive timed out");
        }
    }

}
