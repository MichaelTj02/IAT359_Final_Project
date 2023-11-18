package com.example.iat359_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "travel_logs.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table for storing travel logs
        String createTableQuery = "CREATE TABLE logs (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "distance REAL," +
                "location TEXT," +
                "steps INTEGER" +
                ");";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If needed, handle database upgrade (e.g., alter table, etc.)
        db.execSQL("DROP TABLE IF EXISTS logs");
        onCreate(db);
    }

    public long insertLog(double distance, String location, int steps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("distance", distance);
        values.put("location", location);
        values.put("steps", steps);
        long result = db.insert("logs", null, values);
        db.close();
        return result; // Returns the row ID of the newly inserted row, or -1 if an error occurred
    }


}