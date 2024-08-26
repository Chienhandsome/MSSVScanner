package com.zeeshanelahi.barcodescannerandcameraxdemo.view;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.ActivitySettingBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.DialogChangeLinkServerBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.SharedPreferencesHelper;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.StringValue;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.SeatRepository;

import java.util.Objects;

public class SettingActivity extends AppCompatActivity {
    private final String TAG = "SettingActivity";
    private ActivitySettingBinding viewBinding;
    private SeatRepository seatRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        viewBinding.linkServerTV.setText(SharedPreferencesHelper.getInstance(this).getString(StringValue.LINK_SERVER_KEY, StringValue.LINK_SERVER_DEFAULT));
        setUpViewEvents();

        seatRepository = new SeatRepository(this);
        seatRepository.loadSeats(seats -> {
            runOnUiThread(() -> renderSeatInfo(seats.size()));
            return null;
        });
    }

    private void renderSeatInfo(int seatCount){
        String result = "Have " + seatCount +" seats";
        viewBinding.seatListStatus.setText(result);
    }

    private void setUpViewEvents() {
        viewBinding.backButton.setOnClickListener(v -> finish());

        viewBinding.changeButton.setOnClickListener(v -> displayDialog());

        viewBinding.changeButtonFetchSeatList.setOnClickListener(view -> {
//            Toast.makeText(this, "fetching", Toast.LENGTH_SHORT).show();
            seatRepository.loadSeats(seats -> {
            Toast.makeText(this, "Fetch Done", Toast.LENGTH_SHORT).show();
            return null;
            });
        });
    }

    private void displayDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogChangeLinkServerBinding dialogBinding = DialogChangeLinkServerBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();

        dialogBinding.cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.addButton.setOnClickListener(v -> {
            onChangeServerLink(dialogBinding.editTextText.getText().toString());
            dialog.dismiss();
        });

        dialogBinding.pasteButton.setOnClickListener(v -> {
            dialogBinding.editTextText.setText("");
            dialogBinding.editTextText.setText(getClipboardContent());
        });
    }

    private String getClipboardContent() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.hasPrimaryClip() && Objects.requireNonNull(clipboard.getPrimaryClip()).getItemCount() > 0) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            return item.getText().toString();
        }
        return "";
    }

    void onChangeServerLink(String link) {
        viewBinding.linkServerTV.setText(link);
        SharedPreferencesHelper.getInstance(this).saveString(StringValue.LINK_SERVER_KEY, link);
    }
}
