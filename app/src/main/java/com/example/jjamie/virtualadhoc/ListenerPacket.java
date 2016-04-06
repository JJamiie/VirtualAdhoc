package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListenerPacket extends Thread {

    private static final String TAG = "ListenerPacket";
    public static final int PORT_PACKET = 5555;
    private WifiManager mWifi;
    private ServerSocket serverSocket;
    private Activity activity;
    private AlbumStorageDirFactory mAlbumStorageDirFactory;
    private SQLiteDatabase sqLiteDatabase;
    private MyDatabase myDatabase;
    private ConnectionManager connectionManager;
    public static final int TYPE_LENGTH = 4;
    public static final int IMAGE_TYPE = 1;
    public static final int SENDER_REPORT_LIST_IMAGE = 2;
    public static final int RECIEVE_RESPONSE_LIST_IMAGE = 3;
    private static Unicaster unicaster;


    public ListenerPacket(Activity activity, AlbumStorageDirFactory mAlbumStorageDirFactory, SQLiteDatabase sqLiteDatabase, MyDatabase myDatabase, ConnectionManager connectionManager) {
        this.activity = activity;
        this.mAlbumStorageDirFactory = mAlbumStorageDirFactory;
        this.mWifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        this.sqLiteDatabase = sqLiteDatabase;
        this.myDatabase = myDatabase;
        this.connectionManager = connectionManager;
        unicaster = new Unicaster(ListenerPacket.PORT_PACKET);
    }

    public void run() {
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(); // <-- create an unbound socket first
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(ListenerPacket.PORT_PACKET));
        } catch (SocketException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        while (true) {
            try {
                Log.d(TAG, "Waiting...");
                socket = serverSocket.accept();
                connectionManager.setTransferCondition(true);
                socket.setSoTimeout(10000);

                //Receive file
                Log.d(TAG, "Receive from " + socket.getInetAddress());
                String ipSender = socket.getInetAddress().toString().substring(1);
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                byte[] byte_packet = (byte[]) objectInputStream.readObject();

                socket.close();
                connectionManager.setTransferCondition(false);
                //if type is IMAGE_TYPE, packet is image.
                byte[] type_packet_byte = new byte[TYPE_LENGTH];
                System.arraycopy(byte_packet, 0, type_packet_byte, 0, TYPE_LENGTH);
                int type_packet = Image.bytesToInt(type_packet_byte);
                LogFragment.print("Recieve type packet: " + type_packet);

                Log.d(TAG, "TypePacket: " + type_packet);
                switch (type_packet) {
                    case IMAGE_TYPE:
                        Log.d(TAG, "SAVE IMAGE");
                        saveImage(byte_packet);
                        break;
                    case SENDER_REPORT_LIST_IMAGE:
                        LogFragment.print("SENDER_REPORT_LIST_IMAGE");
                        Log.d(TAG, "SENDER_REPORT_LIST_IMAGE");
                        ArrayList<String> nameImages = ReportNeighbor.recieveListNameImage(byte_packet);
                        ArrayList<String> nonExistImage = checkExistImage(nameImages);
                        LogFragment.print("Send response size: " + nonExistImage.size());
                        sendResponseListNameImage(nonExistImage, ipSender);
                        break;
                    case RECIEVE_RESPONSE_LIST_IMAGE:
                        LogFragment.print("RECIEVE_RESPONSE_LIST_IMAGE");
                        Log.d(TAG, "RECIEVE_RESPONSE_LIST_IMAGE");
                        ArrayList<String> nameImage = ReportNeighbor.recieveListNameImage(byte_packet);
                        sendImage(nameImage, ipSender);
                        break;
                    default:
                        break;
                }

            } catch (SocketException e) {
                LogFragment.print("SocketException: " + e.getMessage().toString());
                e.printStackTrace();
            } catch (OptionalDataException e) {
                LogFragment.print("OptionalDataException: " + e.getMessage().toString());
                e.printStackTrace();
            } catch (IOException e) {
                LogFragment.print("IOException: " + e.getMessage().toString());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                LogFragment.print("ClassNotFoundException: " + e.getMessage().toString());
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                        connectionManager.setTransferCondition(false);
                    } catch (IOException e) {
                        LogFragment.print("Finally: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }


    }

    public ArrayList<String> checkExistImage(ArrayList<String> nameImages) {
        ArrayList<String> list_name_image = new ArrayList<String>();
        for (int i = 0; i < nameImages.size(); ++i) {
            LogFragment.print("NameImage: " + nameImages.get(i));
            Log.d(TAG, "Name Image" + nameImages.get(i));
            Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MyDatabase.TABLE_NAME_PICTURE +
                    " WHERE " + MyDatabase.COL_FILE_NAME + " = '" + nameImages.get(i) + "'", null);
            if (mCursor.getCount() == 0) {
                Log.d(TAG, "Exist");
                list_name_image.add(nameImages.get(i));
            }
            mCursor.close();
        }
        return list_name_image;
    }

    public void saveImage(byte[] img) {
        try {
            Image image = new Image(img);
            System.out.println("SenderName: " + image.senderName + " Filename: " + image.filename + " Message: " + image.message + " Location: " + image.location);

            Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MyDatabase.TABLE_NAME_PICTURE +
                    " WHERE " + MyDatabase.COL_FILE_NAME + " = '" + image.filename + "'", null);
            if (mCursor.getCount() > 0) {
                return;
            }
            mCursor.close();

            mCursor.close();
            if (image.imageBytes != null) {
                Log.d(TAG, "Before SetupPhoto");
                File file = ManageImage.setUpPhotoFile(mAlbumStorageDirFactory);
                System.out.println("filename :====" + file.getName());
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(image.getImageBytes());
                ManageImage.galleryAddPic(file.getAbsolutePath(), activity);
                myDatabase.addToTablePicture(sqLiteDatabase, image.senderName, image.filename, image.message, image.location, file.getName());

            } else {
                myDatabase.addToTablePicture(sqLiteDatabase, image.senderName, image.filename, image.message, image.location, null);

            }
            /**/
            Log.d(TAG, "AddToTablePicture");

            Log.d(TAG, "Finished...");

            LogFragment.print("Time recieve: " + getCurrentTimeStamp() + " | From: " + image.senderName + " | Message:  " + image.message);

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

    public static Long getCurrentTimeStamp() {
        Calendar rightNow = Calendar.getInstance();

        long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                rightNow.get(Calendar.DST_OFFSET);

        long time = (rightNow.getTimeInMillis() + offset) %
                (24 * 60 * 60 * 1000);
        return time;
    }

    public void sendResponseListNameImage(ArrayList<String> nonExistImage, String ipSender) {
        Log.d(TAG, "SendResponseList");
        byte[] type = Image.intToBytes(ListenerPacket.RECIEVE_RESPONSE_LIST_IMAGE);
        byte[] list_image = ReportNeighbor.arrayListStringToByte(nonExistImage);
        byte[] data = new byte[ListenerPacket.TYPE_LENGTH + list_image.length];
        Log.d(TAG, "List Image length" + String.valueOf(list_image.length));
        System.arraycopy(type, 0, data, 0, ListenerPacket.TYPE_LENGTH);
        System.arraycopy(list_image, 0, data, ListenerPacket.TYPE_LENGTH, list_image.length);
        LogFragment.print("sendResponseListNameImage");
        unicaster.unicast(data, ipSender);
    }

    public void sendImage(ArrayList<String> nameImages, String ipSender) {
        LogFragment.print("Sent " + nameImages.size() + " pictures");
        for (int i = 0; i < nameImages.size(); i++) {
            Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MyDatabase.TABLE_NAME_PICTURE +
                    " WHERE " + MyDatabase.COL_FILE_NAME + " = '" + nameImages.get(i) + "'", null);
            if (mCursor.getCount() == 0) {
                continue;
            }
            LogFragment.print("Counttttttttttttttt: "+ mCursor.getCount() );
            mCursor.moveToPosition(0);

            //Set sendername
            int columnIndex = mCursor.getColumnIndex(MyDatabase.COL_SENDER_NAME);
            String senderName = mCursor.getString(columnIndex);

            //Set message
            columnIndex = mCursor.getColumnIndex(MyDatabase.COL_MESSAGE);
            String message = mCursor.getString(columnIndex);

            //Set location
            columnIndex = mCursor.getColumnIndex(MyDatabase.COL_LOCATION);
            String location = mCursor.getString(columnIndex);

            //Set filename
            columnIndex = mCursor.getColumnIndex(MyDatabase.COL_FILE_NAME);
            String filename = mCursor.getString(columnIndex);

            //Set imagename
            columnIndex = mCursor.getColumnIndex(MyDatabase.COL_IMAGE_NAME);
            String imagename = mCursor.getString(columnIndex);
            File fileImage = ManageImage.isExist(imagename);

            LogFragment.print("SEND IMAGE: " + filename);
            try {
                if (fileImage != null) {
                    byte[] img = new byte[(int) fileImage.length()];
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(fileImage));
                    buf.read(img, 0, img.length);
                    buf.close();
                    Image image = new Image(senderName, filename, message, location, img);
                    unicaster.unicast(image.getBytes(), ipSender);
                } else {
                    Image image = new Image(senderName, filename, message, location, null);
                    unicaster.unicast(image.getBytes(), ipSender);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (LengthIncorrectLengthException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCursor.close();

        }
    }


}
