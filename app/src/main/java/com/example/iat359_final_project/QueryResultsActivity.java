package com.example.iat359_final_project;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchResultsActivity extends Activity {
    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_result); // Ensure you have this layout with a RecyclerView

        recyclerView = findViewById(R.id.recylerViewQueries); // Replace with your RecyclerView ID
        db = new Database(this);

        ArrayList<String> searchResults = getIntent().getStringArrayListExtra("searchResults");
        customAdapter = new CustomAdapter(searchResults, db);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onDeleteItemClick(int position) {
                customAdapter.deleteItem(position);
            }
        });
    }
}
