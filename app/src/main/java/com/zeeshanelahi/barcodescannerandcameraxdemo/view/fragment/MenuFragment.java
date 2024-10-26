package com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zeeshanelahi.barcodescannerandcameraxdemo.R;
import com.zeeshanelahi.barcodescannerandcameraxdemo.SendMessageCallback;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.ActivityMenuBinding;

import java.io.Serializable;

public class MenuFragment extends Fragment implements Serializable {
    private String TAG = "MenuActivity";
    private ActivityMenuBinding viewBinding;
    private Context context;
    //private SeatRepository seatRepository;

    private SendMessageCallback callback = new SendMessageCallback() {
        @Override
        public void onMessageSentSucced() {
        }

        @Override
        public void onMessageFailed(String mssv) {
//            mssvQueueManager.addMssvToQueue(mssv);
//            MssvQueueManager.Companion.getInstance(Menu.this).addMssvToQueue(mssv);
//            String queue = SharedPreferencesHelper.getInstance(Menu.this).getString("mssv_queue", " ");
//            viewBinding.queueTv.setText(queue);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public MenuFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_menu, container, false);
        viewBinding = ActivityMenuBinding.bind(view);
        context = getContext();

        return view;
    }

    private void setUpviewEvents() {
        viewBinding.buttonQuet.setOnClickListener(v -> {
            //showDialog();
        });

        viewBinding.buttonNhapMa.setOnClickListener(v -> {
            //showDialogInputCode();
        });
    }

}
