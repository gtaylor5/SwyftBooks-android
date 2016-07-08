package com.swyftlabs.swyftbooks;

import android.app.Application;
import android.content.Intent;
import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Created by Gerard on 6/28/2016.
 */
public class SwyftBooks extends Application {
    
    //used to ensure that parse is always initialized.
    @Override
    public void onCreate() {

        super.onCreate();
        Parse.initialize(this, "VRhTx2xu0CA8OBFOJVVCAMIsOUvu0ptAJALbdtBi", "CYk2jvgE5n4heZMrlnvJhrdCy6kT1m68M4JZri8C");

    }

}
