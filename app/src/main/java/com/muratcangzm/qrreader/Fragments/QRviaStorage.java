package com.muratcangzm.qrreader.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.muratcangzm.qrreader.R;
import com.muratcangzm.qrreader.databinding.QrStorageBinding;

import java.util.Calendar;
import java.util.List;

public class QRviaStorage extends Fragment {

    private static final int STORAGE_REQUEST_CODE = 101;

    private String rawVal, Type = null;
    private InterstitialAd minterstitialAd;

    private QrStorageBinding binding;
    private Uri imageUri = null;
    private BarcodeScannerOptions scannerOptions;
    private BarcodeScanner barcodeScanner;


    public QRviaStorage() {
        //Empty Constructor

        //StorageAds
        //ca-app-pub-1436561055108702/6213126615


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = QrStorageBinding.inflate(getLayoutInflater(), container, false);

        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        AdRequest adRequest = new AdRequest.Builder().build();



        scannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        binding.loadingScreen.setVisibility(View.GONE);
        binding.mainScreen.setVisibility(View.VISIBLE);


        binding.rawTypeResult.setMovementMethod(new ScrollingMovementMethod());


        binding.saveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rawVal != null && Type != null) {

                    sendThreeData(Type, rawVal, getTime());

                }

            }
        });

        barcodeScanner = BarcodeScanning.getClient(scannerOptions);

        binding.pickQRfromGallery.setOnClickListener(view -> {

            if (!checkSelfPermission()) {
                requestStoragePermission();
            } else {
                pickImageGallery();
            }

        });

        binding.Scan.setOnClickListener(v -> {


            detectResultFromImage();

            minterstitialAd.load(requireContext(), "ca-app-pub-1436561055108702/7981503859", adRequest, new InterstitialAdLoadCallback() {

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);

                    minterstitialAd = interstitialAd;

                    if(minterstitialAd != null){

                        minterstitialAd.show(requireActivity());
                    }
                    else{
                        Log.d("ads: ", "The interstitial ad wasn't ready yet.");
                    }
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.d("ads: ", loadAdError.toString());
                    minterstitialAd = null;

                }
            });

        });

        binding.copy.setOnClickListener(v -> {

            if(rawVal != null){

                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clipData = ClipData.newPlainText("Veri", rawVal);
                clipboardManager.setPrimaryClip(clipData);
                Snackbar.make(binding.copy, "Kopyalandı", Snackbar.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(requireContext(), "Tarama düzgün yapılmadı.", Toast.LENGTH_SHORT);

            }


        });

        binding.share.setOnClickListener(v -> {

            if(rawVal != null){

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, rawVal);
                startActivity(Intent.createChooser(intent,"ile paylaş"));
            }
            else{
                Toast.makeText(requireContext(), "Tarama düzgün yapılmadı.", Toast.LENGTH_SHORT);
            }


        });

        return binding.getRoot();
    }

    private void detectResultFromImage() {


        try {

            InputImage inputImage = InputImage.fromFilePath(requireContext(), imageUri);

            Task<List<Barcode>> barcodeResult = barcodeScanner.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {


                            extractBarcodeQRCodeInfo(barcodes);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(requireContext(), "İşlem başarısız. Tekrar Deneyiniz", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.getStackTrace();
            Log.e("Error on StorageFragment: ", e.getMessage());
            Toast.makeText(requireContext(), "İşlem başarısız. Tekrar Deneyiniz", Toast.LENGTH_SHORT).show();

        }

    }

    @SuppressLint("SetTextI18n")
    private void extractBarcodeQRCodeInfo(List<Barcode> barcodes) {

        for (Barcode barcode : barcodes) {

            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();


            String rawValue = barcode.getRawValue();
            Log.d("ExtractBarCodeInfo: ", "Raw Value: " + rawValue);

            int ValueType = barcode.getValueType();

            switch (ValueType) {

                case Barcode.TYPE_WIFI: {

                    Barcode.WiFi typeWifi = barcode.getWifi();

                    String ssid = "" + typeWifi.getSsid();
                    String password = "" + typeWifi.getPassword();
                    String encryptionType = "" + typeWifi.getEncryptionType();

                    //Log da görüntüleme
                    Log.d("Result: ", "ssid: " + ssid);
                    Log.d("Result: ", "password: " + password);
                    Log.d("Result: ", "encryptionType: " + encryptionType);
                    binding.additionalLayout.setVisibility(View.VISIBLE);


                }
                break;

                case Barcode.TYPE_URL: {

                    Barcode.UrlBookmark type_url = barcode.getUrl();

                    String title = "" + type_url.getTitle();
                    String url = "" + type_url.getUrl();

                    Log.d("Result: ", "Type: URL");
                    Log.d("Result: ", "title: " + title);
                    Log.d("Result: ", "url: " + url);


                    binding.typeTextResult.setText("URL");
                    binding.rawTypeResult.setText(url);
                    binding.saveList.setVisibility(View.VISIBLE);
                    binding.additionalLayout.setVisibility(View.VISIBLE);

                    Type = "URL";
                    rawVal = url;

                    binding.loadingScreen.setVisibility(View.VISIBLE);
                    binding.mainScreen.setVisibility(View.GONE);
                    QRviaCamera.checkUrl(url, binding.mainScreen, binding.loadingScreen);
                    QRviaCamera.basicGet();


                }
                break;

                case Barcode.TYPE_EMAIL: {

                    Barcode.Email email = barcode.getEmail();

                    String address = "" + email.getAddress();
                    String body = "" + email.getBody();
                    String subject = "" + email.getSubject();

                    Log.d("Result: ", "Type: E-mail");
                    Log.d("Result: ", "Address: " + address);
                    Log.d("Result: ", "subject: " + subject);


                    binding.saveList.setVisibility(View.INVISIBLE);
                    binding.additionalLayout.setVisibility(View.VISIBLE);


                }
                break;

                case Barcode.TYPE_CONTACT_INFO: {

                    Barcode.ContactInfo contactInfo = barcode.getContactInfo();

                    String title = "" + contactInfo.getTitle();
                    String organizer = "" + contactInfo.getOrganization();
                    String name = "" + contactInfo.getName().getFirst() + " " + contactInfo.getName().getLast();
                    String phone = "" + contactInfo.getPhones().get(0).getNumber();

                    Log.d("Result: ", "Type: Contact-Info");
                    Log.d("Result: ", "Type: title" + title);
                    Log.d("Result: ", "Type: organizer" + organizer);
                    Log.d("Result: ", "Type: name" + name);
                    Log.d("Result: ", "Type: phone" + phone);

                    //  binding.informationText.setText("Türü: İletişim Bilgisi \nBaşlık: " + title + "\nDüzenleyici: " + organizer +
                    //        "\nİsmi: " + name + "\nTelefon: " + phone + "\nSaf Değeri: " + rawValue);

                    binding.saveList.setVisibility(View.INVISIBLE);
                    binding.additionalLayout.setVisibility(View.VISIBLE);


                }
                break;


                case Barcode.TYPE_GEO: {

                    Barcode.GeoPoint geoPoint = barcode.getGeoPoint();

                    Double geoLat = geoPoint.getLat();
                    Double geoLng = geoPoint.getLng();

                    Log.d("Result: ", "Type: Geo");
                    Log.d("Result: ", "Type: geoLat" + geoLat);
                    Log.d("Result: ", "Type: geoLng" + geoLng);

                    binding.additionalLayout.setVisibility(View.VISIBLE);

                }

                break;

                default: {
                    binding.typeTextResult.setText("Ürün değeri");
                    binding.rawTypeResult.setText(rawValue);
                    binding.saveList.setVisibility(View.VISIBLE);
                    binding.additionalLayout.setVisibility(View.VISIBLE);

                    Type = "Ürün";
                    rawVal = rawValue;

                }

            }

        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {


                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d("StorageFragment: ", "ActivityResult: " + imageUri);
                        binding.pickedBarkod.setImageURI(imageUri);

                        if (imageUri != null) {
                            binding.Scan.setVisibility(View.VISIBLE);
                        } else {
                            binding.Scan.setVisibility(View.INVISIBLE);
                        }

                    } else {
                        Snackbar.make(binding.pickQRfromGallery, "İşlem iptal edildi", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

    private final ActivityResultLauncher<String> StoragePermissionRequest = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {

                if (isGranted) {
                    //Storage izni alındı
                    pickImageGallery();

                } else {
                    //Storage izni verilmedi

                    try{

                        Snackbar.make(binding.pickQRfromGallery, "İzin Gerekli", Snackbar.LENGTH_INDEFINITE)
                                .setAction("İzinleri Yönet", v -> {

                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    requireActivity().startActivity(intent);

                                }).setBackgroundTint(Color.BLUE).show();

                    }
                    catch (NullPointerException e){
                        e.getStackTrace();
                        Log.d("Hata: " , "" + e.getMessage());
                    }


                }

            });

    private void requestStoragePermission() {

            StoragePermissionRequest.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    private boolean checkSelfPermission() {

        return ContextCompat.
                checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void pickImageGallery() {


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

    }

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

        Snackbar.make(binding.saveList, "Başarılı bir şekilde eklendi.", Snackbar.LENGTH_SHORT).show();

    }


}
