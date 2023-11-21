package com.example.iat359_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Database {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public Database(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertLog(String location, int steps) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.LOCATION, location);
        values.put(Constants.STEPS_AMOUNT, steps);
        long result = db.insert("sessions_log", null, values);
        db.close();
        return result; // Returns the row ID of the newly inserted row, or -1 if an error occurred
    }

    public Cursor getData()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] columns = {Constants.UID, Constants.LOCATION, Constants.STEPS_AMOUNT};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

//    public ArrayList<String> getSelectedDataList(String location)
//    {
//        //select plants from database of type 'herb'
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        String[] columns = {Constants.LOCATION, Constants.STEPS_AMOUNT};
//
//        String selection = Constants.LOCATION + "='" +location+ "'";  //Constants.TYPE = 'type'
//        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);
//
//        //StringBuffer buffer = new StringBuffer();
//        ArrayList<String> queryResult = new ArrayList<>();
//        while (cursor.moveToNext()) {
//
//            int index1 = cursor.getColumnIndex(Constants.LOCATION);
//            int index2 = cursor.getColumnIndex(Constants.STEPS_AMOUNT);
//            String logLocation = cursor.getString(index1);
//            String logSteps = cursor.getString(index2);
//            queryResult.add(logLocation + "," + logSteps);
//        }
//        return queryResult;
//    }
}