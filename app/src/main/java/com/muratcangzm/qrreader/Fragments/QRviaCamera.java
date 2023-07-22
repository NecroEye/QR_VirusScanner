package com.muratcangzm.qrreader.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.muratcangzm.qrreader.CaptureAct;
import com.muratcangzm.qrreader.R;
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
            else{
                Scanner();
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
                    Scanner();
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

    private void Scanner(){

        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setPrompt("Fener için ses açma tuşuna basın");
        scanOptions.setBeepEnabled(true);
        scanOptions.setOrientationLocked(true);
        scanOptions.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(scanOptions);

    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->{

        if(result.getContents() != null){

        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);



            TextView barcodeTextView  = dialog.findViewById(R.id.barcodeTextView);
            LinearLayout save = dialog.findViewById(R.id.layoutSave);
            LinearLayout share = dialog.findViewById(R.id.layoutShare);

            barcodeTextView.setMovementMethod(new ScrollingMovementMethod());
            barcodeTextView.setText(result.getContents());

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, result.getContents());

                    startActivity(Intent.createChooser(intent,"ile paylaş"));

                }
            });

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);


        }
        else{

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Sonuç")
            .setMessage("Herhangi bir sonuç bulunamadı")
            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            }).show();
        }

    });

}
