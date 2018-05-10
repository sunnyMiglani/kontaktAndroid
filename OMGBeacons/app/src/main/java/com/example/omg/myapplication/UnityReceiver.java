package com.example.omg.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UnityReceiver extends BroadcastReceiver {

    private static UnityReceiver instance;
    private static final String EXTRA_BEACON_NAME = "com.example.omg.myapplication.extra.BEACON_NAME";
    public static String intentMessage = "EMPTYMESSAGE";


    public UnityReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String receiveString = intent.getStringExtra(EXTRA_BEACON_NAME); /* Retrieve extended data from the intent. */
        if (receiveString != null) {
            // We assigned it to our static variable
            intentMessage = receiveString;
        }
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
    }

        public static void createInstance() {
            if (instance == null) {
                instance = new UnityReceiver();
            }
        }

}
