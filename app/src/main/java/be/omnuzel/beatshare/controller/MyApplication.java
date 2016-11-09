package be.omnuzel.beatshare.controller;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by isdc on 9/11/16.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }

}
