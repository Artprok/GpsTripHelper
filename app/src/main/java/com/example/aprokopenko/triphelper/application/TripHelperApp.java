package com.example.aprokopenko.triphelper.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.aprokopenko.triphelper.dependency_injection.AppModule;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.DaggerApplicationComponent;
import com.example.aprokopenko.triphelper.ApplicationComponent;

public class TripHelperApp extends Application {
    private static final String LOG_TAG = "TripHelperApp";
    private static ApplicationComponent applicationComponent;
    private static Context              context;

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public static Context getAppContext() {
        return context;
    }

    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AppModule module = new AppModule(this);
        applicationComponent = DaggerApplicationComponent.builder().appModule(module).build();
        if (ConstantValues.LOGGING_ENABLED) {
            Log.i(LOG_TAG, "onCreate:Dagger2 testing,ApplicationCreated, context is " + context.toString());
        }
    }
}
