package com.example.loginform;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String databaseName = "SignLog.db";
    public DatabaseHelper(@Nullable Context context) {
        super(context, "SignLog.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        MyDatabase.execSQL("create Table users(username TEXT primary key, password TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists users");
    }
    public Boolean insertData(String username, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = MyDatabase.insert("users", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Boolean checkUser(String username){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from users where username = ?", new String[]{username});
        if(cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }
    public Boolean checkUserPassword(String username, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from users where username = ? and password = ?", new String[]{username, password});
        if (cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }
    //show db

    public List<String> getAllData() {
        List<String> data = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                "username",
                "password"
        };

        Cursor cursor = db.query(
                "users", // Tên bảng
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));

                data.add(username);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return data;
    }

}
