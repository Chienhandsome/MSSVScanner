package com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zeeshanelahi.barcodescannerandcameraxdemo.R;

public class QueueListFragment extends Fragment {
    public QueueListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue_list, container, false);
        setUpViewEvents();
        return view;
    }

    private void setUpViewEvents() {

    }
}
