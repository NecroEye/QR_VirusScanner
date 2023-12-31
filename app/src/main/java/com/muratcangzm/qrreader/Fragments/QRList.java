package com.muratcangzm.qrreader.Fragments;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.muratcangzm.qrreader.R;
import com.muratcangzm.qrreader.RecyclerView.Adapter;
import com.muratcangzm.qrreader.RecyclerView.RecyclerModel;
import com.muratcangzm.qrreader.RecyclerView.RecyclerViewEventListener;
import com.muratcangzm.qrreader.databinding.QrListBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class QRList extends Fragment implements RecyclerViewEventListener {


    private LinearLayoutManager manager;
    private List<RecyclerModel> barcodeModel;
    private EditText popupEditText;
    private Fragment fragment;
    private Button popupButton;
    private Spinner popupSpinner;
    private Adapter adapter;
    private View itemView;
    private static final String PREF_NAME = "barcodeStorage";
    private static final String KEY_PREFIX = "Data_";
    private TextView safetyTextView, safetyRealText;

    private SharedPreferences sharedPreferences;

    private QrListBinding binding;

    public QRList() {
        //Empty Constructor

        //QRList Banner
        //ca-app-pub-1436561055108702/7981503859

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = QrListBinding.inflate(getLayoutInflater(), container, false);

        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, getContext().MODE_PRIVATE);
        barcodeModel = new ArrayList<>();

        // Retrieve the number of items stored previously (if any)

        itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_design,
                requireActivity().findViewById(R.id.itemContainer));


        safetyTextView = itemView.findViewById(R.id.safetyStatusText);


        int itemCount = sharedPreferences.getInt("itemCount", 0);


        Bundle bundle = getArguments();


        if (itemCount > 0) {
            // Load previously saved items
            for (int i = itemCount; 1 <= i; i--) {

                String _type = sharedPreferences.getString(KEY_PREFIX + i + "_Type", "");
                String _raw = sharedPreferences.getString(KEY_PREFIX + i + "_Raw", "");
                String _time = sharedPreferences.getString(KEY_PREFIX + i + "_Time", "");
                String _safety = sharedPreferences.getString(KEY_PREFIX + i + "_Safety", "");
                Log.d("Kayıtlı: ", _type);
                if (_type.matches("URL")) {
                    initData(_type, _raw, _time, _safety);

                } else if (_type.matches("Ürün")) {
                    initData(_type, _raw, _time, "");

                }

            }
        } else {
            barcodeModel.add(new RecyclerModel(R.drawable.link, "URL", "www.google.com", "Güvenli", "unknown", null));
        }


        if (bundle != null) {

            String type = bundle.getString("KEY_TYPE");
            String raw = bundle.getString("KEY_RAW");
            String time = bundle.getString("KEY_TIME");

            Log.d("Tipi: ", "" + type);
            Log.d("Güvenlik: ", "" + QRviaCamera.safety);

            initData(type, raw, time, QRviaCamera.safety);
            if (type.matches("Ürün")) saveData(type, raw, time, "");
            else {
                saveData(type, raw, time, QRviaCamera.safety);
            }
        }

        initRecyclerView();

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLongClickable(true);

        fragment = new QRviaCamera();

        safetyRealText = view.findViewById(R.id.safetyStatusText);


        binding.floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createPopUpWindow();


            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void initData(final String type, final String raw, final String time, final String safety) {


        switch (type) {

            case "URL": {

                barcodeModel.add(new RecyclerModel(R.drawable.link, type, raw, safety, time, null));

            }
            break;

            case "Ürün": {

                barcodeModel.add(new RecyclerModel(R.drawable.product, type, raw, "", time, null));

            }
            break;
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerView() {

        manager = new LinearLayoutManager(requireContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        binding.recyclerView.setLayoutManager(manager);
        adapter = new Adapter(barcodeModel, this);
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }


    private void saveData(String type, String raw, String time, String safety) {
        int itemCount = sharedPreferences.getInt("itemCount", 0);
        itemCount++;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PREFIX + itemCount + "_Type", type);
        editor.putString(KEY_PREFIX + itemCount + "_Raw", raw);
        editor.putString(KEY_PREFIX + itemCount + "_Time", time);
        editor.putString(KEY_PREFIX + itemCount + "_Safety", safety);
        editor.putInt("itemCount", itemCount);
        editor.apply();
    }


    @Override
    public void onClickListener(int position) {

        //Toast.makeText(requireContext(), "Tıklandı: " + position, Toast.LENGTH_SHORT).show();

        if (barcodeModel.get(position).getType().contains("URL")) {


            String formatted = null;
            Uri web = null;
            String webURL = barcodeModel.get(position).getRawValue();

            if (!webURL.startsWith("https://")) {


                formatted = "https://" + webURL;
                web = Uri.parse(formatted);

            } else {
                web = Uri.parse(webURL);
            }

            Intent goToWeb = new Intent(Intent.ACTION_VIEW, web);
            requireActivity().startActivity(goToWeb);


        } else {

            ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Veri", barcodeModel.get(position).getRawValue());
            clipboardManager.setPrimaryClip(clipData);
            Snackbar.make(binding.recyclerView, "Kopyalandı", Snackbar.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onLongClickListener(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle("Silme işlemi")
                .setMessage("Kaldırmak istediğinize emin misiniz?")
                .setCancelable(true)
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (position >= 0 && position < barcodeModel.size()) {
                            barcodeModel.remove(position);
                            adapter.notifyItemRemoved(position);

                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            // Clear all previous data
                            editor.clear();

                            // Re-add all items from barcodeModel to SharedPreferences
                            for (int i = 0; i < barcodeModel.size(); i++) {
                                RecyclerModel model = barcodeModel.get(i);
                                String type = model.getType();
                                String raw = model.getRawValue();
                                String time = model.getTime();
                                String safety = model.getSafety();

                                editor.putString(KEY_PREFIX + (i + 1) + "_Type", type);
                                editor.putString(KEY_PREFIX + (i + 1) + "_Raw", raw);
                                editor.putString(KEY_PREFIX + (i + 1) + "_Time", time);
                                editor.putString(KEY_PREFIX + (i + 1) + "_Safety", safety);
                            }

                            // Update the item count and apply changes
                            editor.putInt("itemCount", barcodeModel.size());
                            editor.apply();

                            Toast.makeText(requireContext(), "Kaldırıldı.", Toast.LENGTH_SHORT).show();
                        }


                    }
                })
                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // do nothing
                    }
                });
        builder.show();

    }


    private void createPopUpWindow() {


        LayoutInflater layoutInflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popUpView = layoutInflater.inflate(R.layout.popuplayout, null);

        popupEditText = (EditText) popUpView.findViewById(R.id.editText_popup);
        popupSpinner = (Spinner) popUpView.findViewById(R.id.spinner);
        popupButton = (Button) popUpView.findViewById(R.id.saveManually);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.spinner_array,
                R.layout.spinner_text_layout);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        popupSpinner.setAdapter(spinnerAdapter);


        PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);

        popupWindow.showAtLocation(binding.qrListFrame, Gravity.CENTER, 0, 0);


        binding.qrListFrame.post(() -> {

            popupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    switch (position) {

                        case 0: {

                            popupButton.setOnClickListener(v -> {

                                Snackbar.make(popupSpinner, "Türünü Seçmediniz", Snackbar.LENGTH_SHORT).show();

                            });
                        }

                        break;
                        case 1: {
                            popupButton.setOnClickListener(v -> {

                                if (popupEditText.getText().length() > 0) {
                                    saveData("URL", popupEditText.getText().toString(), getTime(), "");

                                    popupWindow.dismiss();
                                    requireActivity().getSupportFragmentManager()
                                            .beginTransaction().replace(R.id.Fragment_container, fragment, null)
                                            .commit();
                                    Toast.makeText(requireContext(), "Başarılı bir şekilde eklendi.", Toast.LENGTH_SHORT).show();

                                } else {

                                    Snackbar.make(popupSpinner, "Boş bırakamazsınız", Snackbar.LENGTH_SHORT).show();

                                }
                            });
                        }
                        break;
                        case 2: {

                            popupButton.setOnClickListener(v -> {

                                if (popupEditText.getText().length() > 0) {
                                    saveData("Ürün", popupEditText.getText().toString(), getTime(), "");

                                    popupWindow.dismiss();
                                    requireActivity().getSupportFragmentManager()
                                            .beginTransaction().replace(R.id.Fragment_container, fragment, null)
                                            .commit();
                                    Toast.makeText(requireContext(), "Başarılı bir şekilde eklendi.", Toast.LENGTH_SHORT).show();

                                } else {
                                    Snackbar.make(popupSpinner, "Boş bırakamazsınız", Snackbar.LENGTH_SHORT).show();
                                }

                            });
                        }
                        break;
                        default: {
                            popupWindow.dismiss();
                        }

                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                    popupButton.setOnClickListener(v -> {

                        popupWindow.dismiss();

                    });

                }
            });


        });


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

}
