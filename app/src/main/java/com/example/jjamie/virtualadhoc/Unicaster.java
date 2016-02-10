package com.example.jjamie.virtualadhoc;


import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Unicaster{
    Socket socket =null;

    public void unicast(byte[] bytes,String dstAddress){
        try{
            socket=new Socket(dstAddress,Broadcaster.PORT);
            Log.d("Unicast", "Connecting...");

            //Send file
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            Log.d("Unicast","Sending...");

            objectOutputStream.writeObject(bytes);
            objectOutputStream.flush();
            socket.close();

        }catch (IOException ex){
            Log.d("Unicast","IOException: "+ ex);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("Unicast", "Finished...");

    }



}
