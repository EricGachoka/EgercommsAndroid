package com.example.egercomms;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.egercomms.data.DataHandler;
import com.example.egercomms.eventObjects.AccountEventObject;
import com.example.egercomms.models.Account;
import com.example.egercomms.models.Jurisdiction;
import com.example.egercomms.models.NavBarItem;
import com.example.egercomms.models.Staff;
import com.example.egercomms.models.User;
import com.example.egercomms.services.announcement.AnnouncementService;
import com.example.egercomms.services.jurisdiction.JurisdictionService;
import com.example.egercomms.services.staff.StaffService;
import com.example.egercomms.utils.NetworkHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
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
    public static final String NO_INTERNET = "no internet";
    public static final String FOLDER_PATH = File.separator + "Egercomms" + File.separator + "downloads";
    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private DataHandler dataHandler = DataHandler.getInstance();
    DrawerLayout drawer;
    private boolean permissionGranted;
    public List<Account> handlerAccounts = new ArrayList<>();
    private int count = 0;
    private int jurisdictionsSize = 0;
    public static boolean fetch = true;
    private Account noInternetAccount = new Account(new Jurisdiction("no internet"), new Staff(new User("", "")));

    private BroadcastReceiver staffServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ++count;
            Account account = (Account) intent.getParcelableExtra(StaffService.MY_SERVICE_PAYLOAD);
            handlerAccounts = dataHandler.getAccounts();
            if (account != null) {
                if (handlerAccounts != null) {
                    handlerAccounts.remove(noInternetAccount);
                    if (!handlerAccounts.contains(account)) {
                        handlerAccounts.add(account);
                    }
                } else {
                    handlerAccounts = new ArrayList<>(Arrays.asList(account));
                }
                dataHandler.setAccounts(handlerAccounts);
            } else {
                Toast.makeText(context, "No jurisdictions yet", Toast.LENGTH_SHORT).show();
            }
            if (count == jurisdictionsSize) {
                count = 0;
                Toast.makeText(context, "Received " + jurisdictionsSize + " jurisdictions", Toast.LENGTH_SHORT).show();
                AccountEventObject accountEventObject = new AccountEventObject(handlerAccounts);
                Log.e(TAG, "onReceive: " + accountEventObject.getAccounts());
                EventBus.getDefault().post(accountEventObject);
            }
        }
    };

    private BroadcastReceiver jurisdictionsServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Jurisdiction[] dataItems = (Jurisdiction[]) intent
                    .getParcelableArrayExtra(JurisdictionService.MY_SERVICE_PAYLOAD);
            if (dataItems != null && dataItems.length != 0) {
                jurisdictionsSize = dataItems.length;
                List<Jurisdiction> jurisdictions = new ArrayList<>(Arrays.asList(dataItems));
                dataHandler.setJurisdictions(jurisdictions);
                dataHandler.setAccounts(new ArrayList<Account>());
                Toast.makeText(context, "fetching...", Toast.LENGTH_LONG).show();
                Log.e(TAG, "onReceive: " + jurisdictions);
                for (Jurisdiction jurisdiction : jurisdictions) {
                    startAccountService(jurisdiction.getName());
                }
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
        if (dataHandler.getAccounts() == null && networkOk) {
            dataHandler.setItem("faculty-rep");
            startJurisdictionService(FACULTIES);
        } else if (!networkOk) {
            dataHandler.setAccounts(new ArrayList<>(Arrays.asList(noInternetAccount)));
        } else {
            AccountEventObject accountEventObject = new AccountEventObject(dataHandler.getAccounts());
            EventBus.getDefault().post(accountEventObject);
        }
        setNavBarButtons();

        if (!permissionGranted) {
            dataHandler.setPermissionsGranted(checkPermissions());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(staffServiceBroadcastReceiver,
                        new IntentFilter(StaffService.MY_SERVICE_MESSAGE));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(jurisdictionsServiceBroadcastReceiver,
                        new IntentFilter(JurisdictionService.MY_SERVICE_MESSAGE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(staffServiceBroadcastReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(jurisdictionsServiceBroadcastReceiver);
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

    @Override
    public void onListFragmentInteraction(Jurisdiction item) {
        if (NetworkHelper.hasNetworkAccess(this) && !item.getName().equalsIgnoreCase(NO_INTERNET)) {
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
        if (fetch) {
            handlerAccounts.clear();
        }
        switch (NavBarItem.fromViewId(v.getId())) {
            case FACULTY_REPS:
                if (!dataHandler.getItem().equalsIgnoreCase("faculty-rep")) {
                    if (fetch) {
                        dataHandler.setItem("faculty-rep");
                        fetch = false;
                        startJurisdictionService(FACULTIES);
                    } else {
                        Toast.makeText(this, "still fetching previous list...", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case DEPARTMENT_REPS:
                if (!dataHandler.getItem().equalsIgnoreCase("department-rep")) {
                    if (fetch) {
                        dataHandler.setItem("department-rep");
                        fetch = false;
                        startJurisdictionService(DEPARTMENTS);
                    } else {
                        Toast.makeText(this, "still fetching previous list...", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case SUEU_GOVERNORS:
                if (!dataHandler.getItem().equalsIgnoreCase("sueu-leader")) {
                    if (fetch) {
                        dataHandler.setItem("sueu-leader");
                        fetch = false;
                        startJurisdictionService(SUEU_SEATS);
                    } else {
                        Toast.makeText(this, "still fetching previous list...", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case FACULTY_CONGRESS:
                if (!dataHandler.getItem().equalsIgnoreCase("faculty-congress")) {
                    if (fetch) {
                        dataHandler.setItem("faculty-congress");
                        fetch = false;
                        startJurisdictionService(FACULTIES);
                    } else {
                        Toast.makeText(this, "still fetching previous list...", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case RESIDENCE_CONGRESS:
                if (!dataHandler.getItem().equalsIgnoreCase("residence-congress")) {
                    if (fetch) {
                        dataHandler.setItem("residence-congress");
                        fetch = false;
                        startJurisdictionService(RESIDENCE_HALLS);
                    } else {
                        Toast.makeText(this, "still fetching previous list...", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case DOWNLOADS:
                Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + FOLDER_PATH);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(selectedUri, "resource/folder");

                if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
                    startActivity(intent);
                } else {
                    // if you reach this place, it means there is no any file
                    // explorer app installed on your device
                    Intent chooser = Intent.createChooser(intent, "Choose app to open folder");
                    // Verify the intent will resolve to at least one activity
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(chooser);
                    }
                }
                break;
        }
    }

    private void startJurisdictionService(String jurisdiction) {
        if (NetworkHelper.hasNetworkAccess(this)) {
            fetch = false;
            Intent intent = new Intent(this, JurisdictionService.class);
            intent.putExtra(JURISDICTION, jurisdiction);
            startService(intent);
        } else {
            fetch = true;
            dataHandler.setItem("");
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAnnouncementService(String name) {
        if (NetworkHelper.hasNetworkAccess(this)) {
            Intent intent = new Intent(this, AnnouncementService.class);
            intent.putExtra(NAME, name);
            startService(intent);
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAccountService(String name) {
        if (NetworkHelper.hasNetworkAccess(this)) {
            Intent intent = new Intent(this, StaffService.class);
            intent.putExtra(JURISDICTION, name);
            startService(intent);
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

                    if (dataHandler.getAccounts() == null && NetworkHelper.hasNetworkAccess(this)) {
                        dataHandler.setItem("faculty-rep");
                        startJurisdictionService(FACULTIES);
                    } else if (!NetworkHelper.hasNetworkAccess(this)) {
                        dataHandler.setAccounts(new ArrayList<>(Arrays.asList(new Account(new Jurisdiction("no internet"), new Staff(new User("", ""))))));
                    } else {
                        AccountEventObject accountEventObject = new AccountEventObject(dataHandler.getAccounts());
                        EventBus.getDefault().post(accountEventObject);
                    }

                } else {
                    Toast.makeText(this, "You must grant permission to download files", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}