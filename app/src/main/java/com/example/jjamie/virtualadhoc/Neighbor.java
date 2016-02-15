package com.example.jjamie.virtualadhoc;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Created by JJamie on 2/8/16 AD.
 */
public class Neighbor implements Serializable{
    String senderName;
    InetAddress IP;

    public Neighbor(String senderName,InetAddress IP) {
        this.senderName = senderName;
        this.IP = IP;
    }
}
