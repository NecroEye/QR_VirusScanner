package com.muratcangzm.qrreader.Fragments;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


    private LinearLayoutManager manager;
    List<RecyclerModel> barcodeModel;
    Adapter adapter;
    private View itemView;
    private TextView safetyTextView;
    private SharedPreferences sharedPreferences = null;
    private static final String PREF_NAME = "barcodeStorage";

    private QrListBinding binding;

    public QRList() {
        //Empty Constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = QrListBinding.inflate(getLayoutInflater(), container, false);

        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, getContext().MODE_PRIVATE);

        itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_design,
                requireActivity().findViewById(R.id.itemContainer));

        safetyTextView = itemView.findViewById(R.id.safetyStatusText);

        Bundle bundle = getArguments();

        if (bundle != null) {

            String type = bundle.getString("KEY_TYPE");
            String raw = bundle.getString("KEY_RAW");
            String time = bundle.getString("KEY_TIME");


            if (QRviaCamera.safety != null) initData(type, raw, time, QRviaCamera.safety);
            initRecyclerView();


        } else {

            barcodeModel = new ArrayList<>();
            initRecyclerView();
            barcodeModel.add(new RecyclerModel(R.drawable.link, "URL", "www.google.com", "Güvenli", "unknown", null));

        }


        return binding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void initData(final String type, final String raw, final String time, final String safety) {

        barcodeModel = new ArrayList<>();


        switch (type) {

            case "URL": {


                switch (QRviaCamera.safety) {

                    case "Güvenli":
                        safetyTextView.setTextColor(Color.GREEN);
                        break;
                    case "Belirsiz":
                        safetyTextView.setTextColor(Color.YELLOW);
                        break;
                    case "Tehlikeli":
                        safetyTextView.setTextColor(Color.RED);
                }


                barcodeModel.add(new RecyclerModel(R.drawable.link, type, raw, safety, time, null));

            }
            break;

            case "Ürün": {

                barcodeModel.add(new RecyclerModel(R.drawable.product, type, raw, "", time, null));

            }
            break;
        }

    }


    private void initRecyclerView() {

        manager = new LinearLayoutManager(requireContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        binding.recyclerView.setLayoutManager(manager);
        adapter = new Adapter(barcodeModel);
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }
}
