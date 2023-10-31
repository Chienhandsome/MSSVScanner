package com.zeeshanelahi.barcodescannerandcameraxdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class DanhSachSV extends AppCompatActivity {

    ImageButton backBT;
    public ArrayList<SinhVien> sinhVienArrayList = new ArrayList<SinhVien>();
    public ArrayList<Boolean> checkArrayList = new ArrayList<Boolean>();
    Adapter adapter = new Adapter(this,R.layout.info_sv_row, sinhVienArrayList, checkArrayList);
    ListView listView;
    String link = "https://mssvscanner-default-rtdb.asia-southeast1.firebasedatabase.app";
    FirebaseDatabase database = FirebaseDatabase.getInstance(link);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ds_sinh_vien);

        anhXa();

        listView.setAdapter(adapter);

        getNewData();
        adapter.notifyDataSetChanged();

        backBT.setOnClickListener(view -> {
            Intent intent = new Intent(DanhSachSV.this, Menu.class);
            startActivities(new Intent[]{intent});
        });
        //Toast.makeText(DanhSachSV.this, "so sinh vien " + sinhVienArrayList.size() , Toast.LENGTH_SHORT).show();
    }

    public void getNewData(){

        DatabaseReference reference = database.getReference();
        reference.child("Danh Sách Sinh Viên").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String mssv = snapshot.getKey().toString();
                String value = snapshot.getValue().toString();

                SinhVien sv = new SinhVien(mssv," ");

                DanhSachSV.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        sinhVienArrayList.add(sv);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String mssv = snapshot.getValue().toString();
                SinhVien sv = new SinhVien(mssv," ");

                DanhSachSV.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        sinhVienArrayList.remove(sv);
                        adapter.notifyDataSetChanged();
                    }
                });
                Toast.makeText(DanhSachSV.this, "Có dữ liệu bị xóa!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void anhXa(){
        backBT = findViewById(R.id.backButton);
        listView = findViewById(R.id.listview_ds_sinh_vien);
    }
}
