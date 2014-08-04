package com.michaelcarrano.sup;

import com.parse.Parse;
import com.parse.PushService;

import android.app.Application;

/**
 * Created by michaelcarrano on 7/20/14.
 */
public class App extends Application {

    private static final String PARSE_APP_ID = "";

    private static final String PARSE_CLIENT_KEY = "";

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize the Parse SDK.
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);

        // Specify an Activity to handle all pushes by default.
        PushService.setDefaultPushCallback(this, MainActivity.class);
    }

}
