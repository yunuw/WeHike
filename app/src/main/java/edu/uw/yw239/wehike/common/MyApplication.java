package edu.uw.yw239.wehike.common;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by Yun on 12/2/2017.
 */

public class MyApplication extends Application {

    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }
}