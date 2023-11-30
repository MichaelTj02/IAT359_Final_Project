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

    public long insertData (String location, String steps) // to insert data
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.LOCATION, location);
        contentValues.put(Constants.STEPS_AMOUNT, steps);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData() // to get query data
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {Constants.UID, Constants.LOCATION, Constants.STEPS_AMOUNT};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public void deleteLog(int logId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.UID + "=?", new String[]{String.valueOf(logId)});
        db.close();
    }

    public ArrayList<String> getSelectedDataList(String location)
    {
        //select plants from database of type 'herb'
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.LOCATION, Constants.STEPS_AMOUNT};

        String selection = Constants.LOCATION + "='" +location+ "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);

        //StringBuffer buffer = new StringBuffer();
        ArrayList<String> queryResult = new ArrayList<>();
        while (cursor.moveToNext()) {

            int index1 = cursor.getColumnIndex(Constants.LOCATION);
            int index2 = cursor.getColumnIndex(Constants.STEPS_AMOUNT);
            String logLocation = cursor.getString(index1);
            String logSteps = cursor.getString(index2);
            queryResult.add(logLocation + "," + logSteps);
        }
        return queryResult;
    }

    public void updateLog(int logId, String newLocation, String newSteps) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.LOCATION, newLocation);
        values.put(Constants.STEPS_AMOUNT, newSteps);
        db.update(Constants.TABLE_NAME, values, Constants.UID + "=?", new String[]{String.valueOf(logId)});
        db.close();
    }

}

