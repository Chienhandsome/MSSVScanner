//package com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Window;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.zeeshanelahi.barcodescannerandcameraxdemo.SendMessageCallback;
//import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.ActivityMenuBinding;
//import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.DialogInputCodeBinding;
//import com.zeeshanelahi.barcodescannerandcameraxdemo.model.InternetBroadCastReceiver;
//import com.zeeshanelahi.barcodescannerandcameraxdemo.model.SharedPreferencesHelper;
//import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.MssvQueueManager;
//import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.ServerInteractor;
//import com.zeeshanelahi.barcodescannerandcameraxdemo.utils.DataChecker;
//import com.zeeshanelahi.barcodescannerandcameraxdemo.utils.StringHandler;
//import com.zeeshanelahi.barcodescannerandcameraxdemo.view.SettingActivity;
//
//import org.json.JSONException;
//
//import java.io.Serializable;
//
//public class MenuActivity extends AppCompatActivity  implements Serializable {
//    private String TAG = "MenuActivity";
//    private ActivityMenuBinding viewBinding;
//    //private SeatRepository seatRepository;
//
//    private SendMessageCallback callback = new SendMessageCallback() {
//        @Override
//        public void onMessageSentSucced() {
//        }
//
//        @Override
//        public void onMessageFailed(String mssv) {
////            mssvQueueManager.addMssvToQueue(mssv);
//            MssvQueueManager.Companion.getInstance(MenuActivity.this).addMssvToQueue(mssv);
//            String queue = SharedPreferencesHelper.getInstance(MenuActivity.this).getString("mssv_queue", " ");
//            viewBinding.queueTv.setText(queue);
//        }
//    };
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        viewBinding = ActivityMenuBinding.inflate(getLayoutInflater());
//        setContentView(viewBinding.getRoot());
//        InternetBroadCastReceiver.getInstance().activeBroadCast(this);
//        setUpviewEvents();
//
//        //mssvQueueManager = new MssvQueueManager(this);
//
////        seatRepository = new SeatRepository(this);
////        seatRepository.loadSeats(seats -> {
////            Log.d(TAG, "onCreate: "+ seats);
////            return null;
////        });
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        String queue = SharedPreferencesHelper.getInstance(this).getString("mssv_queue", " ");
//        viewBinding.queueTv.setText(StringHandler.formatString(queue));
//    }
//
//    private void setUpviewEvents() {
//        String queue = SharedPreferencesHelper.getInstance(this).getString("mssv_queue", " ");
//        viewBinding.queueTv.setText(queue);
//        viewBinding.buttonQuet.setOnClickListener(v -> {
//            Intent intent = new Intent(MenuActivity.this, ScanFragment.class);
//            activityResultLauncher.launch(intent);
//        });
//        viewBinding.settingButton.setOnClickListener(v -> {
//            Intent intent = new Intent(MenuActivity.this, SettingActivity.class);
//            startActivity(intent);
//        });
//        viewBinding.buttonNhapMa.setOnClickListener(v -> showInputMSSVDialog());
//        viewBinding.pushQueueButton.setOnClickListener(v -> {
//            if (InternetBroadCastReceiver.getInstance().isNetWorkAvailable(MenuActivity.this)){
////                mssvQueueManager.processQueue();
//                MssvQueueManager.Companion.getInstance(MenuActivity.this).processQueue();
//                SharedPreferencesHelper.getInstance(this).removeValue("mssv_queue");
//                viewBinding.queueTv.setText("");
//            }
//        });
//    }
//
//    private void showInputMSSVDialog() {
//        Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        DialogInputCodeBinding dialogBinding = DialogInputCodeBinding.inflate(getLayoutInflater());
//        dialog.setContentView(dialogBinding.getRoot());
//        dialog.show();
//
//        dialogBinding.cancelButton.setOnClickListener(v -> {
//            dialog.dismiss();
//        });
//
//        dialogBinding.addButton.setOnClickListener(v -> {
//            String mssv = dialogBinding.editTextText.getText().toString();
////            seatRepository = new SeatRepository(this);
////            String seatInfo = seatRepository.getSeatInfo(mssv);
//
//            if (mssv.isEmpty()){
//                Toast.makeText(this, "Vui lòng nhập mã số sinh viên", Toast.LENGTH_SHORT).show();
//            }
//            else if (!DataChecker.isMSSV(mssv)) {
//                Toast.makeText(this, "Mã số sinh viên không hợp lệ !", Toast.LENGTH_SHORT).show();
//            } else /*if (seatInfo != null)*/ {
//                if (InternetBroadCastReceiver.getInstance().isNetWorkAvailable(this)){
//                    conConnectToInternet(mssv);
//                    //dialogBinding.seatTv.setText(seatInfo);
//                    //dialogBinding.addButton.setOnClickListener(v1 -> dialog.dismiss());
//                    dialog.dismiss();
//                } else {
//                    onCannotConnectToInternet(mssv);
//                    dialog.dismiss();
//                }
//            }
//        });
//    }
//
//    private void conConnectToInternet(String mssv) {
//        try {
//            ServerInteractor.getInstance(this).sendMessageToServer(this, mssv, callback);
//        } catch (JSONException e) {
//            Toast.makeText(this, "Failed to send message "+e.getMessage(), Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "dialogConfirm: " + e.getMessage());
//        }
//    }
//
//    private void onCannotConnectToInternet(String mssv) {
//        Toast.makeText(this, "Không có kết nối internet!\nMSSV sẽ được gửi khi có mạng trở lại", Toast.LENGTH_SHORT).show();
//        MssvQueueManager.Companion.getInstance(MenuActivity.this).addMssvToQueue(mssv);
//        //lay data tu shared preferences
//        String queue = SharedPreferencesHelper.getInstance(this).getString("mssv_queue", " ");
//        viewBinding.queueTv.setText(StringHandler.formatString(queue));
//    }
//
//    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    // Handle the returned result
//                    showInputMSSVDialog();
//                }
//            }
//    );
//}
