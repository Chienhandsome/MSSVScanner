package com.zeeshanelahi.barcodescannerandcameraxdemo.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mlkit.common.MlKitException;
import com.zeeshanelahi.barcodescannerandcameraxdemo.R;
import com.zeeshanelahi.barcodescannerandcameraxdemo.SendMessageCallback;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.InternetBroadCastReceiver;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.SharedPreferencesHelper;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.MssvQueueManager;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.SeatRepository;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.ServerInteractor;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.barcodescanner.CameraXViewModel;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.barcodescanner.ExchangeScannedData;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.barcodescanner.VisionImageProcessor;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.barcodescanner.BarcodeScannerProcessor;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.ActivityBarcodeScannerBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.WaitingQueue;
import com.zeeshanelahi.barcodescannerandcameraxdemo.utils.DataChecker;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class BarcodeScannerActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, ExchangeScannedData {
    private static final String TAG = "BarcodeScannerActivity";
    private static final int PERMISSION_REQUESTS = 1;
    private ActivityBarcodeScannerBinding binding;
    public boolean dialogIsShowing = false;
    @Nullable
    private ProcessCameraProvider cameraProvider;
    @Nullable
    private Preview previewUseCase;
    @Nullable
    private ImageAnalysis analysisUseCase;
    @Nullable
    private VisionImageProcessor imageProcessor;
    private boolean needUpdateGraphicOverlayImageSourceInfo;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;//setting front or back camera
    private CameraSelector cameraSelector;
    private static final String STATE_SELECTED_MODEL = "selected_model";
    private static final String STATE_LENS_FACING = "lens_facing";
    private SeatRepository seatRepository;
    //private MssvQueueManager mssvQueueManager;
    private SendMessageCallback callback = new SendMessageCallback() {
        @Override
        public void onMessageSentSucced() {
        }

        @Override
        public void onMessageFailed(String mssv) {
            //mssvQueueManager.addMssvToQueue(mssv);
            MssvQueueManager.Companion.getInstance(BarcodeScannerActivity.this).addMssvToQueue(mssv);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            lensFacing = savedInstanceState.getInt(STATE_LENS_FACING, lensFacing);
        }
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        binding = ActivityBarcodeScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //mssvQueueManager = new MssvQueueManager(this);

        new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(CameraXViewModel.class)
                .getProcessCameraProvider()
                .observe(
                        this,
                        provider -> {
                            cameraProvider = provider;
                            //permission check
                            if (allPermissionsGranted()) {
                                bindAllCameraUseCases();
                            }
                        });

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }

        setUpViewEvents();
    }

    private void setUpViewEvents() {
        binding.backButton.setOnClickListener(v -> {
            finish();
        });

        binding.buttonNhapMa.setOnClickListener(v -> {
            //goi menu mo input dialog
            setResult(Activity.RESULT_OK);
            finish();
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(STATE_LENS_FACING, lensFacing);
    }

    @Override
    public void onResume() {
        super.onResume();
        bindAllCameraUseCases();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }

    private void bindAllCameraUseCases() {
        bindPreviewUseCase();
        bindAnalysisUseCase();
    }

    private void bindPreviewUseCase() {

        if (cameraProvider == null) {
            return;
        }
        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        previewUseCase = new Preview.Builder().build();
        previewUseCase.setSurfaceProvider(binding.previewView.createSurfaceProvider());
        cameraProvider.bindToLifecycle(/* lifecycleOwner= */ BarcodeScannerActivity.this, cameraSelector, previewUseCase);
    }

    private void bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return;
        }
        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }
        if (imageProcessor != null) {
            imageProcessor.stop();
        }

        try {
            Log.i(TAG, "Using Barcode Detector Processor");
            imageProcessor = new BarcodeScannerProcessor(this, this);
        } catch (Exception e) {
            Log.e(TAG, "Can not create image processor.", e);
            Toast.makeText(
                    getApplicationContext(),
                    "Can not create image processor: " + e.getLocalizedMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        analysisUseCase = builder.build();

        needUpdateGraphicOverlayImageSourceInfo = true;
        analysisUseCase.setAnalyzer(
                // imageProcessor.processImageProxy will use another thread to run the detection underneath,
                // thus we can just runs the analyzer itself on main thread.
                ContextCompat.getMainExecutor(this),
                imageProxy -> {
                    if (needUpdateGraphicOverlayImageSourceInfo) {
                        boolean isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT;
                        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                        if (rotationDegrees == 0 || rotationDegrees == 180) {
                            binding.graphicOverlay.setImageSourceInfo(
                                    imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
                        } else {
                            binding.graphicOverlay.setImageSourceInfo(
                                    imageProxy.getHeight(), imageProxy.getWidth(), isImageFlipped);
                        }
                        needUpdateGraphicOverlayImageSourceInfo = false;
                    }
                    try {
                        imageProcessor.processImageProxy(imageProxy, binding.graphicOverlay);
                    } catch (   MlKitException e) {
                        Log.e(TAG, "Failed to process image. Error: " + e.getLocalizedMessage());
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, analysisUseCase);
    }

    //return list of required permissions
    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    //check if all permissions are granted
    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    //get needed permissions
    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            bindAllCameraUseCases();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return false;

        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return true;
    }
    @Override
    public void sendScannedCode(String mssv) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (DataChecker.isMSSV(mssv) && !dialogIsShowing) {
                    dialogIsShowing = true;
                    binding.barcodeRawValue.setText(mssv);
                    binding.resultContainer.setVisibility(View.VISIBLE);
                    dialogConfirm(mssv);
            }
        });
    }

    public void dialogConfirm(String mssv) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);

        Button btXacNhan = dialog.findViewById(R.id.btXacNhan);
        Button btHuy = dialog.findViewById(R.id.btHuy);
        TextView message = dialog.findViewById(R.id.message);

//        seatRepository = new SeatRepository(this);
//        String seatInfo = seatRepository.getSeatInfo(mssv);
//        boolean isReadyTosend;

//        if (seatInfo == null) {
//            isReadyTosend = false;
//            message.setText("N/A\n"+mssv);
//            message.setBackgroundColor(Color.YELLOW);
//        } else if (seatInfo.isEmpty()) {
//            isReadyTosend = false;
//            message.setText("Not Found\n"+mssv);
//            message.setBackgroundColor(Color.RED);
//        } else {
//            message.setText(seatInfo+"\n"+mssv);
//            message.setBackgroundColor(Color.GREEN);
//            isReadyTosend = true;
//        }

        dialog.show();

        btXacNhan.setOnClickListener(view -> {
//            if (isReadyTosend){
                if (InternetBroadCastReceiver.getInstance().isNetWorkAvailable(this)){
                    onConnectToInternet(mssv);
                } else {
                    onCannotConnectToInternet(mssv);
                }
//            }
            dialogIsShowing = false;
            dialog.dismiss();
        });

        btHuy.setOnClickListener(view -> {
            dialogIsShowing = false;
            dialog.dismiss();
        });
    }

    private void onConnectToInternet(String mssv) {
        try {
            ServerInteractor.getInstance(this).sendMessageToServer(this, mssv, callback);
        } catch (JSONException e) {
            Toast.makeText(this, "Failed to send message "+e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "dialogConfirm: " + e.getMessage());
        }
    }

    private void onCannotConnectToInternet(String mssv) {
        Toast.makeText(this, "Không có kết nối internet!\nMSSV sẽ được gửi khi có mạng trở lại", Toast.LENGTH_SHORT).show();
        MssvQueueManager.Companion.getInstance(BarcodeScannerActivity.this).addMssvToQueue(mssv);
    }
}