package com.example.omg.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UnityReciever extends BroadcastReceiver {

    private static UnityReciever instance;
    private static final String EXTRA_BEACON_NAME = "com.example.omg.myapplication.extra.BEACON_NAME";
    public static String intentMessage;

    @Override
    public void onReceive(Context context, Intent intent) {
        String recieveString = intent.getStringExtra(EXTRA_BEACON_NAME);
        if (recieveString != null) {
            // We assigned it to our static variable
            intentMessage = recieveString;
        }
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
    }

    public static void createInstance()
    {
        if(instance ==  null)
            instance = new UnityReciever();
    }
}
