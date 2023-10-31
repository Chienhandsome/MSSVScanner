package com.zeeshanelahi.barcodescannerandcameraxdemo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Adapter extends BaseAdapter {
    private DanhSachSV context;
    private int layout;
    private List<SinhVien> sinhVienList;
    private List<Boolean> checkArrayList;
    String link = "https://mssvscanner-default-rtdb.asia-southeast1.firebasedatabase.app";
    FirebaseDatabase database = FirebaseDatabase.getInstance(link);

    public Adapter(DanhSachSV context, int layout, List<SinhVien> sinhVienList, List<Boolean> checkArrayList) {
        this.context = context;
        this.layout = layout;
        this.sinhVienList = sinhVienList;
        this.checkArrayList = checkArrayList;
    }

    @Override
    public int getCount() {
        return sinhVienList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder{
        TextView tvLop;
        TextView tvMSSV;
        LinearLayout infoSV;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder() ;
        if (view == null){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            holder.tvMSSV =  view.findViewById(R.id.mssv);
            holder.tvLop =  view.findViewById(R.id.lop);
            holder.infoSV = view.findViewById(R.id.info_sv);

            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        final SinhVien sinhVien = sinhVienList.get(i);

        try{
            if(checkArrayList.size() != 0 && checkArrayList.get(i)){
                view.setBackgroundColor(Color.GRAY);
            }
        }
        catch (Exception e){
            Toast.makeText(context, "ArrayList rỗng" , Toast.LENGTH_SHORT).show();
        };

//        try{
//            DatabaseReference reference = database.getReference("Danh Sách Sinh Viên");
//            DataSnapshot snapshot = reference.get().getResult();
//            String mssv = sinhVienList.get(i).toString();
//
//            if(checkArrayList.size() != 0 && snapshot.getValue().toString().equals("true")){
//                view.setBackgroundColor(Color.GRAY);
//            }
//        }
//        catch (Exception e){
//            Toast.makeText(context, "ArrayList rỗng" , Toast.LENGTH_SHORT).show();
//        };

        holder.tvMSSV.setText(sinhVien.getMssv());
        holder.tvLop.setText(sinhVien.getLop());

        //click vào 1 sv
        ViewHolder finalHolder = holder;
        holder.infoSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = database.getReference();
                //reference.child("check").child().setValue(true);
                finalHolder.infoSV.setBackgroundColor(Color.GRAY);
            }
        });

        return view;
    }

}
