package com.example.egercomms.services.announcement;

import com.example.egercomms.models.Announcement;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface AnnouncementWebService {

    String BASE_URL = "https://gachokaeric.pythonanywhere.com/announcements/api/";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET
    Call<Announcement[]> facultyRepAnnouncements(@Url String url);

    @GET
    Call<Announcement[]> departmentRepAnnouncements(@Url String url);

    @GET
    Call<Announcement[]> sueuGovernorAnnouncements(@Url String url);

    @GET
    Call<Announcement[]> residenceCongressAnnouncements(@Url String url);

    @GET
    Call<Announcement[]> facultyCongressAnnouncements(@Url String url);

}
