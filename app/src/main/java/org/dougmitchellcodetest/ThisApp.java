package org.dougmitchellcodetest;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;


/**
 * Created by dougmitchell on 11/2/16.
 */

public class ThisApp extends Application {

    private AppComponent appComponent;


    @Override public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        JodaTimeAndroid.init(this);
    }

    public AppComponent getAppComponent() { return appComponent; }
}
