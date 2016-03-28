package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenerPacket extends Thread {

    private static final String TAG = "ListenerPacket";
    public static final int PORT_PACKET = 5555;
    private WifiManager mWifi;
    private ServerSocket serverSocket;
    private Activity activity;
    private AlbumStorageDirFactory mAlbumStorageDirFactory;
    private Cursor mCursor;
    private SQLiteDatabase sqLiteDatabase;
    private MyDatabase myDatabase;
    public static final int TYPE_LENGTH = 4;
    public static final int IMAGE_TYPE = 1;


    public ListenerPacket(Activity activity, AlbumStorageDirFactory mAlbumStorageDirFactory, SQLiteDatabase sqLiteDatabase, MyDatabase myDatabase) {
        this.activity = activity;
        this.mAlbumStorageDirFactory = mAlbumStorageDirFactory;
        this.mWifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        this.sqLiteDatabase = sqLiteDatabase;
        this.myDatabase = myDatabase;
    }

    public void run() {
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(); // <-- create an unbound socket first
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(ListenerPacket.PORT_PACKET));
            while (true) {
                Log.d(TAG, "Waiting...");
                socket = serverSocket.accept();
              //  long startTime = System.currentTimeMillis();

                //Receive file
                Log.d(TAG, "Receive from " + socket.getInetAddress());

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                byte[] byte_packet = (byte[]) objectInputStream.readObject();
               // long estimatedTime1 = System.currentTimeMillis() - startTime;

                socket.close();
                //long estimatedTime2 = System.currentTimeMillis() - startTime;
           //     System.out.println("Endtime: " + System.nanoTime());

                //if type is IMAGE_TYPE, packet is image.
                byte[] type_packet_byte = new byte[TYPE_LENGTH];
                System.arraycopy(byte_packet, 0, type_packet_byte, 0, TYPE_LENGTH);
                int type_packet = Image.bytesToInt(type_packet_byte);

                switch (type_packet) {
                    case IMAGE_TYPE:
//                        System.out.println("Start time: " + startTime);
//                        System.out.println("Estimated Time: " + estimatedTime1);
//                        System.out.println("Estimated Time: " + estimatedTime2);
                        saveImage(byte_packet);
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

    public void saveImage(byte[] img) {
        try {
            Image image = new Image(img);
            System.out.println("SenderName: " + image.senderName + " Filename: " + image.filename + " Message: " + image.message + " Location: " + image.location);

            if (image.senderName.equals(TabActivity.senderName)) {
                return;
            }

            // checking image is exist in database?
            mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MyDatabase.TABLE_NAME_PICTURE +
                    " WHERE " + MyDatabase.COL_SENDER_NAME + " = '" + image.senderName + "' AND " +
                    MyDatabase.COL_FILE_NAME + " = '" + image.filename + "' AND " +
                    MyDatabase.COL_MESSAGE + " = '" + image.message + "' AND " +
                    MyDatabase.COL_LOCATION + " = '" + image.location + "'", null);

            if (mCursor.getCount() == 0) {
                if (image.imageBytes != null) {
                    String filename = image.filename.substring(0, image.filename.length() - 4);
                    File file = ManageImage.setUpPhotoFile(mAlbumStorageDirFactory, filename);
                    System.out.println("filename :====" + file.getName());
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(image.getImageBytes());
                    ManageImage.galleryAddPic(file.getAbsolutePath(), activity);
                }
                myDatabase.addToTablePicture(sqLiteDatabase, image.senderName, image.filename, image.message, image.location);

                Log.d(TAG, "Finished...");
                LogFragment.print("Time recieve: " + System.currentTimeMillis() + " | From: " + image.senderName + " | Message:  " + image.message);

                System.out.println("TestRecieveData: " + System.currentTimeMillis());


                final String sentMsg = "Received";
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NewFeedFragment.updateTable();
                        Toast.makeText(activity, sentMsg, Toast.LENGTH_LONG).show();
                    }
                });

                //Sent to other node
                if (getIPAddressItSelf().equals("0.0.0.0")) {
                    Broadcaster.broadcast(image.getBytes(), ListenerPacket.PORT_PACKET);
                }

            }
          ;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ImageChunkIncorrectLengthException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getIPAddressItSelf() { //if it is hotspot, it will return 0.0.0.0
        return Formatter.formatIpAddress(mWifi.getConnectionInfo().getIpAddress());
    }


}
