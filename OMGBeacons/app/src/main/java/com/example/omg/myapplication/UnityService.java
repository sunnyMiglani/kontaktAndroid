package com.example.omg.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
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
     */
    public static void startActionSendUnity(Context context, String param1) {
        Intent intent = new Intent(context, UnityService.class);
        intent.setAction(ACTION_SEND_UNITY);
        intent.putExtra(EXTRA_BEACON_NAME, param1); /*Add extended data to the intent. The name must include a package prefix*/
        context.startService(intent);
    }


    // This is where the work is performed
    /* This is called by the default worker thread when the service is started */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_UNITY.equals(action)) {
                handleActionSendToUnity(intent);
            } //else if
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSendToUnity(Intent intent) {
        Log.i("tag", "Sending the following message :" + intent.getStringExtra(EXTRA_BEACON_NAME));
        sendBroadcast(intent);
    }
}
