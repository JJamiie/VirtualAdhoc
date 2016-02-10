package com.example.jjamie.virtualadhoc;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by JJamie on 2/8/16 AD.
 */
public class ReportNeighbor {

    public static byte[] arrayListToByte(ArrayList<String> data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        for (String element : data) {
            try {
                out.writeUTF(element);
            } catch (IOException e) {
                Log.i("Broadcaster", "Change arraylist to byte error");
            }
        }
        return baos.toByteArray();

    }

    public static ArrayList<String> byteToArraylist(byte[] data) {
        ArrayList<String> arrayList_data = new ArrayList<>();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream in = new DataInputStream(bais);
        System.out.println("byteToArrayList--------");
        try {
            while (in.available() > 0) {
                String element = in.readUTF();
                arrayList_data.add(element);
                System.out.println(element);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList_data;
    }

    public static void hotspotBroadcastUpdateClient(ArrayList<String> current_neighbor) {
        byte[] data = new byte[Listener.TYPE_LENGTH + current_neighbor.size()];

        byte[] type = Image.intToBytes(Listener.REPORT_NEIGHBOR_TYPE);
        System.arraycopy(type, 0, data, 0, Listener.TYPE_LENGTH);

        byte[] neighbor = arrayListToByte(current_neighbor);
        System.arraycopy(neighbor, 0, data, Listener.TYPE_LENGTH, neighbor.length);

        Broadcaster.broadcast(data);
    }

    public static void clientRecieveUpdateClient(byte[] bytes) {
        int lengthArrayClient = bytes.length - (Listener.TYPE_LENGTH);
        byte[] arrayClient = new byte[lengthArrayClient];
        System.arraycopy(bytes, Listener.TYPE_LENGTH, arrayClient, 0, lengthArrayClient);
        byteToArraylist(arrayClient);

    }

    public static void clientReportInformation() {

        byte[] type = Image.intToBytes(Listener.CLIENT_REPORT_TYPE);

        byte[] senderNameByte = new byte[Image.SENDER_NAME_LENGTH];
        byte[] senderNameBytesShorter = TabActivity.senderName.getBytes(Charset.forName("UTF-8"));
        System.arraycopy(senderNameBytesShorter, 0, senderNameByte, 0, senderNameBytesShorter.length);

        byte[] data = new byte[Listener.TYPE_LENGTH + Image.SENDER_NAME_LENGTH];
        System.arraycopy(type, 0, data, 0, Listener.TYPE_LENGTH);
        System.arraycopy(senderNameByte, 0, data, Listener.TYPE_LENGTH, Image.SENDER_NAME_LENGTH);

        Broadcaster.broadcast(data);

    }

    public static String hotspotRecievedSenderNameFromClient(byte[] bytes) {
        byte[] senderNameBytes = new byte[Image.SENDER_NAME_LENGTH];
        System.arraycopy(bytes, Listener.TYPE_LENGTH, senderNameBytes, 0, Image.SENDER_NAME_LENGTH);
        String senderNameString = new String(senderNameBytes, 0, Image.findActuallength(senderNameBytes), Charset.forName("UTF-8"));
        System.out.println("Recieved from senderName: " + senderNameString);
        return senderNameString;
    }


}
