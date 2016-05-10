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

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Override public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        AppModule module = new AppModule(this);
        applicationComponent = DaggerApplicationComponent.builder().appModule(module).build();
        if (ConstantValues.DEBUG_MODE) {
            Log.i(LOG_TAG, "onCreate:Dagger2 testing,ApplicationCreated, context is "+context.toString());
        }
    }
}
