package com.example.egercomms.services.jurisdiction;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.egercomms.models.Jurisdiction;

import java.io.IOException;
import java.util.Arrays;

import retrofit2.Call;

public class JurisdictionService extends IntentService {

    public static final String TAG = "JurisdictionService";
    public static final String MY_SERVICE_MESSAGE = "myServiceMessage";
    public static final String MY_SERVICE_PAYLOAD = "myServicePayload";

    public JurisdictionService() {
        super("JurisdictionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String jurisdiction_name = intent.getStringExtra("jurisdiction");
//        Make the web service request
        JurisdictionWebService webService =
                JurisdictionWebService.retrofit.create(JurisdictionWebService.class);

        Call<Jurisdiction[]> call = null;
        switch(jurisdiction_name){
            case "faculties":
                call = webService.faculties();
                break;
            case "departments":
                call = webService.departments();
                break;
            case "sueu_seats":
                call = webService.sueu_seats();
                break;
            case "residence_halls":
                call = webService.residence_halls();
                break;
        }

        Jurisdiction[] dataItems;
        if(call != null){
            try {
                dataItems = call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            //        Return the data to MainActivity
            Intent messageIntent = new Intent(MY_SERVICE_MESSAGE);
            messageIntent.putExtra(MY_SERVICE_PAYLOAD, dataItems);
            LocalBroadcastManager manager =
                    LocalBroadcastManager.getInstance(getApplicationContext());
            manager.sendBroadcast(messageIntent);
        }else{
//            Return the data to MainActivity
            Intent messageIntent = new Intent(MY_SERVICE_MESSAGE);
            messageIntent.putExtra(MY_SERVICE_PAYLOAD,  "none");
            LocalBroadcastManager manager =
                    LocalBroadcastManager.getInstance(getApplicationContext());
            manager.sendBroadcast(messageIntent);
        }
    }

}
