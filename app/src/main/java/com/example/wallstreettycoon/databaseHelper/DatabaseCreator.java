package com.example.wallstreettycoon.databaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseCreator extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "localdata.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseCreator(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "userID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "userFName TEXT, userLName TEXT, username TEXT, password TEXT, balance REAL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle schema migrations here
    }
}
