package com.example.aprokopenko.triphelper.application;

import android.content.SharedPreferences;
import android.app.Application;
import android.content.Context;

import com.example.aprokopenko.triphelper.dependency_injection.AppModule;
import com.example.aprokopenko.triphelper.DaggerApplicationComponent;
import com.example.aprokopenko.triphelper.ApplicationComponent;

public class TripHelperApp extends Application {
    private static final String LOG_TAG = "TripHelperApp";
    private static ApplicationComponent applicationComponent;
    private static Context              context;
    private static SharedPreferences    sharedPreferences;

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public static Context getAppContext() {
        return context;
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sharedPreferences = this.getSharedPreferences("tripPreferences", Context.MODE_PRIVATE);
        AppModule module = new AppModule(this);
        applicationComponent = DaggerApplicationComponent.builder().appModule(module).build();
    }
}
