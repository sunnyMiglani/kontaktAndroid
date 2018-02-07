package com.example.omg.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;


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

            // SetAction uses a string which is an important name as it identifies the sender of the intent and that we will give to the receiver to know what to listen.
            // By convention, it's suggested to use the current package name
            sendIntent.setAction("com.example.omg.myapplication.IntentToUnity");

            // Here we fill the Intent with our data, here just a string with an incremented number in it.
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Intent "+numIntent);
            // And here it goes ! our message is send to any other app that want to listen to it.
            sendBroadcast(sendIntent);

            // In our case we run this method each second with postDelayed
            handler.removeCallbacks(this);
            handler.postDelayed(this, 1000);
        }
    };

    public void onStartCommand(Intent intent, int startid) {
        numIntent = 0;
        // We first start the Handler
        handler.removeCallbacks(sendData);
        handler.postDelayed(sendData, 1000);
    }

    public UnityService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
