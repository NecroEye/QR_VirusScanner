package com.muratcangzm.qrreader.Api;

import com.muratcangzm.qrreader.Model.ScanRequest;
import com.muratcangzm.qrreader.Model.VirusTotalModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface VirusTotalApiService {


    String API_KEY = "ca51b3756aa20f7f99d75d3ffe38c1d43bd9ee1bab90c72ff597ccf6df317c9c";

    @Headers({"x-apikey: " + API_KEY})
    @POST("scan")
    Call<VirusTotalModel> scanUrl(@Body ScanRequest request);



}
