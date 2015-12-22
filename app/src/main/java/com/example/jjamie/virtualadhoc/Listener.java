package com.example.jjamie.virtualadhoc;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread{

    private static final String TAG = "Listener";
    private WifiManager mWifi;
    private static final int PORT = 3333;
    ServerSocket serverSocket;
    Broadcaster broadcaster;
    Listener(WifiManager mWifi,Broadcaster broadcaster) {
        this.mWifi = mWifi;
        this.broadcaster = broadcaster;
    }
    public void run(){
        Socket socket = null;
        try {


            serverSocket = new ServerSocket(PORT);

            while (true) {
                Log.d("Listener", "Waiting...");
                socket = serverSocket.accept();

                //Receive file
                Log.d("Listener","Receiving...");

                File file = ManageImage.setUpPhotoFile();
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                byte[] img = (byte[])objectInputStream.readObject();
                Image image = new Image(img,img.length);
                System.out.println("SenderName: " + image.senderName + " Senquence number: " + image.sequenceNumber + " Size packet:" + image.getImageBytes().length);

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(image.getImageBytes());
                Intent mediaScanintent = ManageImage.galleryAddPic(file.getAbsolutePath());
                MainActivity.th.sendBroadcast(mediaScanintent);
                socket.close();

                Log.d("Listener", "Finished...");

                if(getIPAddressItSelf().equals("0.0.0.0")){
                    broadcaster.broadcast(image);
                }

                final String sentMsg = "Received";
                MainActivity.th.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.th, sentMsg, Toast.LENGTH_LONG).show();
                    }
                });

            }
        }catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ImageChunkIncorrectLengthException e) {
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
    public String getIPAddressItSelf(){ //if it is hotspot, it will return 0.0.0.0
        return Formatter.formatIpAddress(mWifi.getConnectionInfo().getIpAddress());
    }

//    DatagramSocket socket = null;
//    public void run() {
//        byte[] buf = new byte[65000];
//        try {
//            socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
//            socket.setBroadcast(true);
//
//            while (true) {
//                System.out.println("---------Listener run---------");
//                DatagramPacket packet = new DatagramPacket(buf, buf.length);
//                socket.receive(packet);
//                System.out.println("Receive from: " + socket.getInetAddress() + " packet: " + packet.getLength());
//
//                //write file
//                File file = ManageImage.setUpPhotoFile();
//
//                try{
//                    Image image = new Image(packet.getData(),packet.getLength());
//                    System.out.println("SenderName: " + image.senderName + " Senquence number: " + image.sequenceNumber + " Size packet:" + image.getImageBytes().length);
//                    FileOutputStream fileOutputStream = new FileOutputStream(file);
//                    fileOutputStream.write(image.getImageBytes());
//                    Intent mediaScanintent = ManageImage.galleryAddPic(file.getAbsolutePath());
//                    MainActivity.th.sendBroadcast(mediaScanintent);
//                }catch (ImageChunkIncorrectLengthException ex){
//                    System.out.println("ImageChunkIncorrectLengthException: "+ex);
//                }
//
//            }
//
//        } catch (SocketTimeoutException e) {
//            Log.d(TAG, "Receive timed out: "+ e);
//        } catch (IOException e){
//            Log.d(TAG, "IOException: " + e);
//        }
//    }

}
