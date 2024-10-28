package com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zeeshanelahi.barcodescannerandcameraxdemo.R;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.FragmentInputCodeBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.InternetBroadCastReceiver;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.enities.MSSVInfo;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.firebase.MssvFirebaseManager;
import com.zeeshanelahi.barcodescannerandcameraxdemo.utils.DataChecker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InputFragment extends Fragment {
    private String TAG = "InputFragment";
    private FragmentInputCodeBinding viewBinding;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_code, container, false);
        viewBinding = FragmentInputCodeBinding.bind(view);
        context = getContext();
        setUpViewEvents();
        return view;
    }

    private void setUpViewEvents() {
        viewBinding.addButton.setOnClickListener(v -> {
            onConfirmButtonClicked();
        });
    }

    private void onConfirmButtonClicked() {
        String mssv = viewBinding.editTextText.getText().toString();
//            seatRepository = new SeatRepository(this);
//            String seatInfo = seatRepository.getSeatInfo(mssv);

        if (mssv.isEmpty()){
            Toast.makeText(context, "Vui lòng nhập mã số sinh viên", Toast.LENGTH_SHORT).show();
        }
        else if (!DataChecker.isMSSV(mssv)) {
            Toast.makeText(context, "Mã số sinh viên không hợp lệ !", Toast.LENGTH_SHORT).show();
        } else /*if (seatInfo != null)*/ {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_pattern), Locale.getDefault());
            String scanTimeString = dateFormat.format(date);
            MSSVInfo mssvInfo = new MSSVInfo(mssv,
                    scanTimeString);
            MssvFirebaseManager mssvFirebaseManager = MssvFirebaseManager.getInstance();
            mssvFirebaseManager.addMSSVListIntoFirebase(mssvInfo);
            viewBinding.editTextText.setText("");
        }
    }

    //constructor
    public InputFragment() {
    }
}
