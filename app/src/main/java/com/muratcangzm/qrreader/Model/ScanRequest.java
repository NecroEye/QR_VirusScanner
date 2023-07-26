package com.muratcangzm.qrreader.Model;

import com.google.gson.annotations.SerializedName;


public class ScanRequest {

    @SerializedName("url")
    private String url;

    public ScanRequest(String url){

        this.url = url;

    }

}
