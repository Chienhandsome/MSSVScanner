package com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zeeshanelahi.barcodescannerandcameraxdemo.R;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.FragmentQueueListBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.SharedPreferencesHelper;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.enities.MSSVInfo;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.firebase.MssvFirebaseManager;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private static final String TAG = "QueueListFragment";
    private FragmentQueueListBinding fragmentBinding;
    private ArrayList<MSSVInfo> mssvList;
    private HistoryAdapter historyAdapter;


    public HistoryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mssvList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentBinding = FragmentQueueListBinding.inflate(inflater, container, false);
        View view = fragmentBinding.getRoot();
        historyAdapter = new HistoryAdapter(mssvList);
        fragmentBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Refurbish the separation line
        Drawable oDrawable = ContextCompat.getDrawable(getContext(), R.drawable.divider_line);
        InsetDrawable insetDrawable = new InsetDrawable(oDrawable, 0, 10, 0, 0);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(insetDrawable);
        fragmentBinding.recyclerView.addItemDecoration(dividerItemDecoration);
        fragmentBinding.recyclerView.setAdapter(historyAdapter);

        //Call firebase methods
        MssvFirebaseManager mssvFirebaseManager = MssvFirebaseManager.getInstance();
        mssvFirebaseManager.getMSSVList(mssvList, mssvInfoList -> {
            Log.d(TAG, "onCreateView: " + mssvInfoList.size());
            historyAdapter.notifyItemChanged(mssvInfoList.size() - 1);
        });

        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(getContext());
        updateUIWhenQueueHaveDate(preferencesHelper.getString("mssv_queue", null));

        setUpViewEvents();
        return view;
    }

    private void setUpViewEvents() {

    }

    public void updateUIWhenQueueHaveDate(String dataInQueue) {
        Log.d(TAG, "updateUIWhenQueueHaveDate: " + dataInQueue);
        if (dataInQueue != null) {
            if (dataInQueue.isEmpty()) {
                fragmentBinding.updateBtn.setVisibility(View.GONE);
            }  else {
                fragmentBinding.updateBtn.setVisibility(View.VISIBLE);
            }
        } else fragmentBinding.updateBtn.setVisibility(View.GONE);
    }

}
