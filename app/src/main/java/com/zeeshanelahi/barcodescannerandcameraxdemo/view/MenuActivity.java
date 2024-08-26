package com.zeeshanelahi.barcodescannerandcameraxdemo.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zeeshanelahi.barcodescannerandcameraxdemo.SendMessageCallback;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.ActivityMenuBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.DialogInputCodeBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.InternetBroadCastReceiver;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.SharedPreferencesHelper;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.MssvQueueManager;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.SeatRepository;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.ServerInteractor;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.WaitingQueue;
import com.zeeshanelahi.barcodescannerandcameraxdemo.utils.DataChecker;

import org.json.JSONArray;
import org.json.JSONException;

public class MenuActivity extends AppCompatActivity {
    private String TAG = "MenuActivity";
    private ActivityMenuBinding viewBinding;
    private SeatRepository seatRepository;
    private long delayTime = 5000;
    private MssvQueueManager mssvQueueManager;
    private SendMessageCallback callback = new SendMessageCallback() {
        @Override
        public void onMessageSentSucced() {
        }

        @Override
        public void onMessageFailed(String mssv) {
            mssvQueueManager.addMssvToQueue(mssv);
            String queue = SharedPreferencesHelper.getInstance(MenuActivity.this).getString("mssv_queue", " ");
            viewBinding.queueTv.setText(queue);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        InternetBroadCastReceiver.getInstance().activeBroadCast(this);
        setUpviewEvents();

        mssvQueueManager = new MssvQueueManager(this);

        seatRepository = new SeatRepository(this);
        seatRepository.loadSeats(seats -> {
            Log.d(TAG, "onCreate: "+ seats);
            return null;
        });


//        selfCheck();
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewBinding.queueTv.setText(SharedPreferencesHelper.getInstance(MenuActivity.this).getString("mssv_queue", " "));
    }

    private void setUpviewEvents() {
        String queue = SharedPreferencesHelper.getInstance(this).getString("mssv_queue", " ");
        viewBinding.queueTv.setText(queue);
        viewBinding.buttonQuet.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, BarcodeScannerActivity.class);
            activityResultLauncher.launch(intent);
        });
        viewBinding.settingButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, SettingActivity.class);
            startActivity(intent);
        });
        viewBinding.buttonNhapMa.setOnClickListener(v -> showInputMSSVDialog());
        viewBinding.pushQueueButton.setOnClickListener(v -> {
            if (InternetBroadCastReceiver.getInstance().isNetWorkAvailable(MenuActivity.this)){
                mssvQueueManager.processQueue();
                SharedPreferencesHelper.getInstance(this).removeValue("mssv_queue");
                viewBinding.queueTv.setText("");
            }
        });
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
            seatRepository = new SeatRepository(this);
            String seatInfo = seatRepository.getSeatInfo(mssv);

            if (mssv.isEmpty()){
                Toast.makeText(this, "Vui lòng nhập mã số sinh viên", Toast.LENGTH_SHORT).show();
            }
            else if (!DataChecker.isMSSV(mssv)) {
                Toast.makeText(this, "Mã số sinh viên không hợp lệ !", Toast.LENGTH_SHORT).show();
            } else if (seatInfo != null){
                if (InternetBroadCastReceiver.getInstance().isNetWorkAvailable(this)){
                    try {
                        ServerInteractor.getInstance(this).sendMessageToServer(this, mssv, callback);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Failed to send message "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "dialogConfirm: " + e.getMessage());
                    }

                    dialogBinding.seatTv.setText(seatInfo);
                    dialogBinding.addButton.setOnClickListener(v1 -> dialog.dismiss());
                } else {
                    Toast.makeText(this, "Không có kết nối internet!\nMSSV sẽ được gửi khi có mạng trở lại", Toast.LENGTH_SHORT).show();
                    mssvQueueManager.addMssvToQueue(mssv);
                    //lay data tu shared preferences
                    String queue = SharedPreferencesHelper.getInstance(this).getString("mssv_queue", " ");
                    viewBinding.queueTv.setText(queue);
                    dialog.dismiss();
                }
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

    private void selfCheck() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (InternetBroadCastReceiver.getInstance().isNetWorkAvailable(MenuActivity.this)){
                    try {
                        WaitingQueue.getInstance().submit(MenuActivity.this);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                handler.postDelayed(this, delayTime);
            }
        }, 1000);
    }
}
