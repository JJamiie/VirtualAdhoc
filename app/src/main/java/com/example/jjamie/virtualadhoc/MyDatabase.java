package com.example.jjamie.virtualadhoc;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JJamie on 2/6/16 AD.
 */
public class MyDatabase extends SQLiteOpenHelper {
    public static final String DB_NAME = "PIGEON";
    public static final int DB_VERSION = 1;

    // TABLE PICTURE
    public static final String TABLE_NAME_PICTURE = "Picture";
    public static final String COL_SENDER_NAME = "sender";
    public static final String COL_FILE_NAME = "filename";
    public static final String COL_MESSAGE = "message";
    public static final String COL_LOCATION = "location";

    // TABLE USER
    public static final String TABLE_NAME_USER = "User";
    public static final String COL_USERNAME = "username";
    public static final String COL_EMAIL = "email";
    public static final String COL_NAME = "name";
    public static final String COL_SURENAME = "surename";
    public static final String COL_SEX = "sex";
    public static final String COL_BIRTHDATE = "birthdate";
    public static final String COL_ADDRESS = "address";


    public MyDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREATE PICTURE TABLE
        db.execSQL("CREATE TABLE " + TABLE_NAME_PICTURE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SENDER_NAME + " TEXT, "
                + COL_FILE_NAME + " TEXT, "
                + COL_MESSAGE + " TEXT, "
                + COL_LOCATION + " TEXT);");

        //CREATE USER TABLE
        db.execSQL("CREATE TABLE " + TABLE_NAME_USER + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT, "
                + COL_EMAIL + " TEXT, "
                + COL_NAME + " TEXT, "
                + COL_SURENAME + " TEXT, "
                + COL_SEX + " TEXT, "
                + COL_BIRTHDATE + " TEXT, "
                + COL_FILE_NAME + " TEXT, "
                + COL_ADDRESS + " TEXT);");

    }

    public void addToTablePicture(SQLiteDatabase db, String sendername, String filename, String message, String location) {
        db.execSQL("INSERT INTO " + TABLE_NAME_PICTURE + " (" + COL_SENDER_NAME + ", " + COL_FILE_NAME
                + ", " + COL_MESSAGE + ", " + COL_LOCATION + ") VALUES ('" + sendername + "', '" + filename + "', '" + message + "', '" + location + "');");
    }

    public void addToTableUser(SQLiteDatabase db, String username, String email, String name, String surename, String sex, String birthdate, String filename, String address) {
        db.execSQL("INSERT INTO " + TABLE_NAME_USER + " (" + COL_USERNAME + ", " + COL_EMAIL
                + ", " + COL_NAME + ", " + COL_SURENAME + ", " + COL_SEX + ", " + COL_BIRTHDATE
                + ", " + COL_FILE_NAME + ", " + COL_ADDRESS + ") VALUES ('" + username + "', '"
                + filename + "', '" + email + "', '" + name + "', '" + surename + "', '" + sex
                + "', '" + birthdate + "', '" + filename + "');");
    }

    public void updateToTableUser(SQLiteDatabase db, String username, String email, String name, String surename, String sex, String birthdate, String filename, String address) {
        ContentValues con = new ContentValues();
        con.put(COL_USERNAME, username);
        con.put(COL_EMAIL, email);
        con.put(COL_NAME, name);
        con.put(COL_SURENAME, surename);
        con.put(COL_BIRTHDATE, birthdate);
        con.put(COL_SEX,sex);
        con.put(COL_FILE_NAME, filename);
        con.put(COL_ADDRESS, address);
        db.update(TABLE_NAME_USER, con, COL_USERNAME + "='" + username + "'",null);

//        db.execSQL("UPDATE " + TABLE_NAME_USER + " SET " + COL_USERNAME + "=' " + username + "',"
//                + COL_EMAIL + "=' " + email + "'," + COL_NAME + "=' " + name + "',"
//                + COL_SURENAME + "=' " + surename + "'," + COL_SEX + "=' " + sex + "'," + COL_BIRTHDATE + "=' " + birthdate + "',"
//                + COL_FILE_NAME + "=' " + filename + "'," + COL_ADDRESS + "=' " + address + "' WHERE " + COL_USERNAME + "='" + username + "'");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PICTURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER);
        onCreate(db);
    }
}
