package com.zeeshanelahi.barcodescannerandcameraxdemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.ActivityMenuBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.InternetBroadCastReceiver;

public class MenuActivity extends AppCompatActivity {
    private ActivityMenuBinding viewBinding;
    private boolean haveJustOpenedApp = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        InternetBroadCastReceiver.getInstance().activeBroadCast(this);
        setUpviewEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setUpviewEvents() {
        viewBinding.buttonQuet.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, BarcodeScannerActivity.class);
            startActivities(new Intent[]{intent});
        });
        viewBinding.settingButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, SettingActivity.class);
            startActivities(new Intent[]{intent});
        });
    }
}
