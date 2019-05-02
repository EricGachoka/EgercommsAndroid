package com.example.egercomms.services.announcement;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.egercomms.data.DataHandler;
import com.example.egercomms.models.Announcement;

import java.io.IOException;

import retrofit2.Call;

public class AnnouncementService extends IntentService {

    public static final String TAG = "AnnouncementService";
    public static final String MY_SERVICE_MESSAGE = "announcementMessage";
    public static final String MY_SERVICE_PAYLOAD = "announcementServicePayload";
    private DataHandler dataHandler = DataHandler.getInstance();
    String BASE_URL = "https://gachokaeric.pythonanywhere.com/announcements/api/";
    private StringBuilder builder = new StringBuilder();
    String url;
    private static final String FACULTY_REP = "faculty_rep";
    private static final String DEPARTMENT_REP = "department-rep";
    private static final String SUEU_LEADER = "sueu-leader";
    private static final String FACULTY_CONGRESS = "faculty-congress";
    private static final String RESIDENCE_CONGRESS = "residence-congress";

    public AnnouncementService() {
        super("AnnouncementService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String name = intent.getStringExtra("name");
        String clickedItemName = dataHandler.getItem();
//        Make the web service request
        AnnouncementWebService webService =
                AnnouncementWebService.retrofit.create(AnnouncementWebService.class);

        Call<Announcement[]> call = null;
        if (clickedItemName != null) {
            switch (clickedItemName) {
                case FACULTY_REP:
                    url = builder.append(BASE_URL).append(FACULTY_REP).append("/").append(name).toString();
                    call = webService.facultyRepAnnouncements(url);
                    break;
                case DEPARTMENT_REP:
                    url = builder.append(BASE_URL).append(DEPARTMENT_REP).append("/").append(name).toString();
                    call = webService.departmentRepAnnouncements(url);
                    break;
                case SUEU_LEADER:
                    url = builder.append(BASE_URL).append(SUEU_LEADER).append("/").append(name).toString();
                    call = webService.sueuGovernorAnnouncements(url);
                    break;
                case FACULTY_CONGRESS:
                    url = builder.append(BASE_URL).append(FACULTY_CONGRESS).append("/").append(name).toString();
                    call = webService.facultyCongressAnnouncements(url);
                    break;
                case RESIDENCE_CONGRESS:
                    url = builder.append(BASE_URL).append(RESIDENCE_CONGRESS).append("/").append(name).toString();
                    call = webService.residenceCongressAnnouncements(url);
                    break;
            }
        } else {
            url = builder.append(BASE_URL).append(FACULTY_REP).append("/").append(name).toString();
            call = webService.facultyRepAnnouncements(url);
        }

        Announcement[] dataItems;
        if (call != null) {
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
        } else {
//            Return the data to MainActivity
            Intent messageIntent = new Intent(MY_SERVICE_MESSAGE);
            messageIntent.putExtra(MY_SERVICE_PAYLOAD, "none");
            LocalBroadcastManager manager =
                    LocalBroadcastManager.getInstance(getApplicationContext());
            manager.sendBroadcast(messageIntent);
        }
    }

}
