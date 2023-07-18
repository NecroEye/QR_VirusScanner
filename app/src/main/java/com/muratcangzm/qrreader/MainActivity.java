package com.muratcangzm.qrreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

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