package com.zeeshanelahi.barcodescannerandcameraxdemo.model.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.enities.MSSVInfo;
import com.zeeshanelahi.barcodescannerandcameraxdemo.utils.MssvKeyHandler;

import java.util.ArrayList;
import java.util.Objects;

public class MssvFirebaseManager {
    private static final String TAG = "MssvFirebaseManager";
    private static final String SV_LIST = "Danh Sách Sinh Viên";
    private DatabaseReference svListReference;
    private static MssvFirebaseManager instance;

    public static synchronized MssvFirebaseManager getInstance(){
        if (instance == null){
            instance = new MssvFirebaseManager();
        }
        return instance;
    }

    private MssvFirebaseManager() {
        svListReference = FirebaseDatabase.getInstance("https://mssvscanner-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    }

    public void addMSSVListIntoFirebase(MSSVInfo mssvInfo){
        svListReference.child(SV_LIST).child(mssvInfo.getScanTime()).setValue(mssvInfo.getMssv()).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d(TAG, "addMSSVListIntoFirebase: success");
            }
            if (task.isCanceled()){
                Error error = new Error("Push data into firebase failed");
                Log.e(TAG, "addMSSVListIntoFirebase: ", error);
            }
        });
    }

    public void getMSSVList(ArrayList<MSSVInfo> mssvInfoList, GetMSSVCallBack callBack){
        svListReference.child(SV_LIST).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mssvInfoList.clear();
                for (DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String timeString = Objects.requireNonNull(singleSnapshot.getKey());
                    String mssv = singleSnapshot.getValue(String.class);
                    MSSVInfo mssvInfo = new MSSVInfo(mssv, timeString);
                    mssvInfoList.add(mssvInfo);
                }
                callBack.onGetMSSVSuccess(mssvInfoList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Exception e = error.toException();
                Log.e(TAG, "onCancelled: ", e);
            }
        });
    }
}
