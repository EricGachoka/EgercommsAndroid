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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.egercomms.data.DataHandler;
import com.example.egercomms.eventObjects.AnnouncementEventObject;
import com.example.egercomms.eventObjects.FilePathEventObject;
import com.example.egercomms.models.Announcement;
import com.example.egercomms.services.announcement.AnnouncementService;
import com.example.egercomms.services.announcement.FileDownloadService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

public class AnnouncementActivity extends AppCompatActivity implements AnnouncementFragment.OnListFragmentInteractionListener{
    public static final String TAG = "AnnActivity";
    private DataHandler dataHandler = DataHandler.getInstance();
    private static final String PATH = "filePath";
    private StringBuilder builder = new StringBuilder();

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

    private BroadcastReceiver fileDownloadServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(FileDownloadService.MY_SERVICE_PAYLOAD);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        //TODO: set title dynamically
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(announcementServiceBroadcastReceiver,
                        new IntentFilter(AnnouncementService.MY_SERVICE_MESSAGE));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(fileDownloadServiceBroadcastReceiver,
                        new IntentFilter(FileDownloadService.MY_SERVICE_MESSAGE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(announcementServiceBroadcastReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(fileDownloadServiceBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onListFragmentInteraction(Announcement announcement) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downloadFile(FilePathEventObject filePathEventObject){
        String path = filePathEventObject.getPath();
        Intent intent = new Intent(this, FileDownloadService.class);
        intent.putExtra(PATH, path);
        startService(intent);
    }
}
