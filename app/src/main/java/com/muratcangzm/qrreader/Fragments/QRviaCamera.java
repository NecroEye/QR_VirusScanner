package com.muratcangzm.qrreader.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.muratcangzm.qrreader.CaptureAct;
import com.muratcangzm.qrreader.R;
import com.muratcangzm.qrreader.databinding.QrCameraBinding;

import java.util.Calendar;

public class QRviaCamera extends Fragment {


    private static final int CAMERA_REQUEST_CODE = 100;

    private String rawVal, Type = null;
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

            } else {
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

    private void Scanner() {

        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setPrompt("Fener için ses açma tuşuna basın");
        scanOptions.setBeepEnabled(true);
        scanOptions.setOrientationLocked(true);
        scanOptions.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(scanOptions);

    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {

        if (result.getContents() != null) {

            final Dialog dialog = new Dialog(requireContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottomsheetlayout);


            TextView barcodeTextView = dialog.findViewById(R.id.barcodeTextView);

            LinearLayout save = dialog.findViewById(R.id.layoutSave);
            LinearLayout share = dialog.findViewById(R.id.layoutShare);
            LinearLayout scanner = dialog.findViewById(R.id.layoutScan);
            LinearLayout browser = dialog.findViewById(R.id.layoutBrowser);

            barcodeTextView.setMovementMethod(new ScrollingMovementMethod());
            barcodeTextView.setText(result.getContents());


            if(result.getContents().startsWith("www") || result.getContents().startsWith("http")){

                 Type = "URL";
                 scanner.setVisibility(View.VISIBLE);
                 browser.setVisibility(View.VISIBLE);


            }
            else{
                Type = "Ürün";

                scanner.setVisibility(View.GONE);
                browser.setVisibility(View.GONE);

            }

            rawVal = result.getContents();

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Type != null && rawVal != null) {

                      sendThreeData(Type, rawVal, getTime());
                      dialog.dismiss();


                    }
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, result.getContents());

                    startActivity(Intent.createChooser(intent, "ile paylaş"));

                }
            });

            scanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // it will empty until virustotal api is ready
                }
            });

            browser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Uri webUri = Uri.parse(result.getContents());

                    Intent goToLink = new Intent(Intent.ACTION_VIEW, webUri);
                    startActivity(goToLink);


                }
            });

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);


        } else {

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


    private String getTime() {

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        String currentTimeString = String.format("%02d:%02d", hour, minute);

        return currentTimeString + " " + day + "/" + month + "/" + year;
    }

    private void sendThreeData(final String type, final String rawValue, final String time) {

        QRList fragmentList = new QRList();

        Bundle bundle = new Bundle();

        bundle.putString("KEY_TYPE", type);
        bundle.putString("KEY_RAW", rawValue);
        bundle.putString("KEY_TIME", time);

        fragmentList.setArguments(bundle);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.Fragment_container, fragmentList);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
        Snackbar.make(binding.scanQR,"Başarılı bir şekilde eklendi.", Snackbar.LENGTH_SHORT).show();
    }

}
