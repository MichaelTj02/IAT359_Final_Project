package com.example.iat359_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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


    public ArrayList<String> getSelectedDataList(String location)
    {
        //select plants from database of type 'herb'
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.SESSION_TITLE, Constants.LOCATION, Constants.STEPS_AMOUNT};

        String selection = Constants.LOCATION + "='" +location+ "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);

        //StringBuffer buffer = new StringBuffer();
        ArrayList<String> queryResult = new ArrayList<>();
        while (cursor.moveToNext()) {

            int index1 = cursor.getColumnIndex(Constants.SESSION_TITLE);
            int index2 = cursor.getColumnIndex(Constants.LOCATION);
            int index3 = cursor.getColumnIndex(Constants.STEPS_AMOUNT);
            String logTitle = cursor.getString(index1);
            String logLocation = cursor.getString(index2);
            String logSteps = cursor.getString(index3);
            queryResult.add(logTitle + "," + logLocation + "," + logSteps);
        }
        return queryResult;
    }

    public void updateLog(int logId, String newLocation, String newSteps, String newTitle) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.SESSION_TITLE, newTitle);
        values.put(Constants.LOCATION, newLocation);
        values.put(Constants.STEPS_AMOUNT, newSteps);
        db.update(Constants.TABLE_NAME, values, Constants.UID + "=?", new String[]{String.valueOf(logId)});
        db.close();
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

