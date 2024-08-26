package com.zeeshanelahi.barcodescannerandcameraxdemo.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.ActivityMenuBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.DialogChangeLinkServerBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.DialogInputCodeBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.InternetBroadCastReceiver;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.ServerInteractor;
import com.zeeshanelahi.barcodescannerandcameraxdemo.utils.DataChecker;

import org.json.JSONException;

public class MenuActivity extends AppCompatActivity {
    private ActivityMenuBinding viewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        InternetBroadCastReceiver.getInstance().activeBroadCast(this);
        setUpviewEvents();
    }

    private void setUpviewEvents() {
        viewBinding.buttonQuet.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, BarcodeScannerActivity.class);
            activityResultLauncher.launch(intent);
        });
        viewBinding.settingButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, SettingActivity.class);
            startActivity(intent);
        });
        viewBinding.buttonNhapMa.setOnClickListener(v -> showInputMSSVDialog());
    }

    private void showInputMSSVDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogInputCodeBinding dialogBinding = DialogInputCodeBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();

        dialogBinding.cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.addButton.setOnClickListener(v -> {
            String mssv = dialogBinding.editTextText.getText().toString();
            if (mssv.isEmpty()){
                Toast.makeText(this, "Vui lòng nhập mã số sinh viên", Toast.LENGTH_SHORT).show();
            }
            else if (!DataChecker.isMSSV(mssv)) {
                Toast.makeText(this, "Mã số sinh viên không hợp lệ !", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    ServerInteractor.getInstance(this).sendMessageToServer(this, mssv);
                } catch (JSONException e) {
                    Toast.makeText(this, "Failed to send message "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Handle the returned result
                    showInputMSSVDialog();
                }
            }
    );
}
