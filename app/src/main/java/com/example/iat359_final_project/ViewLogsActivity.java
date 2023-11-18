package com.example.iat359_final_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewLogsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewLogs;
    private ArrayList<String> travelLogsList; // List of travel logs
    private LogsAdapter logsAdapter; // Create your custom RecyclerView Adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logs);

        recyclerViewLogs = findViewById(R.id.recyclerViewLogs);
        travelLogsList = new ArrayList<>(); // Initialize your list of logs

        // Mock data - replace this with actual data retrieval logic
        travelLogsList.add("Log 1");
        travelLogsList.add("Log 2");
        travelLogsList.add("Log 3");

        logsAdapter = new LogsAdapter(travelLogsList); // Create your custom Adapter
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLogs.setAdapter(logsAdapter);
    }
}