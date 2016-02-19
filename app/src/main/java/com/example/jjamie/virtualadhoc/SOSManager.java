package com.example.jjamie.virtualadhoc;

import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Administrator on 18/2/2559.
 */
public class SOSManager extends Thread {
    SQLiteDatabase sQLiteDatabase;
    MyDatabase myDatabase;
    private String senderName;
    private Double latitude;
    private Double longitude;
    private Boolean isCaptured = false;
    private boolean active = false;
    public void run(){
        while (true){
            synchronized (this) {
                if (!active) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
    private void addMessageToPicture() {
        String message = "Help me";
        String latitudeAndLongtitude = latitude + "," + longitude;
        File currentPhoto = new File(ManageImage.getFile()[0].getAbsolutePath());
        byte[] img = new byte[(int) currentPhoto.length()];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(currentPhoto));
            buf.read(img, 0, img.length);
            buf.close();
            Image image = null;
            myDatabase.addToTablePicture(sQLiteDatabase, senderName, null, message, latitudeAndLongtitude);
            image = new Image(senderName, "null", message, latitudeAndLongtitude, img);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LengthIncorrectLengthException e) {
            e.printStackTrace();
        }
    }
    public void wake() {
        active = true;
        notifyAll();
    }

    public void sleep() {
        active = false;
    }
}
