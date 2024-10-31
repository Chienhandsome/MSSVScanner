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
import com.zeeshanelahi.barcodescannerandcameraxdemo.SendMessageCallback;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.FragmentInputCodeBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.InternetBroadCastReceiver;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.enities.MSSVInfo;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.firebase.MssvFirebaseManager;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.MssvQueueManager;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.ServerInteractor;
import com.zeeshanelahi.barcodescannerandcameraxdemo.utils.DataChecker;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InputFragment extends Fragment {
    private String TAG = "InputFragment";
    private FragmentInputCodeBinding viewBinding;
    private Context context;
    private SendMessageCallback callback = new SendMessageCallback() {
        @Override
        public void onMessageSentSucced() {
        }

        @Override
        public void onMessageFailed(String mssv) {
            MssvQueueManager.Companion.getInstance(context).addMssvToQueue(mssv);
            Toast.makeText(context, "Gửi thất bại\nThử kiểm tra link server !", Toast.LENGTH_SHORT).show();
        }
    };

    //constructor
    public InputFragment() {
    }

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
            if (InternetBroadCastReceiver.getInstance().isNetWorkAvailable(context)) {
                onConnectToInternet(mssv);
            } else {
                onCannotConnectToInternet(mssv);
            }
            viewBinding.editTextText.setText("");
        }
    }

    private void onConnectToInternet(String mssv) {
        try {
            // Send mssv to server
            ServerInteractor.getInstance(requireActivity()).sendMessageToServer(requireActivity(), mssv, callback);

            // Send mssv to Firebase
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.DATE_PATTERN), Locale.getDefault());
            String scanTimeString = dateFormat.format(date);
            MSSVInfo mssvInfo = new MSSVInfo(mssv, scanTimeString);
            Log.d(TAG, "dialogConfirm: true");
            MssvFirebaseManager.getInstance().addMSSVListIntoFirebase(mssvInfo);

        } catch (JSONException e) {
//            Toast.makeText(requireActivity(), "Failed to send message " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "dialogConfirm: " + e.getMessage());
        }
    }

    private void onCannotConnectToInternet(String mssv) {
        Toast.makeText(context, "Không có kết nối internet\nMã số sinh viên đã được lưu vào danh sách chờ", Toast.LENGTH_SHORT).show();
        MssvQueueManager.Companion.getInstance(context).addMssvToQueue(mssv);
    }
}
