package com.example.egercomms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.egercomms.eventObjects.AnnouncementEventObject;
import com.example.egercomms.models.Announcement;
import com.example.egercomms.models.Jurisdiction;
import com.example.egercomms.services.announcement.AnnouncementService;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

public class AnnouncementActivity extends AppCompatActivity implements AnnouncementFragment.OnListFragmentInteractionListener{
    public static final String TAG = "AnnActivity";

    private BroadcastReceiver announcementServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Announcement[] dataItems = (Announcement[]) intent
                    .getParcelableArrayExtra(AnnouncementService.MY_SERVICE_PAYLOAD);
            Log.e(TAG, "onReceive: "+ Arrays.toString(dataItems));
            if (dataItems != null) {
                Toast.makeText(context,
                        "Received " + dataItems.length + " items from service",
                        Toast.LENGTH_SHORT).show();

                List<Announcement> announcements = Arrays.asList(dataItems);
                AnnouncementEventObject announcementEventObject = new AnnouncementEventObject(announcements);
                EventBus.getDefault().post(announcementEventObject);
            }else{
                Toast.makeText(context, "Connection Problem", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(announcementServiceBroadcastReceiver,
                        new IntentFilter(AnnouncementService.MY_SERVICE_MESSAGE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(announcementServiceBroadcastReceiver);
    }

    @Override
    public void onListFragmentInteraction(Announcement announcement) {
    }
}
