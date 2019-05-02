package com.example.egercomms.services.jurisdiction;
import com.example.egercomms.models.Jurisdiction;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface JurisdictionWebService {

    String BASE_URL = "https://gachokaeric.pythonanywhere.com/";
    String FACULTIES_FEED = "jurisdictions/api/faculties";
    String DEPARTMENTS_FEED= "jurisdictions/api/departments";
    String SUEU_SEATS_FEED = "jurisdictions/api/sueu-seats";
    String RESIDENCE_HALLS_FEED = "jurisdictions/api/residence-halls";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET(FACULTIES_FEED)
    Call<Jurisdiction[]> faculties();

    @GET(DEPARTMENTS_FEED)
    Call<Jurisdiction[]> departments();

    @GET(SUEU_SEATS_FEED)
    Call<Jurisdiction[]> sueu_seats();

    @GET(RESIDENCE_HALLS_FEED)
    Call<Jurisdiction[]> residence_halls();
}
