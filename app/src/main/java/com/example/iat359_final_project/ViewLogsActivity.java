package com.example.iat359_final_project;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ViewLogsActivity extends Activity {
    private RecyclerView myRecycler;
    private Database db;
    private CustomAdapter customAdapter;
    private DatabaseHelper helper;
    private LinearLayoutManager mLayoutManager;

    EditText searchEditText;
    Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logs);
        myRecycler = (RecyclerView) findViewById(R.id.recyclerViewLogs);

        db = new Database(this);
        helper = new DatabaseHelper(this);

        Button btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllLogs();
                customAdapter.notifyDataSetChanged();
            }
        });

        Cursor cursor = db.getData();

        int index1 = cursor.getColumnIndex(Constants.SESSION_TITLE);
        int index2 = cursor.getColumnIndex(Constants.LOCATION);
        int index3 = cursor.getColumnIndex(Constants.STEPS_AMOUNT);

        ArrayList<String> mArrayList = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String logTitle = cursor.getString(index1);
            String logLocation = cursor.getString(index2);
            String logSteps = cursor.getString(index3);
            String s = logTitle + "," + logLocation + "," + logSteps;
            mArrayList.add(s);
            cursor.moveToNext();
        }

        ArrayList<String> queryResults = getIntent().getStringArrayListExtra("queryResults");

        if(queryResults != null){
            customAdapter = new CustomAdapter(queryResults, db);
        } else{
            customAdapter = new CustomAdapter(mArrayList, db);
        }
        myRecycler.setAdapter(customAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        myRecycler.setLayoutManager(mLayoutManager);

        customAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            public void onDeleteItemClick(int position) {
                customAdapter.deleteItem(position);
                // No need to call db.deleteData here as it's handled within deleteItem method of CustomAdapter
            }
        });

        // for query
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationQuery = searchEditText.getText().toString();
                queryLogsByLocation(locationQuery);
            }
        });
    }

    private void deleteAllLogs() {
        // Assuming 'db' is an instance of your Database class
        db.deleteAllRecords();

        // Clear the data set used by the adapter
        ArrayList<String> emptyList = new ArrayList<>();
        customAdapter.updateDataSet(emptyList);

        // Notify the adapter to refresh the RecyclerView
        customAdapter.notifyDataSetChanged();

        // Optionally, display a message to the user
        Toast.makeText(this, "All logs deleted", Toast.LENGTH_SHORT).show();
    }

    private void queryLogsByLocation(String location) {
        // Query the database for logs at the specified location
        ArrayList<String> filteredList = db.queryLogs(location);

        // Update the RecyclerView with the filtered list
        customAdapter.updateDataSet(filteredList);
        customAdapter.notifyDataSetChanged();
    }

}
