package com.muratcangzm.qrreader.RecyclerView;

import androidx.annotation.Nullable;

public class RecyclerModel {


    private int imageView;
    private String type, rawValue, safety, time;
    private String divider;


    public RecyclerModel(int imageView, String type, String rawValue, String safety, String time, @Nullable String divider) {
        this.imageView = imageView;
        this.type = type;
        this.rawValue = rawValue;
        this.safety = safety;
        this.time = time;
        this.divider = divider;
    }


    public int getImageView(){
        return imageView;
    }

    public String getType() {
        return type;
    }

    public String getRawValue() {
        return rawValue;
    }

    public String getSafety() {
        return safety;
    }

    public String getTime() {
        return time;
    }


    public String getDivider() {
        return divider;
    }
}
