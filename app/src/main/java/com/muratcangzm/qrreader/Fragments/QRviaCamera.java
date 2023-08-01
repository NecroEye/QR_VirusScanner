package com.muratcangzm.qrreader.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.muratcangzm.qrreader.CaptureAct;
import com.muratcangzm.qrreader.R;
import com.muratcangzm.qrreader.databinding.QrCameraBinding;

import java.io.IOException;
import java.util.Calendar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class QRviaCamera extends Fragment {


    private static final int CAMERA_REQUEST_CODE = 100;
    private static FragmentActivity activity;
    private String rawVal, Type = null;
    private QrCameraBinding binding;
    private static boolean isSafe = false;
    public static String safety;

    private static boolean checkingUrl;
    private static Uri scannedUrl;
    private static String checkId;
    private static View _mainScreen, _loadingScreen;

    public QRviaCamera() {
        //Empty Constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = QrCameraBinding.inflate(getLayoutInflater(), container, false);


        this.activity = requireActivity();

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
            LinearLayout copy = dialog.findViewById(R.id.layoutCopy);

            barcodeTextView.setMovementMethod(new ScrollingMovementMethod());
            barcodeTextView.setText(result.getContents());


            if (result.getContents().startsWith("www") || result.getContents().startsWith("http")) {

                Type = "URL";
                scanner.setVisibility(View.VISIBLE);
                browser.setVisibility(View.VISIBLE);
                binding.web.setWebViewClient(new WebViewClient());

                WebSettings webSettings = binding.web.getSettings();
                webSettings.setJavaScriptEnabled(true);
                binding.web.loadUrl(result.getContents());

                binding.webLayout.setVisibility(View.VISIBLE);


            } else {
                Type = "Ürün";

                scanner.setVisibility(View.GONE);
                browser.setVisibility(View.GONE);

                binding.webLayout.setVisibility(View.INVISIBLE);

            }

            rawVal = result.getContents();

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Type != null && rawVal != null) {

                        sendDataToFragment(Type, rawVal, getTime());
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

            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                    ClipData clipData = ClipData.newPlainText("Veri", result.getContents());
                    clipboardManager.setPrimaryClip(clipData);
                    dialog.dismiss();
                    Snackbar.make(binding.scanQR, "Kopyalandı", Snackbar.LENGTH_SHORT).show();

                }
            });

            scanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSafe = false;

                    binding.cameraMainScreen.setVisibility(View.GONE);
                    binding.loadingScreen.setVisibility(View.VISIBLE);

                    dialog.dismiss();
                    checkUrl(result.getContents(), binding.cameraMainScreen, binding.loadingScreen);
                    basicGet();
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

    private void sendDataToFragment(final String type, final String rawValue, final String time) {

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
        Snackbar.make(binding.scanQR, "Başarılı bir şekilde eklendi.", Snackbar.LENGTH_SHORT).show();
    }

    public static void basicGet() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://icanhazip.com")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.d("parazit2", e.getMessage());
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                // Handle successful response
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Do something with the response body
                    Log.d("parazit", "" + responseBody);
                }
            }
        });
    }


    public static void urlAnalysisFromId(String id) {

        id = id.split("-")[1];
        Log.d("Url anaylsisID", "id " + id);
        OkHttpClient client = new OkHttpClient();

        String apiKey = "ca51b3756aa20f7f99d75d3ffe38c1d43bd9ee1bab90c72ff597ccf6df317c9c";
        String url = "https://www.virustotal.com/api/v3/urls/" + id;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("x-apikey", apiKey)
                .build();

        // Send the request asynchronously and process the response
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        String responseBodyString = responseBody.string();
                        checkingUrl = false;
                        JsonObject json = new JsonParser().parse(responseBodyString).getAsJsonObject();
                        JsonObject totalVotes = json.get("data").getAsJsonObject().get("attributes").getAsJsonObject().get("last_analysis_stats").getAsJsonObject();
                        final int harmless = totalVotes.get("harmless").getAsInt();
                        final int malicious = totalVotes.get("malicious").getAsInt();
                        final int suspicious = totalVotes.get("suspicious").getAsInt();
                        final int Reputation = json.get("data").getAsJsonObject().get("attributes").getAsJsonObject().get("reputation").getAsInt();


                        activity.runOnUiThread(() -> {
                            _mainScreen.setVisibility(View.VISIBLE);
                            _loadingScreen.setVisibility(View.GONE);
                        });


                        Log.d("Data", String.format("Good %s, Bad %s, Suspicious %s, Reputation %s", harmless, malicious, suspicious, Reputation));


                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Show the appropriate dialog here based on the analysis result
                                if (malicious <= 0 && harmless > 0) {
                                    showSafeDialog(harmless, malicious, suspicious);
                                    safety = "Güvenli";
                                    isSafe = true;
                                } else if (malicious > 0) {
                                    showDangerDialog(harmless, malicious, suspicious);
                                    safety = "Tehlikeli";
                                    isSafe = false;
                                } else if (harmless == 0 && malicious == 0) {
                                    showWarningDialog(harmless, malicious, suspicious);
                                    safety = "Belirsiz";
                                    isSafe = true;
                                } else {
                                    showWarningDialog(harmless, malicious, suspicious);
                                }
                                Log.d("Güvenlik1: ", "" + safety);
                            }
                        });


                        Log.d("parazitnotdead", "Response Body: " + responseBodyString);
                    } else {
                        Log.d("parazit_revamped", "Error: " + response.code() + " " + response.message());
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    activity.runOnUiThread(() -> {
                        _mainScreen.setVisibility(View.VISIBLE);
                        _loadingScreen.setVisibility(View.GONE);
                    });


                    Toast.makeText(activity, "Zaman Aşımına Uğradı Tekrar Deneyin", Toast.LENGTH_SHORT).show();
                } finally {
                    // Close the client after processing the response
                    client.dispatcher().executorService().shutdown();
                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                // Close the client in case of failure
                client.dispatcher().executorService().shutdown();
            }
        });

    }


    public static void checkUrl(String url, View mainScreen, View loadingScreen) {

        _mainScreen = mainScreen;
        _loadingScreen = loadingScreen;
        scannedUrl = Uri.parse(url);

        checkingUrl = true;
        OkHttpClient client = new OkHttpClient();

        // Replace the API key and URL with your own values
        String apiKey = "ca51b3756aa20f7f99d75d3ffe38c1d43bd9ee1bab90c72ff597ccf6df317c9c";
        String url1 = "https://www.virustotal.com/api/v3/urls";

        // Create the request body
        RequestBody requestBody = new FormBody.Builder()
                .add("url", url).build();
        // Create the POST request and add headers
        Request request = new Request.Builder()
                .url(url1)
                .post(requestBody)
                .addHeader("accept", "application/json")
                .addHeader("x-apikey", apiKey)
                .build();

        // Send the request and process the response asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        String responseBodyString = responseBody.string();
                        JsonObject json = new JsonParser().parse(responseBodyString).getAsJsonObject();
                        Log.d("parazit3", "" + responseBodyString);
                        checkId = json.get("data").getAsJsonObject().get("id").getAsString();
                        urlAnalysisFromId(checkId);
                    } else {
                        Log.d("parazit4", "Error: " + response.code() + " " + response.message());
                    }
                } catch (Exception e) {
                    Toast.makeText(activity, "Zaman Aşımına Uğradı Tekrar Deneyin", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void showSafeDialog(int harmless, int malicious, int suspicious) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_safe_dialog, (ConstraintLayout)
                activity.findViewById(R.id.layoutDialogContainer));

        builder.setView(view);

        ((TextView) view.findViewById(R.id.titleText)).setText("Taramada Virüs Bulunmadı!");
        ((TextView) view.findViewById(R.id.textMessage)).setText("Bulunan Zararsız İçerik: " + harmless
                + "\nBulunan Virüs Sayısı: " + malicious + "\nBelirsiz İçerik: " + suspicious);
        ((Button) view.findViewById(R.id.buttonActionStay)).setText("Tamam");
        ((Button) view.findViewById(R.id.buttonActionGo)).setText("Tarayıcıya git");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.safeicon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonActionStay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonActionGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (scannedUrl != null) {
                    Intent goToWeb = new Intent(Intent.ACTION_VIEW, scannedUrl);
                    activity.startActivity(goToWeb);
                }

            }
        });

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        alertDialog.show();


    }

    private static void showWarningDialog(int harmless, int malicious, int suspicious) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_warning_dialog, (ConstraintLayout)
                activity.findViewById(R.id.layoutDialogContainer));

        builder.setView(view);

        ((TextView) view.findViewById(R.id.titleText)).setText("Belirsiz!");
        ((TextView) view.findViewById(R.id.textMessage)).setText("Bulunan Zararsız İçerik: " + harmless
                + "\nBulunan Virüs Sayısı: " + malicious + "\nBelirsiz İçerik: " + suspicious);
        ((Button) view.findViewById(R.id.buttonActionStay)).setText("Tamam");
        ((Button) view.findViewById(R.id.buttonActionGo)).setText("Tarayıcıya git");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.safeicon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonActionStay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Webten görüntüle
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonActionGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "K", Toast.LENGTH_SHORT).show();
            }
        });

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        alertDialog.show();

    }

    private static void showDangerDialog(int harmless, int malicious, int suspicious) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_danger_dialog, (ConstraintLayout)
                activity.findViewById(R.id.layoutDialogContainer));

        builder.setView(view);

        ((TextView) view.findViewById(R.id.titleText)).setText("Virüs Tespit Edildi!");
        ((TextView) view.findViewById(R.id.textMessage)).setText("Bulunan Zararsız İçerik: " + harmless
                + "\nBulunan Virüs Sayısı: " + malicious + "\nBelirsiz İçerik: " + suspicious);
        ((Button) view.findViewById(R.id.buttonAction)).setText("Tamam");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.virus);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Webten görüntüle
                alertDialog.dismiss();
            }
        });


        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        alertDialog.show();

    }

}
