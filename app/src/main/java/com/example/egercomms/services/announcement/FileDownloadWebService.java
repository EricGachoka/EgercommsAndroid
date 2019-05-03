package com.example.egercomms.services.announcement;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface FileDownloadWebService {
    String BASE_URL = "https://gachokaeric.pythonanywhere.com";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET
    Call<ResponseBody> downloadAttachments(@Url String url);
}
