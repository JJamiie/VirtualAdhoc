package com.example.jjamie.virtualadhoc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Broadcaster {

    private static final String TAG = "Broadcast";
    public static int PORT = 3333;

    public static void broadcast(Image image) {
        broadcastToNeighbor(image);
    }


    public static void broadcastToNeighbor(Image image) {
        Unicaster unicaster = new Unicaster();
        if (getNeighborList().size() == 0) return;
        for (int i = 0; i < getNeighborList().size(); i++) {
            unicaster.unicast(image, getNeighborList().get(i));
        }
    }

    public static ArrayList<String> getNeighborList() {
        ArrayList<String> clientList = new ArrayList<String>();
        int macCount = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            System.out.println("-----------Neighbor list----------");
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null) {
                    // Basic sanity check
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        macCount++;
                        clientList.add(splitted[0]);
                        System.out.println("Count : " + macCount + " IP Address : " + splitted[0]);
                    }

                }
            }
        } catch (Exception e) {

        }
        return clientList;
    }



}
