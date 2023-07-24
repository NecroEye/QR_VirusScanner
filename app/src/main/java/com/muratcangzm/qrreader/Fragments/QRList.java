package com.muratcangzm.qrreader.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muratcangzm.qrreader.R;
import com.muratcangzm.qrreader.RecyclerView.Adapter;
import com.muratcangzm.qrreader.RecyclerView.RecyclerModel;
import com.muratcangzm.qrreader.databinding.QrListBinding;

import java.util.ArrayList;
import java.util.List;

public class QRList extends Fragment {


    LinearLayoutManager manager;
    List<RecyclerModel> barcodeModel;
    Adapter adapter;

    private QrListBinding binding;

    public QRList(){
        //Empty Constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = QrListBinding.inflate(getLayoutInflater(), container,false);

        initData();
        initRecyclerView();

        return binding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void initData() {


        barcodeModel = new ArrayList<>();

        barcodeModel.add(new RecyclerModel(R.drawable.link,"URL","www.google.com","Güvenli", "22:10",null));
        barcodeModel.add(new RecyclerModel(R.drawable.product,"Product","1234312341","Güvenli", "22:10",null));
        barcodeModel.add(new RecyclerModel(R.drawable.documents,"Saf Değeri","gDx1?ic,","", "22:10",null));

    }


    private void initRecyclerView(){

        manager = new LinearLayoutManager(requireContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        binding.recyclerView.setLayoutManager(manager);
        adapter = new Adapter(barcodeModel);
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }
}
