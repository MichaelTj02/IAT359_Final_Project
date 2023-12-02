package com.example.iat359_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.iat359_final_project.Constants;
import com.example.iat359_final_project.DatabaseHelper;

import java.util.ArrayList;

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

        String[] columns = {Constants.SESSION_TITLE, Constants.LOCATION, Constants.STEPS_AMOUNT};
        String selection = Constants.LOCATION + "= ?";

        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, new String[]{location}, null, null, null);

        ArrayList<String> resultList = new ArrayList<>();

        int titleIndex = cursor.getColumnIndex(Constants.SESSION_TITLE);
        int locationIndex = cursor.getColumnIndex(Constants.LOCATION);
        int stepsIndex = cursor.getColumnIndex(Constants.STEPS_AMOUNT);

        while (cursor.moveToNext()) {
            String sessionTitle = cursor.getString(titleIndex);
            String logLocation = cursor.getString(locationIndex);
            String stepsAmount = cursor.getString(stepsIndex);
            resultList.add(sessionTitle + "," + logLocation + "," + stepsAmount);
        }

        cursor.close();
        return resultList;
    }



    public void deleteData(String location) {
        db = helper.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.SESSION_TITLE + "=?", new String[]{location});
        db.close();
    }

    public void deleteAllRecords() {
        db = helper.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, null, null);
        db.close();
    }

}

