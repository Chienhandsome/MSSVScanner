package com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mlkit.common.MlKitException;
import com.zeeshanelahi.barcodescannerandcameraxdemo.R;
import com.zeeshanelahi.barcodescannerandcameraxdemo.SendMessageCallback;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.FragmentScanBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.InternetBroadCastReceiver;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.MssvQueueManager;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.SeatRepository;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo.ServerInteractor;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.barcodescanner.CameraXViewModel;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.barcodescanner.ExchangeScannedData;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.barcodescanner.VisionImageProcessor;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.barcodescanner.BarcodeScannerProcessor;
import com.zeeshanelahi.barcodescannerandcameraxdemo.utils.DataChecker;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ScanFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback, ExchangeScannedData {
    private static final String TAG = "ScanFragment";
    private static final int PERMISSION_REQUESTS = 1;
    private FragmentScanBinding binding;
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
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private CameraSelector cameraSelector;
    private static final String STATE_LENS_FACING = "lens_facing";
    private SeatRepository seatRepository;
    private Context context;
    private SendMessageCallback callback = new SendMessageCallback() {
        @Override
        public void onMessageSentSucced() {
        }

        @Override
        public void onMessageFailed(String mssv) {
            MssvQueueManager.Companion.getInstance(context).addMssvToQueue(mssv);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        if (savedInstanceState != null) {
            lensFacing = savedInstanceState.getInt(STATE_LENS_FACING, lensFacing);
        }
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(CameraXViewModel.class)
                .getProcessCameraProvider()
                .observe(
                        getViewLifecycleOwner(),
                        provider -> {
                            cameraProvider = provider;
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
        });

        binding.buttonNhapMa.setOnClickListener(v -> {
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(STATE_LENS_FACING, lensFacing);
    }

    @Override
    public void onResume() {
        super.onResume();
        bindAllCameraUseCases();
    }

    @Override
    public void onPause() {
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
        previewUseCase.setSurfaceProvider(binding.previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase);
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
            imageProcessor = new BarcodeScannerProcessor(context, this);
        } catch (Exception e) {
            Log.e(TAG, "Can not create image processor.", e);
            Toast.makeText(getActivity(), "Can not create image processor: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        analysisUseCase = builder.build();

        needUpdateGraphicOverlayImageSourceInfo = true;
        analysisUseCase.setAnalyzer(
                ContextCompat.getMainExecutor(getActivity()),
                imageProxy -> {
                    if (needUpdateGraphicOverlayImageSourceInfo) {
                        boolean isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT;
                        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                        if (rotationDegrees == 0 || rotationDegrees == 180) {
                            binding.graphicOverlay.setImageSourceInfo(imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
                        } else {
                            binding.graphicOverlay.setImageSourceInfo(imageProxy.getHeight(), imageProxy.getWidth(), isImageFlipped);
                        }
                        needUpdateGraphicOverlayImageSourceInfo = false;
                    }
                    try {
                        imageProcessor.processImageProxy(imageProxy, binding.graphicOverlay);
                    } catch (MlKitException e) {
                        Log.e(TAG, "Failed to process image. Error: " + e.getLocalizedMessage());
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase);
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), PackageManager.GET_PERMISSIONS);
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

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (isPermissionGranted(requireActivity(), permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (isPermissionGranted(requireActivity(), permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            bindAllCameraUseCases();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
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
        Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);

        Button btXacNhan = dialog.findViewById(R.id.btXacNhan);
        Button btHuy = dialog.findViewById(R.id.btHuy);
        TextView message = dialog.findViewById(R.id.message);

        dialog.show();

        btXacNhan.setOnClickListener(view -> {
            if (InternetBroadCastReceiver.getInstance().isNetWorkAvailable(requireActivity())) {
                onConnectToInternet(mssv);
            } else {
                onCannotConnectToInternet(mssv);
            }
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
            ServerInteractor.getInstance(requireActivity()).sendMessageToServer(requireActivity(), mssv, callback);
        } catch (JSONException e) {
            Toast.makeText(requireActivity(), "Failed to send message " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "dialogConfirm: " + e.getMessage());
        }
    }

    private void onCannotConnectToInternet(String mssv) {
        Toast.makeText(requireActivity(), "Không có kết nối internet!\nMSSV sẽ được gửi khi có mạng trở lại", Toast.LENGTH_SHORT).show();
        MssvQueueManager.Companion.getInstance(context).addMssvToQueue(mssv);
    }
}