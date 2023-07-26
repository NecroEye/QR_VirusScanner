package com.muratcangzm.qrreader.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muratcangzm.qrreader.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements View.OnClickListener {

    private List<RecyclerModel> barcodeList;
    private AdapterView.OnItemClickListener clickListener;

    public Adapter(List<RecyclerModel> barcodeList){

        this.barcodeList = barcodeList;

    }


    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_design, parent ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {

        int resource = barcodeList.get(position).getImageView();
        String type = barcodeList.get(position).getType();
        String safety = barcodeList.get(position).getSafety();
        String rawValue = barcodeList.get(position).getRawValue();
        String time = barcodeList.get(position).getTime();
        String divider = barcodeList.get(position).getDivider();

        holder.setData(resource, type, safety, rawValue, time, divider);

    }




    @Override
    public int getItemCount() {
        return barcodeList.size();
    }

    @Override
    public void onClick(View v) {



    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView barcodeIcon;
        private TextView typeText;
        private TextView safetyText;
        private TextView rawValueText;
        private TextView timeText;
        private View dividerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            barcodeIcon = itemView.findViewById(R.id.imageViewItem);
            typeText = itemView.findViewById(R.id.typeOfBarcodeName);
            safetyText = itemView.findViewById(R.id.safetyStatusText);
            rawValueText = itemView.findViewById(R.id.barcodeRawValueText);
            timeText = itemView.findViewById(R.id.saveTimeText);
            dividerView = itemView.findViewById(R.id.dividerView);


        }


        public void setData(int resource, String type, String safety, String rawValue, String time, String divider) {

            barcodeIcon.setImageResource(resource);
            typeText.setText(type);
            safetyText.setText(safety);
            rawValueText.setText(rawValue);
            timeText.setText(time);
        }
    }
}
