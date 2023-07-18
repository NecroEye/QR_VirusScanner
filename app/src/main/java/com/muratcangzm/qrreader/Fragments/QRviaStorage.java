package com.muratcangzm.qrreader.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.muratcangzm.qrreader.databinding.QrStorageBinding;

public class QRviaStorage extends Fragment {

    private static final int STORAGE_REQUEST_CODE = 101;

    private QrStorageBinding binding;

    public QRviaStorage() {
        //Empty Constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = QrStorageBinding.inflate(getLayoutInflater(), container, false);


        binding.pickQRfromGallery.setOnClickListener(view -> {

            if (!checkSelfPermission()) {
                requestStoragePermission();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private ActivityResultLauncher<String> StoragePermissionRequest = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {

                if (isGranted) {
                    //Storage izni alındı
                } else {
                    //Storage izni verilmedi

                }

            });

    private void requestStoragePermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

            showExplanationDialog();

        } else {
            StoragePermissionRequest.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

        }
    }

    private boolean checkSelfPermission() {

        return ContextCompat.
                checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void showExplanationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("İzin Gerekli")
                .setMessage("Bu özelliği kullanmak için storage iznini vermelisiniz.")
                .setPositiveButton("İzin ver", (dialog, which) -> {

                    requestStoragePermission();

                })
                .setNegativeButton("İzin verme", (dialog, which) -> {


                })
                .show();


    }

}
