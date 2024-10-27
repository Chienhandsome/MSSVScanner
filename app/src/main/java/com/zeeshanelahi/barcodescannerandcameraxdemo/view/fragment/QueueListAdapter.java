package com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeshanelahi.barcodescannerandcameraxdemo.R;

import java.util.ArrayList;

public class QueueListAdapter extends RecyclerView.Adapter<QueueListAdapter.ViewHolder> {
    private ArrayList<String> mssvList;

    public QueueListAdapter(ArrayList<String> mssvList) {
        this.mssvList = mssvList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mssv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String mssv = mssvList.get(position);
        String displayText = position + ". " + mssv;
        holder.mssvTV.setText(displayText);
    }

    @Override
    public int getItemCount() {
        return mssvList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView mssvTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mssvTV = itemView.findViewById(R.id.mssvTV);
        }
    }
}
