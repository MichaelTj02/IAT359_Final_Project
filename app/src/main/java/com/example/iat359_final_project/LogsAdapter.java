package com.example.iat359_final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.LogViewHolder> {
    private ArrayList<LogModel> logsList;

    public LogsAdapter(ArrayList<LogModel> logsList) {
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
        LogModel log = logsList.get(position);
        holder.bind(log);
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

        public void bind(LogModel log) {
            textViewLog.setText("Location: " + log.getLocation() + ", Steps: " + log.getSteps());
        }
    }
}