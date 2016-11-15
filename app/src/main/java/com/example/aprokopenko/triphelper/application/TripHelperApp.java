package com.example.aprokopenko.triphelper.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.aprokopenko.triphelper.ApplicationComponent;
import com.example.aprokopenko.triphelper.DaggerApplicationComponent;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.dependency_injection.AppModule;

public class TripHelperApp extends Application {
  private static final String LOG_TAG = "TripHelperApp";

  private static ApplicationComponent applicationComponent;
  private static SharedPreferences sharedPreferences;

  public static ApplicationComponent getApplicationComponent() {
    return applicationComponent;
  }

  public static SharedPreferences getSharedPreferences() {
    return sharedPreferences;
  }

  @Override public void onCreate() {
    super.onCreate();
    sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCES_TAG), Context.MODE_PRIVATE);
    applicationComponent = DaggerApplicationComponent.builder().appModule(new AppModule(this)).build();
  }
}