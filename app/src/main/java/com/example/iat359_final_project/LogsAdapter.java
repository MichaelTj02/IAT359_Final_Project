package com.example.iat359_final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.LogViewHolder> {
    private ArrayList<String> logsList;

    public LogsAdapter(ArrayList<String> logsList) {
        this.logsList = logsList;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        String logText = logsList.get(position);
        holder.textViewLog.setText(logText);
    }

    @Override
    public int getItemCount() {
        return logsList.size();
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLog;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLog = itemView.findViewById(R.id.textViewLog);
        }
    }
}