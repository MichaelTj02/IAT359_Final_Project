package com.example.iat359_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Database {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public Database(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertLog(String location, int steps) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("location", location);
        values.put("steps", steps);
        long result = db.insert("logs", null, values);
        db.close();
        return result; // Returns the row ID of the newly inserted row, or -1 if an error occurred
    }
}