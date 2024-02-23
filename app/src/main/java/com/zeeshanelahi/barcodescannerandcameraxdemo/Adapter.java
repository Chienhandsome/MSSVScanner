package com.zeeshanelahi.barcodescannerandcameraxdemo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class Adapter extends BaseAdapter {
    private DanhSachSV context;
    private int layout;
    private List<SinhVien> sinhVienList;

    String link = "https://mssvscanner-default-rtdb.asia-southeast1.firebasedatabase.app";
    FirebaseDatabase database = FirebaseDatabase.getInstance(link);

    public Adapter(DanhSachSV context, int layout, List<SinhVien> sinhVienList) {
        this.context = context;
        this.layout = layout;
        this.sinhVienList = sinhVienList;
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

        holder.tvMSSV.setText(sinhVien.getMssv());
        holder.tvLop.setText(sinhVien.getLop());

        DatabaseReference reference = database.getReference();
        Task<DataSnapshot> task = reference.get();
        ViewHolder finalHolder = holder;
        task.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    boolean value = Boolean.TRUE.equals(dataSnapshot.child("Danh Sách Sinh Viên").child(sinhVien.getMssv()).getValue(boolean.class));
                    if (value){
                        finalHolder.infoSV.setBackgroundColor(Color.GRAY);
                    }
                } else {

                }
            }
        });

        holder.infoSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference studentReference = reference.child("Danh Sách Sinh Viên").child(sinhVien.getMssv());
                studentReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            DataSnapshot dataSnapshot = task.getResult();
                            boolean value = Boolean.TRUE.equals(dataSnapshot.getValue(boolean.class));

                            if (!value) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Show SV có mã "+sinhVienList.get(i).getMssv()+ " ?");
                                //builder.setMessage("biến SV có mã "+sinhVienList.get(i).getMssv()+ "?");

                                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        studentReference.setValue(true);
                                        finalHolder.infoSV.setBackgroundColor(Color.GRAY);
                                    }
                                });

                                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.show();
                            }
                        } else {
                            // Xử lý lỗi
                        }
                    }
                });
            }
        });

        return view;
    }

}
