package com.example.egercomms;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.egercomms.data.DataHandler;
import com.example.egercomms.eventObjects.JurisdictionEventObject;
import com.example.egercomms.models.Announcement;
import com.example.egercomms.models.Jurisdiction;
import com.example.egercomms.models.NavBarItem;
import com.example.egercomms.services.announcement.AnnouncementService;
import com.example.egercomms.services.jurisdiction.JurisdictionService;
import com.example.egercomms.utils.NetworkHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements JurisdictionFragment.OnListFragmentInteractionListener, View.OnClickListener {

    public static final String JURISDICTION = "jurisdiction";
    public static final String NAME = "name";
    public static final String FACULTIES = "faculties";
    public static final String DEPARTMENTS = "departments";
    public static final String SUEU_SEATS = "sueu_seats";
    public static final String RESIDENCE_HALLS = "residence_halls";
    public static final String TAG = "MyActivity";
    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private DataHandler dataHandler = DataHandler.getInstance();
    DrawerLayout drawer;
    private boolean permissionGranted;

    private BroadcastReceiver jurisdictionServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Jurisdiction[] dataItems = (Jurisdiction[]) intent
                    .getParcelableArrayExtra(JurisdictionService.MY_SERVICE_PAYLOAD);
            if (dataItems != null) {
                Toast.makeText(context,
                        "Received " + dataItems.length + " items from service",
                        Toast.LENGTH_SHORT).show();

                List<Jurisdiction> jurisdictions = new ArrayList<>(Arrays.asList(dataItems));
                dataHandler.setJurisdictions(jurisdictions);
                JurisdictionEventObject jurisdictionEventObject = new JurisdictionEventObject(jurisdictions);
                EventBus.getDefault().post(jurisdictionEventObject);
            } else {
                Toast.makeText(context, "No jurisdictions yet", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        boolean networkOk = NetworkHelper.hasNetworkAccess(this);
        if (dataHandler.getJurisdictions() == null && networkOk) {
            startJurisdictionService(FACULTIES);
        } else if (!networkOk) {
            dataHandler.setJurisdictions(new ArrayList<>(Arrays.asList(new Jurisdiction("please connect to the internet"))));
        } else {
            JurisdictionEventObject jurisdictionEventObject = new JurisdictionEventObject(dataHandler.getJurisdictions());
            EventBus.getDefault().post(jurisdictionEventObject);
        }
        setNavBarButtons();

        if(!permissionGranted){
            dataHandler.setPermissionsGranted(checkPermissions());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(jurisdictionServiceBroadcastReceiver,
                        new IntentFilter(JurisdictionService.MY_SERVICE_MESSAGE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(jurisdictionServiceBroadcastReceiver);
    }

    private void setNavBarButtons() {
        for (NavBarItem item : NavBarItem.values()) {
            TextView itemView = (TextView) findViewById(item.getItemId());
            itemView.setOnClickListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onListFragmentInteraction(Jurisdiction item) {
        Log.e(TAG, "onListFragmentInteraction:" + item);
        if (NetworkHelper.hasNetworkAccess(this) && !item.getName().equalsIgnoreCase("please connect to the internet")) {
            Intent activityIntent = new Intent(this, AnnouncementActivity.class);
            startActivity(activityIntent);
            startAnnouncementService(item.getName());
        } else if (!NetworkHelper.hasNetworkAccess(this)) {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Invalid selection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        drawer.closeDrawer(GravityCompat.START);
        switch (NavBarItem.fromViewId(v.getId())) {
            case FACULTY_REPS:
                dataHandler.setItem("faculty-rep");
                startJurisdictionService(FACULTIES);
                break;
            case DEPARTMENT_REPS:
                dataHandler.setItem("department-rep");
                startJurisdictionService(DEPARTMENTS);
                break;
            case SUEU_GOVERNORS:
                dataHandler.setItem("sueu-leader");
                startJurisdictionService(SUEU_SEATS);
                break;
            case FACULTY_CONGRESS:
                dataHandler.setItem("faculty-congress");
                startJurisdictionService(FACULTIES);
                break;
            case RESIDENCE_CONGRESS:
                dataHandler.setItem("residence-congress");
                startJurisdictionService(RESIDENCE_HALLS);
                break;
        }
    }

    private void startJurisdictionService(String jurisdiction) {
        if (NetworkHelper.hasNetworkAccess(this)) {
            Intent intent = new Intent(this, JurisdictionService.class);
            intent.putExtra(JURISDICTION, jurisdiction);
            startService(intent);
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAnnouncementService(String name) {
        if (NetworkHelper.hasNetworkAccess(this)) {
            Intent intent = new Intent(this, AnnouncementService.class);
            intent.putExtra(NAME, name);
            startService(intent);
            Log.e(TAG, "startAnnouncementService: " + name);
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }

    // Checks if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // Initiate request for permissions.
    private boolean checkPermissions() {

        if (!isExternalStorageWritable()) {
            Toast.makeText(this, "This app only works on devices with usable external storage",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE);
            return false;
        } else {
            return true;
        }
    }

    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    dataHandler.setPermissionsGranted(true);
                    Toast.makeText(this, "External storage permission granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "You must grant permission to download files", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}