package com.example.omg.myapplication;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 *

public class UnityService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SEND_UNITY = "com.example.omg.myapplication.action.SEND_UNITY";
    // TODO: Rename parameters
    private static final String EXTRA_BEACON_NAME = "com.example.omg.myapplication.extra.BEACON_NAME";

    public UnityService() {
        super("UnityService");
    }


    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     *
    public static void startActionSendUnity(Context context, String param1) {
        Intent intent = new Intent(context, UnityService.class);
        intent.setAction(ACTION_SEND_UNITY);
        intent.putExtra(EXTRA_BEACON_NAME, param1); /*Add extended data to the intent. The name must include a package prefix
        context.startService(intent);
    }


    // This is where the work is performed
    /* This is called by the default worker thread when the service is started *
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_UNITY.equals(action)) {
                handleActionSendToUnity(intent);
                //sendBroadcast(intent);
            } //else if
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     *
    private void handleActionSendToUnity(Intent intent) {
        Log.i("tag", "Sending the following message :" + intent.getStringExtra(EXTRA_BEACON_NAME));
        sendBroadcast(intent);
    }
}
*/
/*
public class UnityService extends Service {

    private final Handler handler = new Handler();

    private int numIntent;

    // It's the code we want our Handler to execute to send data
    private Runnable sendData = new Runnable() {
        // the specific method which will be executed by the handler
        public void run() {
            numIntent++;

            // sendIntent is the object that will be broadcast outside our app
            Intent sendIntent = new Intent();

            // We add flags for example to work from background
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_FROM_BACKGROUND|Intent.FLAG_INCLUDE_STOPPED_PACKAGES  );

            // SetAction uses a string which is an important name as it identifies the sender of the itent and that we will give to the receiver to know what to listen.
            // By convention, it's suggested to use the current package name
            sendIntent.setAction("com.example.omg.myapplication.action.SEND_UNITY");

            // Here we fill the Intent with our data, here just a string with an incremented number in it.
            sendIntent.putExtra("com.example.omg.myapplication.extra.BEACON_NAME", "Intent "+numIntent);
            // And here it goes ! our message is send to any other app that want to listen to it.
            sendBroadcast(sendIntent);

            // In our case we run this method each second with postDelayed
            handler.removeCallbacks(this);
            handler.postDelayed(this, 1000);
        }
    };

    // When service is started
    @Override
    public void onStart(Intent intent, int startid) {
        numIntent = 0;
        // We first start the Handler
        handler.removeCallbacks(sendData);
        handler.postDelayed(sendData, 1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}*/
public class UnityService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private static final String ACTION_SEND_UNITY = "com.example.omg.myapplication.action.SEND_UNITY";
    private static final String EXTRA_BEACON_NAME = "com.example.omg.myapplication.extra.BEACON_NAME";
    private static final String EXTRA_DEVICE = "com.example.omg.myapplication.extra.DEVICE";

    private int counter = 0;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            Intent thisIntent = (Intent) msg.obj; //Have to typecast this for sum reason void*?
            Intent sendIntent = new Intent();
            sendIntent.setAction(ACTION_SEND_UNITY);
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_FROM_BACKGROUND|Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendIntent.putExtra(EXTRA_BEACON_NAME, thisIntent.getStringExtra(EXTRA_BEACON_NAME));
            //sendIntent.putExtra(EXTRA_BEACON_NAME, thisIntent.getStringExtra(EXTRA_DEVICE));

            //thisIntent.setAction(ACTION_SEND_UNITY); //Set the action of the intent
            //thisIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_FROM_BACKGROUND|Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(sendIntent);
            //Log.i("Handle message", thisIntent.getStringExtra(EXTRA_BEACON_NAME));
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.obj = intent; // CONTAINS ACTION AND BEACON NAME
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }


}