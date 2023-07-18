package com.muratcangzm.qrreader.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.muratcangzm.qrreader.databinding.QrCameraBinding;

public class QRviaCamera extends Fragment {


    private static final int CAMERA_REQUEST_CODE = 100;

    private QrCameraBinding binding;

    public QRviaCamera() {
        //Empty Constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = QrCameraBinding.inflate(getLayoutInflater(), container, false);


        binding.scanQR.setOnClickListener(view -> {

            if (!checkSelfPermission()) {
                requestCameraPermission();

            }

        });

        return binding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private ActivityResultLauncher<String> cameraRequestLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {

                if (isGranted) {
                    //Camera izni alındı
                    Toast.makeText(getContext(), "izin verildi", Toast.LENGTH_SHORT).show();
                } else {
                    //Camera izni verilmedi
                    Toast.makeText(getContext(), "izin verilmedi", Toast.LENGTH_SHORT).show();
                }

            });

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            showExplanationDialog();

        } else {
            cameraRequestLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private boolean checkSelfPermission() {

        return ContextCompat.
                checkSelfPermission(requireContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }


    private void showExplanationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("İzin Gerekli")
                .setMessage("Bu özelliği kullanmak için kamera iznini vermelisiniz.")
                .setPositiveButton("İzin ver", (dialog, which) -> {

                    requestCameraPermission();

                })
                .setNegativeButton("İzin verme", (dialog, which) -> {


                })
                .show();


    }

}
