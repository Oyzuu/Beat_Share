package be.omnuzel.beatshare.controller;

import com.facebook.stetho.Stetho;
import com.orm.SugarApp;
import com.orm.SugarContext;

/**
 * Created by isdc on 9/11/16.
 */

public class MyApplication extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
        Stetho.initializeWithDefaults(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}
