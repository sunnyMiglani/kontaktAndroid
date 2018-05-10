package com.example.omg.myapplication;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanMode;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.Proximity;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;

import java.util.List;

import static com.example.omg.myapplication.MainActivity.ACTION_BEACON_DISCOVERED;
import static com.example.omg.myapplication.MainActivity.EXTRA_DEVICE;


/* Service that implements a background scan for Kontakt beacons.
*  We use a service, as if this is performed in the main activity, scanning is suspended when
*  the app is not in-use
*/
public class KontaktBackgroundService extends Service {

    private static final String TAG = "KontaktBGS";
    private final Handler handler = new Handler();

    // proximity Manager is an entry point for all operations connected with ranging
    // and monitoring BLE devices
    private ProximityManager proximityManager;
    private boolean isRunning; // Flag indicating if service is already running.
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    /* Constructor */
    public KontaktBackgroundService() {
    }

    /* If the Service is not active, it must be created in the call to
     * startService("my intent", KontaktBackgroundService.class)
     * */
    @Override
    public void onCreate() {
        super.onCreate();
        /* Create a new thread that runs in the background, this
         * will perform the work that we want the service to implement */
        HandlerThread thread = new HandlerThread("BeaconScanService",
                Process.THREAD_PRIORITY_DEFAULT);
        thread.start(); //Start the thread
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        setupProximityManager();
        //setupNotification();
        isRunning = false;
    }

    /* Create a notification that keeps the user informed that the background service is still running
    */
    private void setupNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Beacon Search Active")
                .setContentText("Beacon Search Active")
                .setPriority(android.support.v7.app.NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager mnotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mnotificationManager.notify(001, mBuilder.build());
    }


    /* Configures the Kontakt ProximityManager -
    *  The Proximity Manager is an entry point for all operations
    *  connected with ranging and monitoring BLE device */
    private void setupProximityManager() {
        proximityManager = ProximityManagerFactory.create(this); //Create proximity manager instance
        /* Configure proximity manager basic options */
        proximityManager.configuration()
                .scanPeriod(ScanPeriod.RANGING) //Continuous scanning
                .scanMode(ScanMode.BALANCED)
                .activityCheckConfiguration(ActivityCheckConfiguration.DEFAULT);
        //Setting up iBeacon listener
        proximityManager.setIBeaconListener(createIBeaconListener());
    }

    /* This is called after onCreate (by the system) to start the service */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Check if service is already active
        if (isRunning) {
            Toast.makeText(this, "Service is already running.", Toast.LENGTH_SHORT).show();
            return START_STICKY;
        }
        startScanning();
        isRunning = true;

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
                Toast.makeText(KontaktBackgroundService.this, "Scanning...",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Listener used to report iBeacon scanning results. */
    private IBeaconListener createIBeaconListener() {
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                Log.i(TAG, "BEACON DISCOVERED: " + ibeacon.toString());
            }

            @Override
            public void onIBeaconLost(IBeaconDevice ibeacon, IBeaconRegion region){
                Log.i(TAG, "BEACON LOST: " + ibeacon.toString());
            }

            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> iBeacons, IBeaconRegion region) {
                Log.i(TAG,"UPDATED CALLED" + iBeacons.toString());
                double best = Double.POSITIVE_INFINITY; //Current best distance
                IBeaconDevice beacon = null;
                for(IBeaconDevice b : iBeacons) {
                    if ( ( b.getDistance() < best ) && ( b.getProximity() != Proximity.UNKNOWN )) {
                        beacon = b;
                        best = b.getDistance();
                    }
                }
                if(beacon!=null && (beacon.getDistance() < 0.5)) {
                    Log.i(".", "" + beacon.getMajor());
                    onDeviceDiscovered(beacon);
                }
            }
        };
    }

    /* This method implements the behavior that we desire when a
    *  beacon has been discovered by the system */
    private void onDeviceDiscovered(IBeaconDevice device) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage(); //Create a message
        msg.obj = device; // CONTAINS ACTION AND BEACON NAME
        mServiceHandler.sendMessage(msg);
        //TODO --> if we want to handle different callbacks then we could switch based on msg.arg1
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (proximityManager != null) {
            proximityManager.disconnect();
            proximityManager = null;
        }
        Toast.makeText(KontaktBackgroundService.this, "Scanning service stopped.", Toast.LENGTH_SHORT).show();
        Log.i("Destroy", "Service destroyed" + KontaktBackgroundService.class);
        //NotificationManager mnotificationManager =
          //      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //mnotificationManager.cancel(001);
        super.onDestroy();
        //TODO stop the thread
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            IBeaconDevice beacon = (IBeaconDevice) msg.obj;
            Intent intent = new Intent();
            intent.setAction(ACTION_BEACON_DISCOVERED);
            intent.putExtra(EXTRA_DEVICE, Integer.toString(beacon.getMajor()));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            Log.i( TAG,"Message sent");
        }
    }
}
