package com.example.omg.myapplication;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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

}