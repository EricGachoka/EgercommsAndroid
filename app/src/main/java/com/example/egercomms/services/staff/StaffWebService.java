package com.example.egercomms.services.staff;

import com.example.egercomms.models.Staff;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface StaffWebService {
    String BASE_URL = "https://gachokaeric.pythonanywhere.com/";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET
    Call<Staff[]> getStaff(@Url String url);
}
