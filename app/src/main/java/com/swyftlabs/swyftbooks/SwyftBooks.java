package com.swyftlabs.swyftbooks;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

/**
 * Created by Gerard on 6/28/2016.
 */
public class SwyftBooks extends Application {
    
    //used to ensure that parse is always initialized.
    @Override
    public void onCreate() {

        super.onCreate();
        Parse.initialize(this, "VRhTx2xu0CA8OBFOJVVCAMIsOUvu0ptAJALbdtBi", "CYk2jvgE5n4heZMrlnvJhrdCy6kT1m68M4JZri8C");
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });


    }

}
