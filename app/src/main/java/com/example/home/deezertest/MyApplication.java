package com.example.home.deezertest;

import android.app.Application;
import android.content.Context;

/**
 *Creates an application context so that the context can be used by other (classes).
 */
public class MyApplication extends Application {

    public static final String DEEZER_APP_ID = "";
    private static MyApplication sInstance;


    @Override
    public void onCreate(){
        super.onCreate();
        sInstance=this;
    }

    public static MyApplication getsInstance(){
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }

}

