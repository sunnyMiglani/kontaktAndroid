package com.example.omg.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;


import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;


public class MainActivity extends AppCompatActivity {

    //A number we create to request permissions
    private static final int MY_REQ_FINE_LOC = 1234;

    //Button functionality
    private boolean hasBeenClicked = false;

    /* Strings used to store intents */
    public static final String EXTRA_BEACON_NAME        = "com.example.omg.myapplication.extra.BEACON_NAME";
    public static final String EXTRA_DEVICE             = "com.example.omg.myapplication.extra.DEVICE";
    public static final String ACTION_BEACON_DISCOVERED = "com.example.omg.myapplication.action.BEACON_DISCOVERED";
    public static final String ACTION_SEND_UNITY        = "com.example.omg.myapplication.action.SEND_UNITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KontaktSDK.initialize(this);
        setContentView(R.layout.activity_main);
        checkPermissions();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(ACTION_BEACON_DISCOVERED));
    }


    /* Receives broadcasts from KontaktBackgroundScan
    *  then subsequently broadcasts them (globally) to UnityReceiver */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(EXTRA_DEVICE); // Get extra data included in the Intent
            Log.d("receiver", "Got message: " + message);
            Intent unityIntent = new Intent(getApplicationContext(), UnityService.class);
            unityIntent.putExtra(EXTRA_BEACON_NAME, message);
            startService(unityIntent);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        final Button button  = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasBeenClicked) {
                    button.setText("Actively searching for beacons!");
                    startService( new Intent(getApplicationContext(), KontaktBackgroundService.class) );
                    hasBeenClicked = true;
                }
                else if(hasBeenClicked){
                    button.setText("Click To Search for Beacons");
                    stopService(new Intent(getApplicationContext(), KontaktBackgroundService.class));
                    hasBeenClicked = false;
                    onStop();
                }
            }
        });
    }

    /* Check the permissions of the device, and request them if necessary */
    private void checkPermissions() {
        int checkSelfPermissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (PackageManager.PERMISSION_GRANTED != checkSelfPermissionResult) {
            //Permission not granted so we ask for it. Results are handled in onRequestPermissionsResult() callback.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQ_FINE_LOC);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQ_FINE_LOC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do what we need to do with the location

                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    // permission denied, boo!
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy()
    {
        stopService(new Intent(getApplicationContext(), KontaktBackgroundService.class));
        stopService(new Intent(getApplicationContext(), UnityService.class));
        Log.i("Destroy", "Service destroyed" + MainActivity.class);
        super.onDestroy();
    }
}
