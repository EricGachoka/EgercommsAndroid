package com.example.egercomms.services.staff;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.egercomms.data.DataHandler;
import com.example.egercomms.models.Account;
import com.example.egercomms.models.Jurisdiction;
import com.example.egercomms.models.Staff;
import com.example.egercomms.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;

public class StaffService extends IntentService {
    public static final String TAG = "ServiceStaff";
    public static final String MY_SERVICE_MESSAGE = "staffServiceMessage";
    public static final String MY_SERVICE_PAYLOAD = "staffServicePayload";
    public static final String FACULTY_REPS = "faculty-rep";
    public static final String DEPARTMENT_REPS = "department-rep";
    public static final String SUEU_GOVERNORS = "sueu-leader";
    public static final String FACULTY_CONGRESS = "faculty-congress";
    public static final String RESIDENCE_CONGRESS = "residence-congress";
    private DataHandler dataHandler = DataHandler.getInstance();
    String BASE_URL = "https://gachokaeric.pythonanywhere.com/account/api/";

    public StaffService() {
        super("StaffService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String jurisdictionScope = dataHandler.getItem();
        String jurisdiction_name = intent.getStringExtra("jurisdiction");
//        Make the web service request
        StaffWebService webService =
                StaffWebService.retrofit.create(StaffWebService.class);

        Call<Staff[]> call = null;
        switch (jurisdictionScope) {
            case FACULTY_REPS:
                call = webService.getStaff(getFullUrl(FACULTY_REPS, jurisdiction_name));
                break;
            case DEPARTMENT_REPS:
                call = webService.getStaff(getFullUrl(DEPARTMENT_REPS, jurisdiction_name));
                break;
            case SUEU_GOVERNORS:
                call = webService.getStaff(getFullUrl(SUEU_GOVERNORS, jurisdiction_name));
                break;
            case FACULTY_CONGRESS:
                call = webService.getStaff(getFullUrl(FACULTY_CONGRESS, jurisdiction_name));
                break;
            case RESIDENCE_CONGRESS:
                call = webService.getStaff(getFullUrl(RESIDENCE_CONGRESS, jurisdiction_name));
                break;
        }

        Account account;
        Jurisdiction jurisdiction = new Jurisdiction(jurisdiction_name);
        Staff jurisdictionStaff;
        Staff[] staff;
        if (call != null) {
            try {
                staff = call.execute().body();
                jurisdictionStaff = staff[0];
                account = new Account(jurisdiction, jurisdictionStaff);
            } catch (IOException e) {
                e.printStackTrace();
                account = new Account(jurisdiction, new Staff(new User("", "")));
                return;
            }
            returnToMainActivity(account);
        } else {
            returnToMainActivity(new Account(new Jurisdiction(""), new Staff(new User("", ""))));
        }
    }

    private void returnToMainActivity(Account account) {
        Intent messageIntent = new Intent(MY_SERVICE_MESSAGE);
        messageIntent.putExtra(MY_SERVICE_PAYLOAD, account);
        LocalBroadcastManager manager =
                LocalBroadcastManager.getInstance(getApplicationContext());
        manager.sendBroadcast(messageIntent);
    }

    private String getFullUrl(String scope, String name) {
        String url = new StringBuilder().append(BASE_URL).append(scope).append("/").append(name).toString();
        return url;
    }
}
