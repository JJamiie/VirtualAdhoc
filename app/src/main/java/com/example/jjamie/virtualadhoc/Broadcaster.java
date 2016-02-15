package com.example.jjamie.virtualadhoc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Broadcaster {


    public static void broadcast(byte[] bytes,int port) {
        Unicaster unicaster = new Unicaster(port);
        //Get neighborlist
        ArrayList<String> neighborList = getNeighborList();
        if (neighborList.size() == 0) return;
        for (int i = 0; i < neighborList.size(); i++) {
            unicaster.unicast(bytes, neighborList.get(i));
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
