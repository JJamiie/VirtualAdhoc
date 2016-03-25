package com.example.jjamie.virtualadhoc;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Shimanosk on 3/6/2016.
 */
public class MessageListener extends Thread {
    private ServerSocket serverSocket;
    private static final String TAG = "MessageListener";
    public static final int PORT_MESSAGE = 3333;
    public static final int TYPE_LENGTH = 4;
    public static final int AP_CONFIRM_TYPE = 6;
    public static final int CHANGE_AP_TYPE = 5;
    public void run() {
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(); // <-- create an unbound socket first
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(MessageListener.PORT_MESSAGE));
            while (true) {
                Log.d(TAG, "Waiting...");
                socket = serverSocket.accept();

                //Receive file
                Log.d(TAG, "Receive from " + socket.getInetAddress());


                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                byte[] byte_packet = (byte[]) objectInputStream.readObject();
                socket.close();
                byte[] type_packet_byte = new byte[TYPE_LENGTH];
                System.arraycopy(byte_packet, 0, type_packet_byte, 0, TYPE_LENGTH);
                int type_packet = Image.bytesToInt(type_packet_byte);
                switch (type_packet){
                    case CHANGE_AP_TYPE:
                        //Change state to AP
                        TabActivity.changeConnectionManagerStateToAP();
                        TabActivity.sendConfirmationMessage();
                        break;
                    case AP_CONFIRM_TYPE:
                        //halt AP
                        TabActivity.changeConfirmFlag(true);
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
}
