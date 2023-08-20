package com.muratcangzm.qrreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;
import com.muratcangzm.qrreader.Fragments.QRList;
import com.muratcangzm.qrreader.Fragments.QRviaCamera;
import com.muratcangzm.qrreader.Fragments.QRviaStorage;
import com.muratcangzm.qrreader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new QRviaCamera());


        // AdsMob ID ca-app-pub-1436561055108702~8789256862

        //Geçişli Test1 ca-app-pub-3940256099942544/8691691433
        //Banner test ca-app-pub-3940256099942544/6300978111

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });



        binding.bottomNavContainer.setOnItemSelectedListener(item -> {


            int itemId = item.getItemId();

            if (itemId == R.id.qrReader) {

                replaceFragment(new QRviaCamera());

            }
            else if (itemId == R.id.qrDocReader) {

                replaceFragment(new QRviaStorage());

            }
            else if (itemId == R.id.qrList) {

                replaceFragment(new QRList());

            }
            else {
                Snackbar.make(binding.bottomNavContainer, "Beklenmedik bir hata meydana geldi", Snackbar.LENGTH_SHORT).show();
            }

            return true;
        });


    }


    private void replaceFragment(Fragment fragment){

        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.Fragment_container,fragment,null).
                commit();

    }

}