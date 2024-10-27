package com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeshanelahi.barcodescannerandcameraxdemo.R;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.FragmentQueueListBinding;

import java.util.ArrayList;

public class QueueListFragment extends Fragment {
    private FragmentQueueListBinding fragmentBinding;
    private ArrayList<String> mssvList;
    private QueueListAdapter queueListAdapter;
    public QueueListFragment() {
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
        queueListAdapter = new QueueListAdapter(mssvList);
        fragmentBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Drawable oDrawable = ContextCompat.getDrawable(getContext(), R.drawable.divider_line);
        InsetDrawable insetDrawable = new InsetDrawable(oDrawable, 0, 10, 0, 0);
        //
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(insetDrawable);
        fragmentBinding.recyclerView.addItemDecoration(dividerItemDecoration);
        fragmentBinding.recyclerView.setAdapter(queueListAdapter);
        setUpViewEvents();
        return view;
    }

    private void setUpViewEvents() {

    }
}
