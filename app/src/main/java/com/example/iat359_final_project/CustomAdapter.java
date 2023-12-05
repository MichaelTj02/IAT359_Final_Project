package com.example.iat359_final_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    public ArrayList<String> list;
    private Database db;
    public Button deleteItemButton;

    public CustomAdapter(ArrayList<String> list, Database db) {
        this.list = list; this.db = db;
    }

    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomAdapter.MyViewHolder holder, int position) {

        String[]  results = (list.get(position).toString()).split(",");
        holder.sessionTitle.setText(results[1]);
        holder.locationTextView.setText(results[0]);
        holder.stepsTextView.setText(results[2]);

    }


    @Override
    public int getItemCount() {
        return list.size();
    } // get amount of item available in the view

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView locationTextView, stepsTextView, sessionTitle;
        public LinearLayout myLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            myLayout = (LinearLayout) itemView;

            locationTextView = (TextView) itemView.findViewById(R.id.locationEntry);
            stepsTextView = (TextView) itemView.findViewById(R.id.stepsEntry);
            sessionTitle = (TextView) itemView.findViewById(R.id.sessionTitleEntry);

            deleteItemButton = itemView.findViewById(R.id.delete_button);

            // Set a click listener on the delete button
            deleteItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Notify the listener and pass the position
                        listener.onDeleteItemClick(position);
                    }
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onDeleteItemClick(int position);
    }

    private OnItemClickListener listener;

    // Method for setting the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void deleteItem(int position) {
        String item = list.get(position);
        String[] results = item.split(",");
        String location = results[0];
        list.remove(position);
        notifyItemRemoved(position);
        db.deleteData(location);
    }

    public void updateDataSet(ArrayList<String> newList) {
        // update database when item is deleted
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }
}