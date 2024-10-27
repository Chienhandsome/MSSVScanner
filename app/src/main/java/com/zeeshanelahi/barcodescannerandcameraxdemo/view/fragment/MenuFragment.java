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
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.StringValue;
import com.zeeshanelahi.barcodescannerandcameraxdemo.view.main.OnFragmentChangeListener;

import java.io.Serializable;

public class MenuFragment extends Fragment implements Serializable {
    private String TAG = "MenuActivity";
    private ActivityMenuBinding viewBinding;
    private Context context;
    private OnFragmentChangeListener fragmentChangeListener;

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

        setUpviewEvents();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentChangeListener) {
            fragmentChangeListener = (OnFragmentChangeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentChangeListener = null;
    }

    private void setUpviewEvents() {
        viewBinding.buttonQuet.setOnClickListener(v -> {
            if (fragmentChangeListener != null) {
                fragmentChangeListener.onChangeFragment(StringValue.SCAN_FRAGMENT);
            }
        });

        viewBinding.buttonNhapMa.setOnClickListener(v -> {
            if (fragmentChangeListener != null) {
                fragmentChangeListener.onChangeFragment(StringValue.INPUT_FRAGMENT);
            }
        });


    }
}
