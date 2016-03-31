package com.example.jjamie.virtualadhoc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by JJamie on 2/8/16 AD.
 */
public class ReportNeighbor {

    public static byte[] arrayListToByte(ArrayList<Neighbor> data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(data);//mArrayList is the array to convert
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buff = bos.toByteArray();
        return buff;

    }



    public static ArrayList<Neighbor> byteToArraylist(byte[] data) {

        ObjectInputStream ois = null;
        ArrayList<Neighbor> arrayList_data = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(data));
            arrayList_data = (ArrayList<Neighbor>) ois.readObject();
            ois.close();
        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
        }
        return arrayList_data;
    }

    public static void hotspotBroadcastUpdateClient(ArrayList<Neighbor> current_neighbor) {

        byte[] type = Image.intToBytes(ListenerNeighbor.REPORT_NEIGHBOR_TYPE);
        byte[] neighbor = arrayListToByte(current_neighbor);

        byte[] data = new byte[ListenerNeighbor.TYPE_LENGTH + neighbor.length];
        System.arraycopy(type, 0, data, 0, ListenerNeighbor.TYPE_LENGTH);

        System.arraycopy(neighbor, 0, data, ListenerNeighbor.TYPE_LENGTH, neighbor.length);

        Broadcaster.broadcast(data, ListenerNeighbor.PORT_NEIGHBOR);
    }

    public static void hotspotBroadcastDestroyNetwork() {
        byte[] type = Image.intToBytes(ListenerNeighbor.REPORT_DESTROY_TYPE);
        Broadcaster.broadcast(type, ListenerNeighbor.PORT_NEIGHBOR);
    }

    public static ArrayList<Neighbor> clientRecieveUpdateClient(byte[] bytes) {
        int lengthArrayClient = bytes.length - (ListenerNeighbor.TYPE_LENGTH);
        byte[] arrayClient = new byte[lengthArrayClient];
        System.arraycopy(bytes, ListenerNeighbor.TYPE_LENGTH, arrayClient, 0, lengthArrayClient);
        ArrayList<Neighbor> neighbors = byteToArraylist(arrayClient);
        System.out.println("clientRecieveUpdateClient: " + neighbors.size());
        return neighbors;
    }

    public static void clientReportJoin() {

        byte[] type = Image.intToBytes(ListenerNeighbor.CLIENT_REPORT_TYPE);

        byte[] senderNameByte = new byte[Image.SENDER_NAME_LENGTH];
        byte[] senderNameBytesShorter = TabActivity.senderName.getBytes(Charset.forName("UTF-8"));
        System.arraycopy(senderNameBytesShorter, 0, senderNameByte, 0, senderNameBytesShorter.length);

        byte[] data = new byte[ListenerNeighbor.TYPE_LENGTH + Image.SENDER_NAME_LENGTH];
        System.arraycopy(type, 0, data, 0, ListenerNeighbor.TYPE_LENGTH);
        System.arraycopy(senderNameByte, 0, data, ListenerNeighbor.TYPE_LENGTH, Image.SENDER_NAME_LENGTH);

        Broadcaster.broadcast(data, ListenerNeighbor.PORT_NEIGHBOR);

    }

    public static void clientReportLeave() {

        byte[] type = Image.intToBytes(ListenerNeighbor.CLIENT_LEAVE_TYPE);

        byte[] senderNameByte = new byte[Image.SENDER_NAME_LENGTH];
        byte[] senderNameBytesShorter = TabActivity.senderName.getBytes(Charset.forName("UTF-8"));
        System.arraycopy(senderNameBytesShorter, 0, senderNameByte, 0, senderNameBytesShorter.length);

        byte[] data = new byte[ListenerNeighbor.TYPE_LENGTH + Image.SENDER_NAME_LENGTH];
        System.arraycopy(type, 0, data, 0, ListenerNeighbor.TYPE_LENGTH);
        System.arraycopy(senderNameByte, 0, data, ListenerNeighbor.TYPE_LENGTH, Image.SENDER_NAME_LENGTH);

        Broadcaster.broadcast(data, ListenerNeighbor.PORT_NEIGHBOR);

    }

    public static String hotspotRecievedSenderNameFromClient(byte[] bytes) {
        byte[] senderNameBytes = new byte[Image.SENDER_NAME_LENGTH];
        System.arraycopy(bytes, ListenerNeighbor.TYPE_LENGTH, senderNameBytes, 0, Image.SENDER_NAME_LENGTH);
        String senderNameString = new String(senderNameBytes, 0, Image.findActuallength(senderNameBytes), Charset.forName("UTF-8"));
        System.out.println("Recieved from senderName: " + senderNameString);

        return senderNameString;
    }


    public static byte[] arrayListStringToByte(ArrayList<String> data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(data);//mArrayList is the array to convert
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buff = bos.toByteArray();
        return buff;

    }

    public static ArrayList<String> byteToArraylistString(byte[] data) {

        ObjectInputStream ois = null;
        ArrayList<String> arrayList_data = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(data));
            arrayList_data = (ArrayList<String>) ois.readObject();
            ois.close();
        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
        }
        return arrayList_data;
    }

    public static ArrayList<String> recieveListNameImage(byte[] bytes) {
        int lengthArrayClient = bytes.length - (ListenerPacket.TYPE_LENGTH);
        byte[] arrayNameImage = new byte[lengthArrayClient];
        System.arraycopy(bytes, ListenerPacket.TYPE_LENGTH, arrayNameImage, 0, lengthArrayClient);
        ArrayList<String> nameImage = byteToArraylistString(arrayNameImage);
        System.out.println("Recieve list of name image " + nameImage.size());
        return nameImage;
    }





}
