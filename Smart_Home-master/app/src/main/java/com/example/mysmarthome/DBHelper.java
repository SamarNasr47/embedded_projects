package com.example.mysmarthome;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_BIRTHDATE = "birthdate";
    private static final String COLUMN_LIGHT = "light";
    private static final String COLUMN_FAN = "fan";
    private static final String COLUMN_DOOR_PASSWORD = "door_password";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY," +
                COLUMN_NAME + " TEXT," +
                COLUMN_EMAIL + " TEXT," +
                COLUMN_PASSWORD + " TEXT," +
                COLUMN_BIRTHDATE + " TEXT," +
                COLUMN_LIGHT + " BOOLEAN," +
                COLUMN_FAN + " BOOLEAN," +
                COLUMN_DOOR_PASSWORD + " TEXT)"
                ;
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_BIRTHDATE, user.getBirthdate());
        values.put(COLUMN_LIGHT, false);
        values.put(COLUMN_FAN, false);
        values.put(COLUMN_DOOR_PASSWORD, "");
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public void updateLight(boolean light) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("light", light);
        db.update("light_status", values, null, null);
        db.close();
    }


    public void updateFan(boolean fan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fan", fan);
        db.update("fan_status", values, null, null);
        db.close();
    }

    public void updateDoorPassword(String doorPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOOR_PASSWORD, doorPassword);
        // Assuming you are updating the password for the user table without any condition
        db.update(TABLE_USERS, values, null, null);
        db.close();
    }

    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USERNAME, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_BIRTHDATE},
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null, null);

        User user = null;

        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            int columnIndexUsername = cursor.getColumnIndex(COLUMN_USERNAME);
            int columnIndexName = cursor.getColumnIndex(COLUMN_NAME);
            int columnIndexEmail = cursor.getColumnIndex(COLUMN_EMAIL);
            int columnIndexPassword = cursor.getColumnIndex(COLUMN_PASSWORD);
            int columnIndexBirthdate = cursor.getColumnIndex(COLUMN_BIRTHDATE);

            if (columnIndexUsername != -1) {
                user.setUsername(cursor.getString(columnIndexUsername));
            }
            if (columnIndexName != -1) {
                user.setName(cursor.getString(columnIndexName));
            }
            if (columnIndexEmail != -1) {
                user.setEmail(cursor.getString(columnIndexEmail));
            }
            if (columnIndexPassword != -1) {
                user.setPassword(cursor.getString(columnIndexPassword));
            }
            if (columnIndexBirthdate != -1) {
                user.setBirthdate(cursor.getString(columnIndexBirthdate));
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return user;
    }
}
