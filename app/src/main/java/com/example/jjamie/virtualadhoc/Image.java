package com.example.jjamie.virtualadhoc;


import android.util.Log;

import java.nio.charset.Charset;


public class Image {
    public static final int SENDER_NAME_LENGTH = 50;
    public static final int MESSAGE_LENGTH = 100;
    public static final int GPS_LENGTH = 30;
    public static final int SEQUENCE_NUMBER_LENGTH = 4;

    public String senderName;
    public int sequenceNumber;
    public String message;
    public String location;
    public byte[] imageBytes;


    public Image(String senderName, int sequenceNumber, String message, String location, byte[] imageBytes) throws LengthIncorrectLengthException {
        if (senderName.length() > SENDER_NAME_LENGTH || message.length() > MESSAGE_LENGTH || location.length() > GPS_LENGTH) {
            throw new LengthIncorrectLengthException();
        }
        this.senderName = senderName;
        this.sequenceNumber = sequenceNumber;
        this.message = message;
        this.location = location;
        this.imageBytes = imageBytes;

    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public Image(byte[] imageBytes, int lengthPacket) throws ImageChunkIncorrectLengthException {
        Log.d("Image", "Length: " + lengthPacket);

        //	assign the senderName
        byte[] senderNameBytes = new byte[SENDER_NAME_LENGTH];
        System.arraycopy(imageBytes, 0, senderNameBytes, 0, SENDER_NAME_LENGTH);

        //	find the actual sender name, because this.senderName should contain only the name string with actual length
        //	this cannot be determined trivially
        //	we have to detect the '\0' char (0 in utf-8 byte) to see the end of string

        String senderNameString = new String(senderNameBytes, 0, findActuallength(senderNameBytes), Charset.forName("UTF-8"));
        this.senderName = senderNameString;

        //	assign the sequenceNumber
        byte[] sequenceNumberBytes = new byte[SEQUENCE_NUMBER_LENGTH];
        System.arraycopy(imageBytes, SENDER_NAME_LENGTH, sequenceNumberBytes, 0, SEQUENCE_NUMBER_LENGTH);
        this.sequenceNumber = Image.bytesToInt(sequenceNumberBytes);

        // assign message
        byte[] messageBytes = new byte[MESSAGE_LENGTH];
        System.arraycopy(imageBytes, SENDER_NAME_LENGTH + SEQUENCE_NUMBER_LENGTH, messageBytes, 0, MESSAGE_LENGTH);
        String messageString = new String(messageBytes, 0, findActuallength(messageBytes), Charset.forName("UTF-8"));
        this.message = messageString;

        // assign location
        byte[] locationBytes = new byte[GPS_LENGTH];
        System.arraycopy(imageBytes, SENDER_NAME_LENGTH + SEQUENCE_NUMBER_LENGTH + MESSAGE_LENGTH, locationBytes, 0, GPS_LENGTH);
        String locationString = new String(locationBytes, 0, findActuallength(locationBytes), Charset.forName("UTF-8"));
        this.location = locationString;

        //	assign the imageBytes
        int lengthImage = lengthPacket - (SENDER_NAME_LENGTH + SEQUENCE_NUMBER_LENGTH + MESSAGE_LENGTH + GPS_LENGTH);
        this.imageBytes = new byte[lengthImage];
        System.arraycopy(imageBytes, SENDER_NAME_LENGTH + SEQUENCE_NUMBER_LENGTH + MESSAGE_LENGTH + GPS_LENGTH, this.imageBytes, 0, lengthImage);
    }

    public static int findActuallength(byte[] bytes) {
        int length = 0;
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] != 0) {
                length += 1;
            }
        }
        return length;
    }


    public static byte[] intToBytes(int intValue) {
        byte[] result = new byte[4];
        result[0] = (byte) ((intValue & 0xFF000000) >> 24);
        result[1] = (byte) ((intValue & 0x00FF0000) >> 16);
        result[2] = (byte) ((intValue & 0x0000FF00) >> 8);
        result[3] = (byte) ((intValue & 0x000000FF) >> 0);
        return result;
    }

    public static int bytesToInt(byte[] intBytes) {
        int result = 0;

        result |= (intBytes[0] & (0xff) | 0) << 24;
        result |= (intBytes[1] & (0xff) | 0) << 16;
        result |= (intBytes[2] & (0xff) | 0) << 8;
        result |= (intBytes[3] & (0xff) | 0) << 0;
        return result;
    }

    public byte[] getBytes() {
        //	resizing the sender name to be SENDER_NAME_LENGTH
        byte[] senderNameBytes = new byte[SENDER_NAME_LENGTH];
        //	using utf-8 as encoding for converting chars to bytes
        byte[] senderNameBytesShorter = senderName.getBytes(Charset.forName("UTF-8"));
        System.arraycopy(senderNameBytesShorter, 0, senderNameBytes, 0, senderNameBytesShorter.length);

        byte[] sequenceNumberBytes = Image.intToBytes(sequenceNumber);

        byte[] messageBytes = new byte[MESSAGE_LENGTH];
        byte[] messageBytesShorter = message.getBytes(Charset.forName("UTF-8"));
        System.arraycopy(messageBytesShorter, 0, messageBytes, 0, messageBytesShorter.length);

        byte[] locationBytes = new byte[GPS_LENGTH];
        byte[] locationBytesShorter = location.getBytes(Charset.forName("UTF-8"));
        System.arraycopy(locationBytesShorter, 0, locationBytes, 0, locationBytesShorter.length);

        byte[] imageChunkBytes = new byte[SENDER_NAME_LENGTH + SEQUENCE_NUMBER_LENGTH + MESSAGE_LENGTH + GPS_LENGTH + imageBytes.length];

        System.arraycopy(senderNameBytes, 0, imageChunkBytes, 0, SENDER_NAME_LENGTH);
        System.arraycopy(sequenceNumberBytes, 0, imageChunkBytes, SENDER_NAME_LENGTH, SEQUENCE_NUMBER_LENGTH);
        System.arraycopy(messageBytes, 0, imageChunkBytes, SENDER_NAME_LENGTH + SEQUENCE_NUMBER_LENGTH, MESSAGE_LENGTH);
        System.arraycopy(locationBytes, 0, imageChunkBytes, SENDER_NAME_LENGTH + SEQUENCE_NUMBER_LENGTH + MESSAGE_LENGTH, GPS_LENGTH);
        System.arraycopy(imageBytes, 0, imageChunkBytes, SENDER_NAME_LENGTH + SEQUENCE_NUMBER_LENGTH + MESSAGE_LENGTH + GPS_LENGTH, imageBytes.length);

        return imageChunkBytes;
    }
}
