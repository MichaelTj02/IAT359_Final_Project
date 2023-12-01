package com.example.iat359_final_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    public ArrayList<String> list;
    private Database db;

    public CustomAdapter(ArrayList<String> list) {
        this.list = list;
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
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView locationTextView, stepsTextView, sessionTitle;
        public LinearLayout myLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            myLayout = (LinearLayout) itemView;

            locationTextView = (TextView) itemView.findViewById(R.id.locationEntry);
            stepsTextView = (TextView) itemView.findViewById(R.id.stepsEntry);
            sessionTitle = (TextView) itemView.findViewById(R.id.sessionTitleEntry);


//            itemView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    int action = event.getActionMasked();
//                    int position = getAdapterPosition();
//                    if (action == MotionEvent.ACTION_DOWN && position != RecyclerView.NO_POSITION) {
//                        // Handle swipe-to-delete here
//                        deleteItem(position);
//
//                        return true;
//
//                    }
//                    return false;
//                }
//
//
//            });

        }
    }

    public void deleteItem(int position) {
        String item = list.get(position);
        String[] results = item.split(",");
        String location = results[0];
        list.remove(position);
        notifyItemRemoved(position);
        db.deleteData(location);
    }
}