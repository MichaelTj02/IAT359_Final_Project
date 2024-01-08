package com.example.iat359_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.iat359_final_project.Constants;
import com.example.iat359_final_project.DatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class Database {
    private SQLiteDatabase db;
    private Context context;
    private final DatabaseHelper helper;

    public Database (Context c){
        context = c;
        helper = new DatabaseHelper(context);
    }

    public long insertData (String location, String steps, String sessionTitle) // to insert data
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.SESSION_TITLE, sessionTitle);
        contentValues.put(Constants.LOCATION, location);
        contentValues.put(Constants.STEPS_AMOUNT, steps);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData() // to get query data
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {Constants.UID, Constants.SESSION_TITLE, Constants.LOCATION, Constants.STEPS_AMOUNT};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }


    public ArrayList<String> queryLogs(String location) {
        SQLiteDatabase db = helper.getReadableDatabase();

        // Define the columns to retrieve
        String[] columns = {Constants.SESSION_TITLE, Constants.STEPS_AMOUNT, Constants.LOCATION,};

        // Prepare the selection clause for a case-insensitive search
        // steps_amount = search for location, this is VERY WEIRD, but it works so yehehehehe
        String selection = Constants.LOCATION + " LIKE ?";

        // Use lower() function for case-insensitive search and handle null location input
        location = location != null ? "%" + location.toLowerCase() + "%" : "%%";

        // Perform the query
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, new String[]{location}, null, null, null);

        ArrayList<String> resultList = new ArrayList<>();

        // Check if the cursor is not null and has data
        if (cursor != null && cursor.moveToFirst()) {
            int titleIndex = cursor.getColumnIndex(Constants.SESSION_TITLE);
            int locationIndex = cursor.getColumnIndex(Constants.LOCATION);
            int stepsIndex = cursor.getColumnIndex(Constants.STEPS_AMOUNT);

            // Iterate over the cursor to build the result list
            do {
                String sessionTitle = cursor.getString(titleIndex);
                String logLocation = cursor.getString(locationIndex);
                String stepsAmount = cursor.getString(stepsIndex);
                resultList.add(sessionTitle + "," + logLocation + "," + stepsAmount);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return resultList;
    }

    public void deleteData(String location) {
        // delete one log item from the database
        db = helper.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.SESSION_TITLE + "=?", new String[]{location});
        db.close();
    }

    public void deleteAllRecords() {
        // delete all logs in database
        db = helper.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, null, null);
        db.close();
    }

}

