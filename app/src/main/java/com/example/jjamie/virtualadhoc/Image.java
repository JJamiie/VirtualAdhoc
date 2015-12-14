package com.example.jjamie.virtualadhoc;


import android.annotation.TargetApi;
import android.os.Build;

import java.nio.charset.StandardCharsets;

public class Image {
    public static final int BUFFER_SIZE = 5000;
    public static final int SENDER_NAME_LENGTH = 50;
    public static final int TOTAL_LENGTH = SENDER_NAME_LENGTH + 4 + Image.BUFFER_SIZE;

    public String senderName;
    public int sequenceNumber;
    public byte[] imageBytes;

    public Image(String senderName, int sequenceNumber, byte[] imageBytes) throws SenderNameIncorrectLengthException{
        if (senderName.length() > SENDER_NAME_LENGTH) {
            throw new SenderNameIncorrectLengthException();
        }
        this.senderName = senderName;
        this.sequenceNumber = sequenceNumber;
        this.imageBytes = imageBytes;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public Image(byte[] audioChunkBytes) throws ImageChunkIncorrectLengthException {
        if (audioChunkBytes.length != TOTAL_LENGTH) {
            throw new ImageChunkIncorrectLengthException();
        }

//		assign the senderName
        byte[] senderNameBytes = new byte[SENDER_NAME_LENGTH];
        System.arraycopy(audioChunkBytes, 0, senderNameBytes, 0, SENDER_NAME_LENGTH);

//		find the actual sender name, because this.senderName should contain only the name string with actual length
//		this cannot be determined trivially
//		we have to detect the '\0' char (0 in utf-8 byte) to see the end of string
        int senderNameLength = 0;
        for (int i = 0; i < senderNameBytes.length; ++i) {
            if (senderNameBytes[i] != 0) {
                senderNameLength += 1;
            }
        }

        String senderNameString = new String(senderNameBytes, 0, senderNameLength, StandardCharsets.UTF_8);
        this.senderName = senderNameString;

//		assign the sequenceNumber
        byte[] sequenceNumberBytes = new byte[4];
        System.arraycopy(audioChunkBytes, SENDER_NAME_LENGTH, sequenceNumberBytes, 0, 4);
        this.sequenceNumber = Image.bytesToInt(sequenceNumberBytes);

//		assign the audioBytes
        this.imageBytes = new byte[Image.BUFFER_SIZE];
        System.arraycopy(audioChunkBytes, SENDER_NAME_LENGTH + 4, this.imageBytes, 0, Image.BUFFER_SIZE);
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
//		resizing the sender name to be SENDER_NAME_LENGTH
        byte[] senderNameBytes = new byte[SENDER_NAME_LENGTH];
//		using utf-8 as encoding for converting chars to bytes
        byte[] senderNameBytesShorter = senderName.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(senderNameBytesShorter, 0, senderNameBytes, 0, senderNameBytesShorter.length);

        byte[] sequenceNumberBytes = Image.intToBytes(sequenceNumber);
        byte[] imageChunkBytes = new byte[TOTAL_LENGTH];

        System.arraycopy(senderNameBytes, 0, imageChunkBytes, 0, SENDER_NAME_LENGTH);
        System.arraycopy(sequenceNumberBytes, 0, imageChunkBytes, SENDER_NAME_LENGTH, 4);
        System.arraycopy(imageBytes, 0, imageChunkBytes, SENDER_NAME_LENGTH + 4,Image.BUFFER_SIZE);

        return imageChunkBytes;
    }
}
