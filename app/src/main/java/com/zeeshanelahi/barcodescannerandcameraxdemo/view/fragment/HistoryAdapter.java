package com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeshanelahi.barcodescannerandcameraxdemo.R;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.enities.MSSVInfo;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private ArrayList<MSSVInfo> mssvList;

    public HistoryAdapter(ArrayList<MSSVInfo> mssvList) {
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
        MSSVInfo mssv = mssvList.get(position);
        String displayText = (position + 1) + ". " + mssv.getMssv() + " - " + mssv.getScanTime();
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
